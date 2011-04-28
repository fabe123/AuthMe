package de.fgtech.fabe.AuthMe.Sessions;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

public class SessionHandler {

	private Map<String, Session> sessionTable = new HashMap<String, Session>();
	private int maxSessionTime;
	private boolean ipCheck;

	public SessionHandler(int maxSessionTime, boolean ipCheck) {
		this.maxSessionTime = maxSessionTime;
		this.ipCheck = ipCheck;
	}

	public void createSession(Player player) {
		long curTime = System.currentTimeMillis();
		String recentHost = player.getAddress().getHostName();
		
		Session newSession = new Session(curTime, recentHost);

		// remove possible sessions made before
		removeSession(player);

		sessionTable.put(player.getName().toLowerCase(), newSession);
	}

	public void removeSession(Player player) {
		if (sessionTable.containsKey(player.getName().toLowerCase())) {
			sessionTable.remove(player.getName().toLowerCase());
		}
	}

	public boolean isSessionValid(Player player) {
		if (!sessionTable.containsKey(player.getName().toLowerCase())) {
			return false;
		}
		String recentHost = player.getAddress().getHostName();
		Session session = sessionTable.get(player.getName().toLowerCase());
		long timeSpan = System.currentTimeMillis() - session.getTime();

		// timeout already reached?!
		if (timeSpan < (maxSessionTime * 1000)) {
			if (ipCheck) {
				if (session.getAddress().equals(recentHost)) {
					return true;
				}
			} else {
				return true;
			}
		} else {
			// Session timed out, so we can remove it now
			removeSession(player);
		}

		return false;
	}
}
