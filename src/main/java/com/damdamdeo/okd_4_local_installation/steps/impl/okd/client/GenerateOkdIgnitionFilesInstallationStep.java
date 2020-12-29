package com.damdamdeo.okd_4_local_installation.steps.impl.okd.client;

import com.damdamdeo.okd_4_local_installation.steps.InstallationStep;
import com.damdamdeo.okd_4_local_installation.steps.impl.BaseInstallationPath;
import com.damdamdeo.okd_4_local_installation.steps.impl.ContainerRegistry;
import com.damdamdeo.okd_4_local_installation.steps.impl.OkdNetwork;
import com.damdamdeo.okd_4_local_installation.steps.impl.SshRsaPublicKey;
import com.damdamdeo.okd_4_local_installation.steps.impl.host.VmType;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class GenerateOkdIgnitionFilesInstallationStep extends InstallationStep {

    private final PebbleEngine engine = new PebbleEngine.Builder().build();

    private final BaseInstallationPath baseInstallationPath;
    private final OkdRemoteInstaller okdRemoteInstaller;
    private final SshRsaPublicKey sshRsaPublicKey;
    private final ContainerRegistry containerRegistry;
    private final OkdNetwork okdNetwork;

    public GenerateOkdIgnitionFilesInstallationStep(final BaseInstallationPath baseInstallationPath,
                                                    final OkdRemoteInstaller okdRemoteInstaller,
                                                    final SshRsaPublicKey sshRsaPublicKey,
                                                    final ContainerRegistry containerRegistry,
                                                    final OkdNetwork okdNetwork) {
        this.baseInstallationPath = Objects.requireNonNull(baseInstallationPath);
        this.okdRemoteInstaller = Objects.requireNonNull(okdRemoteInstaller);
        this.sshRsaPublicKey = Objects.requireNonNull(sshRsaPublicKey);
        this.containerRegistry = Objects.requireNonNull(containerRegistry);
        this.okdNetwork = Objects.requireNonNull(okdNetwork);
    }

    // https://docs.openshift.com/container-platform/4.6/installing/installing_bare_metal/installing-restricted-networks-bare-metal.html#installation-bare-metal-config-yaml_installing-restricted-networks-bare-metal
    // conf for mirror registry is present :)

    private String generateInstallConfigYaml() {
        try {
            final PebbleTemplate compiledTemplate = engine.getTemplate("okd/installConfigYaml");

            final Map<String, Object> context = new HashMap<>();
            context.put("clusterBaseDomain", okdNetwork.clusterBaseDomain());
            context.put("clusterName", okdNetwork.clusterName());
            context.put("nbOfWorkers", okdNetwork.nbOfWorkers());
            context.put("nbOfMasters", okdNetwork.nbOfMasters());
            context.put("sshPublicKey", sshRsaPublicKey.key());
            context.put("domainCrt", containerRegistry.domainCrt());
            context.put("podmanRegistryDnsName", okdNetwork.getServiceNetworkVM().getFqdn());
            final Writer writer = new StringWriter();
            compiledTemplate.evaluate(writer, context);

            return writer.toString();
        } catch (final IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    private String generate20EnablePasswordsConf() {
        try {
            final PebbleTemplate compiledTemplate = engine.getTemplate("okd/20EnablePasswordConf");

            final Writer writer = new StringWriter();
            compiledTemplate.evaluate(writer);

            return writer.toString();
        } catch (final IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    @Override
    public String title() {
        return String.format("Generate Okd install-config.yaml");
    }

    @Override
    public String verifyStepIsAlreadyDoneCommand() {
        return okdNetwork.findNetworkVMsByVmType(VmType.BOOTSTRAP, VmType.MASTER, VmType.WORKER)
                .stream()
                .map(networkVm -> String.format("test -f \"%s/%s.ign\"", baseInstallationPath.path(), networkVm.getFqdn()))
                .collect(Collectors.joining(" && "));
    }

    @Override
    public String commandToExecute() {
        final String installConfigYaml = generateInstallConfigYaml();
        writeFile(String.format("%s/install-config.yaml", baseInstallationPath.path()), installConfigYaml);

        okdNetwork.findNetworkVMsByVmType(VmType.BOOTSTRAP, VmType.MASTER, VmType.WORKER)
                .forEach(networkVm -> {
                    final String generated20enablePasswordsConf = generate20EnablePasswordsConf();
                    writeFile(String.format("%s/%s/etc/ssh/sshd_config.d/20-enable-passwords.conf",
                            baseInstallationPath.path(), networkVm.getFqdn()), generated20enablePasswordsConf, "rw-r--r--");//0644
                });
        return String.format("mkdir -p %s && ", baseInstallationPath.path()) +
                String.format("rm -f %s/.openshift_install.log && ", baseInstallationPath.path()) +
                String.format("rm -f %s/.openshift_install_state.json && ", baseInstallationPath.path()) +
                String.format("cp -f %1$s/install-config.yaml %1$s/install-config.yaml.bak && ", baseInstallationPath.path()) +
                String.format("(if [ ! -f \"%1$s/%2$s\" ]; then curl -L -o %1$s/%2$s %3$s; fi;) && ", baseInstallationPath.path(), okdRemoteInstaller.fileName(), okdRemoteInstaller.remoteInstallerUrl()) +
                String.format("(find %1$s -type f -name \"openshift-install-linux*\" | xargs tar --directory %1$s -zxvf) && ", baseInstallationPath.path()) +
                String.format("%1$s/openshift-install create manifests --dir=%1$s && ", baseInstallationPath.path()) +
                String.format("%1$s/openshift-install create ignition-configs --dir=%1$s && ", baseInstallationPath.path()) +
                "podman pull docker.io/achelouah/filetranspiler:1.1.2 && " +
                okdNetwork.findNetworkVMsByVmType(VmType.BOOTSTRAP, VmType.MASTER, VmType.WORKER)
                        .stream()
                        .map(networkVm -> {
                            final String okdGeneratedIgnition;
                            switch (networkVm.getGuestVirtualMachine().getVmType()) {
                                case BOOTSTRAP:
                                    okdGeneratedIgnition = "bootstrap.ign";
                                    break;
                                case MASTER:
                                    okdGeneratedIgnition = "master.ign";
                                    break;
                                case WORKER:
                                    okdGeneratedIgnition = "worker.ign";
                                    break;
                                default:
                                    throw new IllegalStateException(String.format("Unsupported VmType '%s'", networkVm.getGuestVirtualMachine().getVmType()));
                            }
                            return String.format("podman run --rm -ti --volume %s/:/srv:z docker.io/achelouah/filetranspiler:1.1.2 -i %s -f  %s -o %s.ign",
                                    baseInstallationPath.path(), okdGeneratedIgnition, networkVm.getFqdn(), networkVm.getFqdn());
                        })
                        .collect(Collectors.joining(" && "));
    }

    // ./openshift-install create manifests
    // https://www.openshift.com/blog/advanced-network-customizations-for-openshift-install

    // "podman run --rm -ti --volume /tmp/okd/:/srv:z docker.io/achelouah/filetranspiler:1.1.2 -i bootstrap.ign -f  bootstrap.sandbox.okd.local -o bootstrap.sandbox.okd.local.ign"

}
