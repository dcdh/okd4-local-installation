package com.damdamdeo.okd_4_local_installation.steps.impl.guest;

import java.util.Objects;

public final class OkdRemoteInstaller {

    private final String remoteInstallerUrl;
    private final String fileName;

    public OkdRemoteInstaller(final String remoteInstallerUrl) {
        Objects.requireNonNull(remoteInstallerUrl);
        if (!remoteInstallerUrl.contains("openshift-install-linux")) {
            throw new IllegalStateException(String.format("The remoteInstallerUrl '%s' is not the openshift installer", remoteInstallerUrl));
        }
        if (!remoteInstallerUrl.endsWith(".tar.gz")) {
            throw new IllegalStateException(String.format("remoteInstallerUrl '%s' must be a 'tar.gz' compressed file", remoteInstallerUrl));
        }
        this.remoteInstallerUrl = remoteInstallerUrl;
        this.fileName = remoteInstallerUrl.substring(remoteInstallerUrl.lastIndexOf("/") + 1);
    }

    public String remoteInstallerUrl() {
        return remoteInstallerUrl;
    }

    public String fileName() {
        return fileName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OkdRemoteInstaller)) return false;
        OkdRemoteInstaller that = (OkdRemoteInstaller) o;
        return Objects.equals(remoteInstallerUrl, that.remoteInstallerUrl) &&
                Objects.equals(fileName, that.fileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(remoteInstallerUrl, fileName);
    }

    @Override
    public String toString() {
        return "OkdRemoteInstaller{" +
                "remoteInstallerUrl='" + remoteInstallerUrl + '\'' +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}
