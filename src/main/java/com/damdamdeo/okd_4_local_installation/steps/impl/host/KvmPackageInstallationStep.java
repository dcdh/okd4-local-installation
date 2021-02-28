package com.damdamdeo.okd_4_local_installation.steps.impl.host;

import com.damdamdeo.okd_4_local_installation.steps.InstallationStep;

public class KvmPackageInstallationStep extends InstallationStep {

    @Override
    public String title() {
        return "Installation of required package for kvm.";
    }

    @Override
    public String verifyStepIsAlreadyDoneCommand() {
        return "systemctl status libvirtd.service";
    }

    @Override
    public String commandToExecute() {
        return "dnf module install virt -y && dnf install virt-install virt-viewer libguestfs-tools -y";
    }

}
