package com.company.lighting.data;

/**
 *
 * @author silay.ugurlu
 */
public class LightingElement {

    private String name;

    public LightingElement(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof LightingElement)) {
            return false;
        }
        LightingElement other = (LightingElement) o;
        return this.name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

}
