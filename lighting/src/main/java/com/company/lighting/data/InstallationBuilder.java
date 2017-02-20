package com.company.lighting.data;

import com.company.lighting.exception.InvalidLinkException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author silay.ugurlu
 */
public class InstallationBuilder {

    private String name;

    private Map<String, Lamp> lamps = new HashMap<>();

    private Map<String, Gateway> gateways = new HashMap<>();

    private Map<String, Set<LightingElement>> linkedElements = new HashMap<>();

    public InstallationBuilder(String name) {
        this.name = name;
    }

    public InstallationBuilder setLamps(List<String> lampNames) {
        lampNames.forEach(lampName -> this.lamps.put(lampName, new Lamp(lampName)));
        return this;
    }

    public InstallationBuilder setGateways(List<String> gatewayNames) {
        gatewayNames.forEach(gatewayName -> this.gateways.put(gatewayName, new Gateway(gatewayName)));
        return this;
    }

    public InstallationBuilder addLinkedElement(String sourceName, String destinationName) throws InvalidLinkException {

        LightingElement sourceElement = this.lamps.containsKey(sourceName) ? this.lamps.get(sourceName) : this.gateways.get(sourceName);
        LightingElement destinationElement = this.lamps.containsKey(destinationName) ? this.lamps.get(destinationName) : this.gateways.get(destinationName);

        if (sourceElement == null) {
            throw new InvalidLinkException("Invalid Link : " + sourceName + "(not exist) -> " + destinationName);
        }

        if (destinationElement == null) {
            throw new InvalidLinkException("Invalid Link : " + sourceName + " -> " + destinationName + "(not exist)");
        }

        Set<LightingElement> elementsSetSource = this.linkedElements.getOrDefault(sourceName, new HashSet<>());
        Set<LightingElement> elementsSetDest = this.linkedElements.getOrDefault(destinationName, new HashSet<>());
        elementsSetSource.add(destinationElement);
        elementsSetDest.add(sourceElement);
        this.linkedElements.put(sourceName, elementsSetSource);
        this.linkedElements.put(destinationName, elementsSetDest);
        return this;
    }

    public Installation build() {
        return new Installation(this.name, this.lamps, this.gateways, this.linkedElements);
    }

}
