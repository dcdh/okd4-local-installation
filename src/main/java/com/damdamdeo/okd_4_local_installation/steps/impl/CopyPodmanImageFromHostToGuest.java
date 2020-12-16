package com.damdamdeo.okd_4_local_installation.steps.impl;

import java.util.Objects;

public class CopyPodmanImageFromHostToGuest {

    // We've got a bootstrap and a master.
    // We do not want to download image twice.
    // Moreover on my testing installation I may do it multiple times and I do not want to download them again and again !
    private final PodmanImage podmanImage;
    private final SshGuestRemoteCommand sshGuestRemoteCommand;

    public CopyPodmanImageFromHostToGuest(final PodmanImage podmanImage, final SshGuestRemoteCommand sshGuestRemoteCommand) {
        this.podmanImage = Objects.requireNonNull(podmanImage);
        this.sshGuestRemoteCommand = Objects.requireNonNull(sshGuestRemoteCommand);
    }

    // TODO podman remote guest ... to check if it as to be copied
    // cf DownloadOkdPodmanImageToHostInstallationStep

    // TODO skopeo !!!

}
