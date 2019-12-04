package com.apromore;

import com.opencsv.bean.CsvBindByName;

import java.util.HashMap;

public class LogModel {

	@CsvBindByName
	private String caseID;

	@CsvBindByName
	private String activity;

	private HashMap<String, String> otherAttributes;


	public LogModel(){

	}
	public LogModel(String caseID, String activity, HashMap<String, String> otherAttributes) {
		setCaseID(caseID);
		setActivity(activity);
		setOtherAttributes(otherAttributes);
	}


	public void setCaseID(String ID) {
		this.caseID = ID;
	}

	public String getCaseID() {
		return caseID;
	}

	public void setActivity(String con) {
		this.activity = con;
	}

	public String getActivity() {
		return activity;
	}

    public void setOtherAttributes(HashMap<String, String> oth)
    {
    	this.otherAttributes = oth;
    }

    public HashMap<String, String> getOtherAttributes()
    {
        return otherAttributes;
    }

}
