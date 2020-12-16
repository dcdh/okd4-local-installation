package com.damdamdeo.okd_4_local_installation.steps.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class KvmPackageInstallationStepTest {

    private KvmPackageInstallationStep kvmPackageInstallationStep;

    @BeforeEach
    public void setup() {
        kvmPackageInstallationStep = new KvmPackageInstallationStep();
    }

    @Test
    public void should_verify_step_is_already_done_command_returns_expected_value() {
        // Given

        // When
        final String verifyStepIsAlreadyDoneCommand = kvmPackageInstallationStep.verifyStepIsAlreadyDoneCommand();

        // Then
        assertThat(verifyStepIsAlreadyDoneCommand)
                .isEqualTo("systemctl status libvirtd.service");
    }

    @Test
    public void should_command_to_execute_returns_expected_value() {
        // Given

        // When
        final String commandToExecute = kvmPackageInstallationStep.commandToExecute();

        // Then
        assertThat(commandToExecute)
                .isEqualTo("dnf module install virt -y && dnf install virt-install virt-viewer libguestfs-tools -y");
    }

}
