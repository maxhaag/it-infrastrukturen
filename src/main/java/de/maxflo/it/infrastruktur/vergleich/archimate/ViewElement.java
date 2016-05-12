/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.maxflo.it.infrastruktur.vergleich.archimate;

import java.util.ArrayList;
import java.util.Objects;

/**
 *
 * @author fn
 */
public class ViewElement {
    
    
    private String name;
    private ArrayList<String> documentation;
    private String id;

    public ViewElement() {
        documentation = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getDocumentation() {
        return documentation;
    }

    public void setDocumentation(ArrayList<String> documentation) {
        this.documentation = documentation;
    }

   

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.name);
        hash = 97 * hash + Objects.hashCode(this.documentation);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ViewElement other = (ViewElement) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.documentation, other.documentation)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ViewElement{" + "name=" + name + ", documentation=" + documentation + ", id=" + id + '}';
    }
    
    
}
