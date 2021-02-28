package com.damdamdeo.okd_4_local_installation.steps.impl.host;

import com.damdamdeo.okd_4_local_installation.steps.InstallationStep;
import com.damdamdeo.okd_4_local_installation.steps.impl.BaseInstallationPath;
import com.damdamdeo.okd_4_local_installation.steps.impl.PodmanImage;
import com.damdamdeo.okd_4_local_installation.steps.impl.SshGuestRemoteCommand;

import java.util.Objects;

public class PullOkdPodmanImageToServicesPodmanRegistryGuestVMInstallationStep extends InstallationStep {

    private final PodmanImage podmanImage;
    private final SshGuestRemoteCommand sshGuestRemoteCommand;
    private final NetworkVM serviceNetworkVm;

    public PullOkdPodmanImageToServicesPodmanRegistryGuestVMInstallationStep(final PodmanImage podmanImage,
                                                                             final BaseInstallationPath baseInstallationPath,
                                                                             final String keyName,
                                                                             final NetworkVM serviceNetworkVm) {
        this.podmanImage = Objects.requireNonNull(podmanImage);
        this.sshGuestRemoteCommand = new SshGuestRemoteCommand(baseInstallationPath, keyName, serviceNetworkVm);
        this.serviceNetworkVm = Objects.requireNonNull(serviceNetworkVm);
        if (!VmType.SERVICES.equals(serviceNetworkVm.getGuestVirtualMachine().getVmType())) {
            throw new IllegalStateException();
        }
    }

    @Override
    public String title() {
        return String.format("Pull podman image '%s' to services podman registry", podmanImage.getImage());
    }

    @Override
    public String verifyStepIsAlreadyDoneCommand() {
// https://www.redhat.com/sysadmin/simple-container-registry
//        /var/lib/registry : empty ...
//        curl -k https://localhost:5000/v2/_catalog
//        podman login --cert-dir=/certs
// curl -k https://10.0.5.57:5000/v2/_catalog
// podman login services.sandbox.okd.local:5000 -u -p --cert-dir=/certs --tls-verify=false
//        skopeo copy docker://quay.io/openshift/okd-content@sha256:cddf4d16e947698ef2c2f63674a7e7fe0a9982adb46869764a3cbfe070748eed docker://services.sandbox.okd.local:5000/openshift/okd-content@sha256:cddf4d16e947698ef2c2f63674a7e7fe0a9982adb46869764a3cbfe070748eed --tls-verify=false
//        tester via "curl -k https://10.0.5.57:5000/v2/_catalog" ^^
//        tester /var/lib/registry non vide !!! et cela marche tranquille ;)
        return sshGuestRemoteCommand.getCommand(String.format("curl -k https://services.sandbox.okd.local:5000/v2/%s/manifests/%s | grep -v errors",
                extractImage(podmanImage.getPullSpec()),
                extractShaId(podmanImage.getPullSpec()),
                removeRemoteRepository(podmanImage.getPullSpec())));
    }

    @Override
    public String commandToExecute() {
        return sshGuestRemoteCommand.getCommand(String.format("skopeo copy docker://%s docker://%s:5000/%s --tls-verify=false", podmanImage.getPullSpec(),
                serviceNetworkVm.getFqdn(),
                removeRemoteRepository(podmanImage.getPullSpec())));
    }

    private String removeRemoteRepository(final String pullSpec) {
        return pullSpec.substring(pullSpec.indexOf("/") + 1);
    }

    private String extractImage(final String pullSpec) {
        final String imageWithSha = removeRemoteRepository(pullSpec);
        return imageWithSha.substring(0, imageWithSha.indexOf("@"));
    }

    private String extractShaId(final String pullSpec) {
        return pullSpec.substring(pullSpec.lastIndexOf("@") + 1);
    }

}
