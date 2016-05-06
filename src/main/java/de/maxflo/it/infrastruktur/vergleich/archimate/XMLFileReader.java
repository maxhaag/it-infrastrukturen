/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.maxflo.it.infrastruktur.vergleich.archimate;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private ArrayList<SourceConnection> allRefSourceConnections = new ArrayList<>();
    private ArrayList<SourceConnection> allInstSourceConnections = new ArrayList<>();

    //rot
    private ArrayList<Figure> ref_fig_changes = new ArrayList<>();
    //grün
    private ArrayList<Figure> inst_fig_changes = new ArrayList<>();

    //rot
    private ArrayList<Relation> ref_rel_changes = new ArrayList<>();
    //grün
    private ArrayList<Relation> inst_rel_changes = new ArrayList<>();

    //Benötigt für GUI Elemente
    private static boolean startGui = false;
    private String refFileStr = "Archisurance_BusinessCorpV_Mod-CustInfoServ.archimate";
    private String instFileStr = "Archisurance_BusinessCorpV_Mod-ClaimRegServ.archimate";
    private File refFile = new File(refFileStr);
    private File instFile = new File(instFileStr);

    public static void main(String[] args) {

        XMLFileReader fReader = new XMLFileReader();

        if (startGui) {
            GuiWindow gui = new GuiWindow(fReader);
            gui.setVisible(true);
        }

        //Später von GUI aus aufrufen...
        fReader.readFiles();
        fReader.parseFigures();
        fReader.checkChanges();
        fReader.printSolutionToFile(fReader.buildSolution());

    }

    /**
     * This Method read the two Files wich are will be compared. The refDoc File
     * is the Reference for the other File The instDoc File ist the Instance
     * wich is created with Reference as Template
     */
    private void readFiles() {

        BufferedReader bRef = null;
        BufferedReader bIns = null;
        try {
            bRef = new BufferedReader(new FileReader(refFile));
            bIns = new BufferedReader(new FileReader(instFile));
            String oneLine = "";
            while ((oneLine = bRef.readLine()) != null) {
                refDoc.add(oneLine);
            }
            while ((oneLine = bIns.readLine()) != null) {
                instDoc.add(oneLine);
            }
            bRef.close();
            bIns.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(XMLFileReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(XMLFileReader.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                bRef.close();
                bIns.close();
            } catch (IOException ex) {
                Logger.getLogger(XMLFileReader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    /**
     * This Method Parse severale Differences from the refDoc and instDoc lists.
     *
     */
    private void parseFigures() {

        //Erzeugt eine Liste in welcher die Figures aus der refDoc liste sind welche nicht in der instDoc liste zu finden sind. In der Solution dann Rot eingefärbt.
        patternSearchFigures(refDoc, REF);
        //Erzeugt eine Liste in welcher die Figures aus der instDoc liste sind welche nicht in der refDoc liste zu finden sind. In der Solution dann Grün eingefärbt.
        patternSearchFigures(instDoc, INST);
        //Erzeugt eine Liste in welcher die Relations aus der refDoc liste sind welche nicht in der instDoc liste zu finden sind. In der Solution dann Rot eingefärbt.
        patternSearchRelations(refDoc, REF);
        //Erzeugt eine Liste in welcher die Relations aus der instDoc liste sind welche nicht in der refDoc liste zu finden sind. In der Solution dann Grün eingefärbt.
        patternSearchRelations(instDoc, INST);
        //Erzeugt eine Liste aller Childs aus der refDoc Liste. Um Später die Childs heraus zu filtern welche nicht in der instDoc Liste vorhanden sind. In der Solution dann Rot eingefärbt.
        patternSearchChilds(refDoc, REF);
        //Erzeugt eine Liste aller Childs aus der instDoc Liste.
        patternSearchChilds(instDoc, INST);
        //Erzeugt eine Liste aller SourceConnection aus der refDoc Liste um die SourceConnection in die SolutionListe zu schreiben welche nicht in der instDoc Liste vorhanden sind.
        patternSearchSourceConnection(refDoc, REF);
        //Noch nicht benutzt bisher.
        patternSearchSourceConnection(instDoc, INST);

    }

    /**
     *
     * DEBUG_UPDATE (MAX) 06.05.2016: ViewElement bzw. "name" nach Views NULL.
     * Sollte aber auch so gehen da wir die ja nicht brauchen...
     *
     * Reads all Childs from xml Archimate List
     *
     * @param docList StingArray from Archimate XML List
     * @param listType Typ REF or INST, REF writes the allRefChilds and INST the
     * allInstChilds List.
     */
    private void patternSearchChilds(ArrayList<String> docList, int listType) {

        //String regex für verschiedene vergleiche von String Lines
        final String START_FIG = "<folder name=\"Views\"";
        final String ELSELECTOR = "element xsi";
        final String VIEW_NAME_REGEX = "name=\"(.*?)\"";
        final String CHILDSTART = "child xsi";
        final String CHILDEND = "/child";
        final String ARCHIELEMENTID = "archimateElement=\"(.*?)\"";
        final String SOURCECONNECTION = "sourceConnection xsi";
        final String RELATIONSHIP = "relationship=\"(.*?)\"";

        //Um in einem Child zu Speichern zu welcher View dieses gehört
        String currentViewName = "";

        //Zählt das lesen von Child lines um die richtige anzahl von Child Ende lines zu erzeugen und weit genug zu lesen.
        int childCounter = 0;

        //beginnt erst zu lesen wenn die View in der xml begonnen hat
        boolean inView = false;

        //Liest weiter sobald ein Child beginnt um die einzelnen Childs komplett zu lesen.
        boolean isInChild = false;

        Child child = new Child();

        //Gesamtes Doc.
        for (int i = 0; i < docList.size(); i++) {
            String oneLine = docList.get(i);

            //Prüft ob die View startet
            if (oneLine.contains(START_FIG)) {
                inView = true;
            }

            //Sobald in der View
            if (inView) {
                //Prüft ob eine neue View startet und schreibt den wert in currentViewname
                if (oneLine.contains(ELSELECTOR)) {

                    Pattern pat = Pattern.compile(VIEW_NAME_REGEX);
                    Matcher mat = pat.matcher(oneLine);
                    if (mat.find()) {
                        String viewElement = mat.group(1);
                        currentViewName = viewElement;
                    }
                    continue;
                }

                //Prüft ob ein Child startet
                if (oneLine.contains(CHILDSTART)) {
                    childCounter++;

                    Pattern idPat = Pattern.compile(ARCHIELEMENTID);
                    Matcher idMat = idPat.matcher(oneLine);
                    if (idMat.find()) {
                        String archiMateElementId = idMat.group(1);
                        child.setArchimateElementID(archiMateElementId);
                        child.setViewObjekt(currentViewName);
                    }

                    //child.setViewObjekt(currentViewName);
                    isInChild = true;
                }

                //Prüft ob man sich gerade in einem Child befindet
                if (isInChild) {
                    if (oneLine.contains(SOURCECONNECTION)) {
                        Pattern pat = Pattern.compile(RELATIONSHIP);
                        Matcher mat = pat.matcher(oneLine);
                        if (mat.find()) {
                            child.getRelationshipIDs().add(mat.group(1));
                        }

                    }

                    child.getchildlines().add(oneLine);

                    //Sobald ein Childende tag auftaucht wird überprüft ob es das letzte in diesem Child ist und falls ja wird in die jeweilige nach Typdefinierte Liste geschrieben.
                    if (oneLine.contains(CHILDEND)) {
                        childCounter--;
                        if (childCounter <= 0) {
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
    }

    /**
     * Reads all SourceConnection from XML Archimate List
     *
     * @param docList StingArray from Archimate XML List
     * @param listType Typ REF or INST, REF writes the allRefSourceConnections
     * and INST the allInstSourceConnections List.
     */
    private void patternSearchSourceConnection(ArrayList<String> docList, int listType) {

        //String regex für verschiedene vergleiche von String Lines
        final String START_FIG = "<folder name=\"Views\"";
        final String IDPEREGEX = "id=\"(.*?)\"";
        final String SOURCESTART = "sourceConnection xsi";
        final String RELATIONSHIP = "relationship=\"(.*?)\"";
        final String CHILDSTART = "child xsi";

        //Speichert die Childe ID in welcher man sich gerade befindet
        String childeID = "";

        //beginnt erst zu lesen wenn die View in der xml begonnen hat
        boolean inView = false;

        SourceConnection source = new SourceConnection();

        for (int i = 0; i < docList.size(); i++) {
            String oneLine = docList.get(i);
            if (oneLine.contains(START_FIG)) {
                inView = true;
            }
            if (inView) {

                if (oneLine.contains(CHILDSTART)) {

                    Pattern pat = Pattern.compile(IDPEREGEX);
                    Matcher mat = pat.matcher(oneLine);
                    if (mat.find()) {
                        childeID = mat.group(1);
                    }
                    continue;
                }

                if (oneLine.contains(SOURCESTART)) {

                    Pattern pat = Pattern.compile(IDPEREGEX);
                    Matcher mat = pat.matcher(oneLine);
                    if (mat.find()) {
                        source.setId(mat.group(1));
                        source.setConnectionLine(oneLine);
                        source.setChildID(childeID);
                        Pattern patRel = Pattern.compile(RELATIONSHIP);
                        Matcher matRel = patRel.matcher(oneLine);
                        if (matRel.find()) {
                            source.setRelationship(matRel.group(1));
                        }
                    }
                    if (listType == INST) {
                        allInstSourceConnections.add(source);
                        source = new SourceConnection();

                    } else {
                        allRefSourceConnections.add(source);
                        source = new SourceConnection();

                    }
                }

            }

        }
    }

    /**
     *
     * @param docList StingArray from Archimate XML List
     * @param listType Typ REF or INST, REF writes the refRel and INST the
     * instRel List.
     */
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

    /**
     *
     * @param docList StingArray from Archimate XML List
     * @param listType Typ REF or INST, REF writes the refFig and INST the
     * instFig List.
     */
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

    /**
     * Writes some differend Changes betwenn ref and inst compared by oneInstFig
     * and relations
     */
    private void checkChanges() {
        ref_fig_changes = (ArrayList<Figure>) createChangeList(refFig, instFig);
        inst_fig_changes = (ArrayList<Figure>) createChangeList(instFig, refFig);
        ref_rel_changes = (ArrayList<Relation>) createChangeList(refRel, instRel);
        inst_rel_changes = (ArrayList<Relation>) createChangeList(instRel, refRel);
    }

    /**
     * Print a Line of String so Console for Debugging oder Logging
     *
     * @param toPrint
     */
    public void print(String toPrint) {
        System.out.println(toPrint);
    }

    /**
     * Create an Change List of two ArrayLists comparte by equals Method of a
     * Class
     *
     * @param refList
     * @param instList
     * @return
     */
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

    /**
     *
     * @param id
     * @return
     */
    private String findNameOfFigureByIDFromInstList(String id) {
        for (Figure f : instFig) {
            if (f.getId().equals(id)) {
                return f.getName();
            }
        }
        return "";
    }

    /**
     *
     * @param id
     * @return
     */
    private String findNameOfFigureByIDFromRefList(String id) {
        for (Figure f : refFig) {
            if (f.getId().equals(id)) {
                return f.getName();
            }
        }
        return "";
    }

    /**
     *
     * @param solution
     */
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

    /**
     * Build the Solution doc after reading Reference and Instance, you need
     * first get the Changes Lists and the differend compare Lists. readFiles();
     * parseFigures(); checkChanges(); in this order
     *
     * @return
     */
    public ArrayList<String> buildSolution() {

        //Zu beginn wird zunächst das solutionDoc auf das InstDoc gesetzt und mit Informationen angereichert
        ArrayList<String> solutionDoc = instDoc;

        String relStart = "<folder name=\"Relations\"";
        String relEnd = "<folder name=\"Views\"";

        //Fehlende Ref Figures in DOC in richtigen "folder" einfügen
        //Zunächst werden alle Figurs aus der ref_Fig_change (alle Figurs welche nicht in der inst liste aber in der ref liste sind) Liste in das Solution doc mit eingefügt 
        for (int i = 0; i < ref_fig_changes.size(); i++) {
            Figure oneRefFig = ref_fig_changes.get(i);

            boolean inFig = true;
            for (int solLineCount = 0; solLineCount < solutionDoc.size(); solLineCount++) {
                String oneSolLine = solutionDoc.get(solLineCount);

                //Start bei Instanz Figures, Ende bei Instanz Relations
                if (inFig) {
                    if (oneSolLine.contains(relStart)) {
                        inFig = false;
                    }
                    if (oneSolLine.contains("<folder name") && oneSolLine.contains(oneRefFig.getFolder())) {
                        boolean idExist = false;
                        //Prüfen ob IDs aus Instanz und Referenz gleich (bereits da), ansonsten ID anpassen und hinzufügen
                        for (Figure oneInstFig : instFig) {
                            if (oneInstFig.getId().equals(oneRefFig.getId())) {
                                idExist = true;
                                solutionDoc.add(solLineCount + 1, getLineWithOtherID(oneRefFig.getLine()));

                            }
                        }
                        //Id noch nicht vorhanden, also Änderung einfach so adden
                        if (!idExist) {
                            solutionDoc.add(solLineCount + 1, oneRefFig.getLine());
                        }
                    }

                }

            }
        }

        //REF Relations
        //Schreibt alle Relations aus der ref_rel_changes (alle Relations welche in ref aber nicht in inst sind) in die Solution Doc an die richtige Stelle
        for (int i = 0; i < ref_rel_changes.size(); i++) {
            Relation oneRefRel = ref_rel_changes.get(i);

            boolean beginRel = false;
            for (int j = 0; j < solutionDoc.size(); j++) {
                String oneSolLine = solutionDoc.get(j);

                //Start bei "Relations"
                if (oneSolLine.contains(relStart)) {
                    beginRel = true;
                }
                //Ende bei "Views"
                if (oneSolLine.contains(relEnd)) {
                    beginRel = false;
                }
                if (beginRel) {
                    if (oneSolLine.contains("<folder name") && oneSolLine.contains(oneRefRel.getFolder())) {
                        boolean idExist = false;
                        //Prüfen ob IDs aus Instanz und Referenz gleich (bereits da), ansonsten ID anpassen und hinzufügen
                        for (Relation oneInstRel : instRel) {
                            if (oneInstRel.getId().equals(oneRefRel.getId())) {
                                idExist = true;
                                solutionDoc.add(j + 1, getLineWithOtherID(oneRefRel.getLine()));
                            }
                        }
                        //Id noch nicht vorhanden, also Änderung einfach so adden
                        if (!idExist) {
                            solutionDoc.add(j + 1, oneRefRel.getLine());
                        }
                    }

                }

            }
        }

        //Färbt alle Childs Grün die NUR in den Inst Figures vorhanden sind.
        for (int i = 0; i < inst_fig_changes.size(); i++) {
            Figure oneInstFig = inst_fig_changes.get(i);
            for (int j = 0; j < solutionDoc.size(); j++) {
                String solutionLine = solutionDoc.get(j);

                if (solutionLine.contains("child xsi")) {
                    Pattern pat = Pattern.compile("archimateElement=\"(.*?)\"");
                    Matcher mat = pat.matcher(solutionLine);
                    if (mat.find()) {
                        //Prüfung: archimateElement aus Childs == ID aus Figures
                        if (mat.group(1).equals(oneInstFig.getId())) {

                            if (solutionLine.contains("fillColor")) {
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

        //Färbt alle SourceConnection Grün, welche NUR in den Inst Relationen zu finden sind.
        for (int i = 0; i < inst_rel_changes.size(); i++) {
            Relation oneInstRel = inst_rel_changes.get(i);
            for (int j = 0; j < solutionDoc.size(); j++) {
                String solutionLine = solutionDoc.get(j);

                if (solutionLine.contains("sourceConnection xsi")) {
                    Pattern pat = Pattern.compile("relationship=\"(.*?)\"");
                    Matcher mat = pat.matcher(solutionLine);
                    if (mat.find()) {
                        //Prüfung: relationship aus Childs == ID aus Relations
                        if (mat.group(1).equals(oneInstRel.getId())) {
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

        //TODO
        //MAX: 06.05.2016
        //Wenn zN lineColor gar nicht vorhanden addet er beides...
        //Wir nehmen bereits die INST Doc und fügen noch alles dazu
        //Er macht auch nicht immer ein neues CHild .....to be done
        
        //Fügt alle Childs in die Solution Doc hinzu welche den Figures zugrunde liegen, die nur in Ref aber nicht in Inst sind.
        for (int i = 0; i < ref_fig_changes.size(); i++) {
            Figure oneRefFig = ref_fig_changes.get(i);
            for (int j = 0; j < allRefChilds.size(); j++) {
                Child oneRefChild = allRefChilds.get(j);

                //Child ID überhaupt vorhanden, Child archimateElement == ID aus Figures
                if (oneRefChild.getArchimateElementID() != null && oneRefChild.getArchimateElementID().equals(oneRefFig.getId())) {
                    boolean inView = false;
                    for (int k = 0; k < solutionDoc.size(); k++) {

                        String solutionLine = solutionDoc.get(k);

                        //Nur durch Childs bzw. View
                        if (solutionLine.contains(relEnd)) {
                            inView = true;
                        }
                        if (inView) {
                            if (solutionLine.contains("element xsi")) {
                                String NAMEREGEX = "name=\"(.*?)\"";
                                Pattern pat = Pattern.compile(NAMEREGEX);
                                Matcher mat = pat.matcher(solutionLine);
                                if (mat.find()) {

                                    //View Name gleich? (zB. name="Archimate View")
                                    String viewElementInst = mat.group(1);
                                    if (viewElementInst.equals(oneRefChild.getViewObjekt())) {
                                        ArrayList<String> childLines = oneRefChild.getchildlines();

                                        //Child Lines rot färben, 
                                        if (!childLines.isEmpty()) {
                                            for (int l = childLines.size() - 1; l >= 0; l--) {
                                                String oneChildLine = childLines.get(l);
                                               
                                                
                                                //print(oneChildLine);
                                                
                                                if (l == 0) {
                                                    if (oneChildLine.contains("fillColor")) {
                                                        oneChildLine = oneChildLine.replaceAll("fillColor=\"(.*?)\"", "fillColor=" + "\"#FF0000\"");
                                                    } else {
                                                        oneChildLine = getLineInsertLineColor(oneChildLine, COLOR_RED, FILL_COL);
                                                    }
                                                } else if (oneChildLine.contains("sourceConnection xsi")) {

                                                    if (oneChildLine.contains("lineColor")) {
                                                        oneChildLine = oneChildLine.replaceAll("lineColor=\"(.*?)\"", "lineColor=" + "\"#FF0000\"");
                                                    } else {
                                                        oneChildLine = getLineInsertLineColor(oneChildLine, COLOR_RED, LINE_COL);
                                                    }
                                                }
                                                //old
                                                //solutionDoc.add(k + 1, oneChildLine);
                                                solutionDoc.add(k+1, oneChildLine);
                                                //solutionDoc.remove(k+1);
                                            }
                                        }

                                    }

                                }
                            }
                        }

                    }

                }
            }

        }

        //Fügt alle SourceConnections hinzu welche die RefRelations beinhalten welche nicht in Int sind.
        for (int i = 0; i < ref_rel_changes.size(); i++) {
            Relation r = ref_rel_changes.get(i);
            for (int j = 0; j < allRefSourceConnections.size(); j++) {
                SourceConnection s = allRefSourceConnections.get(j);
                if (s.getRelationship().equals(r.getId())) {
                    for (int k = 0; k < solutionDoc.size(); k++) {
                        String oneSolutionLine = solutionDoc.get(k);
                        if (oneSolutionLine.contains("child xsi")) {
                            String NAMEREGEX = "id=\"(.*?)\"";
                            Pattern pat = Pattern.compile(NAMEREGEX);
                            Matcher mat = pat.matcher(oneSolutionLine);
                            if (mat.find()) {
                                if (s.getChildID().equals(mat.group(1))) {
                                    solutionDoc.add(k + 2, s.getConnectionLine());
                                }
                            }
                        }

                    }
                }

            }

        }

        //debugPrint(allRefChilds);
        return solutionDoc;
    }

    private void debugPrint(ArrayList<?> toPrintList) {
        for (int i = 0; i < toPrintList.size(); i++) {
            String oneLine = toPrintList.get(i).toString();
            //
            if (oneLine.contains(".............")) {
                System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXX");
            }
            System.out.println(toPrintList.get(i));
        }
    }

    /**
     *
     * @param line
     * @return
     */
    public String getLineWithOtherID(String line) {

        Pattern pat = Pattern.compile("id=\"(.*?)\"");
        Matcher mat = pat.matcher(line);
        if (mat.find()) {
            int i = Integer.parseInt(mat.group(1));
            line = line.replaceAll("id=\"(.*?)\"", "id=" + i * 1000);
        }

        return line;
    }

    /**
     *
     * @param line
     * @param color
     * @param type
     * @return
     */
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

    /**
     *
     * @return
     */
    public File getRefFile() {
        return refFile;
    }

    /**
     *
     * @param refFile
     */
    public void setRefFile(File refFile) {
        this.refFile = refFile;
    }

    /**
     *
     * @return
     */
    public File getInstFile() {
        return instFile;
    }

    /**
     *
     * @param instFile
     */
    public void setInstFile(File instFile) {
        this.instFile = instFile;
    }

}
