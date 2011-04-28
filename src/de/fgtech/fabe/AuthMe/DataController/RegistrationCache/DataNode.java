package de.fgtech.fabe.AuthMe.DataController.RegistrationCache;

public class DataNode extends ListElement{

	public ListElement naechster;
	public RegistrationData inhalt;
	
	public DataNode(ListElement naechster, RegistrationData inhalt){
		this.naechster = naechster;
		this.inhalt = inhalt;
	}
	
	@Override
	public DataNode insert(RegistrationData inhalt) {
		naechster = naechster.insert(inhalt);
		return this;
	}
	
	@Override
	public void remove(ListElement vorheriger, String username) {
		if(inhalt.getUsername().equalsIgnoreCase(username) && vorheriger != null){
			DataNode node = (DataNode) vorheriger;
			node.naechster = naechster;
		}
		else {
			naechster.remove((DataNode) this, username);
		}
	}

	@Override
	public int getPlayerAmount() {
		return naechster.getPlayerAmount()+1;
	}

	@Override
	public String[] getPlayerArray(String[] current, int currentIndex) {
		current[currentIndex] = inhalt.getUsername();
		return naechster.getPlayerArray(current, ((int) currentIndex + 1));
	}

	@Override
	public RegistrationData getPlayerData(String username) {
		if(inhalt.getUsername().equalsIgnoreCase(username)){
			return inhalt;
		}
		else {
			return naechster.getPlayerData(username);
		}
	}
	
}
