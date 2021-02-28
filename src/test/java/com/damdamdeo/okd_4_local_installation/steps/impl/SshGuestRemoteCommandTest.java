package com.damdamdeo.okd_4_local_installation.steps.impl;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class SshGuestRemoteCommandTest {

    @Test
    public void should_get_command_returns_expected_ssh_command() {
        // Given
        final NetworkVM networkVm = new NetworkVM(new GuestVirtualMachine("services", VmType.SERVICES, "root"), "10.0.5.57", "52:54:10:00:05:57", "services.sandbox.okd.local", Collections.emptyList());
        final SshGuestRemoteCommand sshGuestRemoteCommand = new SshGuestRemoteCommand(
                new BaseInstallationPath("/tmp/okd"),
                "id_okd_vm",
                networkVm);

        // When
        final String command = sshGuestRemoteCommand.getCommand("podman list");

        // Then
        assertThat(command).isEqualTo("ssh -i /tmp/okd/id_okd_vm root@10.0.5.57 'podman list'");
    }

}
