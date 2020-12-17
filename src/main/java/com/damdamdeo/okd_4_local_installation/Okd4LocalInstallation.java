package com.damdamdeo.okd_4_local_installation;

import com.damdamdeo.okd_4_local_installation.prerequists.impl.*;
import com.damdamdeo.okd_4_local_installation.steps.impl.*;
import com.damdamdeo.okd_4_local_installation.steps.impl.guest.CreateDomainCertificatesForPodmanRegistryInstallationStep;
import com.damdamdeo.okd_4_local_installation.steps.impl.guest.CreateOkdFCOSGuestVirtualMachineInstallationStep;
import com.damdamdeo.okd_4_local_installation.steps.impl.guest.CreateServicesCentOS8GuestVirtualMachineInstallationStep;
import com.damdamdeo.okd_4_local_installation.steps.impl.host.*;
import com.damdamdeo.okd_4_local_installation.steps.impl.okd.client.GenerateOkdIgnitionFilesInstallationStep;
import com.damdamdeo.okd_4_local_installation.steps.impl.okd.client.OkdRemoteInstaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Okd4LocalInstallation {

    private static final List<PodmanImage> PODMAN_IMAGES = new ArrayList<>();

    static {
        // 4.6.0-0.okd-2020-12-12-135354: ko some images could not be copied using skopeo ...
//        PODMAN_IMAGES.add(new PodmanImage("aws-ebs-csi-driver", "quay.io/openshift/okd-content@sha256:cddf4d16e947698ef2c2f63674a7e7fe0a9982adb46869764a3cbfe070748eed"));
//        PODMAN_IMAGES.add(new PodmanImage("aws-ebs-csi-driver-operator", "quay.io/openshift/okd-content@sha256:0e01c67ef308041fdc1c2e7095590a2776279b931af0950c74f51aebf980aa46"));
//        PODMAN_IMAGES.add(new PodmanImage("aws-machine-controllers", "quay.io/openshift/okd-content@sha256:4b18aff6f97c576d3ff1eef89c2e7e41dbba7064ef84f50ef6ec20655d2fb56e"));
//        PODMAN_IMAGES.add(new PodmanImage("aws-pod-identity-webhook", "quay.io/openshift/okd-content@sha256:9fad8ebf16d8d32967f3946ae2c4589a7dc19ca788c5b4a657c38b9947377690"));
//        PODMAN_IMAGES.add(new PodmanImage("azure-machine-controllers", "quay.io/openshift/okd-content@sha256:3086eae8cb6adb69bbae3a2b1b7e87bc4b6730d6b177e960b221d2833a41e288"));
//        PODMAN_IMAGES.add(new PodmanImage("baremetal-installer", "quay.io/openshift/okd-content@sha256:aa079839d7bee1f879409a2b8466a0e2f5bf7dd13c95bf39ebd73157006601c1"));
//        PODMAN_IMAGES.add(new PodmanImage("baremetal-machine-controllers", "quay.io/openshift/okd-content@sha256:227fd1bb9185e667de520c3428e07c2a3b19f47a30f3770a06611d4d9d1901a4"));
//        PODMAN_IMAGES.add(new PodmanImage("baremetal-operator", "quay.io/openshift/okd-content@sha256:227fd1bb9185e667de520c3428e07c2a3b19f47a30f3770a06611d4d9d1901a4"));
//        PODMAN_IMAGES.add(new PodmanImage("baremetal-runtimecfg", "quay.io/openshift/okd-content@sha256:78891a856d2826238d6e685b8e7de92006d0c5c57078fca212ebac96eb6da6eb"));
//        PODMAN_IMAGES.add(new PodmanImage("branding", "quay.io/openshift/okd-content@sha256:57a0ec35c89ce06d6495e4c18b2763647b131871f09579787d8146179d77cd8a"));
//        PODMAN_IMAGES.add(new PodmanImage("cli", "quay.io/openshift/okd-content@sha256:87e8b650cee1e2177e09f28102a42dd680f479a78c87fb0dd1d69b1a7f1a667a"));
//        PODMAN_IMAGES.add(new PodmanImage("cli-artifacts", "quay.io/openshift/okd-content@sha256:41795a8a1f1daf0ff6ebfef854ba927fdd0aa12f66d3f934508f9450624e2d30"));
//        PODMAN_IMAGES.add(new PodmanImage("cloud-credential-operator", "quay.io/openshift/okd-content@sha256:c60915306dd6555837ecacee21cddff88ef366116786f7c1274eb31b7b34251f"));
//        PODMAN_IMAGES.add(new PodmanImage("cluster-authentication-operator", "quay.io/openshift/okd-content@sha256:41218e70b1b39d2e08485029ab69eb7ac384896b649782e11622926f93806f33"));
//        PODMAN_IMAGES.add(new PodmanImage("cluster-autoscaler", "quay.io/openshift/okd-content@sha256:eac32c3d308a97e63ae41e6497dd7c8a6a087643e91afd8d70e2883429cfc268"));
//        PODMAN_IMAGES.add(new PodmanImage("cluster-autoscaler-operator", "quay.io/openshift/okd-content@sha256:f4277a15720f122d0cc199fc4889b2b161b39f6ec521a83e456c1728724cd8ec"));
//        PODMAN_IMAGES.add(new PodmanImage("cluster-bootstrap", "quay.io/openshift/okd-content@sha256:d374ab4a9324381f06d10d664c9ce811c7ed2518dbdf1de4216d5270cc921f56"));
//        PODMAN_IMAGES.add(new PodmanImage("cluster-config-operator", "quay.io/openshift/okd-content@sha256:8fe95a0182b99d9082ce53401b6c7c356d6375e49342ca8a48864c49f45412b8"));
//        PODMAN_IMAGES.add(new PodmanImage("cluster-csi-snapshot-controller-operator", "quay.io/openshift/okd-content@sha256:964cbdb397c265ccf4a5846cc6c6a8858a31e020f9eeb452209e0cdc3b250f39"));
//        PODMAN_IMAGES.add(new PodmanImage("cluster-dns-operator", "quay.io/openshift/okd-content@sha256:65b5240385479ec1ba7751191457c27e0ac862ed47dc4558ba12d450a08a80bd"));
//        PODMAN_IMAGES.add(new PodmanImage("cluster-etcd-operator", "quay.io/openshift/okd-content@sha256:8a0ecd6b7c34f44b6153dd7faf4495e5c70bb38d79c2fd7c5859431ed71a189e"));
//        PODMAN_IMAGES.add(new PodmanImage("cluster-image-registry-operator", "quay.io/openshift/okd-content@sha256:3e73807d6b6a3e11769c46c19965c1601822c56fa94295c324ef7acd01b8c4a1"));
//        PODMAN_IMAGES.add(new PodmanImage("cluster-ingress-operator", "quay.io/openshift/okd-content@sha256:c244caa7f7be3d5496e174de150d7123a2138edd54cd045f6a0b5dd7970a4b5a"));
//        PODMAN_IMAGES.add(new PodmanImage("cluster-kube-apiserver-operator", "quay.io/openshift/okd-content@sha256:dbd95d134cb30cc5e111a837ad933b5451f93246003bb499a5eec262cbf7d1fb"));
//        PODMAN_IMAGES.add(new PodmanImage("cluster-kube-controller-manager-operator", "quay.io/openshift/okd-content@sha256:19b4edb7c032b8fa05c2cc5e609b66843e9223cf61ae7371ae9b1e8a10b3c92c"));
//        PODMAN_IMAGES.add(new PodmanImage("cluster-kube-scheduler-operator", "quay.io/openshift/okd-content@sha256:f9b7077592e9dc0b26bc5bc21bf41f7201aeae5651dffd2b92608db7cb7d7406"));
//        PODMAN_IMAGES.add(new PodmanImage("cluster-kube-storage-version-migrator-operator", "quay.io/openshift/okd-content@sha256:b1749d01de5e3edfef9f4cf8a56118800b081d36d0cb00482d20c741f82634a3"));
//        PODMAN_IMAGES.add(new PodmanImage("cluster-machine-approver", "quay.io/openshift/okd-content@sha256:436adffccdccdfc64b74eb5361d66e6fc0aeede3e7a2405e1ebc3d6de3331d23"));
//        PODMAN_IMAGES.add(new PodmanImage("cluster-monitoring-operator", "quay.io/openshift/okd-content@sha256:f1280d6e314d9e48888fd1d9fc622ef158a3abda60e2610f64189fa420092240"));
//        PODMAN_IMAGES.add(new PodmanImage("cluster-network-operator", "quay.io/openshift/okd-content@sha256:e02e269f65905063e9cfde69d638eec2cc175ca541992b1bb611f713ef34371d"));
//        PODMAN_IMAGES.add(new PodmanImage("cluster-node-tuning-operator", "quay.io/openshift/okd-content@sha256:5a66e66d72720bd6434ba69d1da6793dd7ac13ebb41e8abac29ae464358a200c"));
//        PODMAN_IMAGES.add(new PodmanImage("cluster-openshift-apiserver-operator", "quay.io/openshift/okd-content@sha256:d870672cc21d7c0b96a5da2f39ba244200d0496924e2b3d513094c0b2737fc3e"));
//        PODMAN_IMAGES.add(new PodmanImage("cluster-openshift-controller-manager-operator", "quay.io/openshift/okd-content@sha256:fec4346f25d8f5a179650cce4404a7cb0a7db9fbebf56322c61c4462cb4810e7"));
//        PODMAN_IMAGES.add(new PodmanImage("cluster-policy-controller", "quay.io/openshift/okd-content@sha256:80dc4868af84ad42e55ac151c3791b6856df455f578ae919ed5f7a87f330094c"));
//        PODMAN_IMAGES.add(new PodmanImage("cluster-samples-operator", "quay.io/openshift/okd-content@sha256:f46011e584fb2049cb19379bcd238669a30e82883ee20b027af98b93c793557f"));
//        PODMAN_IMAGES.add(new PodmanImage("cluster-storage-operator", "quay.io/openshift/okd-content@sha256:f2c3c6b5d90ab9cbe340feaec00f6bc997e922b41f21465145eee11df81c21bf"));
//        PODMAN_IMAGES.add(new PodmanImage("cluster-svcat-apiserver-operator", "quay.io/openshift/okd-content@sha256:882e5ce3ca1e1e18ffe18a872a8a969317e6ea07b617e22732b40da598c4d71b"));
//        PODMAN_IMAGES.add(new PodmanImage("cluster-svcat-controller-manager-operator", "quay.io/openshift/okd-content@sha256:3ff046a021724da5205c4d97e44acc67ee324593fe1901ff14fcde337da2947d"));
//        PODMAN_IMAGES.add(new PodmanImage("cluster-update-keys", "quay.io/openshift/okd-content@sha256:8c8fe224969fe5e83fd1eabdf616e472e508c7304db87be2e7b519f4d20070fa"));
//        PODMAN_IMAGES.add(new PodmanImage("cluster-version-operator", "quay.io/openshift/okd-content@sha256:432844f70bd042abe517c486408ac75343b2059a39057d014bde925164dcf109"));
//        PODMAN_IMAGES.add(new PodmanImage("configmap-reloader", "quay.io/openshift/okd-content@sha256:1b096b148ab58359c47c4f329709228b42827cb4c5bc0e5d717aab68403ec23b"));
//        PODMAN_IMAGES.add(new PodmanImage("console", "quay.io/openshift/okd-content@sha256:4e4aff7d7a57d3acdc715f542eac206f12b61b16b0d13c6db1578eaacc5d0236"));
//        PODMAN_IMAGES.add(new PodmanImage("console-operator", "quay.io/openshift/okd-content@sha256:45d75bb652e41be00d6e16c0eaa47fa402b6f9db3bffe2d44d41689b6257783a"));
//        PODMAN_IMAGES.add(new PodmanImage("container-networking-plugins", "quay.io/openshift/okd-content@sha256:7378012df67e3e73ee1974ab241eff685eeb50fccd02188c1f1cdba3141eaa93"));
//        PODMAN_IMAGES.add(new PodmanImage("coredns", "quay.io/openshift/okd-content@sha256:e3c4e91204336ba8742e203c3ddf3b9d8b01f516bcca4768cd2f802bc7425ae1"));
//        PODMAN_IMAGES.add(new PodmanImage("csi-driver-manila", "quay.io/openshift/okd-content@sha256:f35c907f95d4abb10d91e36bc13c86927c3d416d8dc26c9de9b4cbc18d51aa27"));
//        PODMAN_IMAGES.add(new PodmanImage("csi-driver-manila-operator", "quay.io/openshift/okd-content@sha256:7c2b635c6da43230244b631071baa087afc20d1a2936a8918786eb21032167f9"));
//        PODMAN_IMAGES.add(new PodmanImage("csi-driver-nfs", "quay.io/openshift/okd-content@sha256:199c70a877e7818982d93cb686820a21bc34970956d58817ad3a350f054ac9ee"));
//        PODMAN_IMAGES.add(new PodmanImage("csi-external-attacher", "quay.io/openshift/okd-content@sha256:6ede91b95b1d5ae513db35a2ca885dab38cc148fb3ad7a43cdd60a08b2937eff"));
//        PODMAN_IMAGES.add(new PodmanImage("csi-external-provisioner", "quay.io/openshift/okd-content@sha256:f0f29e3653f4d87226f8f5436db2081584a8812bbeaf64dff81031f75d3bdb0b"));
//        PODMAN_IMAGES.add(new PodmanImage("csi-external-resizer", "quay.io/openshift/okd-content@sha256:7e11c3f3070db89fb3074977332e6946caccec897480042cb2eb8143ac0956a4"));
//        PODMAN_IMAGES.add(new PodmanImage("csi-external-snapshotter", "quay.io/openshift/okd-content@sha256:b6eef171c760769404f17818a5561e236d0156586c6b162346d80e2ce99674ae"));
//        PODMAN_IMAGES.add(new PodmanImage("csi-livenessprobe", "quay.io/openshift/okd-content@sha256:ac30620a2658ce33cdd39c398a1601b033e863d2d248b0aa1ba55e01a423f2c5"));
//        PODMAN_IMAGES.add(new PodmanImage("csi-node-driver-registrar", "quay.io/openshift/okd-content@sha256:2846c1a36493f43ef30d2452e0cc7d3192599b840a2d9e2d581c8cd616e381c7"));
//        PODMAN_IMAGES.add(new PodmanImage("csi-snapshot-controller", "quay.io/openshift/okd-content@sha256:5911e62ea67bad4cd6f35279e168cd8c1a8f73b98c92e2c631b0dc76036a49f4"));
//        PODMAN_IMAGES.add(new PodmanImage("deployer", "quay.io/openshift/okd-content@sha256:a5bbfad60af08bbeadf25ef8ea68a8d90a35d0401429aaf9d10aead95e86f8a6"));
//        PODMAN_IMAGES.add(new PodmanImage("docker-builder", "quay.io/openshift/okd-content@sha256:32f110adbb397f844bd1761d1e050e9492676df25023a576ad801a75c2fa9205"));
//        PODMAN_IMAGES.add(new PodmanImage("docker-registry", "quay.io/openshift/okd-content@sha256:ec781f86dc3f9ae1179e90f2245a1298e4b3a384a4e261f825d97adb144ef9b7"));
//        PODMAN_IMAGES.add(new PodmanImage("etcd", "quay.io/openshift/okd-content@sha256:e4767f90cd2979e10b2414684debfc53bb9d7e85a22806574f9d6e450db0a3b8"));
//        PODMAN_IMAGES.add(new PodmanImage("gcp-machine-controllers", "quay.io/openshift/okd-content@sha256:3f0f1fec7fbeee5e4cce1434a3d78c640fa4832a32e1a33cf00c80be14e1bbf8"));
//        PODMAN_IMAGES.add(new PodmanImage("grafana", "quay.io/openshift/okd-content@sha256:e2e72305b7138e13849b684c9f3c20432ac56dcbf58701213b9941a1c2870101"));
//        PODMAN_IMAGES.add(new PodmanImage("haproxy-router", "quay.io/openshift/okd-content@sha256:3e539f74e1586f4ac423a753fc95e2a5f15f38581344d0b08aed1c3a26c74115"));
//        PODMAN_IMAGES.add(new PodmanImage("hyperkube", "quay.io/openshift/okd-content@sha256:8e3090ec65c83afd746b86bea803c77ac12a24693f2b0d08eef0b39d44e65553"));
//        PODMAN_IMAGES.add(new PodmanImage("insights-operator", "quay.io/openshift/okd-content@sha256:cc95740064fcf6d38c540dc8e5887cd87014679fcaff6c10238cdaa941f70b88"));
//        PODMAN_IMAGES.add(new PodmanImage("installer", "quay.io/openshift/okd-content@sha256:78d3adbdeb8eab555aeb4dadb87d9b33454f5e04e9f2dfefae67a3bd7eb4b13e"));
//        PODMAN_IMAGES.add(new PodmanImage("installer-artifacts", "quay.io/openshift/okd-content@sha256:5b8f790516f8bbffe2080fea6d98a32478a30eedf1a1f5b43dd8fb87a58586eb"));
//        PODMAN_IMAGES.add(new PodmanImage("ironic", "quay.io/openshift/okd-content@sha256:227fd1bb9185e667de520c3428e07c2a3b19f47a30f3770a06611d4d9d1901a4"));
//        PODMAN_IMAGES.add(new PodmanImage("ironic-hardware-inventory-recorder", "quay.io/openshift/okd-content@sha256:377b334d98a24010ef801ecd551b258b92a7c9007bebf9c44bf224294bbe2194"));
//        PODMAN_IMAGES.add(new PodmanImage("ironic-inspector", "quay.io/openshift/okd-content@sha256:227fd1bb9185e667de520c3428e07c2a3b19f47a30f3770a06611d4d9d1901a4"));
//        PODMAN_IMAGES.add(new PodmanImage("ironic-ipa-downloader", "quay.io/openshift/okd-content@sha256:227fd1bb9185e667de520c3428e07c2a3b19f47a30f3770a06611d4d9d1901a4"));
//        PODMAN_IMAGES.add(new PodmanImage("ironic-machine-os-downloader", "quay.io/openshift/okd-content@sha256:227fd1bb9185e667de520c3428e07c2a3b19f47a30f3770a06611d4d9d1901a4"));
//        PODMAN_IMAGES.add(new PodmanImage("ironic-static-ip-manager", "quay.io/openshift/okd-content@sha256:227fd1bb9185e667de520c3428e07c2a3b19f47a30f3770a06611d4d9d1901a4"));
//        PODMAN_IMAGES.add(new PodmanImage("jenkins", "quay.io/openshift/okd-content@sha256:7ca2220cdf4a7b38f13a4631a03b0ff7d7afca9da64872e18e7ed1667abd2d85"));
//        PODMAN_IMAGES.add(new PodmanImage("jenkins-agent-base", "quay.io/openshift/okd-content@sha256:951141b58a87e10dac578118d13962413afb9b9ecbbb699be2b595d5d4a4a38c"));
//        PODMAN_IMAGES.add(new PodmanImage("jenkins-agent-maven", "quay.io/openshift/okd-content@sha256:37a24cc86a24482de6cd5c5a8ad6723838400681cff744e289c82befbe4e9007"));
//        PODMAN_IMAGES.add(new PodmanImage("jenkins-agent-nodejs", "quay.io/openshift/okd-content@sha256:cb90f70d15982ef19f47ea43457fa78caf556a8b22f66b92b0a150f10d4acfbe"));
//        PODMAN_IMAGES.add(new PodmanImage("k8s-prometheus-adapter", "quay.io/openshift/okd-content@sha256:7119afe433f7bdd1ce6e4b9f89cada6aed762ba820753114135d765e60645d4b"));
//        PODMAN_IMAGES.add(new PodmanImage("keepalived-ipfailover", "quay.io/openshift/okd-content@sha256:e8bb7f7859a3d9678279944de863b303a77079bf2b30b8ce6485e7eafb6ad260"));
//        PODMAN_IMAGES.add(new PodmanImage("kube-proxy", "quay.io/openshift/okd-content@sha256:260955a6372421ff7785126dd59dc540a403dd2a85cf4e2fa799b8d6d54b90dc"));
//        PODMAN_IMAGES.add(new PodmanImage("kube-rbac-proxy", "quay.io/openshift/okd-content@sha256:9efd0551d14278a761a480667bf39da79ba93282509d828efa1ab207ce21033d"));
//        PODMAN_IMAGES.add(new PodmanImage("kube-state-metrics", "quay.io/openshift/okd-content@sha256:8cc28e10e816aa65fe652271cab3f9f4e93d16f9331384a10c1c7490e7518978"));
//        PODMAN_IMAGES.add(new PodmanImage("kube-storage-version-migrator", "quay.io/openshift/okd-content@sha256:ccbfcea0fc67844e748601e885c0f988b86db548da693a833645ff15db87ad5e"));
//        PODMAN_IMAGES.add(new PodmanImage("kuryr-cni", "quay.io/openshift/okd-content@sha256:71545cc590b27d12424b96cd81a71bab4eacd529c5c3a9b5fe930d6936f5fcf1"));
//        PODMAN_IMAGES.add(new PodmanImage("kuryr-controller", "quay.io/openshift/okd-content@sha256:859da5432245d150aaa03e0216e0e7801c2192220beb134c454ebaaadb3fac78"));
//        PODMAN_IMAGES.add(new PodmanImage("libvirt-machine-controllers", "quay.io/openshift/okd-content@sha256:cd4ea1f5873f127f694abcb6dca94c18d0d506843120213c03ca7dcbd1075c9f"));
//        PODMAN_IMAGES.add(new PodmanImage("local-storage-static-provisioner", "quay.io/openshift/okd-content@sha256:8eb465039bd98f4916c17fd583fca710ee2405a6c046e60810ab1cab649c1a53"));
//        PODMAN_IMAGES.add(new PodmanImage("machine-api-operator", "quay.io/openshift/okd-content@sha256:d74217faf391a6f372c09bae818770503e64afd3f0ab395f4fb725a9c5cf8709"));
//        PODMAN_IMAGES.add(new PodmanImage("machine-config-operator", "quay.io/openshift/okd-content@sha256:fbf4e96cf886fd90d589306d6040d882f45b6ccf6375fdb53430b2d4aa24d297"));
//        PODMAN_IMAGES.add(new PodmanImage("machine-os-content", "quay.io/openshift/okd-content@sha256:95034a94e28949af41a53b9efb2fbb0651454a7c37bab002b0646e73c4721829"));
//        PODMAN_IMAGES.add(new PodmanImage("mdns-publisher", "quay.io/openshift/okd-content@sha256:967dbd63416f762cf5680c5aa959f0f30d81fe42b9b8a39a2bd0548f51cb1a52"));
//        PODMAN_IMAGES.add(new PodmanImage("multus-admission-controller", "quay.io/openshift/okd-content@sha256:e6187f501c87f507c4abd920962229121cce0754b516b15eecf7867755c2317f"));
//        PODMAN_IMAGES.add(new PodmanImage("multus-cni", "quay.io/openshift/okd-content@sha256:834300e6eefe21f4d7a681985e8b7077ca45a23ed0fabd0351df793e24db72d7"));
//        PODMAN_IMAGES.add(new PodmanImage("multus-route-override-cni", "quay.io/openshift/okd-content@sha256:d2bf3426e09de63c1a5555f91caa94f8e835e81a09cc69c273b5f3beba253b27"));
//        PODMAN_IMAGES.add(new PodmanImage("multus-whereabouts-ipam-cni", "quay.io/openshift/okd-content@sha256:3a5803e6db603e2160eb5a3b7cde4b3f1a6c73d3a5439180763e6d863a5065c7"));
//        PODMAN_IMAGES.add(new PodmanImage("must-gather", "quay.io/openshift/okd-content@sha256:39ea7401aa6201e5603fbd3f30de01d10751906992e4ab8ffa6a220f8d56cd1f"));
//        PODMAN_IMAGES.add(new PodmanImage("network-metrics-daemon", "quay.io/openshift/okd-content@sha256:163426b566456bbfb29b7dcf626906d2e1151e3fc62220470d00e68e646e06af"));
//        PODMAN_IMAGES.add(new PodmanImage("oauth-apiserver", "quay.io/openshift/okd-content@sha256:605fe555cbf03512b782232e9d726db296c45757f5e12683e69f0435741328eb"));
//        PODMAN_IMAGES.add(new PodmanImage("oauth-proxy", "quay.io/openshift/okd-content@sha256:18f1d4f7681326eaed7e252f8bedcf092e064e2981f4bcb6918bc0ec6c765496"));
//        PODMAN_IMAGES.add(new PodmanImage("oauth-server", "quay.io/openshift/okd-content@sha256:afb3f016c42918ff01f25b5dd33e4b5ba1a1c3ea394510199262872a2b726eeb"));
//        PODMAN_IMAGES.add(new PodmanImage("openshift-apiserver", "quay.io/openshift/okd-content@sha256:ff039607b36f4681cd1a670d279299aa8b456277748fca6ffee7d726fa918573"));
//        PODMAN_IMAGES.add(new PodmanImage("openshift-controller-manager", "quay.io/openshift/okd-content@sha256:810528276d0bb040b16013b9876cc5366466919b21f5776b705b68ceeac91030"));
//        PODMAN_IMAGES.add(new PodmanImage("openshift-state-metrics", "quay.io/openshift/okd-content@sha256:2efb0d9ca67286fd4014d5c3134c71f829b3fa5ee8d7248d1c7fda7299a6330e"));
//        PODMAN_IMAGES.add(new PodmanImage("openstack-machine-controllers", "quay.io/openshift/okd-content@sha256:bd4b1aaf82bd8cfc560a7c8fbe25c60fcea0edffb1cdd6c3d1ab20912dc4b513"));
//        PODMAN_IMAGES.add(new PodmanImage("operator-lifecycle-manager", "quay.io/openshift/okd-content@sha256:f66e09447fa7c1f1457fc56fece1a04feb36416a834b9dc56f0970b2022a78be"));
//        PODMAN_IMAGES.add(new PodmanImage("operator-marketplace", "quay.io/openshift/okd-content@sha256:3c82f26c20e9468eb2e34e08b580588bec36741610ec5be32c7e0d87556a670d"));
//        PODMAN_IMAGES.add(new PodmanImage("operator-registry", "quay.io/openshift/okd-content@sha256:cb6e30d9463590faee202ba960a9993e0c4558a3ae119b1d7c35192abab7c145"));
//        PODMAN_IMAGES.add(new PodmanImage("ovirt-csi-driver", "quay.io/openshift/okd-content@sha256:573381fadaf66f0db3bc80e502cb6ff20f4e1bb5b8c3fe98b2a93089bcef0f9d"));
//        PODMAN_IMAGES.add(new PodmanImage("ovirt-csi-driver-operator", "quay.io/openshift/okd-content@sha256:1fa20143e7407e58c6a2579293b93d8d63287f1a91029a7a302e50d7c14462ed"));
//        PODMAN_IMAGES.add(new PodmanImage("ovirt-machine-controllers", "quay.io/openshift/okd-content@sha256:cbf725763a8a559942fb8015d8dbdc1dd4f4e8422121487fef0e133815e61d8d"));
//        PODMAN_IMAGES.add(new PodmanImage("ovn-kubernetes", "quay.io/openshift/okd-content@sha256:1ab2d34adf498438c6981478c8143299557f658b4a7c3e63f2e91401f721a8cd"));
//        PODMAN_IMAGES.add(new PodmanImage("pod", "quay.io/openshift/okd-content@sha256:da61622fee32eb427cd75591a0f8653c43a3c2dfa3108282d3a5eca357bf6560"));
//        PODMAN_IMAGES.add(new PodmanImage("prom-label-proxy", "quay.io/openshift/okd-content@sha256:db2b20a34792374c011390946900e1cbe5fbeaecb603c3b739b5b8ee8f0e2016"));
//        PODMAN_IMAGES.add(new PodmanImage("prometheus", "quay.io/openshift/okd-content@sha256:5268251ae3e2396a9221658ee2eeb876128a4299d26ac4bcbf939048eff94ebf"));
//        PODMAN_IMAGES.add(new PodmanImage("prometheus-alertmanager", "quay.io/openshift/okd-content@sha256:d9443fde64f52a8d1ac05591586fc92cdf930981e1139e5fd7b871ed4829b75a"));
//        PODMAN_IMAGES.add(new PodmanImage("prometheus-config-reloader", "quay.io/openshift/okd-content@sha256:1dbdf35aeaa1b7af23a9ce55d292d5693557383ad1509ddf6fe2c3350abe2e12"));
//        PODMAN_IMAGES.add(new PodmanImage("prometheus-node-exporter", "quay.io/openshift/okd-content@sha256:8e172c95eed52921566657789684778ac3772fed4a55d2be3635b7157880fc30"));
//        PODMAN_IMAGES.add(new PodmanImage("prometheus-operator", "quay.io/openshift/okd-content@sha256:006fd3a5134f3dc37e0a20741c022c9df9e57522ed847b10ddbd8a81b3009396"));
//        PODMAN_IMAGES.add(new PodmanImage("sdn", "quay.io/openshift/okd-content@sha256:f8e915e393be5e7e37e5db75b6ad19b8c4503ff547946328824e13e64c48b277"));
//        PODMAN_IMAGES.add(new PodmanImage("service-ca-operator", "quay.io/openshift/okd-content@sha256:10a9725fad152151223692d839b8fba5b5d8cf71638dd36637e32f9d7c12d359"));
//        PODMAN_IMAGES.add(new PodmanImage("special-resource-operator", "quay.io/openshift/okd-content@sha256:e6d870f6f2b5b1680353d8336ba61666e1541bdd26ac62ceff7c686bee5efb6f"));
//        PODMAN_IMAGES.add(new PodmanImage("telemeter", "quay.io/openshift/okd-content@sha256:d100e16eda307f05bfe85cbb6707c3b82401ed1b6dd034ea814d0f9e88143167"));
//        PODMAN_IMAGES.add(new PodmanImage("tests", "quay.io/openshift/okd-content@sha256:7ceec69873d9368d983b3b0f31d1d10fe01d8279963fc16548b6598ab3416c79"));
//        PODMAN_IMAGES.add(new PodmanImage("thanos", "quay.io/openshift/okd-content@sha256:91d87cb05ab723d6fc123c2d9f57ceee501e3f66d532e4b2a7f01b1065016d76"));
//        PODMAN_IMAGES.add(new PodmanImage("tools", "quay.io/openshift/okd-content@sha256:2b60f0fb7b8dc24a729a7cfeced7b3a96143382b1430c4b14a120426d4ec0e14"));

        // 4.5.0-0.okd-2020-10-15-235428
        PODMAN_IMAGES.add(new PodmanImage("okd-release", "quay.io/openshift/okd@sha256:67cc7cb47d56237adcf0ecc2ee76446785add5fa236cd08746b55f578b9200a5"));
        PODMAN_IMAGES.add(new PodmanImage("aws-machine-controllers", "quay.io/openshift/okd-content@sha256:63f3207ed300afa108b77037ea180eee0cfea4dfb65d453f0510972f690b0bf0"));
        PODMAN_IMAGES.add(new PodmanImage("aws-pod-identity-webhook", "quay.io/openshift/okd-content@sha256:6eb872d15beba0e99bc461b890f548475e55b90c65479d65fe5c26cba36f0650"));
        PODMAN_IMAGES.add(new PodmanImage("azure-machine-controllers", "quay.io/openshift/okd-content@sha256:708df2dc1097f3471865d6fd57751f328af0ceba970070c4b08e7236febef916"));
        PODMAN_IMAGES.add(new PodmanImage("baremetal-installer", "quay.io/openshift/okd-content@sha256:75586c7f4861a69eac99ebfb7a2a92d3b9b2e78049669d34b6bbccc8ca4794ca"));
        PODMAN_IMAGES.add(new PodmanImage("baremetal-machine-controllers", "quay.io/openshift/okd-content@sha256:227fd1bb9185e667de520c3428e07c2a3b19f47a30f3770a06611d4d9d1901a4"));
        PODMAN_IMAGES.add(new PodmanImage("baremetal-operator", "quay.io/openshift/okd-content@sha256:227fd1bb9185e667de520c3428e07c2a3b19f47a30f3770a06611d4d9d1901a4"));
        PODMAN_IMAGES.add(new PodmanImage("baremetal-runtimecfg", "quay.io/openshift/okd-content@sha256:54ea8cd9dba05afa87faebf283fea8dd64c7678b82840b8b7037cc9bda0acdfd"));
        PODMAN_IMAGES.add(new PodmanImage("branding", "quay.io/openshift/okd-content@sha256:57a0ec35c89ce06d6495e4c18b2763647b131871f09579787d8146179d77cd8a"));
        PODMAN_IMAGES.add(new PodmanImage("cli", "quay.io/openshift/okd-content@sha256:e87ccf31c42554e2b62d5a441a68307043576dfc1c509b8e72fadc815591d3d2"));
        PODMAN_IMAGES.add(new PodmanImage("cli-artifacts", "quay.io/openshift/okd-content@sha256:4a1c32c0f8e69f6f7d08bb08427f4c0dfc90c10263b9aa4659a5b955de82542a"));
        PODMAN_IMAGES.add(new PodmanImage("cloud-credential-operator", "quay.io/openshift/okd-content@sha256:f61778499f38dca0f3ef55ffa2d3ee1af6966758938025d9696b30bdff5428a2"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-authentication-operator", "quay.io/openshift/okd-content@sha256:54ad39d55bd2704d8341ade69c27561ba0921445bef51fd9eb4ae4bdc38bc57d"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-autoscaler", "quay.io/openshift/okd-content@sha256:d5bcc63147b023344279e2a172739b0afc48aad4f0835c1e249cae500d697045"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-autoscaler-operator", "quay.io/openshift/okd-content@sha256:47fad064aa392cafd63abe1ae381d0871bda1bf88dd19941b531402c482f2a21"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-bootstrap", "quay.io/openshift/okd-content@sha256:24cf6c65901fecd258cf2fadac59ba58586c28647622dfe35bdb38a88a72e894"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-config-operator", "quay.io/openshift/okd-content@sha256:03114c178f3758d14a6b654b5204e887c58ed6ab06dba866732efba47ff1f2f0"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-csi-snapshot-controller-operator", "quay.io/openshift/okd-content@sha256:5b00f636ee423af15c834d839cee7c937b939967af3c53531b2ca28ef29664eb"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-dns-operator", "quay.io/openshift/okd-content@sha256:04ed5d9d6fb14c0005c7074b659d6587c117da0e7e3b98f506f6cf3440c45358"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-etcd-operator", "quay.io/openshift/okd-content@sha256:453e9c59dffdde4a3712ca4ec7e004cc210662f447819e49f5abae33e0caa146"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-image-registry-operator", "quay.io/openshift/okd-content@sha256:f4a8abfd8ba50b29292aaaf6c6cfaf8e068678fb269b9eb117ca40f24b8bc1dd"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-ingress-operator", "quay.io/openshift/okd-content@sha256:b89852f7319fa26a70a0c3064402e45bb955ae76d54c835bed09b7ea59e69354"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-kube-apiserver-operator", "quay.io/openshift/okd-content@sha256:412889dd9213d02952ae3992779736e487f4f7867f97befe9403234783e58a3d"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-kube-controller-manager-operator", "quay.io/openshift/okd-content@sha256:193f98f953f37014be8b4b6de371499f555e5bf216483fdc3f2e5ad264b256b9"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-kube-scheduler-operator", "quay.io/openshift/okd-content@sha256:ab2b2547cb585e36f56a16e875a81d42800a0e03ef4ccd659eccd9bea975f299"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-kube-storage-version-migrator-operator", "quay.io/openshift/okd-content@sha256:ea9816e88fa55f623c7ff53192a244389b7e7ac8505e709ba9e59732c9ffd173"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-machine-approver", "quay.io/openshift/okd-content@sha256:2a2da41cd61df987f9d5a79046e673f707d08fab51a8b25edd494be55483f52a"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-monitoring-operator", "quay.io/openshift/okd-content@sha256:59fb32e264a6bb2f5a08a1280e7f1edbef76fd159ba03d61282d1766d43037d2"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-network-operator", "quay.io/openshift/okd-content@sha256:67b9295b85c1fc057d5eaf70c7bb18f8f6c217ff41a3fe96cc95822c45eb2833"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-node-tuned", "quay.io/openshift/okd-content@sha256:233726e2acc6c92f3ace9f8d2cd8e6f41364e765676f18e108141153f76bc5a3"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-node-tuning-operator", "quay.io/openshift/okd-content@sha256:6b4bf0926816c18a607b5c57933c2d3a4af7d2a497a096b67c9fe892d2333be4"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-openshift-apiserver-operator", "quay.io/openshift/okd-content@sha256:3bd1d8b92486354b0cd6165599c154022ca1239812231810db09d8f71a2059e2"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-openshift-controller-manager-operator", "quay.io/openshift/okd-content@sha256:4918d773fa905a88639f44a9e0f72f43334f4369d80a94a08ce0fa5f75aa25e7"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-policy-controller", "quay.io/openshift/okd-content@sha256:c300fa1af453fd829faf0e8bc7d0b7289c350faf87fe830605cac5ff74ce5cb2"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-samples-operator", "quay.io/openshift/okd-content@sha256:d2c22542b9a714b92d3ad1eade502591a085b2e9d3a03d8b0a4b48e12dc3f35e"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-storage-operator", "quay.io/openshift/okd-content@sha256:7d674b92d02a0ce2085d2b84b8930587f32bb483afb338acad018185d28e76f2"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-svcat-apiserver-operator", "quay.io/openshift/okd-content@sha256:cb29c490896e95687c4543839bf83aca07f3dd7508b711db1c49a07cfa6649da"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-svcat-controller-manager-operator", "quay.io/openshift/okd-content@sha256:0081718a95d1380ccc6c2add77f55becfe302c48a65865875e9b812ba484689f"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-update-keys", "quay.io/openshift/okd-content@sha256:850a426d6e9865148218c57a44231a247b833fafbe0b3838004079f0b5ecbc40"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-version-operator", "quay.io/openshift/okd-content@sha256:b807a37a507ad2919b353a7c5d7af353f7bcceddd166cb352531ba6336bfe7ed"));
        PODMAN_IMAGES.add(new PodmanImage("configmap-reloader", "quay.io/openshift/okd-content@sha256:df485a383ff41ef2606041aacb54a89889c1a0b7cc3ca712e938ae67df93bac5"));
        PODMAN_IMAGES.add(new PodmanImage("console", "quay.io/openshift/okd-content@sha256:f4756951cf7f923b049f860310f38b8acd95ad6c6024e41b93f3e5e121b89734"));
        PODMAN_IMAGES.add(new PodmanImage("console-operator", "quay.io/openshift/okd-content@sha256:5c02d821a994f327e861ae29302f226c7da9dd2a92259ae012b58e111db20308"));
        PODMAN_IMAGES.add(new PodmanImage("container-networking-plugins", "quay.io/openshift/okd-content@sha256:d60edbb885cef89750cc20e5b92dab4636b0ad0badd1112927d228a070d1e831"));
        PODMAN_IMAGES.add(new PodmanImage("coredns", "quay.io/openshift/okd-content@sha256:cf916e742d1a01f5632b42e1cdc7d918385eb85408faf32d3c92796f9faa6234"));
        PODMAN_IMAGES.add(new PodmanImage("csi-snapshot-controller", "quay.io/openshift/okd-content@sha256:e69949554a71f58b02984e14eecbad4a79812c7295cac1dda1382751b5bf818f"));
        PODMAN_IMAGES.add(new PodmanImage("deployer", "quay.io/openshift/okd-content@sha256:2e287ffdf74f3d830201f0640e1efd01dc611bfdc3494cfd64f86131bee3873c"));
        PODMAN_IMAGES.add(new PodmanImage("docker-builder", "quay.io/openshift/okd-content@sha256:758196af8b69b6347ff6224bd461d825ab238cf27944b7f178a184c76d8dcabd"));
        PODMAN_IMAGES.add(new PodmanImage("docker-registry", "quay.io/openshift/okd-content@sha256:9b1d20844abc6561d7b9e1e193a7e2e2eb3f27eadcc489a0cf3a074e06cca87d"));
        PODMAN_IMAGES.add(new PodmanImage("etcd", "quay.io/openshift/okd-content@sha256:0b7867ae6684db31e6481a8390a0126c6c961610643b0254761e6f495720dd8c"));
        PODMAN_IMAGES.add(new PodmanImage("gcp-machine-controllers", "quay.io/openshift/okd-content@sha256:8a3d0402a420640a7a2578863b08be452766659debacea4b8f8d4b6bd022573b"));
        PODMAN_IMAGES.add(new PodmanImage("grafana", "quay.io/openshift/okd-content@sha256:fe0e253849c6a771d10ec1f41c2fed0d95180cb77febd56f5d5ee21f639758dc"));
        PODMAN_IMAGES.add(new PodmanImage("haproxy-router", "quay.io/openshift/okd-content@sha256:9c40cb7dc99bcdb4e2aefe46a76da293e7f6a8165e9951fd17ba11791c33cce2"));
        PODMAN_IMAGES.add(new PodmanImage("hyperkube", "quay.io/openshift/okd-content@sha256:c86892d9863a3821f748204006a12a09bf7d0ff54cb545b60c98886bd9ed6afe"));
        PODMAN_IMAGES.add(new PodmanImage("insights-operator", "quay.io/openshift/okd-content@sha256:3eefbceb082c13bc989de422505d4cdae517dd30213acb12069c8fa229f5b016"));
        PODMAN_IMAGES.add(new PodmanImage("installer", "quay.io/openshift/okd-content@sha256:c4d4369e10868dcbf126019761a4e3b8aa2c8eaf64c1581ae9917dd918cece99"));
        PODMAN_IMAGES.add(new PodmanImage("installer-artifacts", "quay.io/openshift/okd-content@sha256:eec5fa6405c8796a45adc55ff23fb13e00de32614d4943bcb82c8bbe3f9f975e"));
        PODMAN_IMAGES.add(new PodmanImage("ironic", "quay.io/openshift/okd-content@sha256:227fd1bb9185e667de520c3428e07c2a3b19f47a30f3770a06611d4d9d1901a4"));
        PODMAN_IMAGES.add(new PodmanImage("ironic-hardware-inventory-recorder", "quay.io/openshift/okd-content@sha256:e8860e1596daa046c36e2f33d95159eca8ad90176c93148b71447a8dcace232a"));
        PODMAN_IMAGES.add(new PodmanImage("ironic-inspector", "quay.io/openshift/okd-content@sha256:227fd1bb9185e667de520c3428e07c2a3b19f47a30f3770a06611d4d9d1901a4"));
        PODMAN_IMAGES.add(new PodmanImage("ironic-ipa-downloader", "quay.io/openshift/okd-content@sha256:227fd1bb9185e667de520c3428e07c2a3b19f47a30f3770a06611d4d9d1901a4"));
        PODMAN_IMAGES.add(new PodmanImage("ironic-machine-os-downloader", "quay.io/openshift/okd-content@sha256:227fd1bb9185e667de520c3428e07c2a3b19f47a30f3770a06611d4d9d1901a4"));
        PODMAN_IMAGES.add(new PodmanImage("ironic-static-ip-manager", "quay.io/openshift/okd-content@sha256:227fd1bb9185e667de520c3428e07c2a3b19f47a30f3770a06611d4d9d1901a4"));
        PODMAN_IMAGES.add(new PodmanImage("jenkins", "quay.io/openshift/okd-content@sha256:4d260147f803ebc165c02da94bf5b175f0c43c4af7ca109b66f838fc19641e12"));
        PODMAN_IMAGES.add(new PodmanImage("jenkins-agent-maven", "quay.io/openshift/okd-content@sha256:0838e6559c74d7aa00a1bb0444fa8f1e107b567e6aa499298117ddc6faf32526"));
        PODMAN_IMAGES.add(new PodmanImage("jenkins-agent-nodejs", "quay.io/openshift/okd-content@sha256:7550b5bb2fa2dc681c142d7e6a6d0b3bbf55a5b15151a667a8ddbbf4acbeef1a"));
        PODMAN_IMAGES.add(new PodmanImage("k8s-prometheus-adapter", "quay.io/openshift/okd-content@sha256:3a917191acac83e540e35f3769c5738a5342ae255374491ffc1e0e239dc6369f"));
        PODMAN_IMAGES.add(new PodmanImage("keepalived-ipfailover", "quay.io/openshift/okd-content@sha256:3d86b36f0fdc20c1feb3cb0187ed6b3469b440bd868c4b2b5245915df2c40808"));
        PODMAN_IMAGES.add(new PodmanImage("kube-client-agent", "quay.io/openshift/okd-content@sha256:b1b293baab5c93d29b41806a6d8b265377402960d454e621931b474396e81dab"));
        PODMAN_IMAGES.add(new PodmanImage("kube-etcd-signer-server", "quay.io/openshift/okd-content@sha256:c15a0286676d0dad695368a9b31bcb895987ad39833c9f9367992b6d37679cec"));
        PODMAN_IMAGES.add(new PodmanImage("kube-proxy", "quay.io/openshift/okd-content@sha256:684d30b1e18c8fd69aba65c89a9771f88d5a0ff362357c0b1a1f2c27394a02dd"));
        PODMAN_IMAGES.add(new PodmanImage("kube-rbac-proxy", "quay.io/openshift/okd-content@sha256:1aa5bb03d0485ec2db2c7871a1eeaef83e9eabf7e9f1bc2c841cf1a759817c99"));
        PODMAN_IMAGES.add(new PodmanImage("kube-state-metrics", "quay.io/openshift/okd-content@sha256:45f9dff539d7a43efd674bd82f95d0515f5d6c26f47eba6250001a40eab40625"));
        PODMAN_IMAGES.add(new PodmanImage("kube-storage-version-migrator", "quay.io/openshift/okd-content@sha256:ead86cb945a675f34fef0fc468a1b76fe2c26302c8fd9f830c00ead7852596c9"));
        PODMAN_IMAGES.add(new PodmanImage("kuryr-cni", "quay.io/openshift/okd-content@sha256:7abdb49022db871dd2c20c7f1b67cb02d670a7b96e825edb692502d5b0ae0f04"));
        PODMAN_IMAGES.add(new PodmanImage("kuryr-controller", "quay.io/openshift/okd-content@sha256:dc569617c327d3c5a629ab3afc1ba9df81957a7e99265f2ce6f3c5ba0a847e57"));
        PODMAN_IMAGES.add(new PodmanImage("libvirt-machine-controllers", "quay.io/openshift/okd-content@sha256:96582a1b2bd3fdc51c45305c05d0c40b2aba3668eba5979acd099c2fac806262"));
        PODMAN_IMAGES.add(new PodmanImage("local-storage-static-provisioner", "quay.io/openshift/okd-content@sha256:610628bd8a6dd843cbf1133301a84b5279321bf022f73a00298559db1d3be523"));
        PODMAN_IMAGES.add(new PodmanImage("machine-api-operator", "quay.io/openshift/okd-content@sha256:7f92398bbffe02646c312a4ba064106a74d04eced27443776350f8650507d54f"));
        PODMAN_IMAGES.add(new PodmanImage("machine-config-operator", "quay.io/openshift/okd-content@sha256:177145c9dfc36daa48ba0f19f176adb049b913bac9314196e353bac193b1e6b4"));
        PODMAN_IMAGES.add(new PodmanImage("machine-os-content", "quay.io/openshift/okd-content@sha256:8cf7e06dd4095f2cd54e13fdb6fd313abbeb6e03d568f17956d97433623093c2"));
        PODMAN_IMAGES.add(new PodmanImage("mdns-publisher", "quay.io/openshift/okd-content@sha256:5e2220a1a9155a18db4bb7edc3b8a7a90a43d123b886a5bd118eefc2f012ef7b"));
        PODMAN_IMAGES.add(new PodmanImage("multus-admission-controller", "quay.io/openshift/okd-content@sha256:0ae31e711c3c76d141c8fd3689d3d711ec3d4dfae17a2fa5e15d4cdfb443372e"));
        PODMAN_IMAGES.add(new PodmanImage("multus-cni", "quay.io/openshift/okd-content@sha256:c51aeeee3afa67a7adf44f6712c730f52beca54680705fc96a5277d02f727eb4"));
        PODMAN_IMAGES.add(new PodmanImage("multus-route-override-cni", "quay.io/openshift/okd-content@sha256:63ede6a22b13b3cb10de00fdc82d46a71894d2bb93fd33cffb4c31b2f2d01b9d"));
        PODMAN_IMAGES.add(new PodmanImage("multus-whereabouts-ipam-cni", "quay.io/openshift/okd-content@sha256:447953a2ae849fa449ec6776771f5b6df054d6ddb5acbcf9a3c2ccc5d8f807d3"));
        PODMAN_IMAGES.add(new PodmanImage("must-gather", "quay.io/openshift/okd-content@sha256:5a9b4b4cdd15e8587d02490cf753c385213e6a97e5c2bf7babf416585e42fd70"));
        PODMAN_IMAGES.add(new PodmanImage("oauth-proxy", "quay.io/openshift/okd-content@sha256:644bcaca0a108801e83f4c3585c267e069bd6d7975c4234cd6498d96693211b0"));
        PODMAN_IMAGES.add(new PodmanImage("oauth-server", "quay.io/openshift/okd-content@sha256:cb424b022c8ea11645227a44968c5d7162718d076c9a630aa7eefae635daba2d"));
        PODMAN_IMAGES.add(new PodmanImage("openshift-apiserver", "quay.io/openshift/okd-content@sha256:d840e0784e0ab6fcf0409fa5bc41b687e62fec423763e140e250739802c34004"));
        PODMAN_IMAGES.add(new PodmanImage("openshift-controller-manager", "quay.io/openshift/okd-content@sha256:631508354e7e51755f301d754ce639655935f0d641d1f6be230ca54a617888b5"));
        PODMAN_IMAGES.add(new PodmanImage("openshift-state-metrics", "quay.io/openshift/okd-content@sha256:0e5e7934cbc243b9d660aeb4d016222fc99acc8c2a5652776e56748fbef8bf57"));
        PODMAN_IMAGES.add(new PodmanImage("openstack-machine-controllers", "quay.io/openshift/okd-content@sha256:2c874cfd510a6adb00f23b92517235b8aa578ab451556e46eda5ae3d54e11582"));
        PODMAN_IMAGES.add(new PodmanImage("operator-lifecycle-manager", "quay.io/openshift/okd-content@sha256:8fed3d869e4ca94c58ea58fa3a8a4367e09906966bfb8393ae2a69e89ec22341"));
        PODMAN_IMAGES.add(new PodmanImage("operator-marketplace", "quay.io/openshift/okd-content@sha256:3a50e6e6073744a48c5b4e029f2466d209ea707ed5b90c93c2eb90ead7c20f3a"));
        PODMAN_IMAGES.add(new PodmanImage("operator-registry", "quay.io/openshift/okd-content@sha256:7c635fbb0779455b3a44ff864ba80c7418f071134700fcebf4aab86811944b5c"));
        PODMAN_IMAGES.add(new PodmanImage("ovirt-machine-controllers", "quay.io/openshift/okd-content@sha256:cd497dcf84c99e8edbd0998f041acfc7ed5210d5694a83b7c95a9e01acf0543c"));
        PODMAN_IMAGES.add(new PodmanImage("ovn-kubernetes", "quay.io/openshift/okd-content@sha256:dcffdf217cf0dad3f5dddebf866a5d04c194e901789f6e98a1c8d8aee34e92c6"));
        PODMAN_IMAGES.add(new PodmanImage("pod", "quay.io/openshift/okd-content@sha256:1207114d4db1bdb9431fdcc890b6813e889fdcfba94900396f0ab9ca4f0c5dbd"));
        PODMAN_IMAGES.add(new PodmanImage("prom-label-proxy", "quay.io/openshift/okd-content@sha256:4e7adadef24e3f570d79abb89a6097cd27ceff4ed03ffe7b9ff27df9c6db0647"));
        PODMAN_IMAGES.add(new PodmanImage("prometheus", "quay.io/openshift/okd-content@sha256:398aa5ba55f0061e8d21219f416a8ea2a403de473d232b0907db7b0f70393189"));
        PODMAN_IMAGES.add(new PodmanImage("prometheus-alertmanager", "quay.io/openshift/okd-content@sha256:40e5d5c8e6d597473f62486babd659bd7d90b56f5591eac14114d6f18ee91454"));
        PODMAN_IMAGES.add(new PodmanImage("prometheus-config-reloader", "quay.io/openshift/okd-content@sha256:7c359dfb80b6ed7aabc61bf675071cdd5280ed23e3032da626b34d89c985b88e"));
        PODMAN_IMAGES.add(new PodmanImage("prometheus-node-exporter", "quay.io/openshift/okd-content@sha256:15afe593031a33abca68bca67dcdf932307f38cfd82f22da81b0654c28b8b8ae"));
        PODMAN_IMAGES.add(new PodmanImage("prometheus-operator", "quay.io/openshift/okd-content@sha256:ce1f7a95e44c64b6941e8239a40021a955dd5df2ffe61e9a4cae9798a94e2383"));
        PODMAN_IMAGES.add(new PodmanImage("sdn", "quay.io/openshift/okd-content@sha256:71dbab00e9803acb3bcf859607d9d3ed445b6f3a063ecedd6b3ea02a7a8fdd80"));
        PODMAN_IMAGES.add(new PodmanImage("service-ca-operator", "quay.io/openshift/okd-content@sha256:d5ab863a154efd4014b0e1d9f753705b97a3f3232bd600c0ed9bde71293c462e"));
        PODMAN_IMAGES.add(new PodmanImage("special-resource-operator", "quay.io/openshift/okd-content@sha256:6aa87a65480c15c2a4269e9921ea68b70f66129671a71bb2754ca25dabb8285f"));
        PODMAN_IMAGES.add(new PodmanImage("telemeter", "quay.io/openshift/okd-content@sha256:b248d9ed840885394583a497d071873f9931e93e187a7fc3cf4d97fa0e17f991"));
        PODMAN_IMAGES.add(new PodmanImage("tests", "quay.io/openshift/okd-content@sha256:ecd1f549d0a8534b00da98abb3d61674c467f670602f4eddcb3f48cd934256f6"));
        PODMAN_IMAGES.add(new PodmanImage("thanos", "quay.io/openshift/okd-content@sha256:048ef6d2ea93c4a890c270b9b8623df624b7199c6ede15c7de78e0213caf9f00"));
        PODMAN_IMAGES.add(new PodmanImage("tools", "quay.io/openshift/okd-content@sha256:bc554eda96eb214b7ca62634a6e5988de27fc7cc929eb3c5d995937416195dc9"));
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Okd4LocalInstallation.class);

    private static final String QEMU_REMOTE_FEDORA_CORE_OS = "https://builds.coreos.fedoraproject.org/prod/streams/stable/builds/32.20201104.3.0/x86_64/fedora-coreos-32.20201104.3.0-qemu.x86_64.qcow2.xz";

    private static final String QEMU_REMOTE_CENTOS8 = "https://cloud.centos.org/centos/8/x86_64/images/CentOS-8-GenericCloud-8.2.2004-20200611.2.x86_64.qcow2";

    private static final String REMOTE_OKD_INSTALLER = "https://github.com/openshift/okd/releases/download/4.5.0-0.okd-2020-10-15-235428/openshift-install-linux-4.5.0-0.okd-2020-10-15-235428.tar.gz";

    public static void main(final String[] args) {
        final BaseInstallationPath baseInstallationPath = new BaseInstallationPath("/opt/okd");
        final GuestVirtualMachine serviceGuestVirtualMachine = new GuestVirtualMachine("services", VmType.SERVICES, "centos");
        final GuestVirtualMachine bootstrapGuestVirtualMachine = new GuestVirtualMachine("bootstrap", VmType.BOOTSTRAP, "core");
        final GuestVirtualMachine controlPlane0GuestVirtualMachine = new GuestVirtualMachine("control-plane-0", VmType.MASTER, "core");
        final OkdNetwork okdNetwork = new OkdNetwork(
                "virbr1",
                "10.0.5.1",
                "10.0.5.255",
                "okd.local",
                "sandbox",
                "/24",
                Arrays.asList(
                        new NetworkVM(serviceGuestVirtualMachine,"10.0.5.57", "52:54:10:00:05:57", "services.sandbox.okd.local", Arrays.asList("okd.local", "lb.sandbox.okd.local","api.sandbox.okd.local","api-int.sandbox.okd.local")),
                        new NetworkVM(bootstrapGuestVirtualMachine,"10.0.5.58", "52:54:10:00:05:58", "bootstrap.sandbox.okd.local", Arrays.asList("bootstrap.sandbox.okd.local")),
                        new NetworkVM(controlPlane0GuestVirtualMachine,"10.0.5.59", "52:54:10:00:05:59", "control-plane-0.sandbox.okd.local", Arrays.asList("control-plane-0.sandbox.okd.local","etcd-0.sandbox.okd.local")))
        );
        Stream.of(new CentOS8Prerequisite(),
                new RootPrerequisite(),
                new CurlPrerequisite(),
                new SshPrerequisite(),
                new XZPrerequisite(),
                new DigPrerequisite(),
                new QemuImgPrerequisite())
                .forEach(prerequisite -> {
                    try {
                        LOGGER.info(prerequisite.title());
                        prerequisite.execute();
                    } catch (final CommandExecutionFailedException commandExecutionFailedException) {
                        LOGGER.error(commandExecutionFailedException.failingCommand());
                        LOGGER.info(String.format("The command '%s ; echo $?' must return 0 from your host bash in root",
                                commandExecutionFailedException.failingCommand()));
                        LOGGER.error(commandExecutionFailedException.executionOutput());
                        LOGGER.error("Please fix it.Exiting ...");
                        System.exit(0);
                    }
                });
        final SshRsaPublicKey sshRsaPublicKey = new SshRsaPublicKey(baseInstallationPath,"id_okd_vm.pub");
        final ContainerRegistry containerRegistry = new ContainerRegistry(baseInstallationPath,"domain.crt", "domain.key");
        Stream.of(
                Arrays.asList(
                        new CreateDomainCertificatesForPodmanRegistryInstallationStep(baseInstallationPath, okdNetwork.getServiceNetworkVM()),
                        new SshKeysToCommunicateBetweenHostAndGuestInstallationStep(baseInstallationPath, "id_okd_vm"),
                        new KvmPackageInstallationStep(),
                        new EnableAndStartLibvirtServiceInstallationStep(),
                        new DestroyLibvirtDefaultNetworkInstallationStep(),
                        new CreateLibvirtOkdNetworkInstallationStep(baseInstallationPath, okdNetwork),
                        new DownloadQEmuDiskInstallationStep(
                                QEMU_REMOTE_FEDORA_CORE_OS,
                                baseInstallationPath
                        ),
                        new DownloadQEmuDiskInstallationStep(
                                QEMU_REMOTE_CENTOS8,
                                baseInstallationPath
                        ),
                        new UnzipQEmuDisksInstallationStep(baseInstallationPath),
                        new GenerateOkdIgnitionFilesInstallationStep(baseInstallationPath,
                                new OkdRemoteInstaller(REMOTE_OKD_INSTALLER),
                                sshRsaPublicKey,
                                containerRegistry,
                                okdNetwork),
                        new CreateServicesCentOS8GuestVirtualMachineInstallationStep(baseInstallationPath,
                                "id_okd_vm",
                                new CentOS8Disk(baseInstallationPath, "CentOS-8-GenericCloud-8.2.2004-20200611.2.x86_64.qcow2"),
                                sshRsaPublicKey,
                                containerRegistry,
                                serviceGuestVirtualMachine,
                                okdNetwork)
                ),
                PODMAN_IMAGES.stream().map(podmanImage -> new PullOkdPodmanImageToServicesPodmanRegistryGuestVMInstallationStep(podmanImage, baseInstallationPath,
                        "id_okd_vm",
                        okdNetwork.getServiceNetworkVM()))
                        .collect(Collectors.toList()),
                Arrays.asList(
                        new CreateOkdFCOSGuestVirtualMachineInstallationStep(baseInstallationPath,
                                new FedoraCoreOSDisk(baseInstallationPath, "fedora-coreos-32.20201104.3.0-qemu.x86_64.qcow2"),
                                bootstrapGuestVirtualMachine,
                                okdNetwork),
                        new CreateOkdFCOSGuestVirtualMachineInstallationStep(baseInstallationPath,
                                new FedoraCoreOSDisk(baseInstallationPath, "fedora-coreos-32.20201104.3.0-qemu.x86_64.qcow2"),
                                controlPlane0GuestVirtualMachine,
                                okdNetwork)
                        )
//                Arrays.asList().stream().map(CreateOkdFCOSGuestVirtualMachineInstallationStep).collect(Collectors.toList())
        )
                .flatMap(Collection::stream)
                .filter(installationStep -> {
                    final boolean shouldSkipInstallationStep = installationStep.shouldSkipInstallationStep();
                    if (shouldSkipInstallationStep) {
                        LOGGER.info(String.format("Skipping already done step '%s'", installationStep.title()));
                    }
                    return !shouldSkipInstallationStep;
                })
                .forEach(installationStep -> {
                    try {
                        LOGGER.info(installationStep.title());
                        installationStep.execute();
                        if (!installationStep.shouldSkipInstallationStep()) {
                            LOGGER.error(installationStep.verifyStepIsAlreadyDoneCommand());
                            LOGGER.error(String.format("The command '%s' failed. It seems that the installation step does not produce the expected state.",
                                    installationStep.verifyStepIsAlreadyDoneCommand()));
                            LOGGER.error("Please fix it.Exiting ...");
                            System.exit(0);
                        }
                    } catch (final CommandExecutionFailedException commandExecutionFailedException) {
                        LOGGER.error(installationStep.verifyStepIsAlreadyDoneCommand());
                        LOGGER.error(commandExecutionFailedException.failingCommand());
                        LOGGER.error(commandExecutionFailedException.executionOutput());
                        LOGGER.error("Please fix it.Exiting ...");
                        System.exit(0);
                    }
                });
        // je dois attendre que l'installation soit fini ...
        // je dois arreter le bootstrap
        LOGGER.info("finished !");// TODO reword message to give url to OKD 4 console !
    }

}
