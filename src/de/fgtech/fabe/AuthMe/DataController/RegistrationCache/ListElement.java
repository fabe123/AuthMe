package de.fgtech.fabe.AuthMe.DataController.RegistrationCache;


public abstract class ListElement {

	public abstract DataNode insert(RegistrationData inhalt);
	public abstract void remove(ListElement vorheriger, String username);
	public abstract int getPlayerAmount();
	public abstract String[] getPlayerArray(String[] current, int currentIndex);
	public abstract RegistrationData getPlayerData(String username);
	
}
