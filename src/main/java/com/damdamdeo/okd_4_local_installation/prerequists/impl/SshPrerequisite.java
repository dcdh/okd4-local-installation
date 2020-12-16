package com.damdamdeo.okd_4_local_installation.prerequists.impl;

import com.damdamdeo.okd_4_local_installation.prerequists.Prerequisite;

public class SshPrerequisite extends Prerequisite {

    @Override
    public String title() {
        return "Check 'ssh' is available";
    }

    @Override
    protected String commandToExecute() {
        return "ssh -V";
    }

    @Override
    protected String failingMessage() {
        return "'ssh' is not available. Please install it or check why root user cannot use it.";
    }

}
