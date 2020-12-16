package com.damdamdeo.okd_4_local_installation;

import java.util.Objects;

public class CommandExecutionFailedException extends RuntimeException {

    private final Integer exitVal;
    private final String failingCommand;
    private final String executionOutput;

    public CommandExecutionFailedException(final Integer exitVal,
                                           final String failingCommand,
                                           final String executionOutput) {
        this.exitVal = Objects.requireNonNull(exitVal);
        this.failingCommand = Objects.requireNonNull(failingCommand);
        this.executionOutput = Objects.requireNonNull(executionOutput);
    }

    public Integer exitVal() {
        return exitVal;
    }

    public String failingCommand() {
        return failingCommand;
    }

    public String executionOutput() {
        return executionOutput;
    }

    @Override
    public String getMessage() {
        return String.format("Failed to run command '%s'", failingCommand);
    }
}
