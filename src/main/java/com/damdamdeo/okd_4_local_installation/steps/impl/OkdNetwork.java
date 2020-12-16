package com.damdamdeo.okd_4_local_installation.steps.impl;

import com.damdamdeo.okd_4_local_installation.steps.impl.host.NetworkVM;
import com.damdamdeo.okd_4_local_installation.steps.impl.host.VmType;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class OkdNetwork {

    private final String name;
    private final String bridgeName;
    private final String gatewayIp;
    private final String broadcastIp;
    private final String clusterBaseDomain;
    private final String clusterName;
    private final List<NetworkVM> networkVMS;

    public OkdNetwork(final String bridgeName,
                      final String gatewayIp,
                      final String broadcastIp,
                      final String clusterBaseDomain,
                      final String clusterName,
                      final List<NetworkVM> networkVMS) {
        this.name = "okd_network";
        this.bridgeName = Objects.requireNonNull(bridgeName);
        if (!bridgeName.matches("^[0-9a-z]{6}$")) {
            throw new IllegalStateException(String.format("bridge name '%s' is invalid", bridgeName));
        }
        this.gatewayIp = Objects.requireNonNull(gatewayIp);// TODO tester value
        this.broadcastIp = Objects.requireNonNull(broadcastIp);// TODO tester value
        this.clusterBaseDomain = Objects.requireNonNull(clusterBaseDomain);// TODO tester value
        this.clusterName = Objects.requireNonNull(clusterName);// TODO tester value
        this.networkVMS = Objects.requireNonNull(networkVMS);// TODO tester contenu
    }

    public String name() {
        return name;
    }

    public String bridgeName() {
        return bridgeName;
    }

    public String gatewayIp() {
        return gatewayIp;
    }

    public String broadcastIp() {
        return broadcastIp;
    }

    public String clusterBaseDomain() {
        return clusterBaseDomain;
    }

    public String clusterName() {
        return clusterName;
    }

    public List<NetworkVM> networkVMs() {
        return networkVMS;
    }

    public List<NetworkVM> findNetworkVMsByVmType(final VmType... filters) {
        Objects.requireNonNull(filters);
        return networkVMS
                .stream()
                .filter(networkVM -> Arrays.asList(filters).contains(networkVM.getGuestVirtualMachine().getVmType()))
                .collect(Collectors.toList());
    }

    public NetworkVM getNetworkVmByGuestVirtualMachine(final GuestVirtualMachine guestVirtualMachine) {
        return networkVMS.stream()
                .filter(networkVM -> networkVM.getGuestVirtualMachine().equals(guestVirtualMachine))
                .findFirst()
                .get();
    }

    public Long nbOfWorkers() {
        return networkVMS.stream()
                .filter(networkVM -> VmType.WORKER.equals(networkVM.getGuestVirtualMachine().getVmType()))
                .count();
    }

    public Long nbOfMasters() {
        return networkVMS.stream()
                .filter(networkVM -> VmType.MASTER.equals(networkVM.getGuestVirtualMachine().getVmType()))
                .count();
    }

    public NetworkVM getServiceNetworkVM() {
        return networkVMS.stream()
                .filter(networkVM -> VmType.SERVICES.equals(networkVM.getGuestVirtualMachine().getVmType()))
                .findFirst()
                .get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OkdNetwork)) return false;
        OkdNetwork that = (OkdNetwork) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(bridgeName, that.bridgeName) &&
                Objects.equals(gatewayIp, that.gatewayIp) &&
                Objects.equals(broadcastIp, that.broadcastIp) &&
                Objects.equals(networkVMS, that.networkVMS);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, bridgeName, gatewayIp, broadcastIp, networkVMS);
    }

    @Override
    public String toString() {
        return "OkdNetwork{" +
                "name='" + name + '\'' +
                ", bridgeName='" + bridgeName + '\'' +
                ", gatewayIp='" + gatewayIp + '\'' +
                ", broadcastIp='" + broadcastIp + '\'' +
                ", networkVMs=" + networkVMS +
                '}';
    }
}
