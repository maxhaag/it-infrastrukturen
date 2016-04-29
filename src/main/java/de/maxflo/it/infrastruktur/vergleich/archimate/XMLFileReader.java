/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.maxflo.it.infrastruktur.vergleich.archimate;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.xml.sax.SAXException;

/**
 *
 * @author fnmh
 */
public class XMLFileReader {

    private final static int REF = 0;
    private final static int INST = 1;

    private final static int COLOR_RED = 2;
    private final static int COLOR_GREEN = 3;

    private final static int LINE_COL = 4;
    private final static int FILL_COL = 5;

    private ArrayList<String> refDoc = new ArrayList<>();
    private ArrayList<String> instDoc = new ArrayList<>();

    private ArrayList<Figure> refFig = new ArrayList<>();
    private ArrayList<Figure> instFig = new ArrayList<>();
    private ArrayList<Relation> refRel = new ArrayList<>();
    private ArrayList<Relation> instRel = new ArrayList<>();
    private ArrayList<Child> allRefChilds = new ArrayList<>();
    private ArrayList<Child> allInstChilds = new ArrayList<>();

    //rot
    private ArrayList<Figure> ref_fig_changes = new ArrayList<>();
    //grün
    private ArrayList<Figure> inst_fig_changes = new ArrayList<>();
    //rot
    private ArrayList<Relation> ref_rel_changes = new ArrayList<>();
    //grün
    private ArrayList<Relation> inst_rel_changes = new ArrayList<>();

