package com.apromore;

import com.opencsv.bean.CsvBindByName;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

public class LogModel {

	@CsvBindByName
	private String caseID;

	@CsvBindByName
	private String activity;

	private HashMap<String, String> literalValues;
	private HashMap<String, Date> timestampValues;
	private HashMap<String, Boolean> booleanValues;
	private HashMap<String, Long> discreteValues;
	private HashMap<String, Double> continuousValues;

	public LogModel(){

	}
	public LogModel(String caseID, String activity, HashMap<String, String> literals, HashMap<String, Date> timestampValues, HashMap<String, Boolean> booleanValues, HashMap<String, Long> discreteValues, HashMap<String, Double> continuousValues) {
		setCaseID(caseID);
		setActivity(activity);
		setLiteralValues(literals);
		setTimestampValues(timestampValues);
		setBooleanValues(booleanValues);
		setDiscreteValues(discreteValues);
		setContinuousValues(continuousValues);
	}

	public HashMap<String, Date> getTimestampValues() {
		return timestampValues;
	}

	public void setTimestampValues(HashMap<String, Date> timestampValues) {
		this.timestampValues = timestampValues;
	}

	public HashMap<String, Boolean> getBooleanValues() {
		return booleanValues;
	}

	public void setBooleanValues(HashMap<String, Boolean> booleanValues) {
		this.booleanValues = booleanValues;
	}

	public HashMap<String, Long> getDiscreteValues() {
		return discreteValues;
	}

	public void setDiscreteValues(HashMap<String, Long> discreteValues) {
		this.discreteValues = discreteValues;
	}

	public HashMap<String, Double> getContinuousValues() {
		return continuousValues;
	}

	public void setContinuousValues(HashMap<String, Double> continuousValues) {
		this.continuousValues = continuousValues;
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

    public void setLiteralValues(HashMap<String, String> oth)
    {
    	this.literalValues = oth;
    }

    public HashMap<String, String> getLiteralValues()
    {
        return literalValues;
    }

}
