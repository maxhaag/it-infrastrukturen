/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.maxflo.it.infrastruktur.vergleich.archimate;

import com.sun.xml.internal.ws.util.StringUtils;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.xml.sax.SAXException;

/**
 *
 * @author fnmh
 */
public class XMLFileReader {

    final static int REF = 0;
    final static int INST = 1;
    
    ArrayList<String> refDoc = new ArrayList<>();
    ArrayList<String> instDoc = new ArrayList<>();
    
    ArrayList<Figure> refFig = new ArrayList<>();
    ArrayList<Figure> instFig = new ArrayList<>();
    ArrayList<Relation> refRel = new ArrayList<>();
    ArrayList<Relation> instRel = new ArrayList<>();
    
    
    
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
        
        patternSearch(refDoc, REF);
        patternSearch(instDoc, INST);

        //TEST...ok
        for(int i = 0; i<refFig.size(); i++) {
            //print(refFig.get(i).getType());
            //print(refFig.get(i).getId());
            //print(refFig.get(i).getName());
        }
         for(int i = 0; i<instFig.size(); i++) {
            //print(instFig.get(i).getType());
            //print(instFig.get(i).getId());
            //print(instFig.get(i).getName());
        }
    }

    private void patternSearch(ArrayList<String> docList, int listType) {
        //Stopp bei:
        //<folder name="Relations" id="408ff6d3" type="relations">
        //regexr.com
        final String END_FIG = "<folder name=\"Relations\"";
       
        final String ELSELECTOR = "element xsi";
        final String TYPEREGEX = "type=\"(.*?)\"";
        final String IDEREGEX = "id=\"(.*?)\"";
        final String NAMEPEREGEX = "name=\"(.*?)\"";
        final String [] REGEXES = {TYPEREGEX,IDEREGEX,NAMEPEREGEX};
      
        boolean inFigures = true;
        for(int i = 0; i<docList.size(); i++) {
            String oneLine = docList.get(i); 
            if(inFigures) {
                if(oneLine.contains(ELSELECTOR)) {
                    Figure toAdd = new Figure();
                    Pattern pat = Pattern.compile(TYPEREGEX);
                    Matcher mat = pat.matcher(oneLine);
                    if(mat.find())
                        toAdd.setType(mat.group(1));
                    pat = Pattern.compile(REGEXES[1]);
                    mat = pat.matcher(oneLine);
                    if(mat.find())
                        toAdd.setId(mat.group(1));
                    pat = Pattern.compile(REGEXES[2]);
                    mat = pat.matcher(oneLine);
                    if(mat.find())
                        toAdd.setName(mat.group(1));
                    if(listType == INST)
                        instFig.add(toAdd);
                    else
                        refFig.add(toAdd);
                }
            }
            if(oneLine.contains(END_FIG)) {
                inFigures = false;
            }
        }
    }

    private void print(String toPrint) {
        System.out.println(toPrint);
    }
        
   
        
    private void parseRelations() {
        
        
    }
    
    
    



    
    
    
}
