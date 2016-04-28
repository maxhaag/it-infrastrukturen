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
    ArrayList<Child> allRefChilds = new ArrayList<>();
    ArrayList<Child> allInstChilds = new ArrayList<>();
    

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

    private void patternSearchChileds(ArrayList<String> docList, int listType) {

        final String START_FIG = "<folder name=\"Views\"";
        final String ELSELECTOR = "element xsi";
        final String NAMEPEREGEX = "name=\"(.*?)\"";
        final String CHILDSTART = "child xsi";
        final String CHILDEND = "/child";
        final String ARCHIELEMENTID = "archimateElement=\"(.*?)\"";
        final String SOURCECONNECTION = "sourceConnection xsi";
        final String RELATIONSHIP = "relationship=\"(.*?)\"";

        String currentViewName = "";

        boolean inView = false;
        boolean isInChild = false;

        Child child = new Child();

        for (int i = 0; i < docList.size(); i++) {
            String oneLine = docList.get(i);
            if (oneLine.contains(START_FIG)) {
                inView = true;
            }
            if (inView) {
                if (oneLine.contains(ELSELECTOR)) {

                    Pattern pat = Pattern.compile(NAMEPEREGEX);
                    Matcher mat = pat.matcher(oneLine);
                    if (mat.find()) {
                        currentViewName = mat.group(1);
                    }
                    continue;
                }

                if (oneLine.contains(CHILDSTART)) {

                    Pattern pat = Pattern.compile(ARCHIELEMENTID);
                    Matcher mat = pat.matcher(oneLine);
                    if (mat.find()) {
                        child.setArchimateElementID(mat.group(1));
                        child.setViewObjekt(currentViewName);
                        child.getchildlines().add(oneLine);
                    }
                    isInChild = true;
                }
                if (isInChild) {
                    if (oneLine.contains(SOURCECONNECTION)) {
                        Pattern pat = Pattern.compile(RELATIONSHIP);
                        Matcher mat = pat.matcher(oneLine);
                        if (mat.find()) {
                            child.getRelationshipIDs().add(mat.group(1));
                        }
                        
                    }
                    child.getchildlines().add(oneLine);
                    if(oneLine.contains(CHILDEND)){
                        isInChild=false;
                        if(listType == INST){
                            allInstChilds.add(child);
                            child = new Child();
                        }else{
                            allRefChilds.add(child);
                            child = new Child();
                        }
                    }
                    
                }
            }
            
        }
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
                        String source = "";
                        if (listType == INST) {
                            source = findNameOfFigureByIDFromInstList(mat.group(1));
                        } else {
                            source = findNameOfFigureByIDFromRefList(mat.group(1));
                        }
                        toAdd.setSource(source);
                    }
                    pat = Pattern.compile(REGEXES[4]);
                    mat = pat.matcher(oneLine);
                    if (mat.find()) {
                        String target = "";
                        if (listType == INST) {
                            target = findNameOfFigureByIDFromInstList(mat.group(1));
                        } else {
                            target = findNameOfFigureByIDFromRefList(mat.group(1));
                        }
                        toAdd.setTarget(target);
                    }
                    if (listType == INST) {
                        instRel.add(toAdd);
                    } else {
                        refRel.add(toAdd);
                    }
                }
                if (oneLine.contains(END_FIG)) {
                    inFigures = false;
                    break;
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
        inst_fig_changes = (ArrayList<Figure>) createChangeList(instFig, refFig);
        ref_rel_changes = (ArrayList<Relation>) createChangeList(refRel, instRel);
        inst_rel_changes = (ArrayList<Relation>) createChangeList(instRel, refRel);

        //Debug
        print("Ref Figure");
        for (int i = 0; i < ref_fig_changes.size(); i++) {
            print(ref_fig_changes.get(i).getType());
            print(ref_fig_changes.get(i).getId());
            print(ref_fig_changes.get(i).getName());
        }
        print("---------------");
        print("Inst Figure");
        for (int i = 0; i < inst_fig_changes.size(); i++) {
            print(inst_fig_changes.get(i).getType());
            print(inst_fig_changes.get(i).getId());
            print(inst_fig_changes.get(i).getName());
        }
        print("---------------");
        print("Ref Rel");
        for (int i = 0; i < ref_rel_changes.size(); i++) {
            print("Type " + ref_rel_changes.get(i).getType());
            print("ID " + ref_rel_changes.get(i).getId());
            print("Name" + ref_rel_changes.get(i).getName());
            print("Source " + ref_rel_changes.get(i).getSource());
            print("Target " + ref_rel_changes.get(i).getTarget());

        }
        print("---------------");
        print("Inst Rel");
        for (int i = 0; i < inst_rel_changes.size(); i++) {
            print("Type " + inst_rel_changes.get(i).getType());
            print("ID " + inst_rel_changes.get(i).getId());
            print("Name " + inst_rel_changes.get(i).getName());
            print("Source " + ref_rel_changes.get(i).getSource());
            print("Target " + ref_rel_changes.get(i).getTarget());
        }

    }

    private void print(String toPrint) {
        System.out.println(toPrint);
    }

    private ArrayList<?> createChangeList(ArrayList<?> refList, ArrayList<?> instList) {
        //Ref Figs -- Inst Figs / CIS -- CRS
        ArrayList<? super Object> changeList = new ArrayList<>();
        for (int i = 0; i < refList.size(); i++) {
            boolean changed = true;
            Object oneRef = refList.get(i);
            for (int j = 0; j < instList.size(); j++) {
                Object oneInst = instList.get(j);
                if (oneRef.equals(oneInst)) {
                    changed = false;
                }
            }
            if (changed) {
                changeList.add(oneRef);
            }
        }
        return changeList;
    }

    private String findNameOfFigureByIDFromInstList(String id) {

        for (Figure f : instFig) {
            if (f.getId().equals(id)) {
                return f.getName();
            }
        }

        return "";

    }

    private String findNameOfFigureByIDFromRefList(String id) {

        for (Figure f : refFig) {
            if (f.getId().equals(id)) {
                return f.getName();
            }
        }

        return "";

    }

    private ArrayList<String> findViewChiledByFigureID(String id, String listType) {

        ArrayList<String> child = new ArrayList<>();

        if (listType.equals("INST")) {

            for (String s : instDoc) {

            }

        } else {
            for (String s : refDoc) {

            }
        }

        return child;
    }
}
