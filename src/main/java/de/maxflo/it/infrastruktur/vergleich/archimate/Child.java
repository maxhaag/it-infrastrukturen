/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.maxflo.it.infrastruktur.vergleich.archimate;

import java.util.ArrayList;
import java.util.Objects;

/**
 * @author 
 * 
 * Florian Neuner
 * Maximilian Haag
 *    
 */
public class Child {
    
    private String childeID;
    private String archimateElementID;
    private ArrayList<String> previousChilds;
    private ArrayList<String> relationshipIDs;
    private ArrayList<String> childlines;
    private String viewObjekt;

    public Child() {
        relationshipIDs = new ArrayList<>();
        childlines = new ArrayList<>();
        previousChilds = new ArrayList<>();
    }

    public String getChildeID() {
        return childeID;
    }

    public void setChildeID(String childeID) {
        this.childeID = childeID;
    }

    public String getArchimateElementID() {
        return archimateElementID;
    }

    public void setArchimateElementID(String archimateElementID) {
        this.archimateElementID = archimateElementID;
    }

    public ArrayList<String> getRelationshipIDs() {
        return relationshipIDs;
    }

    public void setRelationshipIDs(ArrayList<String> relationshipIDs) {
        this.relationshipIDs = relationshipIDs;
    }

    public ArrayList<String> getChildlines() {
        return childlines;
    }

    public void setChildlines(ArrayList<String> childlines) {
        this.childlines = childlines;
    }

    public String getViewObjekt() {
        return viewObjekt;
    }

    public void setViewObjekt(String viewObjekt) {
        this.viewObjekt = viewObjekt;
    }

    public ArrayList<String> getPreviousChilds() {
        return previousChilds;
    }

    public void setPreviousChilds(ArrayList<String> previousChilds) {
        this.previousChilds = previousChilds;
    }

    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.archimateElementID);
        hash = 53 * hash + Objects.hashCode(this.relationshipIDs);
        hash = 53 * hash + Objects.hashCode(this.childlines);
        hash = 53 * hash + Objects.hashCode(this.viewObjekt);
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
        final Child other = (Child) obj;
        if (!Objects.equals(this.archimateElementID, other.archimateElementID)) {
            return false;
        }
        if (!Objects.equals(this.viewObjekt, other.viewObjekt)) {
            return false;
        }
        if (!Objects.equals(this.relationshipIDs, other.relationshipIDs)) {
            return false;
        }
        if (!Objects.equals(this.childlines, other.childlines)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Child{" + "childeID=" + childeID + ", archimateElementID=" + archimateElementID + ", relationshipIDs=" + relationshipIDs + ", childlines=" + childlines + ", viewObjekt=" + viewObjekt + '}';
    }

   
    
}
