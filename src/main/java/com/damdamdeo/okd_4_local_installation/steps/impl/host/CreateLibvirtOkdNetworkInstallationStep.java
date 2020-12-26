package com.damdamdeo.okd_4_local_installation.steps.impl.host;

import com.damdamdeo.okd_4_local_installation.steps.InstallationStep;
import com.damdamdeo.okd_4_local_installation.steps.impl.BaseInstallationPath;
import com.damdamdeo.okd_4_local_installation.steps.impl.OkdNetwork;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

import java.io.*;
import java.util.*;

public class CreateLibvirtOkdNetworkInstallationStep extends InstallationStep {

    private final BaseInstallationPath baseInstallationPath;
    private final OkdNetwork okdNetwork;

    private final PebbleEngine engine = new PebbleEngine.Builder().build();

    public CreateLibvirtOkdNetworkInstallationStep(final BaseInstallationPath baseInstallationPath,
                                                   final OkdNetwork okdNetwork) {
        this.baseInstallationPath = Objects.requireNonNull(baseInstallationPath);
        this.okdNetwork = Objects.requireNonNull(okdNetwork);
    }

    private String generateOkdNetwork() {
        try {
            // <forward mode="nat" /> must be defined to nat to ensure that internet will be accessible from the guest virtual machine
            final PebbleTemplate compiledTemplate = engine.getTemplate("host_libvirt_okd_network");
            final Map<String, Object> context = new HashMap<>();
            context.put("networkName", okdNetwork.name());
            context.put("networkInterfaceBridgeName", okdNetwork.bridgeName());
            context.put("networkGatewayIp", okdNetwork.gatewayIp());
            context.put("networkNetMaskIp", okdNetwork.netmask().getIp());
            context.put("networkVMs", okdNetwork.networkVMs());
            context.put("serviceNetworkIp", okdNetwork.getServiceNetworkVM().ip());
            final Writer writer = new StringWriter();
            compiledTemplate.evaluate(writer, context);

            return writer.toString();
        } catch (final IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    @Override
    public String title() {
        return "Create libvirt network used by all okd Virtual Machines";
    }

    @Override
    public String verifyStepIsAlreadyDoneCommand() {
        return "virsh net-list --all | grep " + okdNetwork.name();
    }

    @Override
    public String commandToExecute() {
        // virsh net-destroy okd_network
        // virsh net-define --file /tmp/okd_network.xml && virsh net-autostart okd_network && virsh net-start okd_network
        final String ignitionFile = generateOkdNetwork();
        writeFile(String.format("%s/okd_network.xml", baseInstallationPath.path()), ignitionFile);

        return String.format("virsh net-define --file %s/okd_network.xml && " +
                "virsh net-autostart %s && " +
                "virsh net-start %s", baseInstallationPath.path(), okdNetwork.name(), okdNetwork.name());
    }

}
