package com.damdamdeo.okd_4_local_installation.steps.impl;

import java.util.Objects;

public final class Netmask {

    private final String ip;
    private final String prefix;

    public Netmask(final String subnetCIDRNotation) {
        Objects.requireNonNull(subnetCIDRNotation);
        if (!subnetCIDRNotation.contains("/")) {
            throw new IllegalStateException(String.format("subnet CIDR Notation '%s' is invalid", subnetCIDRNotation));
        }
        this.ip = subnetCIDRNotation.split("/")[0];
        this.prefix = subnetCIDRNotation.split("/")[1];
    }

    public String getIp() {
        return ip;
    }

    public String getPrefix() {
        return prefix;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Netmask)) return false;
        Netmask netmask = (Netmask) o;
        return Objects.equals(ip, netmask.ip) &&
                Objects.equals(prefix, netmask.prefix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, prefix);
    }

    @Override
    public String toString() {
        return "Netmask{" +
                "ip='" + ip + '\'' +
                ", prefix='" + prefix + '\'' +
                '}';
    }
}
