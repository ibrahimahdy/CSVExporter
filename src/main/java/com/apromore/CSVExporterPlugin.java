package com.apromore;

import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
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
        String caseid;
        String activity;
        HashMap<String, String> others;
        List<LogModel> logData = new ArrayList<LogModel>();
        XAttributeLiteral attHolder;

        for (XTrace myTrace: traces) {
            others = new HashMap<String, String>();
            caseid = activity = "";
            for (Map.Entry<String, XAttribute> tAtt : myTrace.getAttributes().entrySet()){
                attHolder = (XAttributeLiteral)tAtt.getValue();
                if(tAtt.getKey().equals("concept:name")){
                    caseid = attHolder.getValue();
                }else{
                    others.put(tAtt.getKey(),attHolder.getValue());
                }
            }


            List<XEvent> events = myTrace;
            for (XEvent myEvent: events) {

                if(myEvent.getAttributes().containsKey("concept:name")){
                    activity = ((XAttributeLiteral)myEvent.getAttributes().get("concept:name")).getValue();
                }

                for (String item: attributes) {
                    if(myEvent.getAttributes().containsKey(item)){
                        attHolder = (XAttributeLiteral)myEvent.getAttributes().get(item);
                        others.put(item,attHolder.getValue());
                    }else{
                        others.put(item,"");
                    }
                }

//                for (Map.Entry<String, XAttribute> eAtt : myEvent.getAttributes().entrySet()){
//                    attHolder = (XAttributeLiteral)eAtt.getValue();
//                    if(eAtt.getKey().equals("concept:name")){
//                        activity = attHolder.getValue();
//                    }else{
//
//
//                        others.put(eAtt.getKey(),attHolder.getValue());
//                    }
//                }
                logData.add(new LogModel(caseid, activity, others));
            }
        }

    return  logData;
    }

    private static final String STRING_ARRAY_SAMPLE = "./string-array-sample.csv";
    private void writeCSVFile(List<LogModel> log){


        try (
                Writer writer = Files.newBufferedWriter(Paths.get(STRING_ARRAY_SAMPLE));
        ) {
            StatefulBeanToCsv<Object> beanToCsv = new StatefulBeanToCsvBuilder<>(writer)
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .build();


            beanToCsv.write(log);
        }catch (Exception e){

        }




    }

}
