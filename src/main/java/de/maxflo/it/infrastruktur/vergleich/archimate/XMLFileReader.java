/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.maxflo.it.infrastruktur.vergleich.archimate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import org.xml.sax.SAXException;

/**
 *
 * @author fnmh
 */
public class XMLFileReader {


    
    ArrayList<String> refDoc = new ArrayList<>();
    ArrayList<String> instDoc = new ArrayList<>();
    
    ArrayList<Figures> refFig = new ArrayList<>();
    ArrayList<Figures> instFig = new ArrayList<>();
    ArrayList<Relations> refRel = new ArrayList<>();
    ArrayList<Relations> instRel = new ArrayList<>();
    
    
    
    public static void main (String [] args) throws SAXException, IOException {
        XMLFileReader fReader = new XMLFileReader();
        fReader.readFiles();
        fReader.parseFigures();
        fReader.parseRelations();
        
    }
        

    private void readFiles()  throws SAXException, IOException {
        String refFile = "Archisurance_BusinessCorpV_Mod-CustInfoServ.archimate";
        String instFile = "Archisurance_BusinessCorpV_Mod-ClaimRegServ.archimate";
        
        
        BufferedReader bRef = new BufferedReader(new FileReader(refFile));
        BufferedReader bIns = new BufferedReader(new FileReader(instFile));
        
        String oneLine = "";
        while((oneLine = bRef.readLine()) != null) {
            refDoc.add(oneLine);
        }
        while((oneLine = bIns.readLine()) != null) {
            instDoc.add(oneLine);
        }
        
        bRef.close();
        bIns.close();

    }
    
    private void parseFigures() {
        
    }

    private void parseRelations() {
    }
    
    
    
}
