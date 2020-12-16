package com.damdamdeo.okd_4_local_installation.steps.impl;

import com.damdamdeo.okd_4_local_installation.steps.InstallationStep;

import java.util.Objects;

public class UnzipQEmuDisksInstallationStep extends InstallationStep {

    private final BaseInstallationPath baseInstallationPath;

    public UnzipQEmuDisksInstallationStep(final BaseInstallationPath baseInstallationPath) {
        this.baseInstallationPath = Objects.requireNonNull(baseInstallationPath);
    }

    @Override
    public String title() {
        return String.format("Unzip all QEmu disks in '%s'", baseInstallationPath.path());
    }

    @Override
    public String verifyStepIsAlreadyDoneCommand() {
        return "find . -name \"*.xz\" | rev | cut -c 4- | rev | xargs test -f";
    }

    @Override
    public String commandToExecute() {
        return String.format("for a in `ls -1 %s/*.xz`; do xz -v -k -f -d $a; done", baseInstallationPath.path());
    }

}
