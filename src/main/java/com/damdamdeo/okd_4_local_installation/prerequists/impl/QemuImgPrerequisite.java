package com.damdamdeo.okd_4_local_installation.prerequists.impl;

import com.damdamdeo.okd_4_local_installation.prerequists.Prerequisite;

public class QemuImgPrerequisite extends Prerequisite {

    @Override
    public String title() {
        return "Check 'qemu-img' is available";
    }

    @Override
    protected String commandToExecute() {
        return "qemu-img --version";
    }

    @Override
    protected String failingMessage() {
        return "'qemu-img' is not available. Please install it or check why root user cannot use it. It is necessary to resize libvirt image";
    }

}
