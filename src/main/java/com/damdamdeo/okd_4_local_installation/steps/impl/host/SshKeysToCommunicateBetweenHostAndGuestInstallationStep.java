package com.damdamdeo.okd_4_local_installation.steps.impl.host;

import com.damdamdeo.okd_4_local_installation.steps.InstallationStep;
import com.damdamdeo.okd_4_local_installation.steps.impl.BaseInstallationPath;

import java.util.Objects;

public class SshKeysToCommunicateBetweenHostAndGuestInstallationStep extends InstallationStep {

    private final BaseInstallationPath baseInstallationPath;
    private final String keyName;

    public SshKeysToCommunicateBetweenHostAndGuestInstallationStep(final BaseInstallationPath baseInstallationPath,
                                                                   final String keyName) {
        this.baseInstallationPath = Objects.requireNonNull(baseInstallationPath);
        this.keyName = Objects.requireNonNull(keyName);
    }

    @Override
    public String title() {
        return "Generate ssh public private keys to communicate between host and guests virtual machines";
    }

    @Override
    public String verifyStepIsAlreadyDoneCommand() {
        return String.format("ls -ltrh %s | grep %s", baseInstallationPath.path(), keyName);
    }

    @Override
    public String commandToExecute() {
        return String.format("ssh-keygen -C \"OKD VM Login ssh key\" -N \"\" -f %s/%s", baseInstallationPath.path(), keyName);
    }

    // est-ce problematique openshift/99_openshift-machineconfig_99-master-ssh.yaml ?
    /*
spec:
  config:
    ignition:
      version: 3.1.0
    passwd:
      users:
      - name: core
        sshAuthorizedKeys:
        - ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABgQD41a83lxG3K9wZdhFeF98taBgVQ0II+LKkgJ+MMnAdhV5SbUHkdUU47BnM2na3ljlJhwPsc8WO+6NlQ/twD7CEPbCLTtDmp860NipDbROmFxtTCSEzaseeI9v6D1/QHsrBNW7RDgk+gmzTHM0GzXMXzh4KGZI90vGkTQYV+W2h7dlACWwncDPvrko9FVX5vye8dyXaJtcefRXbU0qKcomhLPIy18zakD4WYCeA2eJttRItxcV+sOWK3NHUy7dH6TNBI4ectBso4IM2O5r127t0RuF7gZQp0YQkh9hog0EP02WxB0cK4YNvxByh2MwoQxATySnqdpJ/SAICenZk1d6twn7Pr+LdMVnzDqz1cq/p9u+Ai3CDyCSBY+yhrc5D1SfE23Z5r01mfG08RYB/wOX5nlQXknDtKFoNJko994kd3VvgYIYsvLyePBRNEjqjJNJtIynvYZ+02ceZOdi/69UviLcYzjPdRVD96jX0om0WRLAcmh2KZu8V5umsdQ0ZqHM=
          OKD VM Login ssh key
     */

}
