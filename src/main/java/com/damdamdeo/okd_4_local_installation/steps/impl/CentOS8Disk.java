package com.damdamdeo.okd_4_local_installation.steps.impl;

import java.util.Objects;

public final class CentOS8Disk {

    private final BaseInstallationPath baseInstallationPath;
    private final String fileName;

    public CentOS8Disk(final BaseInstallationPath baseInstallationPath, final String fileName) {
        this.baseInstallationPath = Objects.requireNonNull(baseInstallationPath);
        this.fileName = Objects.requireNonNull(fileName);
        if (!fileName.contains("CentOS-8-GenericCloud")) {
            throw new IllegalStateException(String.format("Only CentOS-8-GenericCloud image is supported '%s'", fileName));
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
        if (!(o instanceof CentOS8Disk)) return false;
        CentOS8Disk that = (CentOS8Disk) o;
        return Objects.equals(baseInstallationPath, that.baseInstallationPath) &&
                Objects.equals(fileName, that.fileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseInstallationPath, fileName);
    }

    @Override
    public String toString() {
        return "CentOS8Disk{" +
                "baseInstallationPath=" + baseInstallationPath +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}
