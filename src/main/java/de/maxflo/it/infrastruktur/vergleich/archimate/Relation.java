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
    
    
   /* @Override
    public boolean equals(Object obj) {
        Relation rel = (Relation)obj;
        boolean equals = true;
        
        if(rel.getType()!= null && type == null) 
            equals = false;
        else if(rel.getType() == null && type != null)
            equals = false;
        else if(rel.getType() == null && type == null)
            equals = true;
        else if(!rel.getType().equals(type))
            equals = false;
 
        if(rel.getId() != null && id == null) 
            equals = false;
        else if(rel.getId() == null && id != null)
            equals = false;
        else if(rel.getId() == null && id == null)
            equals = true;
        else if(!rel.getId().equals(id))
            equals = false;
        
        if(rel.getName()!= null && name == null) 
            equals = false;
        else if(rel.getName() == null && name != null)
            equals = false;
        else if(rel.getName() == null && name == null)
            equals = true;
        else if(!rel.getName().equals(name))
            equals = false;

        if(rel.getSource()!= null && source == null) 
            equals = false;
        else if(rel.getSource() == null && source != null)
            equals = false;
        else if(rel.getSource() == null && source == null)
            equals = true;
        else if(!rel.getSource().equals(source))
            equals = false;
        
        if(rel.getTarget()!= null && target == null) 
            equals = false;
        else if(rel.getTarget() == null && target != null)
            equals = false;
        else if(rel.getTarget() == null && target == null)
            equals = true;
        else if(!rel.getTarget().equals(target))
            equals = false;
                        
        return equals;
    }*/

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.type);
        hash = 97 * hash + Objects.hashCode(this.id);
        hash = 97 * hash + Objects.hashCode(this.name);
        hash = 97 * hash + Objects.hashCode(this.source);
        hash = 97 * hash + Objects.hashCode(this.target);
        return hash;
    }

    
    /**
     * The ID Attribute is not checkt becouce ID can be differend
     * @param obj
     * @return 
     */
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
        return true;
    }
    
    
    
}
