/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.maxflo.it.infrastruktur.vergleich.archimate;

/**
 *
 * @author Max
 */
public class Figure {
    
    
    private String type;
    private String id;
    private String name;

    public Figure(String type, String id, String name) {
        this.type = type;
        this.id = id;
        this.name = name;
    }

    public Figure() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
    
    
    
}
