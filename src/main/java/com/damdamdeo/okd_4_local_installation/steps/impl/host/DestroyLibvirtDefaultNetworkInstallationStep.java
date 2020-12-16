package com.damdamdeo.okd_4_local_installation.steps.impl.host;

import com.damdamdeo.okd_4_local_installation.steps.InstallationStep;

public class DestroyLibvirtDefaultNetworkInstallationStep extends InstallationStep {

    @Override
    public String title() {
        return "Remove default network coming with CentOS 8. Network will be managed by the host using dnsmasq (not the one provided by libvirt)." +
                " No other networks have to be present !";
    }

    @Override
    public String verifyStepIsAlreadyDoneCommand() {
        return "[ $(virsh net-list | grep default | wc -l) -eq 0 ]";
    }

    @Override
    public String commandToExecute() {
        return "virsh net-autostart --disable default && virsh net-destroy default";
    }

//    virsh net-autostart --disable default
//    virsh net-destroy default
//
//    netstat -ltnp | grep 53 | grep dnsmasq
//    virsh net-list | grep default

}
