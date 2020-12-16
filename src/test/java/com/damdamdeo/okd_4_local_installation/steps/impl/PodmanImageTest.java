package com.damdamdeo.okd_4_local_installation.steps.impl;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PodmanImageTest {

    @Test
    public void should_get_expected_digest() {
        // Given
        final PodmanImage podmanImage = new PodmanImage("aws-ebs-csi-driver", "quay.io/openshift/okd-content@sha256:cddf4d16e947698ef2c2f63674a7e7fe0a9982adb46869764a3cbfe070748eed");

        // When
        final String digest = podmanImage.getDigest();

        // Then
        assertThat(digest).isEqualTo("sha256:cddf4d16e947698ef2c2f63674a7e7fe0a9982adb46869764a3cbfe070748eed");
    }

}
