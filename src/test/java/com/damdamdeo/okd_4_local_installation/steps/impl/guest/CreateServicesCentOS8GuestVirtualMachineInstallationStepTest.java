package com.damdamdeo.okd_4_local_installation.steps.impl.guest;

import com.damdamdeo.okd_4_local_installation.steps.impl.*;
import com.damdamdeo.okd_4_local_installation.steps.impl.NetworkVM;
import com.damdamdeo.okd_4_local_installation.steps.impl.VmType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateServicesCentOS8GuestVirtualMachineInstallationStepTest {

    private CreateServicesCentOS8GuestVirtualMachineInstallationStep createServicesCentOS8GuestVirtualMachineInstallationStep;

    // openssl req -newkey rsa:4096 -nodes -sha256 -subj "/C=FR/ST=Paris/L=Paris/O=Organisation/CN=services.sandbox.okd.local" -keyout /tmp/domain.key -x509 -days 36500 -out /tmp/domain.crt
    // cp /root/okd_installation/qemu/fedora-coreos-32.20201104.3.0-qemu.x86_64.qcow2 /tmp
    // ssh-keygen -C "OKD VM Login ssh key" -N "" -f /tmp/id_okd_vm

    // FIXME pour poursuivre je dois d'abord creer le network !!!

    @BeforeEach
    public void setup() {
        final GuestVirtualMachine serviceGuestVirtualMachine = new GuestVirtualMachine("services", VmType.SERVICES, "centos");
        final GuestVirtualMachine bootstrapGuestVirtualMachine = new GuestVirtualMachine("bootstrap", VmType.BOOTSTRAP, "core");
        final GuestVirtualMachine controlPlane0GuestVirtualMachine = new GuestVirtualMachine("control-plane-0", VmType.MASTER, "core");
        // je creer des fichiers avec des contenu attendu ...
        final BaseInstallationPath baseInstallationPath = new BaseInstallationPath("/tmp/okd");
        createServicesCentOS8GuestVirtualMachineInstallationStep = new CreateServicesCentOS8GuestVirtualMachineInstallationStep(
                baseInstallationPath,
                "id_okd_vm",
                new CentOS8Disk(baseInstallationPath, "CentOS-8-GenericCloud-8.2.2004-20200611.2.x86_64.qcow2"),
                new SshRsaPublicKey(baseInstallationPath, "id_okd_vm.pub"),
                new ContainerRegistry(baseInstallationPath,"domain.crt", "domain.key"),
                serviceGuestVirtualMachine,
                new OkdNetwork(
                        "virbr1",
                        "10.0.5.1",
                        "10.0.5.255",
                        "okd.local",
                        "sandbox",
                        new Netmask("255.255.255.0/24"),
                        Arrays.asList(
                                new NetworkVM(serviceGuestVirtualMachine,"10.0.5.57", "52:54:10:00:05:57", "services.sandbox.okd.local", Arrays.asList("okd.local", "lb.sandbox.okd.local","api.sandbox.okd.local","api-int.sandbox.okd.local")),
                                new NetworkVM(bootstrapGuestVirtualMachine,"10.0.5.58", "52:54:10:00:05:58", "bootstrap.sandbox.okd.local", Arrays.asList("bootstrap.sandbox.okd.local")),
                                new NetworkVM(controlPlane0GuestVirtualMachine,"10.0.5.59", "52:54:10:00:05:59", "control-plane-0.sandbox.okd.local", Arrays.asList("control-plane-0.sandbox.okd.local","etcd-0.sandbox.okd.local")))
                )
        );
    }

    @BeforeEach
    public void createFiles() throws Exception {
        Files.copy(getClass().getResourceAsStream("/given/domain.key"),
                Paths.get("/tmp/okd/domain.key"),
                new CopyOption[] {StandardCopyOption.REPLACE_EXISTING});
        Files.copy(getClass().getResourceAsStream("/given/domain.crt"),
                Paths.get("/tmp/okd/domain.crt"),
                new CopyOption[] {StandardCopyOption.REPLACE_EXISTING});
        Files.copy(getClass().getResourceAsStream("/given/id_okd_vm"),
                Paths.get("/tmp/okd/id_okd_vm"),
                new CopyOption[] {StandardCopyOption.REPLACE_EXISTING});
        Files.copy(getClass().getResourceAsStream("/given/id_okd_vm.pub"),
                Paths.get("/tmp/okd/id_okd_vm.pub"),
                new CopyOption[] {StandardCopyOption.REPLACE_EXISTING});
    }

    @AfterEach
    public void deleteAllFiles() {
        // je supprime mes fichiers
        // humm, je vais quand même les garders. Les fichiers sont dans /tmp...
    }

    @Test
    public void should_verify_step_is_already_done_command_returns_expected_value() {
        // Given

        // When
        final String verifyStepIsAlreadyDoneCommand = createServicesCentOS8GuestVirtualMachineInstallationStep.verifyStepIsAlreadyDoneCommand();

        // Then
        assertThat(verifyStepIsAlreadyDoneCommand)
                .isEqualTo("virsh list --all | grep services.sandbox.okd.local");
    }

    @Test
    public void should_generate_expected_meta_data_file() throws Exception {
        // Given

        // When
        createServicesCentOS8GuestVirtualMachineInstallationStep.commandToExecute();

        // Then
        final String generatedServicesMetadata = new Scanner(new FileInputStream("/tmp/okd/services.sandbox.okd.local/meta-data")).useDelimiter("\\A").next();
        final String expectedServicesMetadata = new Scanner(getClass().getResourceAsStream("/expected/services/meta-data")).useDelimiter("\\A").next();

        assertThat(generatedServicesMetadata).isEqualTo(expectedServicesMetadata);
    }

    @Test
    public void should_generate_expected_user_data_file() throws Exception {
        // Given

        // When
        createServicesCentOS8GuestVirtualMachineInstallationStep.commandToExecute();

        // Then
        final String generatedServicesUserdata = new Scanner(new FileInputStream("/tmp/okd/services.sandbox.okd.local/user-data")).useDelimiter("\\A").next();
        final String expectedServicesUserdata = new Scanner(getClass().getResourceAsStream("/expected/services/user-data")).useDelimiter("\\A").next();

        assertThat(generatedServicesUserdata).isEqualTo(expectedServicesUserdata);
    }

    @Test
    public void should_generate_expected_vm() throws Exception {
        // Given

        // When
        createServicesCentOS8GuestVirtualMachineInstallationStep.commandToExecute();

        // Then
        final String generatedServicesVm = new Scanner(new FileInputStream("/tmp/okd/services.sandbox.okd.local/vm.xml")).useDelimiter("\\A").next();
        final String expectedServicesVm = new Scanner(getClass().getResourceAsStream("/expected/services/guest_services_vm.xml")).useDelimiter("\\A").next();

        assertThat(generatedServicesVm).isEqualTo(expectedServicesVm);
    }

    @Test
    public void should_command_to_execute_returns_expected_value() {
        // Given

        // When
        final String commandToExecute = createServicesCentOS8GuestVirtualMachineInstallationStep.commandToExecute();

        // Then
        assertThat(commandToExecute)
                .isEqualTo("mkdir -p ~/.ssh && " +
                        ">> ~/.ssh/known_hosts && " +
                        "sed -i '/^10.0.5.57/d' ~/.ssh/known_hosts && " +
                        "mkdir -p /tmp/okd/services.sandbox.okd.local/ && " +
                        "/bin/cp -rf /tmp/okd/CentOS-8-GenericCloud-8.2.2004-20200611.2.x86_64.qcow2 /tmp/okd/services.sandbox.okd.local/CentOS-8-GenericCloud-8.2.2004-20200611.2.x86_64.qcow2 && " +
                        "genisoimage -output /tmp/okd/services.sandbox.okd.local/boot-init.iso -volid cidata -joliet -r /tmp/okd/services.sandbox.okd.local/user-data /tmp/okd/services.sandbox.okd.local/meta-data && " +
                        "qemu-img resize /tmp/okd/services.sandbox.okd.local/CentOS-8-GenericCloud-8.2.2004-20200611.2.x86_64.qcow2 25G && " +
                        "virsh create /tmp/okd/services.sandbox.okd.local/vm.xml && " +
                        "(while ! nmap -p22 10.0.5.57 -oG - | grep -q 22/open; do echo 'waiting for port 22 to be open' && sleep 5; done) && " +
                        "(while ! ssh-keyscan -t ssh-rsa 10.0.5.57 >> ~/.ssh/known_hosts; do sleep 5; done) && " +
                        "(while ! ssh -i /tmp/okd/id_okd_vm centos@10.0.5.57 'sudo grep -nri \"The system is finally up\" /var/log/cloud-init.log'; do echo 'waiting for services to be ready' && sleep 5; done) && " +
                        "(while ! ssh -i /tmp/okd/id_okd_vm centos@10.0.5.57 'systemctl status dnsmasq.service'; do echo 'waiting for dnsmasq service to be ready' && sleep 5; done) && " +
                        "(while ! ssh -i /tmp/okd/id_okd_vm centos@10.0.5.57 'systemctl status haproxy.service'; do echo 'waiting for haproxy service to be ready' && sleep 5; done) && " +
                        "(while ! ssh -i /tmp/okd/id_okd_vm centos@10.0.5.57 'systemctl status podman-registry.service'; do echo 'waiting for podman-registry service to be ready' && sleep 5; done) && " +
                        "ssh -i /tmp/okd/id_okd_vm centos@10.0.5.57 'dig okd.local +short | grep 10.0.5.57' && " +
                        "ssh -i /tmp/okd/id_okd_vm centos@10.0.5.57 'dig lb.sandbox.okd.local +short | grep 10.0.5.57' && " +
                        "ssh -i /tmp/okd/id_okd_vm centos@10.0.5.57 'dig api.sandbox.okd.local +short | grep 10.0.5.57' && " +
                        "ssh -i /tmp/okd/id_okd_vm centos@10.0.5.57 'dig api-int.sandbox.okd.local +short | grep 10.0.5.57' && " +
                        "ssh -i /tmp/okd/id_okd_vm centos@10.0.5.57 'dig bootstrap.sandbox.okd.local +short | grep 10.0.5.58' && " +
                        "ssh -i /tmp/okd/id_okd_vm centos@10.0.5.57 'dig control-plane-0.sandbox.okd.local +short | grep 10.0.5.59' && " +
                        "ssh -i /tmp/okd/id_okd_vm centos@10.0.5.57 'dig etcd-0.sandbox.okd.local +short | grep 10.0.5.59' && " +
                        "ssh -i /tmp/okd/id_okd_vm centos@10.0.5.57 'dig -x 10.0.5.57 +short | grep okd.local' && " +
                        "ssh -i /tmp/okd/id_okd_vm centos@10.0.5.57 'dig -x 10.0.5.57 +short | grep lb.sandbox.okd.local' && " +
                        "ssh -i /tmp/okd/id_okd_vm centos@10.0.5.57 'dig -x 10.0.5.57 +short | grep api.sandbox.okd.local' && " +
                        "ssh -i /tmp/okd/id_okd_vm centos@10.0.5.57 'dig -x 10.0.5.57 +short | grep api-int.sandbox.okd.local' && " +
                        "ssh -i /tmp/okd/id_okd_vm centos@10.0.5.57 'dig -x 10.0.5.58 +short | grep bootstrap.sandbox.okd.local' && " +
                        "ssh -i /tmp/okd/id_okd_vm centos@10.0.5.57 'dig -x 10.0.5.59 +short | grep control-plane-0.sandbox.okd.local' && " +
                        "ssh -i /tmp/okd/id_okd_vm centos@10.0.5.57 'dig -x 10.0.5.59 +short | grep etcd-0.sandbox.okd.local' && " +
                        "ssh -i /tmp/okd/id_okd_vm centos@10.0.5.57 'nslookup -type=srv _etcd-server-ssl._tcp.sandbox.okd.local.'"
                );
    }

}
