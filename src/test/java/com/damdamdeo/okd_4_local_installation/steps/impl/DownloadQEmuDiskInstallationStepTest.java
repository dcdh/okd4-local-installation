package com.damdamdeo.okd_4_local_installation.steps.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DownloadQEmuDiskInstallationStepTest {

    private DownloadQEmuDiskInstallationStep downloadQEmuDiskInstallationStep;

    @BeforeEach
    public void setup() {
        downloadQEmuDiskInstallationStep = new DownloadQEmuDiskInstallationStep(
                "https://builds.coreos.fedoraproject.org/prod/streams/stable/builds/32.20201104.3.0/x86_64/fedora-coreos-32.20201104.3.0-qemu.x86_64.qcow2.xz",
                new BaseInstallationPath("/tmp/okd"));
    }

    @Test
    public void should_verify_step_is_already_done_command_returns_expected_value() {
        // Given

        // When
        final String verifyStepIsAlreadyDoneCommand = downloadQEmuDiskInstallationStep.verifyStepIsAlreadyDoneCommand();

        // Then
        assertThat(verifyStepIsAlreadyDoneCommand)
                .isEqualTo("test -f \"/tmp/okd/fedora-coreos-32.20201104.3.0-qemu.x86_64.qcow2.xz\"");
    }

    @Test
    public void should_command_to_execute_returns_expected_value() {
        // Given

        // When
        final String commandToExecute = downloadQEmuDiskInstallationStep.commandToExecute();

        // Then
        assertThat(commandToExecute)
                .isEqualTo("mkdir -p /tmp/okd && " +
                        "curl -o /tmp/okd/fedora-coreos-32.20201104.3.0-qemu.x86_64.qcow2.xz https://builds.coreos.fedoraproject.org/prod/streams/stable/builds/32.20201104.3.0/x86_64/fedora-coreos-32.20201104.3.0-qemu.x86_64.qcow2.xz");
    }

}
