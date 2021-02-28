package com.damdamdeo.okd_4_local_installation.steps.impl;

import java.util.Objects;

public final class GuestVirtualMachine {

    private final String machineName;
    private final VmType vmType;
    private final String userName;

    public GuestVirtualMachine(final String machineName,
                               final VmType vmType,
                               final String userName) {
        this.machineName = Objects.requireNonNull(machineName);
        if (!this.machineName.matches("^[a-zA-Z0-9\\-]+$")) {
            throw new IllegalStateException(String.format("machine name '%s' is invalid", machineName));
        }
        this.vmType = Objects.requireNonNull(vmType);
        this.userName = Objects.requireNonNull(userName);
        if (!this.userName.matches("^[a-zA-Z0-9\\-]+$")) {
            throw new IllegalStateException(String.format("user name '%s' is invalid", userName));
        }
    }

    public String getMachineName() {
        return machineName;
    }

    public VmType getVmType() {
        return vmType;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GuestVirtualMachine)) return false;
        GuestVirtualMachine that = (GuestVirtualMachine) o;
        return Objects.equals(machineName, that.machineName) &&
                vmType == that.vmType &&
                Objects.equals(userName, that.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(machineName, vmType, userName);
    }

    @Override
    public String toString() {
        return "GuestVirtualMachine{" +
                "machineName='" + machineName + '\'' +
                ", vmType=" + vmType +
                ", userName='" + userName + '\'' +
                '}';
    }
}
