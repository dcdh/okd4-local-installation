package com.damdamdeo.okd_4_local_installation.steps.impl.guest;

import com.damdamdeo.okd_4_local_installation.steps.InstallationStep;
import com.damdamdeo.okd_4_local_installation.steps.impl.*;
import com.damdamdeo.okd_4_local_installation.steps.impl.host.NetworkVM;
import com.damdamdeo.okd_4_local_installation.steps.impl.host.VmType;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;
import java.util.stream.Collectors;

public class CreateServicesCentOS8GuestVirtualMachineInstallationStep extends InstallationStep {

    private final PebbleEngine engine = new PebbleEngine.Builder().build();

    private final BaseInstallationPath baseInstallationPath;
    private final CentOS8Disk centOS8Disk;
    private final SshRsaPublicKey sshRsaPublicKey;
    private final ContainerRegistry containerRegistry;
    private final NetworkVM serviceNetworkVM;
    private final OkdNetwork okdNetwork;
    private final GuestVirtualMachine guestVirtualMachine;
    private final SshGuestRemoteCommand sshGuestRemoteCommand;
    private final OkdRemoteInstaller okdRemoteInstaller;

    // https://cloud.centos.org/centos/8/x86_64/images/CentOS-8-GenericCloud-8.2.2004-20200611.2.x86_64.qcow2
    public CreateServicesCentOS8GuestVirtualMachineInstallationStep(final BaseInstallationPath baseInstallationPath,
                                                                    final String keyName,
                                                                    final CentOS8Disk centOS8Disk,
                                                                    final SshRsaPublicKey sshRsaPublicKey,
                                                                    final ContainerRegistry containerRegistry,
                                                                    final GuestVirtualMachine guestVirtualMachine,
                                                                    final OkdNetwork okdNetwork,
                                                                    final OkdRemoteInstaller okdRemoteInstaller) {
        this.baseInstallationPath = Objects.requireNonNull(baseInstallationPath);
        this.centOS8Disk = Objects.requireNonNull(centOS8Disk);
        this.sshRsaPublicKey = Objects.requireNonNull(sshRsaPublicKey);
        this.containerRegistry = Objects.requireNonNull(containerRegistry);
        this.okdNetwork = Objects.requireNonNull(okdNetwork);
        this.guestVirtualMachine = Objects.requireNonNull(guestVirtualMachine);
        if (!this.guestVirtualMachine.getVmType().equals(VmType.SERVICES)) {
            throw new IllegalStateException(String.format("guestVirtualMachine '%s' must be the services vm instance", this.guestVirtualMachine));
        }
        this.serviceNetworkVM = okdNetwork.getNetworkVmByGuestVirtualMachine(guestVirtualMachine);
        this.sshGuestRemoteCommand = new SshGuestRemoteCommand(baseInstallationPath,
                keyName,
                okdNetwork.getNetworkVmByGuestVirtualMachine(guestVirtualMachine));
        this.okdRemoteInstaller = Objects.requireNonNull(okdRemoteInstaller);
    }

    @Override
    public String title() {
        return String.format("Generate CentOS8 '%s' guest virtual machine", serviceNetworkVM.getFqdn());
    }

    @Override
    public String verifyStepIsAlreadyDoneCommand() {
        // putain il me faut un certain temps pour que le port ssh soit ouvert !!!
        return String.format("virsh list --all | grep %s", // check vm exists
                serviceNetworkVM.getFqdn());// grep running |  local can be french ...
    }

