package com.damdamdeo.okd_4_local_installation.steps.impl;

import java.util.Objects;

public final class PodmanImage {

    private final String image;
    private final String pullSpec;

    public PodmanImage(final String image, final String pullSpec) {
        this.image = Objects.requireNonNull(image);
        this.pullSpec = Objects.requireNonNull(pullSpec);
    }

    public String getImage() {
        return image;
    }

    public String getPullSpec() {
        return pullSpec;
    }

    public String getDigest() {
        return pullSpec.substring(pullSpec.lastIndexOf("sha256:"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PodmanImage)) return false;
        PodmanImage that = (PodmanImage) o;
        return Objects.equals(image, that.image) &&
                Objects.equals(pullSpec, that.pullSpec);
    }

    @Override
    public int hashCode() {
        return Objects.hash(image, pullSpec);
    }

    @Override
    public String toString() {
        return "DockerImage{" +
                "image='" + image + '\'' +
                ", pullSpec='" + pullSpec + '\'' +
                '}';
    }
}
