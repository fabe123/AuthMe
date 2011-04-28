package de.fgtech.fabe.AuthMe.DataController.RegistrationCache;

public class Ending extends ListElement {

	@Override
	public DataNode insert(RegistrationData inhalt) {
		return new DataNode(this, inhalt);
	}

	@Override
	public void remove(ListElement vorheriger, String username) {
		
	}

	@Override
	public int getPlayerAmount() {
		return 0;
	}

	@Override
	public String[] getPlayerArray(String[] current, int currentIndex) {
		return current;
	}

	@Override
	public RegistrationData getPlayerData(String username) {
		return null;
	}

}
