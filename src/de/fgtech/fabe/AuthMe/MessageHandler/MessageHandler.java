package de.fgtech.fabe.AuthMe.MessageHandler;

import java.util.logging.Logger;

public class MessageHandler {

	public static Logger l = Logger.getLogger("Minecraft");
	
	public static void showInfo(String message){
		l.info("[AuthMe] " + message);
	}
	
	public static void showError(String message){
		l.severe("[AuthMe] " + message);
	}
	
	public static void showWarning(String message){
		l.warning("[AuthMe] " + message);
	}
	
}
