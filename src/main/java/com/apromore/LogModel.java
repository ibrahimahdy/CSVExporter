package com.apromore;

import java.sql.Timestamp;
import java.util.HashMap;

public class LogModel {

	private String caseID;
	private String activity;
	private HashMap<String, String> others;

	public LogModel(){

	}
	public LogModel(String caseID, String activity, HashMap<String, String> others) {
		setCaseID(caseID);
		setActivity(activity);
		setOthers(others);
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

    public void setOthers(HashMap<String, String> oth)
    {
    	this.others= oth;
    }

    public HashMap<String, String> getOthers()
    {
        return others;
    }

}
