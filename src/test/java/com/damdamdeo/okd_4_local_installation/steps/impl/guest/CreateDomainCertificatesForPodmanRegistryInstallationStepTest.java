package com.damdamdeo.okd_4_local_installation.steps.impl.guest;

import com.damdamdeo.okd_4_local_installation.steps.impl.BaseInstallationPath;
import com.damdamdeo.okd_4_local_installation.steps.impl.GuestVirtualMachine;
import com.damdamdeo.okd_4_local_installation.steps.impl.NetworkVM;
import com.damdamdeo.okd_4_local_installation.steps.impl.VmType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateDomainCertificatesForPodmanRegistryInstallationStepTest {

    private CreateDomainCertificatesForPodmanRegistryInstallationStep createDomainCertificatesForPodmanRegistryInstallationStep;

    @BeforeEach
    public void setup() {
        final GuestVirtualMachine serviceGuestVirtualMachine = new GuestVirtualMachine("services", VmType.SERVICES, "centos");
        final NetworkVM serviceNetworkVm = new NetworkVM(serviceGuestVirtualMachine,"10.0.5.57", "52:54:10:00:05:57", "services.sandbox.okd.local", Arrays.asList("okd.local", "lb.sandbox.okd.local","api.sandbox.okd.local","api-int.sandbox.okd.local"));
        createDomainCertificatesForPodmanRegistryInstallationStep = new CreateDomainCertificatesForPodmanRegistryInstallationStep(
                new BaseInstallationPath("/tmp/okd"),
                serviceNetworkVm);
    }

    @Test
    public void should_verify_step_is_already_done_command_returns_expected_value() {
        // Given

        // When
        final String verifyStepIsAlreadyDoneCommand = createDomainCertificatesForPodmanRegistryInstallationStep.verifyStepIsAlreadyDoneCommand();

        // Then
        assertThat(verifyStepIsAlreadyDoneCommand)
                .isEqualTo("test -f \"/tmp/okd/domain.key\" && test -f \"/tmp/okd/domain.crt\"");
    }

    @Test
    public void should_command_to_execute_returns_expected_value() {
        // Given

        // When
        final String commandToExecute = createDomainCertificatesForPodmanRegistryInstallationStep.commandToExecute();

        // Then
        assertThat(commandToExecute)
                .isEqualTo("mkdir -p /tmp/okd && openssl req -newkey rsa:4096 -nodes -sha256 -subj \"/C=FR/ST=Paris/L=Paris/O=Organisation/CN=services.sandbox.okd.local\" -keyout /tmp/okd/domain.key -x509 -days 36500 -out /tmp/okd/domain.crt");
    }
}
