package com.damdamdeo.okd_4_local_installation.steps.impl.host;

import com.damdamdeo.okd_4_local_installation.steps.impl.BaseInstallationPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UnzipQEmuDisksInstallationStepTest {

    private UnzipQEmuDisksInstallationStep unzipQEmuDisksInstallationStep;

    @BeforeEach
    public void setup() {
        unzipQEmuDisksInstallationStep = new UnzipQEmuDisksInstallationStep(
                new BaseInstallationPath("/tmp/okd"));
    }

    @Test
    public void should_verify_step_is_already_done_command_returns_expected_value() {
        // Given

        // When
        final String verifyStepIsAlreadyDoneCommand = unzipQEmuDisksInstallationStep.verifyStepIsAlreadyDoneCommand();

        // Then
        assertThat(verifyStepIsAlreadyDoneCommand)
                .isEqualTo("find . -name \"*.xz\" | rev | cut -c 4- | rev | xargs test -f");
    }

    @Test
    public void should_command_to_execute_returns_expected_value() {
        // Given

        // When
        final String commandToExecute = unzipQEmuDisksInstallationStep.commandToExecute();

        // Then
        assertThat(commandToExecute)
                .isEqualTo("for a in `ls -1 /tmp/okd/*.xz`; do xz -v -k -f -d $a; done");
    }

}
