package com.damdamdeo.okd_4_local_installation.prerequists;

import com.damdamdeo.okd_4_local_installation.CommandExecution;
import com.damdamdeo.okd_4_local_installation.CommandExecutionFailedException;

public abstract class Prerequisite {

    public abstract String title();

    protected abstract String commandToExecute();

    protected abstract String failingMessage();

    public void execute() throws CommandExecutionFailedException {
        new CommandExecution().execute(commandToExecute());
    }

}
