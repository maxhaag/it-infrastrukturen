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
public class Child {
    
    
    private String archimateElementID;
    private ArrayList<String> relationshipIDs;
    private ArrayList<String> childlines;
    private String viewObjekt;

    public Child() {
        relationshipIDs = new ArrayList<>();
        childlines = new ArrayList<>();
    }

    public Child(String archimateElementID, ArrayList<String> relationshipIDs, ArrayList<String> childlines, String viewObjekt) {
        this.archimateElementID = archimateElementID;
        this.relationshipIDs = relationshipIDs;
        this.childlines = childlines;
        this.viewObjekt = viewObjekt;
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

    public ArrayList<String> getchildlines() {
        return childlines;
    }

    public void setchildlines(ArrayList<String> childlines) {
        this.childlines = childlines;
    }

    public String getViewObjekt() {
        return viewObjekt;
    }

    public void setViewObjekt(String viewObjekt) {
        this.viewObjekt = viewObjekt;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.archimateElementID);
        hash = 59 * hash + Objects.hashCode(this.relationshipIDs);
        hash = 59 * hash + Objects.hashCode(this.childlines);
        hash = 59 * hash + Objects.hashCode(this.viewObjekt);
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
        return "Child{" + "archimateElementID=" + archimateElementID + ", relationshipIDs=" + relationshipIDs + ", childlines=" + childlines + ", viewObjekt=" + viewObjekt + '}';
    }
    
    
}
