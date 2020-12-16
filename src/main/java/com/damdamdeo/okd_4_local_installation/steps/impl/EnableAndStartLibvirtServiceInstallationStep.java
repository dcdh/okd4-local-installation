package com.damdamdeo.okd_4_local_installation.steps.impl;

import com.damdamdeo.okd_4_local_installation.steps.InstallationStep;

public class EnableAndStartLibvirtServiceInstallationStep extends InstallationStep {

    @Override
    public String title() {
        return "Enable and start libvirt service";
    }

    @Override
    public String verifyStepIsAlreadyDoneCommand() {
        return "systemctl status libvirtd.service";
    }

    @Override
    public String commandToExecute() {
        return "systemctl enable libvirtd.service && systemctl start libvirtd.service && systemctl status libvirtd.service";
    }

}

