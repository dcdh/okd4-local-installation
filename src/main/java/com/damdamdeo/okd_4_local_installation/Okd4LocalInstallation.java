package com.damdamdeo.okd_4_local_installation;

import com.damdamdeo.okd_4_local_installation.prerequists.impl.*;
import com.damdamdeo.okd_4_local_installation.steps.impl.*;
import com.damdamdeo.okd_4_local_installation.steps.impl.guest.PullOkdPodmanImageToServicesPodmanRegistryGuestVMInstallationStep;
import com.damdamdeo.okd_4_local_installation.steps.impl.host.CreateDomainCertificatesForPodmanRegistryInstallationStep;
import com.damdamdeo.okd_4_local_installation.steps.impl.guest.CreateOkdFCOSGuestVirtualMachineInstallationStep;
import com.damdamdeo.okd_4_local_installation.steps.impl.guest.CreateServicesCentOS8GuestVirtualMachineInstallationStep;
import com.damdamdeo.okd_4_local_installation.steps.impl.host.*;
import com.damdamdeo.okd_4_local_installation.steps.impl.host.GenerateOkdIgnitionFilesInstallationStep;
import com.damdamdeo.okd_4_local_installation.steps.impl.host.OkdRemoteInstaller;
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
        PODMAN_IMAGES.add(new PodmanImage("okd-release", "quay.io/openshift/okd@sha256:6640a4daf0623023b9046fc91858d018bd34433b5c3485c4a61904a33b59a3b9"));
        PODMAN_IMAGES.add(new PodmanImage("aws-ebs-csi-driver", "quay.io/openshift/okd-content@sha256:7a6ac604450818ef0dfc3c44b9c46f57d49ba4654a3319f06ee46736f4939719"));
        PODMAN_IMAGES.add(new PodmanImage("aws-ebs-csi-driver-operator", "quay.io/openshift/okd-content@sha256:a7e27b115fc4c5973444c5f7b3cf052fbabc20548e3ec2ee9edc2ebcd7d1bdc4"));
        PODMAN_IMAGES.add(new PodmanImage("aws-machine-controllers", "quay.io/openshift/okd-content@sha256:4b18aff6f97c576d3ff1eef89c2e7e41dbba7064ef84f50ef6ec20655d2fb56e"));
        PODMAN_IMAGES.add(new PodmanImage("aws-pod-identity-webhook", "quay.io/openshift/okd-content@sha256:1876f70a263b923cae1162e478dd850f55243cf3915a3878e7f2753f16ac4b48"));
        PODMAN_IMAGES.add(new PodmanImage("azure-machine-controllers", "quay.io/openshift/okd-content@sha256:48638ddeb84e70f5c65952b5efac780180e6bf76b6337d6777e0dc3be13c6e3f"));
        PODMAN_IMAGES.add(new PodmanImage("baremetal-installer", "quay.io/openshift/okd-content@sha256:5254a9a4fc77f20a3381132013ca431a86811a96113b78414c5e0d3e29282f1c"));
        PODMAN_IMAGES.add(new PodmanImage("baremetal-machine-controllers", "quay.io/openshift/okd-content@sha256:227fd1bb9185e667de520c3428e07c2a3b19f47a30f3770a06611d4d9d1901a4"));
        PODMAN_IMAGES.add(new PodmanImage("baremetal-operator", "quay.io/openshift/okd-content@sha256:227fd1bb9185e667de520c3428e07c2a3b19f47a30f3770a06611d4d9d1901a4"));
        PODMAN_IMAGES.add(new PodmanImage("baremetal-runtimecfg", "quay.io/openshift/okd-content@sha256:53f8b95fc3d4a7641273208a8e0337ac6a606259a24e8b0e72ef3df36408d279"));
        PODMAN_IMAGES.add(new PodmanImage("branding", "quay.io/openshift/okd-content@sha256:57a0ec35c89ce06d6495e4c18b2763647b131871f09579787d8146179d77cd8a"));
        PODMAN_IMAGES.add(new PodmanImage("cli", "quay.io/openshift/okd-content@sha256:1620573f945cb7bf40d83a9bc8b907970f874ee575f065d6f8bb50a94b6aab0c"));
        PODMAN_IMAGES.add(new PodmanImage("cli-artifacts", "quay.io/openshift/okd-content@sha256:5db44d9034bcef70b36390f521a5aeab8ac17dac9e51698a4b48172592564c35"));
        PODMAN_IMAGES.add(new PodmanImage("cloud-credential-operator", "quay.io/openshift/okd-content@sha256:5fedf5f75f75c885d732cc14cd3018a3c7cb2767e8f9c2997d0dfb55198c0dc8"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-authentication-operator", "quay.io/openshift/okd-content@sha256:215f151ed397cde298bc051e3a33c3274a8359529810e6014f29f3581093b829"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-autoscaler", "quay.io/openshift/okd-content@sha256:eac32c3d308a97e63ae41e6497dd7c8a6a087643e91afd8d70e2883429cfc268"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-autoscaler-operator", "quay.io/openshift/okd-content@sha256:6b9827f1e314011d68001ab5c0bceb3c8c22e89749856f93fbe1b48d4cd5b130"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-bootstrap", "quay.io/openshift/okd-content@sha256:d374ab4a9324381f06d10d664c9ce811c7ed2518dbdf1de4216d5270cc921f56"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-config-operator", "quay.io/openshift/okd-content@sha256:033019bcd67da5125b057ac544c69fda2836cfc406002a801b434244a2e4d9fd"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-csi-snapshot-controller-operator", "quay.io/openshift/okd-content@sha256:be3a5e7c8c7da627fcc279debc6cdc4f850ade3eedf91e4a8311261a3c24cfee"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-dns-operator", "quay.io/openshift/okd-content@sha256:e68802b5877bba7ded4f3703a4eb4b67018c6d12d8279dec4c30ba8d2743271b"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-etcd-operator", "quay.io/openshift/okd-content@sha256:6dc0b62a6ead52880bb0a17f0385a4e865a33e77b92d29118f4b77ca0fbf9b3f"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-image-registry-operator", "quay.io/openshift/okd-content@sha256:b27f940b03ac3f15c5f61e7a1c73d3c02600a7dc0b156c492a78f708793e60dd"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-ingress-operator", "quay.io/openshift/okd-content@sha256:366925674527da54eca22ad459a6e6a0a2e8bd5b09283b53b740f022fbbdbb7f"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-kube-apiserver-operator", "quay.io/openshift/okd-content@sha256:61613d0fc9614d4785b5cc7a344073591a314ec499b0d5d80ad47fd9a29f6424"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-kube-controller-manager-operator", "quay.io/openshift/okd-content@sha256:52eab65a01b39efbba7d205940f65d12b40a978712f02e652e41ed2364f12e1d"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-kube-scheduler-operator", "quay.io/openshift/okd-content@sha256:d1a2c380fc320c32e6ca4fb37bfc6af18839c856c3f6a1ac84c52e2836a838a7"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-kube-storage-version-migrator-operator", "quay.io/openshift/okd-content@sha256:bcad1e99aa191f58c39b33f0c091f78889a9ea967f5ea1dcaeaa9614f841f527"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-machine-approver", "quay.io/openshift/okd-content@sha256:61beafbb0f9458108ca0975715c584581460feb1da994773a6d7773bfe55c69d"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-monitoring-operator", "quay.io/openshift/okd-content@sha256:f2354199c23f0a6ad5d709defdb6e702b2c4818aff4aff0fb893242b0c6a1fca"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-network-operator", "quay.io/openshift/okd-content@sha256:82a57f0ff1cc0f4e4fee2258dc7172e5b1927fdced5c24629f20f9baba34fe42"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-node-tuning-operator", "quay.io/openshift/okd-content@sha256:a583046515f5e847a5cad70208b6f26226e6f2c301d6dcf1bf4c7262aeb314c0"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-openshift-apiserver-operator", "quay.io/openshift/okd-content@sha256:6fb542d6333a521138c61892cf7389a6c13d22342ad6515515757af103dd1a66"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-openshift-controller-manager-operator", "quay.io/openshift/okd-content@sha256:1556a6e4a3c938dbcc9b174cbfdde9cc97945fbf5ecc30aeaf212eb1f3cd7b9f"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-policy-controller", "quay.io/openshift/okd-content@sha256:2574920959af8f1ef6d133af7298c9ca60555729a6c7d570d8d7f147e288d1da"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-samples-operator", "quay.io/openshift/okd-content@sha256:192be20ff0709d29c4e03656ca9d3623fb22c514e2aaa05ddd2f0781a3611a44"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-storage-operator", "quay.io/openshift/okd-content@sha256:8012b605392f8dcb83f48e5101ab750014dcce9c11b1ccb8b3f97b10e924f277"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-svcat-apiserver-operator", "quay.io/openshift/okd-content@sha256:882e5ce3ca1e1e18ffe18a872a8a969317e6ea07b617e22732b40da598c4d71b"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-svcat-controller-manager-operator", "quay.io/openshift/okd-content@sha256:3ff046a021724da5205c4d97e44acc67ee324593fe1901ff14fcde337da2947d"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-update-keys", "quay.io/openshift/okd-content@sha256:f9105a876d7ba731761da404b6a76f6a7c7806e6393521ff3e62c3bdb5cffdbb"));
        PODMAN_IMAGES.add(new PodmanImage("cluster-version-operator", "quay.io/openshift/okd-content@sha256:f0981a5dce16649319196fce0e4acc5e3519d6d161e7a8bce1dc8a7ea7377078"));
        PODMAN_IMAGES.add(new PodmanImage("configmap-reloader", "quay.io/openshift/okd-content@sha256:4c0f2fdf48575cfa09b45e1b102f4c08efc7d3928d248abf6052d735ca319c99"));
        PODMAN_IMAGES.add(new PodmanImage("console", "quay.io/openshift/okd-content@sha256:0b1187c7e149d7daffa8ec083917db65f8707a80730f6a93a4711c41d33c1829"));
        PODMAN_IMAGES.add(new PodmanImage("console-operator", "quay.io/openshift/okd-content@sha256:b4a9bcf7bdc8ff2eba1fa5277ed39e198aaf83ae4f80abc54846b9ef2085ab26"));
        PODMAN_IMAGES.add(new PodmanImage("container-networking-plugins", "quay.io/openshift/okd-content@sha256:7378012df67e3e73ee1974ab241eff685eeb50fccd02188c1f1cdba3141eaa93"));
        PODMAN_IMAGES.add(new PodmanImage("coredns", "quay.io/openshift/okd-content@sha256:5677039e7752619afe9b552d3e77b3b0e4d75551c55cf2fa535bef8bb691b796"));
        PODMAN_IMAGES.add(new PodmanImage("csi-driver-manila", "quay.io/openshift/okd-content@sha256:62c233c13a981ef63b3d53db80237ca113269c3dd64aa52f925719d091ef2995"));
        PODMAN_IMAGES.add(new PodmanImage("csi-driver-manila-operator", "quay.io/openshift/okd-content@sha256:2cb1319e001fc0be016239d78abd4b662fffc48674924989fd426115e9804bc6"));
        PODMAN_IMAGES.add(new PodmanImage("csi-driver-nfs", "quay.io/openshift/okd-content@sha256:3cd4f98b004d30f1086fc56481ac4ed34f86be08f37f879044bae1c381536401"));
        PODMAN_IMAGES.add(new PodmanImage("csi-external-attacher", "quay.io/openshift/okd-content@sha256:a05a18e93b350fb4160f61924b46f99f9b84fc9c1036840d9c68764a678f1d91"));
        PODMAN_IMAGES.add(new PodmanImage("csi-external-provisioner", "quay.io/openshift/okd-content@sha256:4d34ec42b993332a2e9cbd57d909e940699a213e69e6b25132c6fd36aef10e25"));
        PODMAN_IMAGES.add(new PodmanImage("csi-external-resizer", "quay.io/openshift/okd-content@sha256:8ead480de2f6f5a9c582869a43231082102ce0394669eb9335426a892b1a799c"));
        PODMAN_IMAGES.add(new PodmanImage("csi-external-snapshotter", "quay.io/openshift/okd-content@sha256:3a1ebf3e1cf20c10f07426ca9a31ae2025dd2ef138abaa29a49338afab75e272"));
        PODMAN_IMAGES.add(new PodmanImage("csi-livenessprobe", "quay.io/openshift/okd-content@sha256:7075caea70ebc3769fcfef49a89359ac9b11d3c70b207785e3a276f56b5a14c7"));
        PODMAN_IMAGES.add(new PodmanImage("csi-node-driver-registrar", "quay.io/openshift/okd-content@sha256:f4ca8198ab44bede2333991d4504c8c54ac769b9020a25b6dafb4a6ec19210e7"));
        PODMAN_IMAGES.add(new PodmanImage("csi-snapshot-controller", "quay.io/openshift/okd-content@sha256:b7235b803e4d1fb70e252d1b2a49dceb61aba38e6e3e5524ffd44e7ed68b3857"));
        PODMAN_IMAGES.add(new PodmanImage("deployer", "quay.io/openshift/okd-content@sha256:1afb12833865874a53fafdec46a3b88ac0361cc47890e625c4c3dd978b5d197b"));
        PODMAN_IMAGES.add(new PodmanImage("docker-builder", "quay.io/openshift/okd-content@sha256:404c238584164d9504c8312fcb332ac54c8712a9d82f68dc1ab039846034840c"));
        PODMAN_IMAGES.add(new PodmanImage("docker-registry", "quay.io/openshift/okd-content@sha256:ec781f86dc3f9ae1179e90f2245a1298e4b3a384a4e261f825d97adb144ef9b7"));
        PODMAN_IMAGES.add(new PodmanImage("etcd", "quay.io/openshift/okd-content@sha256:905a912400c72f2755cdcae489c6f89f7c4f5975e2814d151ce9add4de178dcd"));
        PODMAN_IMAGES.add(new PodmanImage("gcp-machine-controllers", "quay.io/openshift/okd-content@sha256:3f0f1fec7fbeee5e4cce1434a3d78c640fa4832a32e1a33cf00c80be14e1bbf8"));
        PODMAN_IMAGES.add(new PodmanImage("grafana", "quay.io/openshift/okd-content@sha256:e2e72305b7138e13849b684c9f3c20432ac56dcbf58701213b9941a1c2870101"));
        PODMAN_IMAGES.add(new PodmanImage("haproxy-router", "quay.io/openshift/okd-content@sha256:67a3041a7b5cbdcda8b9a10a909b7c9c84e7f0ec639b9106f7ecca20d3b37343"));
        PODMAN_IMAGES.add(new PodmanImage("hyperkube", "quay.io/openshift/okd-content@sha256:00179835bfde5cccc2f62ec787b69c4ec22380007b60f77647f0feaf2e385155"));
        PODMAN_IMAGES.add(new PodmanImage("insights-operator", "quay.io/openshift/okd-content@sha256:8471558def4d0a7237d9f6a6a3bca7460280fef7abdf71f33e32e1b971d482af"));
        PODMAN_IMAGES.add(new PodmanImage("installer", "quay.io/openshift/okd-content@sha256:b14aee041b51d02bd54dcaaa4d94de14b46368b21da3ee5e5da73610d9036297"));
        PODMAN_IMAGES.add(new PodmanImage("installer-artifacts", "quay.io/openshift/okd-content@sha256:0a8287d457a5a3e984ff39e54b00077fb5d8a9e2e4c19b5ecdb6f068eca913f6"));
        PODMAN_IMAGES.add(new PodmanImage("ironic", "quay.io/openshift/okd-content@sha256:227fd1bb9185e667de520c3428e07c2a3b19f47a30f3770a06611d4d9d1901a4"));
        PODMAN_IMAGES.add(new PodmanImage("ironic-hardware-inventory-recorder", "quay.io/openshift/okd-content@sha256:377b334d98a24010ef801ecd551b258b92a7c9007bebf9c44bf224294bbe2194"));
        PODMAN_IMAGES.add(new PodmanImage("ironic-inspector", "quay.io/openshift/okd-content@sha256:227fd1bb9185e667de520c3428e07c2a3b19f47a30f3770a06611d4d9d1901a4"));
        PODMAN_IMAGES.add(new PodmanImage("ironic-ipa-downloader", "quay.io/openshift/okd-content@sha256:227fd1bb9185e667de520c3428e07c2a3b19f47a30f3770a06611d4d9d1901a4"));
        PODMAN_IMAGES.add(new PodmanImage("ironic-machine-os-downloader", "quay.io/openshift/okd-content@sha256:8a8333772e9e4b84bf8a862c7a555aadee7058261404985972b3215b40c50f92"));
        PODMAN_IMAGES.add(new PodmanImage("ironic-static-ip-manager", "quay.io/openshift/okd-content@sha256:0aad4fa79a082326805d01c0e98f2453d9911d214d3f9e74b35ed126e7943f0b"));
        PODMAN_IMAGES.add(new PodmanImage("jenkins", "quay.io/openshift/okd-content@sha256:0a1ce07b0c848e0e6fecbc49ccd4b402fc05d10a2837093820bdc321df60bb59"));
        PODMAN_IMAGES.add(new PodmanImage("jenkins-agent-base", "quay.io/openshift/okd-content@sha256:2f3c727370832f2f1bd09f5ef7289a1f17a2dee8d9f0da1882ba74d4dcc94a79"));
        PODMAN_IMAGES.add(new PodmanImage("jenkins-agent-maven", "quay.io/openshift/okd-content@sha256:1facb8e8f8867086a49ef6ac882755f02e748097b931578ace15538af2dd3eb3"));
        PODMAN_IMAGES.add(new PodmanImage("jenkins-agent-nodejs", "quay.io/openshift/okd-content@sha256:5afc0ecfded61cc5afb10535a40d929fc1d7acbe49cdba2c6a349ad321e56511"));
        PODMAN_IMAGES.add(new PodmanImage("k8s-prometheus-adapter", "quay.io/openshift/okd-content@sha256:ada2cad3faaeeeb904c5b6ca1442555ce46ac5e3ea708341298b9dfd236ad12f"));
        PODMAN_IMAGES.add(new PodmanImage("keepalived-ipfailover", "quay.io/openshift/okd-content@sha256:cc73f3d57bb2ea974f70cda5d809104ff0adc2a3ffe72a4bea672a8465525274"));
        PODMAN_IMAGES.add(new PodmanImage("kube-proxy", "quay.io/openshift/okd-content@sha256:34e773fb20d661792ee1602f34d1aff4725d52787ee5b5403174fcbe9a801b63"));
        PODMAN_IMAGES.add(new PodmanImage("kube-rbac-proxy", "quay.io/openshift/okd-content@sha256:da899fca877bd4d51aa3e0f8c82833909cd6a6ee81c11f9cdf631a54d52cc194"));
        PODMAN_IMAGES.add(new PodmanImage("kube-state-metrics", "quay.io/openshift/okd-content@sha256:8cc28e10e816aa65fe652271cab3f9f4e93d16f9331384a10c1c7490e7518978"));
        PODMAN_IMAGES.add(new PodmanImage("kube-storage-version-migrator", "quay.io/openshift/okd-content@sha256:ccbfcea0fc67844e748601e885c0f988b86db548da693a833645ff15db87ad5e"));
        PODMAN_IMAGES.add(new PodmanImage("kuryr-cni", "quay.io/openshift/okd-content@sha256:9ae31b187522342348d0f8f08412443180a047d9dcf86003ea2dd7f79de070db"));
        PODMAN_IMAGES.add(new PodmanImage("kuryr-controller", "quay.io/openshift/okd-content@sha256:6915960dae993e6d39831b6527388d65b4b4c71fd6e7fa13fd7c028ee4149828"));
        PODMAN_IMAGES.add(new PodmanImage("libvirt-machine-controllers", "quay.io/openshift/okd-content@sha256:cd4ea1f5873f127f694abcb6dca94c18d0d506843120213c03ca7dcbd1075c9f"));
        PODMAN_IMAGES.add(new PodmanImage("local-storage-static-provisioner", "quay.io/openshift/okd-content@sha256:3a4dcdc1b07edef64e027649ccdd9e5ddffa63c7ac6b6ac60a04e512a95b0327"));
        PODMAN_IMAGES.add(new PodmanImage("machine-api-operator", "quay.io/openshift/okd-content@sha256:9f1eaa389353b40d8259e5359007f24c966cd6c4084b8f473b759b9ca1aa3cf0"));
        PODMAN_IMAGES.add(new PodmanImage("machine-config-operator", "quay.io/openshift/okd-content@sha256:34d57bb37a599a43e02286b9941c9f9c3093648b73a5bfb7b19eda895cc8db90"));
        PODMAN_IMAGES.add(new PodmanImage("machine-os-content", "quay.io/openshift/okd-content@sha256:8a06d787f558a5af0da2b34e30c0aeaa3b1675e150e6ea44645f4ed698df77a3"));
        PODMAN_IMAGES.add(new PodmanImage("mdns-publisher", "quay.io/openshift/okd-content@sha256:967dbd63416f762cf5680c5aa959f0f30d81fe42b9b8a39a2bd0548f51cb1a52"));
        PODMAN_IMAGES.add(new PodmanImage("multus-admission-controller", "quay.io/openshift/okd-content@sha256:d0093bd38dc6396d46699f31277bb9e19d31c65b75badaa1b1b372a5873fdcce"));
        PODMAN_IMAGES.add(new PodmanImage("multus-cni", "quay.io/openshift/okd-content@sha256:3069ceb7da494ddcaeb793a424ba55be6024d49ffd9a8670c87298db757a94e6"));
        PODMAN_IMAGES.add(new PodmanImage("multus-route-override-cni", "quay.io/openshift/okd-content@sha256:b62dfc96853c3d4ce3f7fed51c2d96009847cb47b59eb9345beb2a100369ff87"));
        PODMAN_IMAGES.add(new PodmanImage("multus-whereabouts-ipam-cni", "quay.io/openshift/okd-content@sha256:dc76f04da3c74019c853f3ff19eaa68819119733a11a392f5ab9bb562e0ca1b4"));
        PODMAN_IMAGES.add(new PodmanImage("must-gather", "quay.io/openshift/okd-content@sha256:a9be98e5d01997775763fc3045527f1e030284841c04e76af14b110e5550c2bc"));
        PODMAN_IMAGES.add(new PodmanImage("network-metrics-daemon", "quay.io/openshift/okd-content@sha256:163426b566456bbfb29b7dcf626906d2e1151e3fc62220470d00e68e646e06af"));
        PODMAN_IMAGES.add(new PodmanImage("oauth-apiserver", "quay.io/openshift/okd-content@sha256:08ce8632b8471db6881adc33256b6dbe2bd5491dc1a833059cb650de6401823b"));
        PODMAN_IMAGES.add(new PodmanImage("oauth-proxy", "quay.io/openshift/okd-content@sha256:cb061902876f0eaa58438c2ce65f836c8357579d698d4e78f396a399192657bb"));
        PODMAN_IMAGES.add(new PodmanImage("oauth-server", "quay.io/openshift/okd-content@sha256:74204d329189fb8ec94df6c262ede2e6a485dbf663388ba93a7048657c33c543"));
        PODMAN_IMAGES.add(new PodmanImage("openshift-apiserver", "quay.io/openshift/okd-content@sha256:f9b8f550e96809a500fdc24432a68bf6153786b97638dfbf29feccf1e16ec1d5"));
        PODMAN_IMAGES.add(new PodmanImage("openshift-controller-manager", "quay.io/openshift/okd-content@sha256:856483db24e46812e63511c3835128c299cb8023f0f5479d0fbc46bdd1f7a0b4"));
        PODMAN_IMAGES.add(new PodmanImage("openshift-state-metrics", "quay.io/openshift/okd-content@sha256:2efb0d9ca67286fd4014d5c3134c71f829b3fa5ee8d7248d1c7fda7299a6330e"));
        PODMAN_IMAGES.add(new PodmanImage("openstack-machine-controllers", "quay.io/openshift/okd-content@sha256:bd4b1aaf82bd8cfc560a7c8fbe25c60fcea0edffb1cdd6c3d1ab20912dc4b513"));
        PODMAN_IMAGES.add(new PodmanImage("operator-lifecycle-manager", "quay.io/openshift/okd-content@sha256:9e942748208874f43b390eebf32f299399ab403a4a19a0d94833c4ce97d96a3f"));
        PODMAN_IMAGES.add(new PodmanImage("operator-marketplace", "quay.io/openshift/okd-content@sha256:885202e4723ad893b6db13dbab0673912c2c01023dd795c7915e1a053bdbb73d"));
        PODMAN_IMAGES.add(new PodmanImage("operator-registry", "quay.io/openshift/okd-content@sha256:90ce7573d604a13fced3404b0b5edc6b936e211bb20312f9879cb95e96ecd008"));
        PODMAN_IMAGES.add(new PodmanImage("ovirt-csi-driver", "quay.io/openshift/okd-content@sha256:d05080e9fb17c0fd526ed3e6e88a238406da4572e5b6294d0b9117be4ccd83c3"));
        PODMAN_IMAGES.add(new PodmanImage("ovirt-csi-driver-operator", "quay.io/openshift/okd-content@sha256:bdc1eee29efa2cd3314522447665b2bee5838436d596d5a0bf17d86276c875de"));
        PODMAN_IMAGES.add(new PodmanImage("ovirt-machine-controllers", "quay.io/openshift/okd-content@sha256:a38ca1ab4d005efd7839ed64209aeaea462c380921f6bf26679dc8744235906e"));
        PODMAN_IMAGES.add(new PodmanImage("ovn-kubernetes", "quay.io/openshift/okd-content@sha256:2c0a195dd58cdf27ca2fa99880c7364b29ff8c499aaa7262166576212229eb77"));
        PODMAN_IMAGES.add(new PodmanImage("pod", "quay.io/openshift/okd-content@sha256:ce21d449bd3f3d6d106a89f2a7c089fd4191b8a2ecdda7cb000a9f494c313945"));
        PODMAN_IMAGES.add(new PodmanImage("prom-label-proxy", "quay.io/openshift/okd-content@sha256:e5bde703f38357247d8d5dcdaff28bbcd7b8e2ea9ca09b9b5e2976c2b168bc63"));
        PODMAN_IMAGES.add(new PodmanImage("prometheus", "quay.io/openshift/okd-content@sha256:54043d1d0084d37dae22ef64c21634e1bfb86289173ae4909ac2e000fd5412e9"));
        PODMAN_IMAGES.add(new PodmanImage("prometheus-alertmanager", "quay.io/openshift/okd-content@sha256:5f4ca527ada907031638289254897e524b80e67ccc9d7f9ab58e72ac8a9f3a04"));
        PODMAN_IMAGES.add(new PodmanImage("prometheus-config-reloader", "quay.io/openshift/okd-content@sha256:1dbdf35aeaa1b7af23a9ce55d292d5693557383ad1509ddf6fe2c3350abe2e12"));
        PODMAN_IMAGES.add(new PodmanImage("prometheus-node-exporter", "quay.io/openshift/okd-content@sha256:adbb0d23e6212185cf7f2da13b564928d76447e20e9e079e02af0fed70446530"));
        PODMAN_IMAGES.add(new PodmanImage("prometheus-operator", "quay.io/openshift/okd-content@sha256:006fd3a5134f3dc37e0a20741c022c9df9e57522ed847b10ddbd8a81b3009396"));
        PODMAN_IMAGES.add(new PodmanImage("sdn", "quay.io/openshift/okd-content@sha256:c49033600ffabd3d1a57074c9a3c446be0dd208260dfc74d69a9f32d18d04863"));
        PODMAN_IMAGES.add(new PodmanImage("service-ca-operator", "quay.io/openshift/okd-content@sha256:65e27b8cc3843f7cfcd802bc372e710515519fba20f70ceaa6a6ab628c7b32a0"));
        PODMAN_IMAGES.add(new PodmanImage("special-resource-operator", "quay.io/openshift/okd-content@sha256:e6d870f6f2b5b1680353d8336ba61666e1541bdd26ac62ceff7c686bee5efb6f"));
        PODMAN_IMAGES.add(new PodmanImage("telemeter", "quay.io/openshift/okd-content@sha256:d100e16eda307f05bfe85cbb6707c3b82401ed1b6dd034ea814d0f9e88143167"));
        PODMAN_IMAGES.add(new PodmanImage("tests", "quay.io/openshift/okd-content@sha256:cf0c2885c5489e7a5fac71b6b3b557680b765ddc69bcf91b3b7d99cc518998ff"));
        PODMAN_IMAGES.add(new PodmanImage("thanos", "quay.io/openshift/okd-content@sha256:afbf327685fc5d93f69f15ff1b98b4bdbbf4ad89af3ff0800a9d11fc958f9d77"));
        PODMAN_IMAGES.add(new PodmanImage("tools", "quay.io/openshift/okd-content@sha256:6009d42d3a7122eefc17d3aa2316664316597b5978b62c807fac9a44a4a3f679"));
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Okd4LocalInstallation.class);

    private static final String QEMU_REMOTE_FEDORA_CORE_OS = "https://builds.coreos.fedoraproject.org/prod/streams/stable/builds/33.20210117.3.2/x86_64/fedora-coreos-33.20210117.3.2-qemu.x86_64.qcow2.xz";

    private static final String QEMU_REMOTE_CENTOS8 = "https://cloud.centos.org/centos/8/x86_64/images/CentOS-8-GenericCloud-8.2.2004-20200611.2.x86_64.qcow2";

    private static final String REMOTE_OKD_INSTALLER = "https://github.com/openshift/okd/releases/download/4.6.0-0.okd-2021-02-14-205305/openshift-install-linux-4.6.0-0.okd-2021-02-14-205305.tar.gz";

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
                new Netmask("255.255.255.0/24"),
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
                                new FedoraCoreOSDisk(baseInstallationPath, "fedora-coreos-33.20210117.3.2-qemu.x86_64.qcow2"),
                                bootstrapGuestVirtualMachine,
                                okdNetwork),
                        new CreateOkdFCOSGuestVirtualMachineInstallationStep(baseInstallationPath,
                                new FedoraCoreOSDisk(baseInstallationPath, "fedora-coreos-33.20210117.3.2-qemu.x86_64.qcow2"),
                                controlPlane0GuestVirtualMachine,
                                okdNetwork)
                        )
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
