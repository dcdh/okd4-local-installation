package com.damdamdeo.okd_4_local_installation.steps.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.Scanner;

public final class SshRsaPublicKey {

    private final String path;

    public SshRsaPublicKey(final BaseInstallationPath baseInstallationPath, final String publicKeyName) {
        this.path = baseInstallationPath.path() + "/" + publicKeyName;
        if (!publicKeyName.endsWith(".pub")) {
            throw new IllegalStateException(String.format("sshPublicKey '%s' is invalid. It must be a public ssh key file ending with '.pub'", path));
        }
    }

    public String key() {
        final String key;
        try {
            final String content = new Scanner(new FileInputStream(path)).useDelimiter("\\A").next();
            if (content.endsWith("\n")) {
                key = content.substring(0, content.length() - 1);
            } else {
                key = content;
            }
            if (!key.startsWith("ssh-rsa")) {
                throw new IllegalStateException(String.format("sshPublicKey '%s' is invalid. It must start with 'ssh-rsa'.", path));
            }
        } catch (final FileNotFoundException fileNotFoundException) {
            throw new IllegalStateException(String.format("sshPublicKeyPath '%s' does not exist", path));
        }
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SshRsaPublicKey)) return false;
        SshRsaPublicKey that = (SshRsaPublicKey) o;
        return Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }

    @Override
    public String toString() {
        return "SshRsaPublicKey{" +
                "path='" + path + '\'' +
                '}';
    }
}