    public static void main(String[] args) throws SAXException, IOException {

        XMLFileReader fReader = new XMLFileReader();

        fReader.readFiles();
        fReader.parseFigures();
        fReader.checkChanges();
        fReader.printSolutionToFile(fReader.buildSolution());

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
                    if (oneLine.contains(CHILDEND)) {
                        isInChild = false;
                        if (listType == INST) {
                            allInstChilds.add(child);
                            child = new Child();
                        } else {
                            allRefChilds.add(child);
                            child = new Child();
                        }
                    }

                }
            }

        }
    }

    private void patternSearchRelations(ArrayList<String> docList, int listType) {

        final String END_REL = "<folder name=\"Views\"";
        final String BEGIN_REL = "<folder name=\"Relations\"";
        final String ELSELECTOR = "element xsi";
        final String TYPEREGEX = "type=\"(.*?)\"";
        final String IDEREGEX = "id=\"(.*?)\"";
        final String NAMEPEREGEX = "name=\"(.*?)\"";
        final String SOURCEREX = "source=\"(.*?)\"";
        final String TARGETREX = "target=\"(.*?)\"";
        final String FOLDERNAME = "folder name";
        final String[] REGEXES = {TYPEREGEX, IDEREGEX, NAMEPEREGEX, SOURCEREX, TARGETREX};
        String folderName = "";
        boolean inRelations = false;
        for (int i = 0; i < docList.size(); i++) {
            String oneLine = docList.get(i);
            if (oneLine.contains(BEGIN_REL)) {
                inRelations = true;
            }
            if (inRelations) {
                if (oneLine.contains(FOLDERNAME)) {
                    Pattern pat = Pattern.compile(NAMEPEREGEX);
                    Matcher mat = pat.matcher(oneLine);
                    if (mat.find()) {
                        folderName = mat.group(1);
                    }
                }
                if (oneLine.contains(ELSELECTOR)) {
                    Relation toAdd = new Relation();
                    toAdd.setFolder(folderName);
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
                    toAdd.setLine(oneLine);
                    if (listType == INST) {
                        instRel.add(toAdd);
                    } else {
                        refRel.add(toAdd);
                    }
                }
                if (oneLine.contains(END_REL)) {
                    inRelations = false;
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

        final String FOLDERNAME = "folder name";
        final String ELSELECTOR = "element xsi";
        final String TYPEREGEX = "type=\"(.*?)\"";
        final String IDEREGEX = "id=\"(.*?)\"";
        final String NAMEPEREGEX = "name=\"(.*?)\"";
        final String[] REGEXES = {TYPEREGEX, IDEREGEX, NAMEPEREGEX};
        String folderName = "";
        boolean inFigures = true;
        for (int i = 0; i < docList.size(); i++) {
            String oneLine = docList.get(i);
            if (inFigures) {
                if (oneLine.contains(FOLDERNAME)) {
                    Pattern pat = Pattern.compile(NAMEPEREGEX);
                    Matcher mat = pat.matcher(oneLine);
                    if (mat.find()) {
                        folderName = mat.group(1);
                    }
                }
                if (oneLine.contains(ELSELECTOR)) {
                    Figure toAdd = new Figure();
                    toAdd.setFolder(folderName);
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
                    toAdd.setLine(oneLine);
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

    //Nur sources n childs nötig
    private void printSolutionToFile(ArrayList<String> solution) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("solution.archimate"));
            for (String s : solution) {
                bw.write(s);
                bw.newLine();
            }
            bw.flush();
            bw.close();

        } catch (IOException ex) {
            Logger.getLogger(XMLFileReader.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public ArrayList<String> buildSolution() {
        ArrayList<String> solutionDoc = instDoc;

        String relStart = "<folder name=\"Relations\"";
        String relEnd = "<folder name=\"Views\"";

        for (int i = 0; i < ref_fig_changes.size(); i++) {
            Figure f = ref_fig_changes.get(i);
            for (int j = 0; j < solutionDoc.size(); j++) {
                String oneSolLine = solutionDoc.get(j);
                boolean inFig = true;
                if (inFig) {
                    if (oneSolLine.contains(relStart)) {
                        inFig = false;
                    }
                    if (oneSolLine.contains("<folder name") && oneSolLine.contains(f.getFolder())) {
                        boolean idExist = false;
                        for (Figure figures : instFig) {
                            if (figures.getId().equals(f.getId())) {
                                idExist = true;
                                solutionDoc.add(j + 1, getLineWithOtherID(f.getLine()));
                            }
                        }
                        if (!idExist) {
                            solutionDoc.add(j + 1, f.getLine());
                        }
                    }

                }

            }
        }
        for (int i = 0; i < ref_rel_changes.size(); i++) {
            Relation r = ref_rel_changes.get(i);
            for (int j = 0; j < solutionDoc.size(); j++) {
                String oneSolLine = solutionDoc.get(j);
                boolean beginRel = false;
                if (oneSolLine.contains(relStart)) {
                    beginRel = true;
                }
                if (oneSolLine.contains(relEnd)) {
                    beginRel = false;
                }
                if (beginRel) {
                    if (oneSolLine.contains("<folder name") && oneSolLine.contains(r.getFolder())) {
                        boolean idExist = false;
                        for (Relation relation : instRel) {
                            if (relation.getId().equals(r.getId())) {
                                idExist = true;
                                solutionDoc.add(j + 1, getLineWithOtherID(r.getLine()));
                            }
                        }
                        if (!idExist) {
                            solutionDoc.add(j + 1, r.getLine());
                        }
                    }

                }

            }
        }
        for (int i = 0; i < inst_fig_changes.size(); i++) {
            Figure f = inst_fig_changes.get(i);
            for (int j = 0; j < solutionDoc.size(); j++) {
                String solutionLine = solutionDoc.get(j);
                if (solutionLine.contains("child xsi")) {
                    Pattern pat = Pattern.compile("archimateElement=\"(.*?)\"");
                    Matcher mat = pat.matcher(solutionLine);
                    if (mat.find()) {
                        if (mat.group(1).equals(f.getId())) {

                            if (solutionLine.contains("fillColor")) {
                                //solutionLine = solutionLine.replaceAll("fillColor=\"(.*?)\"", "fillColor=" + "\"#00ff00\"");
                                solutionLine = solutionLine.replaceAll("fillColor=\"(.*?)\"", "fillColor=" + "\"#00FF00\"");

                            } else {
                                solutionLine = getLineInsertLineColor(solutionLine, COLOR_GREEN, FILL_COL);
                            }
                            solutionDoc.add(j, solutionLine);
                            solutionDoc.remove(j + 1);
                        }
                    }
                }

            }
        }

        for (int i = 0; i < inst_rel_changes.size(); i++) {
            Relation r = inst_rel_changes.get(i);
            for (int j = 0; j < solutionDoc.size(); j++) {
                String solutionLine = solutionDoc.get(j);
                if (solutionLine.contains("sourceConnection xsi")) {
                    Pattern pat = Pattern.compile("relationship=\"(.*?)\"");
                    Matcher mat = pat.matcher(solutionLine);
                    if (mat.find()) {
                        if (mat.group(1).equals(r.getId())) {
                            if (solutionLine.contains("lineColor")) {
                                solutionLine = solutionLine.replaceAll("lineColor=\"(.*?)\"", "lineColor=" + "\"#00FF00\"");
                            } else {
                                solutionLine = getLineInsertLineColor(solutionLine, COLOR_GREEN, LINE_COL);
                            }
                            solutionDoc.add(j, solutionLine);
                            solutionDoc.remove(j + 1);
                        }
                    }
                }

            }
        }

        for (int i = 0; i < ref_fig_changes.size(); i++) {

            Figure f = ref_fig_changes.get(i);
            for (int j = 0; j < allRefChilds.size(); j++) {
                Child ch = allRefChilds.get(j);
                if (ch.getArchimateElementID().equals(f.getId())) {
                    for (int k = 0; k < solutionDoc.size(); k++) {
                        String solutionLine = solutionDoc.get(k);
                        boolean inView = false;
                        if (solutionLine.contains(relEnd)) {
                            inView = true;
                        }
                        if (inView) {
                            if (solutionLine.contains("element xsi")) {
                                String NAMEREGEX = "name=\"(.*?)\"";
                                Pattern pat = Pattern.compile(NAMEREGEX);
                                Matcher mat = pat.matcher(solutionLine);
                                if (mat.find()) {
                                    if (mat.group(1).equals(ch.getViewObjekt())) {

                                        ArrayList<String> childLines = ch.getchildlines();
                                        for (int l = childLines.size() - 1; l >= 0; l--) {
                                            //färben
                                            String oneChildLine = childLines.get(l);
                                            if (l == 0) {
                                                if (oneChildLine.contains("fillColor")) {
                                                    oneChildLine = oneChildLine.replaceAll("fillColor=\"(.*?)\"", "fillColor=" + "\"#FF0000\"");
                                                } else {
                                                    oneChildLine = getLineInsertLineColor(solutionLine, COLOR_RED, FILL_COL);
                                                }
                                            } else if (oneChildLine.contains("sourceConnection xsi")) {

                                                if (oneChildLine.contains("lineColor")) {
                                                    oneChildLine = oneChildLine.replaceAll("lineColor=\"(.*?)\"", "lineColor=" + "\"#FF0000\"");
                                                } else {
                                                    oneChildLine = getLineInsertLineColor(solutionLine, COLOR_RED, LINE_COL);
                                                }
                                            }
                                            solutionDoc.add(k + 1, oneChildLine);

                                        }

                                    }

                                }
                            }
                        }

                    }

                }
            }

        }

        return solutionDoc;
    }

    public String getLineWithOtherID(String line) {

        Pattern pat = Pattern.compile("id=\"(.*?)\"");
        Matcher mat = pat.matcher(line);
        if (mat.find()) {
            int i = Integer.parseInt(mat.group(1));
            line = line.replaceAll("id=\"(.*?)\"", "id=" + i * 1000);
        }

        return line;
    }

    public String getLineInsertLineColor(String line, int color, int type) {

        String colString = "";
        String fill = "";
        if (color == COLOR_GREEN) {
            colString = "\"#00FF00\"";
        } else if (color == COLOR_RED) {
            colString = "\"#FF0000\"";
        }

        if (type == FILL_COL) {
            fill = "fillColor";
        } else if (type == LINE_COL) {
            fill = "lineColor";
        }

        Pattern pat = Pattern.compile("id=\"(.*?)\"");
        Matcher mat = pat.matcher(line);
        if (mat.find()) {
            int i = Integer.parseInt(mat.group(1));
            line = line.replaceAll("id=\"(.*?)\"", "id=\"" + i + "\" " + fill + "=" + colString);
        }

        return line;
    }

}
