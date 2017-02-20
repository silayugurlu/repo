package com.company.lighting.data;

import com.company.lighting.exception.InvalidLinkException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author silay.ugurlu
 */
public class Installation {

    private String name;

    private Map<String, Lamp> lamps;

    private Map<String, Gateway> gateways;

    private Map<String, Set<LightingElement>> linkedElements = new HashMap<>();

    public Installation(String name) {
        this.name = name;
    }

    public Installation(String name, Map<String, Lamp> lamps, Map<String, Gateway> gateways, Map<String, Set<LightingElement>> linkedElements) {
        this.name = name;
        this.lamps = lamps;
        this.gateways = gateways;
        this.linkedElements = linkedElements;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Lamp> getLamps() {
        return lamps;
    }

    public void setLamps(Map<String, Lamp> lamps) {
        this.lamps = lamps;
    }

    public Map<String, Gateway> getGateways() {
        return gateways;
    }

    public void setGateways(Map<String, Gateway> gateways) {
        this.gateways = gateways;
    }

    public Map<String, Set<LightingElement>> getLinkedElements() {
        return linkedElements;
    }

    public void setLinkedElements(Map<String, Set<LightingElement>> linkedElements) {
        this.linkedElements = linkedElements;
    }

    public LightingElement getLightingElement(String elementName) {
        LightingElement element = lamps.get(elementName);
        if (element == null) {
            element = gateways.get(elementName);
        }
        return element;
    }

    public void addLinkedElement(String sourceElementName, String destinationElementName) throws InvalidLinkException {
        LightingElement sourceElement = getLightingElement(sourceElementName);
        if (sourceElement == null) {
            throw new InvalidLinkException("Invalid Link : " + sourceElementName + "(not exist) -> " + destinationElementName);
        }
        LightingElement destinationElement = getLightingElement(destinationElementName);
        if (destinationElement == null) {
            throw new InvalidLinkException("Invalid Link : " + sourceElementName + " -> " + destinationElementName + "(not exist)");
        }
        Set<LightingElement> elementsListSrc = this.linkedElements.getOrDefault(sourceElementName, new HashSet<>());
        elementsListSrc.add(destinationElement);
        this.linkedElements.put(sourceElementName, elementsListSrc);

        Set<LightingElement> elementsListDest = this.linkedElements.getOrDefault(destinationElementName, new HashSet<>());
        elementsListDest.add(sourceElement);
        this.linkedElements.put(destinationElementName, elementsListDest);

    }

    private Exception InvalidLinkException(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
