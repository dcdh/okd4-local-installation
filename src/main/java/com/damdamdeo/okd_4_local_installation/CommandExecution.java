package com.damdamdeo.okd_4_local_installation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.*;

public class CommandExecution {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandExecution.class);

    private final ExecutorService taskExecutor = Executors.newSingleThreadExecutor();

    public void execute(final String commandToExecute) throws CommandExecutionFailedException {
        final ProcessBuilder processBuilder = new ProcessBuilder();
        LOGGER.info(String.format("Executing command '%s'", commandToExecute));
        processBuilder.command("sh", "-c", commandToExecute);

        try {
            final Process process = processBuilder.start();
            final StringBuilder output = new StringBuilder();

            final BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
            final Future future = taskExecutor.submit(() -> {
                final int exitVal;
                try {
                    exitVal = process.waitFor();
                } catch (final Exception e) {
                    throw new RuntimeException(e);
                }
                if (exitVal == 0) {
                    // command is good
                } else {
                    throw new CommandExecutionFailedException(exitVal, commandToExecute, output.toString());
                }
            });
            try {
                future.get(1, TimeUnit.HOURS);
            } catch (final TimeoutException | InterruptedException e) {
                future.cancel(true);
                LOGGER.error(e.getMessage());
                throw new RuntimeException(e); // TODO exception specific
            } catch (final ExecutionException e) {
                throw (CommandExecutionFailedException) e.getCause();
            } finally {
                taskExecutor.shutdownNow();
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

}
