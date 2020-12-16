package com.damdamdeo.okd_4_local_installation.prerequists.impl;

import com.damdamdeo.okd_4_local_installation.prerequists.Prerequisite;

public class RootPrerequisite extends Prerequisite {

    @Override
    public String title() {
        return "Check user is root";
    }

    @Override
    protected String commandToExecute() {
        return "[ \"$EUID\" -eq 0 ]";
    }

    @Override
    protected String failingMessage() {
        return "The okd 4 local installation must be run as root";
    }

}
