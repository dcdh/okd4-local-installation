package com.damdamdeo.okd_4_local_installation.steps.impl.guest;

import com.damdamdeo.okd_4_local_installation.steps.InstallationStep;
import com.damdamdeo.okd_4_local_installation.steps.impl.*;
import com.damdamdeo.okd_4_local_installation.steps.impl.NetworkVM;
import com.damdamdeo.okd_4_local_installation.steps.impl.VmType;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateOkdFCOSGuestVirtualMachineInstallationStep extends InstallationStep {

    // https://docs.fedoraproject.org/en-US/fedora-coreos/static-ip-config/

    private final PebbleEngine engine = new PebbleEngine.Builder().build();

    private final BaseInstallationPath baseInstallationPath;
    private final FedoraCoreOSDisk fedoraCoreOSDisk;
    private final GuestVirtualMachine guestVirtualMachine;
    private final OkdNetwork okdNetwork;
    private final NetworkVM okdNetworkVM;

    public CreateOkdFCOSGuestVirtualMachineInstallationStep(final BaseInstallationPath baseInstallationPath,
                                                            final FedoraCoreOSDisk fedoraCoreOSDisk,
                                                            final GuestVirtualMachine guestVirtualMachine,
                                                            final OkdNetwork okdNetwork) {
        this.baseInstallationPath = Objects.requireNonNull(baseInstallationPath);
        this.fedoraCoreOSDisk = Objects.requireNonNull(fedoraCoreOSDisk);
        this.okdNetwork = Objects.requireNonNull(okdNetwork);
        this.guestVirtualMachine = Objects.requireNonNull(guestVirtualMachine);
        if (!Arrays.asList(VmType.BOOTSTRAP, VmType.MASTER, VmType.WORKER).contains(this.guestVirtualMachine.getVmType())) {
            throw new IllegalStateException(String.format("guestVirtualMachine '%s' must be an okd instance", this.guestVirtualMachine));
        }
        this.okdNetworkVM = okdNetwork.getNetworkVmByGuestVirtualMachine(guestVirtualMachine);
    }

    @Override
    public String title() {
        return String.format("Generate OKD FCOS '%s' guest virtual machine", okdNetworkVM.getFqdn());
    }

    @Override
    public String verifyStepIsAlreadyDoneCommand() {
        return String.format("virsh list --all | grep %s", // check vm exists
                okdNetworkVM.getFqdn());
        // je dois tester le dig ...
    }

    private String generateGuestVirtualMachine() {
        try {
            final PebbleTemplate compiledTemplate = engine.getTemplate("guest_virtual_machine");

            final Map<String, Object> context = new HashMap<>();
            context.put("name", okdNetworkVM.getFqdn());
            context.put("memoryInGB", 16);
            context.put("vcpus", 4);
            context.put("diskFile", String.format("%s/%s/%s", baseInstallationPath.path(), okdNetworkVM.getFqdn(), fedoraCoreOSDisk.fileName()));
            context.put("networkMac", okdNetworkVM.mac());
            context.put("networkInterface", okdNetwork.bridgeName());
            context.put("networkName", okdNetwork.name());
            context.put("fwCfg", String.format("name=opt/com.coreos/config,file=%s/%s.ign", baseInstallationPath.path(), okdNetworkVM.getFqdn()));
            final Writer writer = new StringWriter();
            compiledTemplate.evaluate(writer, context);

            return writer.toString();
        } catch (final IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    @Override
    public String commandToExecute() {
        final String virtualMachine = generateGuestVirtualMachine();
        // putain le machine name devrait être réécri pour être unique ... je devrais prendre le cluster + le nom ...
        writeFile(String.format("%s/%s/vm.xml", baseInstallationPath.path(), okdNetworkVM.getFqdn()), virtualMachine);

        return String.format("chcon -t virt_image_t %s/%s.ign && ", baseInstallationPath.path(), okdNetworkVM.getFqdn()) + // fix selinux which may prevent vm from starting. Have a look here /var/log/message // TODO share
                //     n qemu_file_image (98.8 confidence) suggests   *******************#012#012If bootstrap.sandbox.okd.local.ign is a virtualization target#012Then you need to change the label on bootstrap.sandbox.okd.local.ign'#012Do#012# semanage fcontext -a -t virt_image_t '/opt/okd/bootstrap.sandbox.okd.local.ign'#012# restorecon -v '/opt/okd/bootstrap.sandbox.okd.local.ign'#012#012*****  Plugin catchall (2.13 confidence) suggests   **************************#012#012If you believe that qemu-kvm should be allowed open access on the bootstrap.sandbox.okd.local.ign file by default.#012Then you should report this as a bug.#012You can generate a local policy module to allow this access.#012Do#012allow this access
                String.format("cp -f %s %s/%s/%s && ", fedoraCoreOSDisk.diskFrom(), baseInstallationPath.path(), okdNetworkVM.getFqdn(), fedoraCoreOSDisk.fileName()) +
                String.format("chmod 777 %s/%s/%s && ", baseInstallationPath.path(), okdNetworkVM.getFqdn(), fedoraCoreOSDisk.fileName()) +
                String.format("qemu-img resize %s/%s/%s 25G && ", baseInstallationPath.path(), okdNetworkVM.getFqdn(), fedoraCoreOSDisk.fileName()) +
                String.format("virsh create %s/%s/vm.xml", baseInstallationPath.path(), okdNetworkVM.getFqdn());
    }

    // 1. TESTER IP fixed AVANT !!! avec DNS et tous ce qui va bien !!!

    // cp /tmp/fedora-coreos-32.20201104.3.0-qemu.x86_64.qcow2 /tmp/bootstrap.sandbox.okd.local && virsh create /tmp/bootstrap.sandbox.okd.local/vm.xml
    // ssh -i /tmp/id_okd_vm core@10.0.5.58
    // rm -f /tmp/bootstrap.sandbox.okd.local/fedora-coreos-32.20201104.3.0-qemu.x86_64.qcow2 && virsh destroy bootstrap.sandbox.okd.local && > /root/.ssh/known_hosts

    // je dois tester l'ensemble des dig ...

    // docker run --rm -it busybox mkpasswd --method=yescrypt

}
