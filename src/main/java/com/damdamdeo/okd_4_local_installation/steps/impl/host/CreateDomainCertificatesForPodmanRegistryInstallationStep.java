package com.damdamdeo.okd_4_local_installation.steps.impl.host;

import com.damdamdeo.okd_4_local_installation.steps.InstallationStep;
import com.damdamdeo.okd_4_local_installation.steps.impl.BaseInstallationPath;
import com.damdamdeo.okd_4_local_installation.steps.impl.NetworkVM;
import com.damdamdeo.okd_4_local_installation.steps.impl.VmType;

import java.util.Objects;

public class CreateDomainCertificatesForPodmanRegistryInstallationStep extends InstallationStep {

    private final BaseInstallationPath baseInstallationPath;
    private final NetworkVM serviceNetworkVm;

    public CreateDomainCertificatesForPodmanRegistryInstallationStep(final BaseInstallationPath baseInstallationPath,
                                                                     final NetworkVM serviceNetworkVm) {
        this.baseInstallationPath = Objects.requireNonNull(baseInstallationPath);
        this.serviceNetworkVm = Objects.requireNonNull(serviceNetworkVm);
        if (!VmType.SERVICES.equals(serviceNetworkVm.getGuestVirtualMachine().getVmType())) {
            throw new IllegalStateException();
        }
    }

    @Override
    public String title() {
        return "Create domain certificates for podman registry";
    }

    @Override
    public String verifyStepIsAlreadyDoneCommand() {
        return String.format("test -f \"%1$s/domain.key\" && test -f \"%1$s/domain.crt\"", baseInstallationPath.path());
    }

    @Override
    public String commandToExecute() {
        return String.format("mkdir -p %2$s && openssl req -newkey rsa:4096 -nodes -sha256 -subj \"/C=FR/ST=Paris/L=Paris/O=Organisation/CN=%1$s\" -keyout %2$s/domain.key -x509 -days 36500 -out %2$s/domain.crt",
                serviceNetworkVm.getFqdn(),
                baseInstallationPath.path());
    }

}
