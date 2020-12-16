package com.damdamdeo.okd_4_local_installation.prerequists.impl;

import com.damdamdeo.okd_4_local_installation.prerequists.Prerequisite;

public class CurlPrerequisite extends Prerequisite {

    @Override
    public String title() {
        return "Check 'curl' is available";
    }

    @Override
    protected String commandToExecute() {
        return "curl --version";
    }

    @Override
    protected String failingMessage() {
        return "'curl' is not available. Please install it or you won't be able to download stuffs from internet.";
    }

}

