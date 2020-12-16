package com.damdamdeo.okd_4_local_installation.steps.impl;

import com.damdamdeo.okd_4_local_installation.steps.impl.host.NetworkVM;

import java.util.Objects;

public final class SshGuestRemoteCommand {

    private final NetworkVM networkVm;
    private final BaseInstallationPath baseInstallationPath;
    private final String keyName;

    public SshGuestRemoteCommand(final BaseInstallationPath baseInstallationPath,
                                 final String keyName,
                                 final NetworkVM networkVm) {
        this.baseInstallationPath = Objects.requireNonNull(baseInstallationPath);
        this.keyName = Objects.requireNonNull(keyName);
        this.networkVm = Objects.requireNonNull(networkVm);
    }

    public String getCommand(final String commandToRunInGuest) {
        return String.format("ssh -i %s/%s %s@%s '%s'",
                baseInstallationPath.path(),
                keyName,
                networkVm.getGuestVirtualMachine().getUserName(),
                networkVm.ip(),
                commandToRunInGuest);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SshGuestRemoteCommand)) return false;
        SshGuestRemoteCommand that = (SshGuestRemoteCommand) o;
        return Objects.equals(networkVm, that.networkVm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(networkVm);
    }

    @Override
    public String toString() {
        return "SshGuestRemoteCommand{" +
                "networkVm=" + networkVm +
                '}';
    }
}
