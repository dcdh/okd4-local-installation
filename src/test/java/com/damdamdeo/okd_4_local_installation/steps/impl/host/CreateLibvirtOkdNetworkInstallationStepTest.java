package com.damdamdeo.okd_4_local_installation.steps.impl.host;

import com.damdamdeo.okd_4_local_installation.steps.impl.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateLibvirtOkdNetworkInstallationStepTest {

    private CreateLibvirtOkdNetworkInstallationStep createLibvirtOkdNetworkInstallationStep;

    @BeforeEach
    public void setup() {
        final GuestVirtualMachine serviceGuestVirtualMachine = new GuestVirtualMachine("services", VmType.SERVICES, "centos");
        final GuestVirtualMachine bootstrapGuestVirtualMachine = new GuestVirtualMachine("bootstrap", VmType.BOOTSTRAP, "core");
        final GuestVirtualMachine controlPlane0GuestVirtualMachine = new GuestVirtualMachine("control-plane-0", VmType.MASTER, "core");
        createLibvirtOkdNetworkInstallationStep = new CreateLibvirtOkdNetworkInstallationStep(
                new BaseInstallationPath("/tmp/okd"),
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
                ));
    }

    @Test
    public void should_verify_step_is_already_done_command_returns_expected_value() {
        // Given

        // When
        final String verifyStepIsAlreadyDoneCommand = createLibvirtOkdNetworkInstallationStep.verifyStepIsAlreadyDoneCommand();

        // Then
        assertThat(verifyStepIsAlreadyDoneCommand)
                .isEqualTo("virsh net-list --all | grep okd_network");
    }

    @Test
    public void should_generate_okd_network_xml() throws Exception {
        // Given

        // When
        createLibvirtOkdNetworkInstallationStep.commandToExecute();

        // Then
        assertThat(Files.exists(Paths.get("/tmp/okd/okd_network.xml"))).isTrue();

        final String generatedOkdNetwork = new Scanner(new FileInputStream("/tmp/okd/okd_network.xml")).useDelimiter("\\A").next();
        final String expectedOkdNetwork = new Scanner(getClass().getResourceAsStream("/expected/okd_network.xml")).useDelimiter("\\A").next();

        assertThat(generatedOkdNetwork)
                .isEqualTo(expectedOkdNetwork);
    }

    @Test
    public void should_command_to_execute_returns_expected_value() {
        // Given

        // When
        final String commandToExecute = createLibvirtOkdNetworkInstallationStep.commandToExecute();

        // Then
        assertThat(commandToExecute)
                .isEqualTo("virsh net-define --file /tmp/okd/okd_network.xml && virsh net-autostart okd_network && virsh net-start okd_network");
    }

}
