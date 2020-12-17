package com.damdamdeo.okd_4_local_installation.steps.impl.okd.client;

import com.damdamdeo.okd_4_local_installation.steps.impl.*;
import com.damdamdeo.okd_4_local_installation.steps.impl.host.NetworkVM;
import com.damdamdeo.okd_4_local_installation.steps.impl.host.VmType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;

public class GenerateOkdIgnitionFilesInstallationStepTest {

    private GenerateOkdIgnitionFilesInstallationStep generateOkdIgnitionFilesInstallationStep;

    @BeforeEach
    public void setup() {
        final GuestVirtualMachine serviceGuestVirtualMachine = new GuestVirtualMachine("services", VmType.SERVICES, "centos");
        final GuestVirtualMachine bootstrapGuestVirtualMachine = new GuestVirtualMachine("bootstrap", VmType.BOOTSTRAP, "core");
        final GuestVirtualMachine controlPlane0GuestVirtualMachine = new GuestVirtualMachine("control-plane-0", VmType.MASTER, "core");
        // je creer des fichiers avec des contenu attendu ...
        final BaseInstallationPath baseInstallationPath = new BaseInstallationPath("/tmp/okd");
        generateOkdIgnitionFilesInstallationStep = new GenerateOkdIgnitionFilesInstallationStep(
                new BaseInstallationPath("/tmp/okd"),
                new OkdRemoteInstaller("https://github.com/openshift/okd/releases/download/4.6.0-0.okd-2020-11-27-200126/openshift-install-linux-4.6.0-0.okd-2020-11-27-200126.tar.gz"),
                new SshRsaPublicKey(baseInstallationPath, "id_okd_vm.pub"),
                new ContainerRegistry(baseInstallationPath, "domain.crt", "domain.key"),
                new OkdNetwork(
                        "virbr1",
                        "10.0.5.1",
                        "10.0.5.255",
                        "okd.local",
                        "sandbox",
                        new Netmask("255.255.255.0/24"),
                        Arrays.asList(
                                new NetworkVM(serviceGuestVirtualMachine,"10.0.5.57", "52:54:10:00:05:57", "services.sandbox.okd.local", Arrays.asList("okd.local", "lb.sandbox.okd.local","api.sandbox.okd.local","api-int.sandbox.okd.local")),
                                new NetworkVM(bootstrapGuestVirtualMachine,"10.0.5.58", "52:54:10:00:05:58", "bootstrap.sandbox.okd.local", Arrays.asList("bootstrap.sandbox.okd.local")),
                                new NetworkVM(controlPlane0GuestVirtualMachine,"10.0.5.59", "52:54:10:00:05:59", "control-plane-0.sandbox.okd.local", Arrays.asList("control-plane-0.sandbox.okd.local","etcd-0.sandbox.okd.local")))
                )
        );
    }

    @BeforeEach
    public void createFiles() throws Exception {
        Files.copy(getClass().getResourceAsStream("/given/domain.crt"),
                Paths.get("/tmp/okd/domain.crt"),
                new CopyOption[] {StandardCopyOption.REPLACE_EXISTING});
        Files.copy(getClass().getResourceAsStream("/given/domain.key"),
                Paths.get("/tmp/okd/domain.key"),
                new CopyOption[] {StandardCopyOption.REPLACE_EXISTING});
        Files.copy(getClass().getResourceAsStream("/given/id_okd_vm.pub"),
                Paths.get("/tmp/okd/id_okd_vm.pub"),
                new CopyOption[] {StandardCopyOption.REPLACE_EXISTING});
    }

    @BeforeEach
    public void deleteAllFiles() {
        // je supprime mes fichiers
        // humm, je vais quand même les garders. Les fichiers sont dans /tmp...
        Arrays.asList("/tmp/okd/bootstrap.sandbox.okd.local/etc/NetworkManager/system-connections/ens3.nmconnection",
                "/tmp/okd/bootstrap.sandbox.okd.local/etc/ssh/sshd_config.d/20-enable-passwords.conf",
                "/tmp/okd/control-plane-0.sandbox.okd.local/etc/NetworkManager/system-connections/ens3.nmconnection",
                "/tmp/okd/control-plane-0.sandbox.okd.local/etc/ssh/sshd_config.d/20-enable-passwords.conf")
                .forEach(file -> {
                    try {
                        Files.deleteIfExists(Paths.get(file));
                    } catch (final Exception e) {

                    }
                });
    }

    @Test
    public void should_verify_step_is_already_done_command_returns_expected_value() {
        // Given

        // When
        final String verifyStepIsAlreadyDoneCommand = generateOkdIgnitionFilesInstallationStep.verifyStepIsAlreadyDoneCommand();

        // Then
        assertThat(verifyStepIsAlreadyDoneCommand)
                .isEqualTo("test -f \"/tmp/okd/bootstrap.sandbox.okd.local.ign\" && test -f \"/tmp/okd/control-plane-0.sandbox.okd.local.ign\"");
    }

