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
public class Folder {
    
    
    private String name;
    private String id;
    private boolean haselement;
    private ArrayList<String> folderLine;
    private ArrayList<String> previousFolder;

    public Folder() {
        previousFolder = new ArrayList<>();
        folderLine = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isHaselement() {
        return haselement;
    }

    public void setHaselement(boolean haselement) {
        this.haselement = haselement;
    }

    public ArrayList<String> getFolderLine() {
        return folderLine;
    }

    public void setFolderLine(ArrayList<String> folderLine) {
        this.folderLine = folderLine;
    }


    public ArrayList<String> getPreviousFolder() {
        return previousFolder;
    }

    public void setPreviousFolder(ArrayList<String> previousFolder) {
        this.previousFolder = previousFolder;
    }

    @Override
    public int hashCode() {
        int hash = 3;
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
        final Folder other = (Folder) obj;
        if (this.haselement != other.haselement) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }

    

    

   

    @Override
    public String toString() {
        return "Folder{" + "name=" + name + ", id=" + id + ", haselement=" + haselement + ", folderLine=" + folderLine + '}';
    }
    
    
}
