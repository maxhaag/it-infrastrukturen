/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.maxflo.it.infrastruktur.vergleich.archimate;

import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Logger;

/**
 *
 * @author Max
 */
public class Figure {

    private String type;
    private String id;
    private String name;
    private String folder;
    private String line;
    private ArrayList<String> documentation;

  
    public Figure() {
        documentation = new ArrayList<>();
    }

    public ArrayList<String> getDocumentation() {
        return documentation;
    }

    public void setDocumentation(ArrayList<String> documentation) {
        this.documentation = documentation;
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
        int hash = 5;
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
        final Figure other = (Figure) obj;
        if (!Objects.equals(this.type, other.type)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.folder, other.folder)) {
            return false;
        }
        return true;
    }

   

    @Override
    public String toString() {
        return "Figure{" + "type=" + type + ", id=" + id + ", name=" + name + ", folder=" + folder + ", line=" + line + ", documentation=" + documentation + '}';
    }

   
  


    
   

}
