package com.damdamdeo.okd_4_local_installation.steps.impl;

import java.util.Objects;

public final class BaseInstallationPath {

    private final String path;

    public BaseInstallationPath(final String path) {
        this.path = Objects.requireNonNull(path);
        if (!this.path.startsWith("/")) {
            throw new IllegalStateException(String.format("targetBasePath '%s' must start with '/'", path));
        }
        if (this.path.endsWith("/")) {
            throw new IllegalStateException(String.format("targetBasePath '%s' must not end with '/'", path));
        }
    }

    public String path() {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseInstallationPath)) return false;
        BaseInstallationPath that = (BaseInstallationPath) o;
        return Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }

    @Override
    public String toString() {
        return "BaseInstallationPath{" +
                "path='" + path + '\'' +
                '}';
    }
}
