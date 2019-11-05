package com.apromore;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.zkoss.zhtml.Messagebox;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class XesToCsv {


    private static LinkedHashMap<String, String> global;
    private static LinkedHashMap<String, String>  local;
    private static LinkedHashMap<String, String>  compined;
    private static LinkedHashMap<Integer, LinkedHashMap<String, String>> eventData;

    private static int eventsCount;
    private static int eventIndex;

    private static String nodeName;
    private static String columnName;
    private static String columnValue;
    List<String> columnsList;
    private String myFileName;

    public void readFile(String fileName) {
        myFileName = fileName;
        try {
            File fXmlFile = new File(fileName);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(fXmlFile);
            document.getDocumentElement().normalize();


            local = new LinkedHashMap<String, String>();
            eventData = new LinkedHashMap<Integer, LinkedHashMap<String, String>>();

            eventIndex=0;
            eventsCount=0;

            NodeList nList = document.getElementsByTagName("trace");
            for (int count = 0; count < nList.getLength(); count++) {
                Node tempNode = nList.item(count);
                global = new LinkedHashMap<String, String>();
                compined = new LinkedHashMap<String, String>();
                getData(tempNode);
            }
        } catch (Exception e) {
            Messagebox.show(e.getMessage());
        }

        compineAtrributes();

        columnsList = new ArrayList<String>();
        String [][] content = new String [eventsCount][];
        int ind;
        for (Map.Entry<Integer, LinkedHashMap<String, String>> entry : eventData.entrySet()) {
            local = entry.getValue();

            for (Map.Entry<String, String> item : local.entrySet()) {
                if(!columnsList.contains(item.getKey())){
                    columnsList.add(item.getKey());
                }
            }

            ind = 0;
            content[eventIndex] = new String[columnsList.size()];
            for(String one : columnsList){
                if (local.containsKey(one)) {
                    content[eventIndex][ind] = local.get(one);
                }else{
                    content[eventIndex][ind] = "";
                }
                ind++;
            }

            eventIndex++;
        }

        writeFile(content);
    }



    private void getData(Node node) {

        nodeName = node.getNodeName();
        columnName = columnValue = "";

        if (!nodeName.toLowerCase().equals("event")) {

            if(node.getParentNode().getNodeName().toLowerCase().equals("trace")){

                if (((Element) node).hasAttribute("key")) {
                    columnName = node.getAttributes().getNamedItem("key").getNodeValue();
                    if(columnName.toLowerCase().equals("concept:name")){
                        columnName = "Case ID";
                    }
                }
                if (((Element) node).hasAttribute("value")) {
                    columnValue = node.getAttributes().getNamedItem("value").getNodeValue();
                }
                global.put(columnName, columnValue);
            }
            else if (node.getParentNode().getNodeName().toLowerCase().equals("event")){

                if (((Element) node).hasAttribute("key")) {
                    columnName = node.getAttributes().getNamedItem("key").getNodeValue();
                    if(columnName.toLowerCase().equals("concept:name")){
                        columnName = "Activity";
                    }
                }
                if (((Element) node).hasAttribute("value")) {
                    columnValue = node.getAttributes().getNamedItem("value").getNodeValue();
                }
                local.put(columnName, columnValue);
            }

        }else{
            compineAtrributes();
        }


        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                getData(currentNode);
            }
        }
    }


    private void compineAtrributes(){
        if(!local.isEmpty()){
            compined = new LinkedHashMap<String, String>();
            compined.putAll(global);
            compined.putAll(local);
            eventData.put(eventsCount, compined);
            eventsCount++;
            local = new LinkedHashMap<String, String>();
            compined = new LinkedHashMap<String, String>();
        }
    }

    private void writeFile(String [][] data) {

        StringBuilder sb = new StringBuilder();
        myFileName= myFileName.replaceFirst("[.][^.]+$", "") + ".csv";

        try (PrintWriter writer = new PrintWriter(new File(myFileName))) {

            String prefix = "";
            for(String one : columnsList){
                sb.append(prefix);
                prefix = ",";
                sb.append(one);
            }
            sb.append('\n');

            for (int row = 0; row < data.length; row++) {
                prefix = "";
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

}
