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
                List<LogModel> log = createModel(traces, attributes);
                writeCSVFile(log);
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


    private List<LogModel> createModel(List<XTrace> traces, List<String> attributes){
        String caseID;
        String activity;
        HashMap<String, String> literalValues;
        HashMap<String, Date> timestampValues;
        HashMap<String, Boolean> booleanValues;
        HashMap<String, Long> discreteValues;
        HashMap<String, Double> continuousValues;

        List<LogModel> logData = new ArrayList<LogModel>();
        XAttributeLiteral attHolder;

        for (XTrace myTrace: traces) {
            literalValues = new HashMap<String, String>();
            timestampValues = new HashMap<String, Date>();
            booleanValues = new HashMap<String, Boolean>();
            discreteValues = new HashMap<String, Long>();
            continuousValues = new HashMap<String, Double>();


            caseID = activity = "";
            for (Map.Entry<String, XAttribute> tAtt : myTrace.getAttributes().entrySet()){
                attHolder = (XAttributeLiteral)tAtt.getValue();
                if(tAtt.getKey().equals("concept:name")){
                    caseID = attHolder.getValue();
                }else{
                    literalValues.put(tAtt.getKey(),attHolder.getValue());
                }
            }


            List<XEvent> events = myTrace;
            for (XEvent myEvent: events) {

                if(myEvent.getAttributes().containsKey("concept:name")){
                    activity = ((XAttributeLiteral)myEvent.getAttributes().get("concept:name")).getValue();
                }

                for (String item: attributes) {
                    if(myEvent.getAttributes().containsKey(item)){
                        if(myEvent.getAttributes().get(item) instanceof XAttributeLiteral){
                            attHolder = (XAttributeLiteral)myEvent.getAttributes().get(item);
                            literalValues.put(item,attHolder.getValue());
                        }else if (myEvent.getAttributes().get(item) instanceof XAttributeTimestamp){
                            timestampValues.put(item,((XAttributeTimestamp)myEvent.getAttributes().get(item)).getValue());
                        }
                        else if (myEvent.getAttributes().get(item) instanceof XAttributeBoolean){
                            booleanValues.put(item,((XAttributeBoolean)myEvent.getAttributes().get(item)).getValue());
                        }
                        else if (myEvent.getAttributes().get(item) instanceof XAttributeDiscrete){
                            discreteValues.put(item,((XAttributeDiscrete)myEvent.getAttributes().get(item)).getValue());
                        }
                        else if (myEvent.getAttributes().get(item) instanceof XAttributeContinuous){
                            continuousValues.put(item,((XAttributeContinuous)myEvent.getAttributes().get(item)).getValue());
                        }

                    }else{
                        literalValues.put(item,"");
                    }
                }

                logData.add(new LogModel(caseID, activity, literalValues,timestampValues,booleanValues,discreteValues,continuousValues));
            }
        }

    return  logData;
    }

    private static final String STRING_ARRAY_SAMPLE = "string-array-sample.csv";
    private void writeCSVFile(List<LogModel> log){


        StringBuilder sb = new StringBuilder();
        myFileName= myFileName.replaceFirst("[.][^.]+$", "") + ".csv";

        try (PrintWriter writer = new PrintWriter(new File(myFileName))) {

            sb.append("Case ID, Activity");
            sb = addHeader(sb, "", log.get(0).getTimestampValues().keySet());
            sb = addHeader(sb, ",", log.get(0).getLiteralValues().keySet());
            sb = addHeader(sb, ",", log.get(0).getBooleanValues().keySet());
            sb = addHeader(sb, ",", log.get(0).getDiscreteValues().keySet());
            sb = addHeader(sb, ",", log.get(0).getContinuousValues().keySet());
            sb.append('\n');

            for (LogModel row: log) {
                sb.append(row.getCaseID());
                sb.append(",");
                sb.append(row.getActivity());
                sb = addHeader(sb, ",", log.get(0).getTimestampValues().values());


            }
            for (int row = 0; row < data.length; row++) {

                for (int col = 0; col < data[row].length; col++) {
                    sb.append(prefix);
                    prefix = ",";
                    sb.append(data[row][col]);
                }
                sb.append('\n');
            }

            writer.write(sb.toString());
            Messagebox.show("Downloaded! to: " + myFileName);

        }catch (FileNotFoundException e) {
            Messagebox.show(e.getMessage());
        }

    }

    private StringBuilder addHeader(StringBuilder sb, String prefix, Set<String> keySet){
        for ( String one : keySet ) {
            sb.append(prefix);
            prefix = ",";
            sb.append(one);
        }
        return sb;
    }

}
