package com.company.lighting.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 *
 * @author silay.ugurlu
 */
public class InstallationDtoBuilder {

    private String name;

    private List<String> lamps = new ArrayList<>();

    private List<String> gateways = new ArrayList<>();

    private List<String[]> links = new ArrayList<>();

    public InstallationDtoBuilder(String name) {
        this.name = name;
    }

    public InstallationDtoBuilder setLamps(Set<String> lampNames) {
        lampNames.forEach(lampName -> this.lamps.add(lampName));
        return this;
    }

    public InstallationDtoBuilder setGateways(Set<String> gatewayNames) {
        gatewayNames.forEach(gatewayName -> this.gateways.add(gatewayName));
        return this;
    }

    public InstallationDtoBuilder setLamps(String... lampNames) {
        Arrays.asList(lampNames).forEach(lampName -> this.lamps.add(lampName));
        return this;
    }

    public InstallationDtoBuilder setGateways(String... gatewayNames) {
        Arrays.asList(gatewayNames).forEach(gatewayName -> this.gateways.add(gatewayName));
        return this;
    }

    public InstallationDtoBuilder addLink(String source, String... destinationArr) {
        List<String> destinations = Arrays.asList(destinationArr);
        destinations.forEach(destination -> this.links.add(new String[]{source, destination}));
        return this;
    }

    public InstallationDto build() {
        return new InstallationDto(name, lamps, gateways, links);
    }

}
