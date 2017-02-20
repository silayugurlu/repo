package com.company.lighting.dto;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author silay.ugurlu
 */
public class InstallationDto {

    private String name;

    private List<String> lamps;

    private List<String> gateways;
    
    private List<String[]> links = new ArrayList<>();

    public InstallationDto() {
    }

    public InstallationDto(String name) {
        this.name = name;
    }

    public InstallationDto(String name, List<String> lamps, List<String> gateways, List<String[]> links) {
        this.name = name;
        this.lamps = lamps;
        this.gateways = gateways;
        this.links = links;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getLamps() {
        return lamps;
    }

    public void setLamps(List<String> lamps) {
        this.lamps = lamps;
    }

    public List<String> getGateways() {
        return gateways;
    }

    public void setGateways(List<String> gateways) {
        this.gateways = gateways;
    }

    public List<String[]> getLinks() {
        return links;
    }

    public void setLinks(List<String[]> links) {
        this.links = links;
    }

}
