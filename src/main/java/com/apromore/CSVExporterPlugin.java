package com.apromore;

import org.deckfour.xes.model.*;
import org.zkoss.zhtml.Messagebox;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.deckfour.xes.in.XUniversalParser;

public class CSVExporterPlugin {

    private XUniversalParser parser;
    private String myFileName;
    private List<String> columnNames;

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
                List<LogModel> log = createModel(traces);
                writeCSVFile(log);
            }

        }
    }

    private static String CASEID = "Case ID";
    private static String ACTIVITY = "Activity";

    private List<LogModel> createModel(List<XTrace> traces){

        HashMap<String, String> attributeList;
        HashMap<String, String> eventAttributes;

        List<LogModel> logData = new ArrayList<LogModel>();
        String attributeValue;

        Set<String> listOfAttributes = new LinkedHashSet<String>();
        columnNames = new ArrayList<String>();
        columnNames.add(CASEID);
        columnNames.add(ACTIVITY);

        for (XTrace myTrace: traces) {
            listOfAttributes.addAll(myTrace.getAttributes().keySet());

            attributeList = new HashMap<String, String>();

            for (Map.Entry<String, XAttribute> tAtt : myTrace.getAttributes().entrySet()){

                attributeValue = getAttributeValue(tAtt.getValue());
                if(tAtt.getKey().equals("concept:name")){
                    attributeList.put(CASEID, attributeValue);
                }else{
                    attributeList.put(tAtt.getKey(), attributeValue);
                }
            }

            for (XEvent myEvent: myTrace) {
                eventAttributes = new HashMap<String, String>();
                eventAttributes.putAll(attributeList);
                listOfAttributes.addAll(myEvent.getAttributes().keySet());

                for (Map.Entry<String, XAttribute> eAtt : myEvent.getAttributes().entrySet()){

                    attributeValue = getAttributeValue(eAtt.getValue());
                    if(eAtt.getKey().equals("concept:name")){
                        eventAttributes.put(ACTIVITY, attributeValue);
                    }else{
                        eventAttributes.put(eAtt.getKey(), attributeValue);
                    }
                }

                logData.add(new LogModel(eventAttributes));
            }
        }

        if(listOfAttributes.contains("concept:name")){
            listOfAttributes.remove("concept:name");
        }
        columnNames.addAll(new ArrayList<String>(listOfAttributes));

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


    private void writeCSVFile(List<LogModel> log){

        StringBuilder sb = new StringBuilder();
        myFileName= myFileName.replaceFirst("[.][^.]+$", "") + ".csv";
        try (PrintWriter writer = new PrintWriter(new File(myFileName))) {

            String prefix = "";
            for(String one : columnNames){
                sb.append(prefix);
                prefix = ",";
                sb.append(one);
            }
            sb.append('\n');

            String columnValue;
            for (LogModel row : log) {
                prefix = "";
                for(String one : columnNames){
                    sb.append(prefix);
                    prefix = ",";

                    columnValue = row.getAttributeList().get(one);
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
