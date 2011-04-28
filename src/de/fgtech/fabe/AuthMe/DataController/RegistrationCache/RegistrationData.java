package de.fgtech.fabe.AuthMe.DataController.RegistrationCache;

public class RegistrationData {

	private String name;
	private String hash;

	public RegistrationData(String name, String hash) {
		this.name = name;
		this.hash = hash;
	}

	public String getUsername() {
		return name;
	}

	public String getHash() {
		return hash;
	}
	
	public void setHash(String hash) {
		this.hash = hash;
	}
	
}