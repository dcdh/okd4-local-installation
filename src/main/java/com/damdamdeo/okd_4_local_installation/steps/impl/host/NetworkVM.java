package com.damdamdeo.okd_4_local_installation.steps.impl.host;

import com.damdamdeo.okd_4_local_installation.steps.impl.GuestVirtualMachine;

import java.util.List;
import java.util.Objects;

public final class NetworkVM {

    private final GuestVirtualMachine guestVirtualMachine;
    private final String ip;
    private final String mac;
    private final String fqdn;
    private final List<String> dnsNames;

    public NetworkVM(final GuestVirtualMachine guestVirtualMachine,
                     final String ip,
                     final String mac,
                     final String fqdn,
                     final List<String> dnsNames) {
        this.guestVirtualMachine = Objects.requireNonNull(guestVirtualMachine);
        this.ip = Objects.requireNonNull(ip);
        this.mac = Objects.requireNonNull(mac);
        this.fqdn = Objects.requireNonNull(fqdn);
        if (!ip.matches("^[0-9\\.]+$")) {
            throw new IllegalStateException(String.format("ip '%s' is invalid", ip));
        }
        if (!mac.matches("^[0-9]{2}\\:[0-9]{2}\\:[0-9]{2}\\:[0-9]{2}\\:[0-9]{2}\\:[0-9]{2}$")) {
            throw new IllegalStateException(String.format("mac '%s' is invalid", mac));
        }
        if (!fqdn.contains(".")) {
            throw new IllegalStateException(String.format("fqdn '%s' is invalid", fqdn));
        }
        this.dnsNames = Objects.requireNonNull(dnsNames);
    }

    public String getIp() {
        return ip;
    }

    public String getMac() {
        return mac;
    }

    public String getFqdn() {
        return fqdn;
    }

    public GuestVirtualMachine getGuestVirtualMachine() {
        return guestVirtualMachine;
    }

    public List<String> getDnsNames() {
        return dnsNames;
    }

    public String ip() {
        return ip;
    }

    public String mac() {
        return mac;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NetworkVM)) return false;
        NetworkVM networkVM = (NetworkVM) o;
        return Objects.equals(guestVirtualMachine, networkVM.guestVirtualMachine) &&
                Objects.equals(ip, networkVM.ip) &&
                Objects.equals(mac, networkVM.mac) &&
                Objects.equals(fqdn, networkVM.fqdn) &&
                Objects.equals(dnsNames, networkVM.dnsNames);
    }

    @Override
    public int hashCode() {
        return Objects.hash(guestVirtualMachine, ip, mac, fqdn, dnsNames);
    }

    @Override
    public String toString() {
        return "NetworkVM{" +
                "guestVirtualMachine=" + guestVirtualMachine +
                ", ip='" + ip + '\'' +
                ", mac='" + mac + '\'' +
                ", fqdn='" + fqdn + '\'' +
                ", dnsNames=" + dnsNames +
                '}';
    }
}
