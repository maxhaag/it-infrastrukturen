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

    public static void main(String[] args) throws SAXException, IOException {
        XMLFileReader fReader = new XMLFileReader();
        fReader.readFiles();
        fReader.parseFigures();
        fReader.checkChanges();
        
 

    }

    private void readFiles() throws SAXException, IOException {
        String refFile = "Archisurance_BusinessCorpV_Mod-CustInfoServ.archimate";
        String instFile = "Archisurance_BusinessCorpV_Mod-ClaimRegServ.archimate";

        BufferedReader bRef = new BufferedReader(new FileReader(refFile));
        BufferedReader bIns = new BufferedReader(new FileReader(instFile));

        String oneLine = "";
        while ((oneLine = bRef.readLine()) != null) {
            refDoc.add(oneLine);
        }
        while ((oneLine = bIns.readLine()) != null) {
            instDoc.add(oneLine);
        }

        bRef.close();
        bIns.close();

    }

    private void parseFigures() {

        patternSearchFigures(refDoc, REF);
        patternSearchFigures(instDoc, INST);
        patternSearchRelations(refDoc, REF);
        patternSearchRelations(instDoc, INST);
    }

    private void patternSearchRelations(ArrayList<String> docList, int listType) {

        final String END_FIG = "<folder name=\"Views\"";
        final String ELSELECTOR = "element xsi";
        final String TYPEREGEX = "type=\"(.*?)\"";
        final String IDEREGEX = "id=\"(.*?)\"";
        final String NAMEPEREGEX = "name=\"(.*?)\"";
        final String SOURCEREX = "source=\"(.*?)\"";
        final String TARGETREX = "target=\"(.*?)\"";
        final String[] REGEXES = {TYPEREGEX, IDEREGEX, NAMEPEREGEX, SOURCEREX, TARGETREX};

        boolean inFigures = true;
        for (int i = 0; i < docList.size(); i++) {
            String oneLine = docList.get(i);
            if (inFigures) {
                if (oneLine.contains(ELSELECTOR)) {
                    Relation toAdd = new Relation();
                    Pattern pat = Pattern.compile(TYPEREGEX);
                    Matcher mat = pat.matcher(oneLine);
                    if (mat.find()) {
                        toAdd.setType(mat.group(1));
                    }
                    pat = Pattern.compile(REGEXES[1]);
                    mat = pat.matcher(oneLine);
                    if (mat.find()) {
                        toAdd.setId(mat.group(1));
                    }
                    pat = Pattern.compile(REGEXES[2]);
                    mat = pat.matcher(oneLine);
                    if (mat.find()) {
                        toAdd.setName(mat.group(1));
                    }
                    pat = Pattern.compile(REGEXES[3]);
                    mat = pat.matcher(oneLine);
                    if (mat.find()) {
                        toAdd.setSource(mat.group(1));
                    }
                    pat = Pattern.compile(REGEXES[4]);
                    mat = pat.matcher(oneLine);
                    if (mat.find()) {
                        toAdd.setTarget(mat.group(1));
                    }
                    if (listType == INST) {
                        instRel.add(toAdd);
                    } else {
                        refRel.add(toAdd);
                    }
                }
                if (oneLine.contains(END_FIG)) {
                    inFigures = false;
                }
            }
        }
    }

    private void patternSearchFigures(ArrayList<String> docList, int listType) {
        //Stopp bei:
        //<folder name="Relations" id="408ff6d3" type="relations">
        //regexr.com
        final String END_FIG = "<folder name=\"Relations\"";

        final String ELSELECTOR = "element xsi";
        final String TYPEREGEX = "type=\"(.*?)\"";
        final String IDEREGEX = "id=\"(.*?)\"";
        final String NAMEPEREGEX = "name=\"(.*?)\"";
        final String[] REGEXES = {TYPEREGEX, IDEREGEX, NAMEPEREGEX};

        boolean inFigures = true;
        for (int i = 0; i < docList.size(); i++) {
            String oneLine = docList.get(i);
            if (inFigures) {
                if (oneLine.contains(ELSELECTOR)) {
                    Figure toAdd = new Figure();
                    Pattern pat = Pattern.compile(REGEXES[0]);
                    Matcher mat = pat.matcher(oneLine);
                    if (mat.find()) {
                        toAdd.setType(mat.group(1));
                    }
                    pat = Pattern.compile(REGEXES[1]);
                    mat = pat.matcher(oneLine);
                    if (mat.find()) {
                        toAdd.setId(mat.group(1));
                    }
                    pat = Pattern.compile(REGEXES[2]);
                    mat = pat.matcher(oneLine);
                    if (mat.find()) {
                        toAdd.setName(mat.group(1));
                    }
                    if (listType == INST) {
                        instFig.add(toAdd);
                    } else {
                        refFig.add(toAdd);
                    }
                }
            }
            if (oneLine.contains(END_FIG)) {
                inFigures = false;
            }
        }
    }
    
    
    private void checkChanges() {
        
        //grün
        ArrayList<Figure> ref_fig_changes = new ArrayList<>();
        //rot
        ArrayList<Figure> inst_fig_changes = new ArrayList<>();
       
        ArrayList<Relation> ref_rel_changes = new ArrayList<>();
        ArrayList<Relation> inst_rel_changes = new ArrayList<>();
        
        ref_fig_changes = (ArrayList<Figure>) createChangeList(refFig, instFig);
        inst_fig_changes = (ArrayList<Figure>) createChangeList(instFig, refFig );
        ref_rel_changes = (ArrayList<Relation>) createChangeList(refRel, instRel);
        inst_rel_changes = (ArrayList<Relation>) createChangeList(instRel, refRel);
        
        

        //Debug
        for (int i = 0; i < ref_fig_changes.size(); i++) {
            //print(ref_fig_changes.get(i).getType());
            //print(ref_fig_changes.get(i).getId());
            //print(ref_fig_changes.get(i).getName());
        }
        print("---------------");
        
        for (int i = 0; i < inst_fig_changes.size(); i++) {
            //print(inst_fig_changes.get(i).getType());
            //print(inst_fig_changes.get(i).getId());
            //print(inst_fig_changes.get(i).getName());
        }
        print("---------------");
        for (int i = 0; i < ref_rel_changes.size(); i++) {
            print(ref_rel_changes.get(i).getType());
            print(ref_rel_changes.get(i).getId());
            print(ref_rel_changes.get(i).getName());
            print(ref_rel_changes.get(i).getSource());
            print(ref_rel_changes.get(i).getTarget());

        }
        print("---------------");
        
        for (int i = 0; i < inst_rel_changes.size(); i++) {
            print(inst_rel_changes.get(i).getType());
            print(inst_rel_changes.get(i).getId());
            print(inst_rel_changes.get(i).getName());
            print(ref_rel_changes.get(i).getSource());
            print(ref_rel_changes.get(i).getTarget());
        }

    }
    
    
    

    private void print(String toPrint) {
        System.out.println(toPrint);
    }

    private ArrayList<?> createChangeList(ArrayList<?> refList, ArrayList<?> instList ) {
        //Ref Figs -- Inst Figs / CIS -- CRS
       ArrayList<? super Object> changeList = new ArrayList<>();
        for(int i = 0; i<refList.size(); i++) {
            boolean changed = true;
            Object oneRef = refList.get(i);
            for(int j = 0; j<instList.size(); j++) {
                Object oneInst = instList.get(j);
                if(oneRef.equals(oneInst)) {
                    changed = false;
                }
            }
            if(changed) {
                changeList.add(oneRef);
            }
        }
        return changeList;
    }



}
