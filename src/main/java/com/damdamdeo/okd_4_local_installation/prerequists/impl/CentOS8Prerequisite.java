package com.damdamdeo.okd_4_local_installation.prerequists.impl;

import com.damdamdeo.okd_4_local_installation.prerequists.Prerequisite;

public class CentOS8Prerequisite extends Prerequisite {

    @Override
    public String title() {
        return "Check running in a CentOS 8 Linux instance";
    }

    @Override
    protected String commandToExecute() {
        return "[ \"$(grep CentOS /etc/centos-release | grep \"release 8\" | wc -l)\" -eq 1 ]";
    }

    @Override
    protected String failingMessage() {
        return "Only CentOS version 8 is supported.";
    }

}
