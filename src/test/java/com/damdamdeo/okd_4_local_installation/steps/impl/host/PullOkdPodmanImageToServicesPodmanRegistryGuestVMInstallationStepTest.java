package com.damdamdeo.okd_4_local_installation.steps.impl.host;

import com.damdamdeo.okd_4_local_installation.steps.impl.BaseInstallationPath;
import com.damdamdeo.okd_4_local_installation.steps.impl.GuestVirtualMachine;
import com.damdamdeo.okd_4_local_installation.steps.impl.PodmanImage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class PullOkdPodmanImageToServicesPodmanRegistryGuestVMInstallationStepTest {

    private PullOkdPodmanImageToServicesPodmanRegistryGuestVMInstallationStep pullOkdPodmanImageToServicesPodmanRegistryGuestVMInstallationStep;

    @BeforeEach
    public void setup() {
        final NetworkVM servicesNetworkVm = new NetworkVM(new GuestVirtualMachine("services", VmType.SERVICES, "centos"), "10.0.5.57", "52:54:10:00:05:57", "services.sandbox.okd.local", Collections.emptyList());
        pullOkdPodmanImageToServicesPodmanRegistryGuestVMInstallationStep = new PullOkdPodmanImageToServicesPodmanRegistryGuestVMInstallationStep(
                new PodmanImage("aws-ebs-csi-driver",
                        "quay.io/openshift/okd-content@sha256:cddf4d16e947698ef2c2f63674a7e7fe0a9982adb46869764a3cbfe070748eed"),
                new BaseInstallationPath("/tmp/okd"),
                "id_okd_vm",
                servicesNetworkVm
        );
    }

    @Test
    public void should_verify_step_is_already_done_command_returns_expected_value() {
        // Given

        // When
        final String verifyStepIsAlreadyDoneCommand = pullOkdPodmanImageToServicesPodmanRegistryGuestVMInstallationStep.verifyStepIsAlreadyDoneCommand();

        // Then
        assertThat(verifyStepIsAlreadyDoneCommand)
                .isEqualTo("ssh -i /tmp/okd/id_okd_vm centos@10.0.5.57 'curl -k https://services.sandbox.okd.local:5000/v2/openshift/okd-content/manifests/sha256:cddf4d16e947698ef2c2f63674a7e7fe0a9982adb46869764a3cbfe070748eed | grep -v errors'");
    }

    @Test
    public void should_command_to_execute_returns_expected_value() {
        // Given

        // When
        final String commandToExecute = pullOkdPodmanImageToServicesPodmanRegistryGuestVMInstallationStep.commandToExecute();

        // Then
        assertThat(commandToExecute)
                .isEqualTo("ssh -i /tmp/okd/id_okd_vm centos@10.0.5.57 'skopeo copy docker://quay.io/openshift/okd-content@sha256:cddf4d16e947698ef2c2f63674a7e7fe0a9982adb46869764a3cbfe070748eed docker://services.sandbox.okd.local:5000/openshift/okd-content@sha256:cddf4d16e947698ef2c2f63674a7e7fe0a9982adb46869764a3cbfe070748eed --tls-verify=false'");
    }

}
