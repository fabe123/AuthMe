package de.fgtech.fabe.AuthMe.PlayerCache;

public class PlayerData {

	private boolean registered;
	private boolean authenticated;
	private long lastAlert;	


	public PlayerData(boolean registered, boolean authenticated) {
		this.registered = registered;
		this.authenticated = authenticated;
		lastAlert = 0;
	}

	public boolean isRegistered() {
		return registered;
	}
	
	public boolean isAuthenticated() {
		return authenticated;
	}
	
	public void setRegistered(boolean newvalue) {
		registered = newvalue;
	}
	
	public void setAuthenticated(boolean newvalue) {
		authenticated = newvalue;
	}
	
	public void setLastAlertToNow() {
		lastAlert = System.currentTimeMillis();
	}
	
	public long getLastAlert() {
		return lastAlert;
	}

}