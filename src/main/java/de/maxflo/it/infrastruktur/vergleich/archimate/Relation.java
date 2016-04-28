/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.maxflo.it.infrastruktur.vergleich.archimate;

import java.util.Objects;

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
    private String folder;
    private String line;

  
    public Relation() {
    }

    public Relation(String type, String id, String name, String source, String target, String folder) {
        this.type = type;
        this.id = id;
        this.name = name;
        this.source = source;
        this.target = target;
        this.folder = folder;
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

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.type);
        hash = 23 * hash + Objects.hashCode(this.name);
        hash = 23 * hash + Objects.hashCode(this.source);
        hash = 23 * hash + Objects.hashCode(this.target);
        hash = 23 * hash + Objects.hashCode(this.folder);
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
        final Relation other = (Relation) obj;
        if (!Objects.equals(this.type, other.type)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.source, other.source)) {
            return false;
        }
        if (!Objects.equals(this.target, other.target)) {
            return false;
        }
        if (!Objects.equals(this.folder, other.folder)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Relation{" + "type=" + type + ", id=" + id + ", name=" + name + ", source=" + source + ", target=" + target + ", folder=" + folder + '}';
    }
    
    
  
   
    
    
}
