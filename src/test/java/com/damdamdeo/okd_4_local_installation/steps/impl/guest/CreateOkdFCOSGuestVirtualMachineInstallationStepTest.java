package com.damdamdeo.okd_4_local_installation.steps.impl.guest;

import com.damdamdeo.okd_4_local_installation.steps.impl.*;
import com.damdamdeo.okd_4_local_installation.steps.impl.host.NetworkVM;
import com.damdamdeo.okd_4_local_installation.steps.impl.host.VmType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateOkdFCOSGuestVirtualMachineInstallationStepTest {

    private CreateOkdFCOSGuestVirtualMachineInstallationStep createOkdFCOSGuestVirtualMachineInstallationStep;

    @BeforeAll
    public static void deleteAll() {
        try {
            Files.deleteIfExists(Paths.get("/tmp/okd/bootstrap.sandbox.okd.local/vm.xml"));
        } catch (final IOException ioException) {
            ioException.printStackTrace();
        }
    }

    @BeforeEach
    public void setup() {
        final GuestVirtualMachine serviceGuestVirtualMachine = new GuestVirtualMachine("services", VmType.SERVICES, "centos");
        final GuestVirtualMachine bootstrapGuestVirtualMachine = new GuestVirtualMachine("bootstrap", VmType.BOOTSTRAP, "core");
        final GuestVirtualMachine controlPlane0GuestVirtualMachine = new GuestVirtualMachine("control-plane-0", VmType.MASTER, "core");
        // je creer des fichiers avec des contenu attendu ...
        final BaseInstallationPath baseInstallationPath = new BaseInstallationPath("/tmp/okd");
        createOkdFCOSGuestVirtualMachineInstallationStep = new CreateOkdFCOSGuestVirtualMachineInstallationStep(
                baseInstallationPath,
                new FedoraCoreOSDisk(baseInstallationPath, "fedora-coreos-32.20201104.3.0-qemu.x86_64.qcow2"),
                bootstrapGuestVirtualMachine,
                new OkdNetwork(
                        "virbr1",
                        "10.0.5.1",
                        "10.0.5.255",
                        "okd.local",
                        "sandbox",
                        Arrays.asList(
                                new NetworkVM(serviceGuestVirtualMachine,"10.0.5.57", "52:54:10:00:05:57", "services.sandbox.okd.local", Arrays.asList("okd.local", "lb.sandbox.okd.local","api.sandbox.okd.local","api-int.sandbox.okd.local")),
                                new NetworkVM(bootstrapGuestVirtualMachine,"10.0.5.58", "52:54:10:00:05:58", "bootstrap.sandbox.okd.local", Arrays.asList("bootstrap.sandbox.okd.local")),
                                new NetworkVM(controlPlane0GuestVirtualMachine,"10.0.5.59", "52:54:10:00:05:59", "control-plane-0.sandbox.okd.local", Arrays.asList("control-plane-0.sandbox.okd.local","etcd-0.sandbox.okd.local")))
                )
        );
    }

    @BeforeEach
    public void createFiles() throws Exception {
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
        final String verifyStepIsAlreadyDoneCommand = createOkdFCOSGuestVirtualMachineInstallationStep.verifyStepIsAlreadyDoneCommand();

        // Then
        assertThat(verifyStepIsAlreadyDoneCommand)
                .isEqualTo("virsh list --all | grep bootstrap.sandbox.okd.local");
    }

    @Test
    public void should_generate_expected_vm() throws Exception {
        // Given

        // When
        createOkdFCOSGuestVirtualMachineInstallationStep.commandToExecute();

        // Then
        final String generatedServicesVm = new Scanner(new FileInputStream("/tmp/okd/bootstrap.sandbox.okd.local/vm.xml")).useDelimiter("\\A").next();
        final String expectedServicesVm = new Scanner(getClass().getResourceAsStream("/expected/okd/bootstrap/guest_services_vm.xml")).useDelimiter("\\A").next();

        assertThat(generatedServicesVm).isEqualTo(expectedServicesVm);
    }

    @Test
    public void should_command_to_execute_returns_expected_value() {
        // Given

        // When
        final String commandToExecute = createOkdFCOSGuestVirtualMachineInstallationStep.commandToExecute();

        // Then
        assertThat(commandToExecute)
                .isEqualTo("chcon -t virt_image_t /tmp/okd/bootstrap.sandbox.okd.local.ign && " +
                        "cp -f /tmp/okd/fedora-coreos-32.20201104.3.0-qemu.x86_64.qcow2 /tmp/okd/bootstrap.sandbox.okd.local/fedora-coreos-32.20201104.3.0-qemu.x86_64.qcow2 && " +
                        "virsh create /tmp/okd/bootstrap.sandbox.okd.local/vm.xml");
    }

}
