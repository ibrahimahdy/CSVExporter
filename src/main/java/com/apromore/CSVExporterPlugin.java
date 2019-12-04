package com.apromore;

import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.model.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.zkoss.zhtml.Messagebox;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.deckfour.xes.in.XUniversalParser;
import sun.rmi.runtime.Log;

public class CSVExporterPlugin {

    private XUniversalParser parser;
    private String myFileName;

    public void readFile(String fileName) {
        myFileName = fileName;
        File myFile = new File(fileName);
        parser = new XUniversalParser();
        Collection<XLog> myLog = new  ArrayList<XLog>();

        if(parser.canParse(myFile)){
            try {
                myLog = parser.parse(myFile);
            }
            catch (Exception e){
                Messagebox.show("Error Parsing the file!: " + e.getMessage());
            }

            if(myLog.size()!=0){
                List<XTrace> traces = myLog.iterator().next();
                List<String> attributes = getAttributeNames(traces);
                List<LogModel> log = createModel(traces);
                writeCSVFile(log, attributes);
            }

        }
    }


    private List<String> getAttributeNames(List<XTrace> traces){

        Set<String> listOfAttributes = new LinkedHashSet<String>();
        for (XTrace myTrace: traces) {
            listOfAttributes.addAll(myTrace.getAttributes().keySet());
            List<XEvent> traceEvents = myTrace;
            for (XEvent myEvent: traceEvents) {
                listOfAttributes.addAll(myEvent.getAttributes().keySet());
            }
        }

        if(listOfAttributes.contains("concept:name")){
            listOfAttributes.remove("concept:name");
        }

        return new ArrayList<String>(listOfAttributes);
    }


    private List<LogModel> createModel(List<XTrace> traces){
        String caseID;
        String activity;
        HashMap<String, String> otherAttributes;

        List<LogModel> logData = new ArrayList<LogModel>();
        String attributeValue;

        for (XTrace myTrace: traces) {
            caseID = activity = "";
            otherAttributes = new HashMap<String, String>();

            for (Map.Entry<String, XAttribute> tAtt : myTrace.getAttributes().entrySet()){

                attributeValue = getAttributeValue(tAtt.getValue());
                if(tAtt.getKey().equals("concept:name")){
                    caseID = attributeValue;
                }else{
                    otherAttributes.put(tAtt.getKey(), attributeValue);
                }
            }

            for (XEvent myEvent: myTrace) {
                for (Map.Entry<String, XAttribute> eAtt : myEvent.getAttributes().entrySet()){

                    attributeValue = getAttributeValue(eAtt.getValue());
                    if(eAtt.getKey().equals("concept:name")){
                        activity = attributeValue;
                    }else{
                        otherAttributes.put(eAtt.getKey(), attributeValue);
                    }
                }

                logData.add(new LogModel(caseID, activity, otherAttributes));
            }
        }

    return  logData;
    }


    private String getAttributeValue(XAttribute myAttribute){

        if(myAttribute instanceof XAttributeLiteral){
            String theValue = ((XAttributeLiteral)myAttribute).getValue();
            if(theValue.contains(",")) return "\"" + theValue + "\"";
            return  theValue;
        }else if (myAttribute instanceof XAttributeTimestamp){

            DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            return df.format(((XAttributeTimestamp)myAttribute).getValue());
        }
        else if (myAttribute instanceof XAttributeBoolean){
            return String.valueOf(((XAttributeBoolean)myAttribute).getValue());
        }
        else if (myAttribute instanceof XAttributeDiscrete){
            return String.valueOf(((XAttributeDiscrete)myAttribute).getValue());
        }
        else if (myAttribute instanceof XAttributeContinuous){
            return  String.valueOf(((XAttributeContinuous)myAttribute).getValue());
        }
        return "";

    }




    private void writeCSVFile(List<LogModel> log, List<String> columnsList){

        StringBuilder sb = new StringBuilder();
        myFileName= myFileName.replaceFirst("[.][^.]+$", "") + ".csv";
        try (PrintWriter writer = new PrintWriter(new File(myFileName))) {

            sb.append("Case ID, Activity,");
            String prefix = "";
            for(String one : columnsList){
                sb.append(prefix);
                prefix = ",";
                sb.append(one);
            }
            sb.append('\n');

            String columnValue;
            for (LogModel row : log) {
                sb.append(row.getCaseID() + "," + row.getActivity()+ ",");
                prefix = "";
                for(String one : columnsList){
                    sb.append(prefix);
                    prefix = ",";

                    columnValue = row.getOtherAttributes().get(one);
                    if(columnValue != null && columnValue.trim().length() !=0){
                        sb.append(columnValue);
                    }else{
                        sb.append("");
                    }
                }
                sb.append('\n');
            }

            writer.write(sb.toString());
            Messagebox.show("Downloaded! to: " + myFileName);
        }catch (FileNotFoundException e) {
            Messagebox.show(e.getMessage());
        }

    }

}
