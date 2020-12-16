package com.damdamdeo.okd_4_local_installation.prerequists.impl;

import com.damdamdeo.okd_4_local_installation.prerequists.Prerequisite;

public class XZPrerequisite extends Prerequisite {

    @Override
    public String title() {
        return "Check 'xz' is available";
    }

    @Override
    protected String commandToExecute() {
        return "xz --version";
    }

    @Override
    protected String failingMessage() {
        return "'xz' is not available. Please install it or check why root user cannot use it.";
    }
}