    // docker run -it --rm alpine mkpasswd --method=SHA-512
    // cp ~/okd_installation/qemu/CentOS-8-GenericCloud-8.2.2004-20200611.2.x86_64.qcow2 /tmp/services && virt-customize -a /tmp/services/CentOS-8-GenericCloud-8.2.2004-20200611.2.x86_64.qcow2 --root-password password:root && genisoimage -output /tmp/services/boot-init.iso -volid cidata -joliet -r /tmp/services/user-data /tmp/services/meta-data /tmp/services/network-config && qemu-img resize /tmp/services/CentOS-8-GenericCloud-8.2.2004-20200611.2.x86_64.qcow2 25G && virsh create /tmp/services/vm.xml
    // localectl set-keymap fr
    // ssh-keyscan -t ssh-rsa 10.0.5.57 > /root/.ssh/known_hosts
    // ssh -i /tmp/id_okd_vm okd@10.0.5.57
    // ssh -i /tmp/id_okd_vm centos@10.0.5.57
    // rm -f /tmp/services/CentOS-8-GenericCloud-8.2.2004-20200611.2.x86_64.qcow2 && virsh destroy services.sandbox.okd.local && > /root/.ssh/known_hosts

    private String generateMetaData() {
        try {
            final PebbleTemplate compiledTemplate = engine.getTemplate("services/meta-data");

            final Map<String, Object> context = new HashMap<>();
            context.put("virtualMachineFQDN", serviceNetworkVM.getFqdn());
            context.put("virtualMachineInstanceName", guestVirtualMachine.getMachineName());
            final Writer writer = new StringWriter();
            compiledTemplate.evaluate(writer, context);

            return writer.toString();
        } catch (final IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    private String generateUserData() {
        try {
            final PebbleTemplate compiledTemplate = engine.getTemplate("services/user-data");
            final Map<String, Object> context = new HashMap<>();
            context.put("virtualMachineFQDN", serviceNetworkVM.getFqdn());
            context.put("sshPublicKey", sshRsaPublicKey.key());
            context.put("domainCrt", containerRegistry.domainCrt());
            context.put("domainKey", containerRegistry.domainKey());
            context.put("networkVMs", okdNetwork.networkVMs());
            context.put("okd4K8sApiBe", okdNetwork.findNetworkVMsByVmType(VmType.BOOTSTRAP, VmType.MASTER));
            context.put("okd4MachineConfigServerBe", okdNetwork.findNetworkVMsByVmType(VmType.BOOTSTRAP, VmType.MASTER));
            context.put("okd4HttpIngressTrafficBe", okdNetwork.findNetworkVMsByVmType(VmType.MASTER, VmType.WORKER));
            context.put("okd4HttpsIngressTrafficBe", okdNetwork.findNetworkVMsByVmType(VmType.MASTER, VmType.WORKER));
            context.put("clusterName", okdNetwork.clusterName());
            context.put("clusterBaseDomain", okdNetwork.clusterBaseDomain());
            context.put("nbOfWorkers", okdNetwork.nbOfWorkers());
            context.put("nbOfMasters", okdNetwork.nbOfMasters());
            context.put("podmanRegistryDnsName", okdNetwork.getServiceNetworkVM().getFqdn());
            context.put("remoteInstallerUrl", okdRemoteInstaller.remoteInstallerUrl());

            final Writer writer = new StringWriter();
            compiledTemplate.evaluate(writer, context);

            return writer.toString();
        } catch (final IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    private String generateGuestVirtualMachine() {
        try {
            final PebbleTemplate compiledTemplate = engine.getTemplate("guest_virtual_machine");

            final Map<String, Object> context = new HashMap<>();
            context.put("name", serviceNetworkVM.getFqdn());
            context.put("memoryInGB", 4);
            context.put("vcpus", 2);
            context.put("diskFile", String.format("%s/%s/%s", baseInstallationPath.path(), serviceNetworkVM.getFqdn(), centOS8Disk.fileName()));
            context.put("networkMac", serviceNetworkVM.mac());
            context.put("networkInterface", okdNetwork.bridgeName());
            context.put("networkName", okdNetwork.name());
            context.put("bootInitIso", String.format("%s/%s/boot-init.iso", baseInstallationPath.path(), serviceNetworkVM.getFqdn()));
            final Writer writer = new StringWriter();
            compiledTemplate.evaluate(writer, context);

            return writer.toString();
        } catch (final IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    @Override
    public String commandToExecute() {
        final String metaData = generateMetaData();
        writeFile(String.format("%s/%s/meta-data", baseInstallationPath.path(), serviceNetworkVM.getFqdn()), metaData);

        final String userData = generateUserData();
        writeFile(String.format("%s/%s/user-data", baseInstallationPath.path(), serviceNetworkVM.getFqdn()) , userData);

        final String virtualMachine = generateGuestVirtualMachine();
        writeFile(String.format("%s/%s/vm.xml", baseInstallationPath.path(), serviceNetworkVM.getFqdn()), virtualMachine);

        final String serviceNetworkIp = serviceNetworkVM.ip();
        return String.format("mkdir -p ~/.ssh && ") +
                String.format(">> ~/.ssh/known_hosts && ") +
                String.format("sed -i '/^%s/d' ~/.ssh/known_hosts && ", serviceNetworkVM.ip()) +
                String.format("mkdir -p %s/%s/ && ", baseInstallationPath.path(), serviceNetworkVM.getFqdn()) +
                String.format("/bin/cp -rf %s %s/%s/%s && ", centOS8Disk.diskFrom(), baseInstallationPath.path(), serviceNetworkVM.getFqdn(), centOS8Disk.fileName()) +
                String.format("genisoimage -output %1$s/%2$s/boot-init.iso -volid cidata -joliet -r %1$s/%2$s/user-data %1$s/%2$s/meta-data && ", baseInstallationPath.path(), serviceNetworkVM.getFqdn()) +
                String.format("qemu-img resize %s/%s/CentOS-8-GenericCloud-8.2.2004-20200611.2.x86_64.qcow2 25G && ", baseInstallationPath.path(), serviceNetworkVM.getFqdn()) +
                String.format("virsh create %s/%s/vm.xml && ", baseInstallationPath.path(), serviceNetworkVM.getFqdn()) +
                String.format("(while ! nmap -p22 %s -oG - | grep -q 22/open; do echo 'waiting for port 22 to be open' && sleep 5; done) && ", serviceNetworkIp) +
                String.format("(while ! ssh-keyscan -t ssh-rsa %s >> ~/.ssh/known_hosts; do sleep 5; done) && ", serviceNetworkIp) +
                String.format("(while ! %s; do echo 'waiting for services to be ready' && sleep 5; done) && ", sshGuestRemoteCommand.getCommand("sudo grep -nri \"The system is finally up\" /var/log/cloud-init.log")) +
                String.format("(while ! %s; do echo 'waiting for dnsmasq service to be ready' && sleep 5; done) && ", sshGuestRemoteCommand.getCommand("systemctl status dnsmasq.service")) +
                String.format("(while ! %s; do echo 'waiting for haproxy service to be ready' && sleep 5; done) && ", sshGuestRemoteCommand.getCommand("systemctl status haproxy.service")) +
                String.format("(while ! %s; do echo 'waiting for podman-registry service to be ready' && sleep 5; done) && ", sshGuestRemoteCommand.getCommand("systemctl status podman-registry.service")) +
                // test dns resolution
                okdNetwork
                        .networkVMs()
                        .stream()
                        .flatMap(networkVM -> networkVM.getDnsNames()
                                .stream()
                                .map(dnsEntryName -> sshGuestRemoteCommand.getCommand(String.format("dig %s +short | grep %s", dnsEntryName, networkVM.getIp()))))
                        .collect(Collectors.joining(" && ")) + " && " +
                // test dns reverse resolution
                okdNetwork
                        .networkVMs()
                        .stream()
                        .flatMap(networkVM -> networkVM.getDnsNames()
                                .stream()
                                .map(dnsEntryName -> sshGuestRemoteCommand.getCommand(String.format("dig -x %s +short | grep %s", networkVM.getIp(), dnsEntryName))))
                        .collect(Collectors.joining(" && ")) + " && " +
                sshGuestRemoteCommand.getCommand(String.format("nslookup -type=srv _etcd-server-ssl._tcp.%s.%s.", okdNetwork.clusterName(), okdNetwork.clusterBaseDomain()))
                // test ign exposed !
        ;
    }

    // https://github.com/openshift/installer/tree/master/docs/dev/libvirt

}