    @Test
    public void should_generate_expected_meta_data_file() throws Exception {
        // Given

        // When
        generateOkdIgnitionFilesInstallationStep.commandToExecute();

        // Then
        final String generatedServicesMetadata = new Scanner(new FileInputStream("/tmp/okd/install-config.yaml")).useDelimiter("\\A").next();
        final String expectedServicesMetadata = new Scanner(getClass().getResourceAsStream("/expected/okd/install-config.yaml")).useDelimiter("\\A").next();

        assertThat(generatedServicesMetadata).isEqualTo(expectedServicesMetadata);
    }

    @Test
    public void should_generate_expected_bootstrap_network_file() throws Exception {
        // Given

        // When
        generateOkdIgnitionFilesInstallationStep.commandToExecute();

        // Then
        final String generatedNetwork = new Scanner(new FileInputStream("/tmp/okd/bootstrap.sandbox.okd.local/etc/NetworkManager/system-connections/ens3.nmconnection")).useDelimiter("\\A").next();
        final String expectedNetwork = new Scanner(getClass().getResourceAsStream("/expected/okd/bootstrap/network")).useDelimiter("\\A").next();

        assertThat(generatedNetwork).isEqualTo(expectedNetwork);
    }

    @Test
    public void should_generate_expected_control_plane_0_network_file() throws Exception {
        // Given

        // When
        generateOkdIgnitionFilesInstallationStep.commandToExecute();

        // Then
        final String generatedNetwork = new Scanner(new FileInputStream("/tmp/okd/control-plane-0.sandbox.okd.local/etc/NetworkManager/system-connections/ens2.nmconnection")).useDelimiter("\\A").next();
        final String expectedNetwork = new Scanner(getClass().getResourceAsStream("/expected/okd/control-plane0/network")).useDelimiter("\\A").next();

        assertThat(generatedNetwork).isEqualTo(expectedNetwork);
    }

    @Test
    public void should_generate_expected_bootstrap_enable_passwords_conf_file() throws Exception {
        // Given

        // When
        generateOkdIgnitionFilesInstallationStep.commandToExecute();

        // Then
        final String generatedNetwork = new Scanner(new FileInputStream("/tmp/okd/bootstrap.sandbox.okd.local/etc/ssh/sshd_config.d/20-enable-passwords.conf")).useDelimiter("\\A").next();
        final String expectedNetwork = new Scanner(getClass().getResourceAsStream("/expected/okd/bootstrap/20-enable-passwords.conf")).useDelimiter("\\A").next();

        assertThat(generatedNetwork).isEqualTo(expectedNetwork);
    }

    @Test
    public void should_generate_expected_control_plane_0_enable_passwords_conf_file() throws Exception {
        // Given

        // When
        generateOkdIgnitionFilesInstallationStep.commandToExecute();

        // Then
        final String generatedNetwork = new Scanner(new FileInputStream("/tmp/okd/control-plane-0.sandbox.okd.local/etc/ssh/sshd_config.d/20-enable-passwords.conf")).useDelimiter("\\A").next();
        final String expectedNetwork = new Scanner(getClass().getResourceAsStream("/expected/okd/control-plane0/20-enable-passwords.conf")).useDelimiter("\\A").next();

        assertThat(generatedNetwork).isEqualTo(expectedNetwork);
    }

    @Test
    public void should_command_to_execute_returns_expected_value() {
        // Given

        // When
        final String commandToExecute = generateOkdIgnitionFilesInstallationStep.commandToExecute();

        // Then
        assertThat(commandToExecute)
                .isEqualTo("mkdir -p /tmp/okd && " +
                        "rm -f /tmp/okd/.openshift_install.log && " +
                        "rm -f /tmp/okd/.openshift_install_state.json && " +
                        "cp -f /tmp/okd/install-config.yaml /tmp/okd/install-config.yaml.bak && " +
                        "(if [ ! -f \"/tmp/okd/openshift-install-linux-4.6.0-0.okd-2020-11-27-200126.tar.gz\" ]; then curl -L -o /tmp/okd/openshift-install-linux-4.6.0-0.okd-2020-11-27-200126.tar.gz https://github.com/openshift/okd/releases/download/4.6.0-0.okd-2020-11-27-200126/openshift-install-linux-4.6.0-0.okd-2020-11-27-200126.tar.gz; fi;) && " +
                        "(find /tmp/okd -type f -name \"openshift-install-linux*\" | xargs tar --directory /tmp/okd -zxvf) && " +
                        "/tmp/okd/openshift-install create manifests --dir=/tmp/okd && " +
                        "/tmp/okd/openshift-install create ignition-configs --dir=/tmp/okd && " +
                        "podman pull docker.io/achelouah/filetranspiler:1.1.2 && " +
                        "podman run --rm -ti --volume /tmp/okd/:/srv:z docker.io/achelouah/filetranspiler:1.1.2 -i bootstrap.ign -f  bootstrap.sandbox.okd.local -o bootstrap.sandbox.okd.local.ign && " +
                        "podman run --rm -ti --volume /tmp/okd/:/srv:z docker.io/achelouah/filetranspiler:1.1.2 -i master.ign -f  control-plane-0.sandbox.okd.local -o control-plane-0.sandbox.okd.local.ign")
        ;
    }

}

