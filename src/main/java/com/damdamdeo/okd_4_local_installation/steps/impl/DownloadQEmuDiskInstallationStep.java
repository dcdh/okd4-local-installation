package com.damdamdeo.okd_4_local_installation.steps.impl;

import com.damdamdeo.okd_4_local_installation.steps.InstallationStep;

import java.util.Objects;

public class DownloadQEmuDiskInstallationStep extends InstallationStep {

    private final String remotePath;
    private final BaseInstallationPath baseInstallationPath;
    private final String targetFileName;

    public DownloadQEmuDiskInstallationStep(final String remotePath,
                                            final BaseInstallationPath baseInstallationPath) {
        this.remotePath = Objects.requireNonNull(remotePath);
        this.baseInstallationPath = Objects.requireNonNull(baseInstallationPath);
        this.targetFileName = remotePath.substring(remotePath.lastIndexOf("/") + 1);
    }

    @Override
    public String title() {
        return String.format("Download QEmu disk '%s' into target path '%s' using filename '%s'", remotePath, baseInstallationPath.path(), targetFileName);
    }

    @Override
    public String verifyStepIsAlreadyDoneCommand() {
        return String.format("test -f \"%s/%s\"", baseInstallationPath.path(), targetFileName);
    }

    @Override
    public String commandToExecute() {
        // https://ec.haxx.se/usingcurl/usingcurl-downloads
        // curl -o /tmp/index.html http://example.com/
        return String.format("mkdir -p %s && curl -o %s/%s %s", baseInstallationPath.path(), baseInstallationPath.path(), targetFileName, remotePath);
    }

}
