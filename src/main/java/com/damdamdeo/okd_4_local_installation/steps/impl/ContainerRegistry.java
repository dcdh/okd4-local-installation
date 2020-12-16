package com.damdamdeo.okd_4_local_installation.steps.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.Scanner;

public final class ContainerRegistry {

    private final String domainCrtPath;
    private final String domainKeyPath;

    public ContainerRegistry(final BaseInstallationPath baseInstallationPath,
                             final String domainCrtName,
                             final String domainKeyName) {
        this.domainCrtPath = baseInstallationPath.path() + "/" + Objects.requireNonNull(domainCrtName);
        this.domainKeyPath = baseInstallationPath.path() + "/" + Objects.requireNonNull(domainKeyName);
    }

    public String domainCrt() {
        try {
            return new Scanner(new FileInputStream(domainCrtPath)).useDelimiter("\\A").next();
        } catch (final FileNotFoundException fileNotFoundException) {
            throw new IllegalStateException(String.format("domainCrt '%s' does not exist", domainCrtPath));
        }
    }

    public String domainKey() {
        try {
            return new Scanner(new FileInputStream(domainKeyPath)).useDelimiter("\\A").next();
        } catch (final FileNotFoundException fileNotFoundException) {
            throw new IllegalStateException(String.format("domainKey '%s' does not exist", domainKeyPath));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContainerRegistry)) return false;
        ContainerRegistry that = (ContainerRegistry) o;
        return Objects.equals(domainCrtPath, that.domainCrtPath) &&
                Objects.equals(domainKeyPath, that.domainKeyPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(domainCrtPath, domainKeyPath);
    }

    @Override
    public String toString() {
        return "ContainerRegistry{" +
                "domainCrtPath='" + domainCrtPath + '\'' +
                ", domainKeyPath='" + domainKeyPath + '\'' +
                '}';
    }
}
