package com.damdamdeo.okd_4_local_installation.steps.impl.host;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DestroyLibvirtDefaultNetworkInstallationStepTest {

    private DestroyLibvirtDefaultNetworkInstallationStep destroyLibvirtDefaultNetworkInstallationStep;

    @BeforeEach
    public void setup() {
        destroyLibvirtDefaultNetworkInstallationStep = new DestroyLibvirtDefaultNetworkInstallationStep();
    }

    @Test
    public void should_verify_step_is_already_done_command_returns_expected_value() {
        // Given

        // When
        final String verifyStepIsAlreadyDoneCommand = destroyLibvirtDefaultNetworkInstallationStep.verifyStepIsAlreadyDoneCommand();

        // Then
        assertThat(verifyStepIsAlreadyDoneCommand)
                .isEqualTo("[ $(virsh net-list | grep default | wc -l) -eq 0 ]");
    }

    @Test
    public void should_command_to_execute_returns_expected_value() {
        // Given

        // When
        final String commandToExecute = destroyLibvirtDefaultNetworkInstallationStep.commandToExecute();

        // Then
        assertThat(commandToExecute)
                .isEqualTo("virsh net-autostart --disable default && virsh net-destroy default");
    }

}
