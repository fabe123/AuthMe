package de.fgtech.fabe.AuthMe.Sessions;

public class Session {

	private long time;
	private String address;

	public Session(long time, String address) {
		this.time = time;
		this.address = address;
	}

	public long getTime() {
		return this.time;
	}

	public String getAddress() {
		return address;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public void setAddress(String address) {
		this.address = address;
	}

}
