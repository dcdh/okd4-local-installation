package com.damdamdeo.okd_4_local_installation.steps.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EnableAndStartLibvirtServiceInstallationStepTest {

    private EnableAndStartLibvirtServiceInstallationStep enableAndStartLibvirtServiceInstallationStep;

    @BeforeEach
    public void setup() {
        enableAndStartLibvirtServiceInstallationStep = new EnableAndStartLibvirtServiceInstallationStep();
    }

    @Test
    public void should_verify_step_is_already_done_command_returns_expected_value() {
        // Given

        // When
        final String verifyStepIsAlreadyDoneCommand = enableAndStartLibvirtServiceInstallationStep.verifyStepIsAlreadyDoneCommand();

        // Then
        assertThat(verifyStepIsAlreadyDoneCommand)
                .isEqualTo("systemctl status libvirtd.service");
    }

    @Test
    public void should_command_to_execute_returns_expected_value() {
        // Given

        // When
        final String commandToExecute = enableAndStartLibvirtServiceInstallationStep.commandToExecute();

        // Then
        assertThat(commandToExecute)
                .isEqualTo("systemctl enable libvirtd.service && systemctl start libvirtd.service && systemctl status libvirtd.service");
    }
}
