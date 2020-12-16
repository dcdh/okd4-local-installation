package com.damdamdeo.okd_4_local_installation.steps.impl;

import java.util.Objects;

public final class FedoraCoreOSDisk {

    private final BaseInstallationPath baseInstallationPath;
    private final String fileName;

    public FedoraCoreOSDisk(final BaseInstallationPath baseInstallationPath, final String fileName) {
        this.baseInstallationPath = Objects.requireNonNull(baseInstallationPath);
        this.fileName = Objects.requireNonNull(fileName);
        if (!fileName.contains("fedora-coreos")) {
            throw new IllegalStateException(String.format("Only fedora-coreos image is supported '%s'", fileName));
        }
        if (!fileName.endsWith("qcow2")) {
            throw new IllegalStateException(String.format("originalVmDiskPath '%s' must be a 'qcow2' decompressed file", fileName));
        }
    }

    public String diskFrom() {
        return baseInstallationPath.path() + "/" + fileName;
    }

    public String fileName() {
        return fileName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FedoraCoreOSDisk)) return false;
        FedoraCoreOSDisk that = (FedoraCoreOSDisk) o;
        return Objects.equals(baseInstallationPath, that.baseInstallationPath) &&
                Objects.equals(fileName, that.fileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseInstallationPath, fileName);
    }

    @Override
    public String toString() {
        return "FedoraCoreOSDisk{" +
                "baseInstallationPath=" + baseInstallationPath +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}
