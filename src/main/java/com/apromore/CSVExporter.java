package com.apromore;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Textbox;


public class CSVExporter extends SelectorComposer<Component> {

    @Wire
    Textbox fileLoc;

    private static XesToCsv xesCsv = new XesToCsv();

    @Listen("onClick = #toCSVButton")
    public void toCSV() throws Exception{
        xesCsv.readFile(fileLoc.getText());
    }
}