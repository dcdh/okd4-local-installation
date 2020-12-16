package com.damdamdeo.okd_4_local_installation.steps;

import com.damdamdeo.okd_4_local_installation.CommandExecution;
import com.damdamdeo.okd_4_local_installation.CommandExecutionFailedException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

public abstract class InstallationStep {

    public abstract String title();

    public abstract String verifyStepIsAlreadyDoneCommand();

    public abstract String commandToExecute();

    public boolean shouldSkipInstallationStep() {
        try {
            new CommandExecution().execute(verifyStepIsAlreadyDoneCommand());
            return true;
        } catch (final CommandExecutionFailedException commandExecutionFailedException) {
            return false;
        }
    }

    public void execute() throws CommandExecutionFailedException {
        new CommandExecution().execute(commandToExecute());
    }

    protected void writeFile(final String filePath,
                             final String content,
                             final String perms) {
        final Set<PosixFilePermission> posixFilePermissions = PosixFilePermissions.fromString(perms);
        final FileAttribute<?> permissions = PosixFilePermissions.asFileAttribute(posixFilePermissions);
        try {
            final Path pathToCreateFile = Paths.get(filePath);
            if (!pathToCreateFile.getParent().toFile().exists()) {
                pathToCreateFile.getParent().toFile().mkdirs();
            }
            if (pathToCreateFile.toFile().exists()) {
                Files.deleteIfExists(pathToCreateFile);
            }
            final Path newTextFile = Files.createFile(pathToCreateFile, permissions);
            try (final FileWriter fw = new FileWriter(newTextFile.toFile())) {
                fw.write(content);
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void writeFile(final String filePath, final String content) {
        final File newTextFile = new File(filePath);
        if (!newTextFile.getParentFile().exists()) {
            newTextFile.getParentFile().mkdirs();
        }
        try (final FileWriter fw = new FileWriter(newTextFile)) {
            fw.write(content);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

}
