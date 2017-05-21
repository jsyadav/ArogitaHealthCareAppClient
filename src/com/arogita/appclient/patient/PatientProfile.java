package com.arogita.appclient.patient;


public class PatientProfile {

	private String firstName = null;
	private String lastName = null;
	private String id;
	private String dob;
	private String gender;

	
	public PatientProfile(String fName, String lName, String dob, String gender, String uid){
		this.firstName = fName;
		this.lastName = lName;
		this.dob = dob;
		this.gender = gender;
		this.id = uid;
	}
	
	
	public String getName (){
		return firstName + " " + lastName;
	}
	
	public String getId (){
		return id;
	}
	
	
	public String getFirstName(){
		return firstName;
	}

	public String getLastName(){
		return lastName;
	}
	
	public String toString(){
		return (firstName + " " + lastName +  "\n "  +   
				id + "\n" + dob + " " + gender);	
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}
	
}
