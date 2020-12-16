package com.damdamdeo.okd_4_local_installation.prerequists.impl;

import com.damdamdeo.okd_4_local_installation.prerequists.Prerequisite;

public class DigPrerequisite extends Prerequisite {

    @Override
    public String title() {
        return "Check 'dig' is available. We will use it to ensure that dns is well configured.";
    }

    @Override
    protected String commandToExecute() {
        return "dig -v";
    }

    @Override
    protected String failingMessage() {
        return "'dig' is mandatory. Please install it.";
    }

}
