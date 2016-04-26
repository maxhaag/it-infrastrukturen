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
public class Relation {
    
    private String type;
    private String id;
    private String name;
    private String source;
    private String target;

    public Relation(String type, String id, String name, String source, String target) {
        this.type = type;
        this.id = id;
        this.name = name;
        this.source = source;
        this.target = target;
    }

    public Relation() {
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
    
    
    
}
