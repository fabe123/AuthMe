package de.fgtech.fabe.AuthMe.DataController.RegistrationCache;

public class RegistrationCache {

	private ListElement erster;

	public RegistrationCache() {
		erster = new Ending();
	}

	public void insert(RegistrationData data) {
		erster = erster.insert(data);
	}

	public void remove(String username) {
		if (erster instanceof DataNode) {
			if (((DataNode) erster).inhalt.getUsername().equals(username)) {
				erster = ((DataNode) erster).naechster;
			} else {
				erster.remove(null, username);
			}
		}
	}

	public RegistrationData getPlayerData(String username) {
		return erster.getPlayerData(username);
	}

	public String getHash(String username) {
		return getPlayerData(username).getHash();
	}

	public boolean isPlayerRegistered(String username) {

		if (getPlayerData(username) != null) {
			return true;
		}

		return false;
	}

	public int getRegisteredPlayerAmount() {
		return erster.getPlayerAmount();
	}

	public String[] getPlayerArray() {
		String[] tmpString = new String[getRegisteredPlayerAmount()];

		return erster.getPlayerArray(tmpString, 0);
	}

}
