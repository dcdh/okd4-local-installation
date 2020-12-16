package com.damdamdeo.okd_4_local_installation.steps.impl.host;

import com.damdamdeo.okd_4_local_installation.steps.impl.BaseInstallationPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SshKeysToCommunicateBetweenHostAndGuestInstallationStepTest {

    private SshKeysToCommunicateBetweenHostAndGuestInstallationStep sshKeysToCommunicateBetweenHostAndGuestInstallationStep;

    @BeforeEach
    public void setup() {
        sshKeysToCommunicateBetweenHostAndGuestInstallationStep = new SshKeysToCommunicateBetweenHostAndGuestInstallationStep(
                new BaseInstallationPath("/tmp/okd"),
                "id_okd_vm");
    }

    @Test
    public void should_verify_step_is_already_done_command_returns_expected_value() {
        // Given

        // When
        final String verifyStepIsAlreadyDoneCommand = sshKeysToCommunicateBetweenHostAndGuestInstallationStep.verifyStepIsAlreadyDoneCommand();

        // Then
        assertThat(verifyStepIsAlreadyDoneCommand)
                .isEqualTo("ls -ltrh /tmp/okd | grep id_okd_vm");
    }

    @Test
    public void should_command_to_execute_returns_expected_value() {
        // Given

        // When
        final String commandToExecute = sshKeysToCommunicateBetweenHostAndGuestInstallationStep.commandToExecute();

        // Then
        assertThat(commandToExecute)
                .isEqualTo("ssh-keygen -C \"OKD VM Login ssh key\" -N \"\" -f /tmp/okd/id_okd_vm");
    }
}
