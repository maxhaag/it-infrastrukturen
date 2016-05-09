/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.maxflo.it.infrastruktur.vergleich.archimate;

import java.util.Objects;

/**
 *
 * @author fn
 */
public class SourceConnection {
    
    
    private String id;
    private String connectionLine;
    private String target;
    private String childID;
    private String relationship;

    public SourceConnection() {
    }

  

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConnectionLine() {
        return connectionLine;
    }

    public void setConnectionLine(String connectionLine) {
        this.connectionLine = connectionLine;
    }

    public String getChildID() {
        return childID;
    }

    public void setChildID(String childID) {
        this.childID = childID;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return "SourceConnection{" + "id=" + id + ", connectionLine=" + connectionLine + ", target=" + target + ", childID=" + childID + ", relationship=" + relationship + '}';
    }

    
    
    
}
