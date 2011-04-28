package de.fgtech.fabe.AuthMe.PlayerCache;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;

public class PlayerCache {

	private Map<Player, PlayerData> playerDatatable = new HashMap<Player, PlayerData>();

	public PlayerCache() {
	}

	public void createCache(Player player, boolean registered,
			boolean authenticated) {

		if (!playerDatatable.containsKey(player)) {
			PlayerData data = new PlayerData(registered, authenticated);
			playerDatatable.put(player, data);
		}

	}

	public void removeCache(Player player) {
		if (playerDatatable.containsKey(player)) {
			playerDatatable.remove(player);
		}
	}

	public void recreateCache(Player player) {
		removeCache(player);
		createCache(player, false, false);
	}

	private PlayerData getPlayerData(Player player) {
		if (playerDatatable.containsKey(player)) {
			return playerDatatable.get(player);
		}
		return null;
	}

	public boolean isPlayerInCache(Player player) {
		if (playerDatatable.containsKey(player)) {
			return true;
		}
		return false;
	}

	public void setPlayerRegistered(Player player, boolean newvalue) {
		getPlayerData(player).setRegistered(newvalue);
	}

	public boolean isPlayerRegistered(Player player) {
		return isPlayerInCache(player) ? getPlayerData(player).isRegistered()
				: false;
	}

	public void setPlayerAuthenticated(Player player, boolean newvalue) {
		getPlayerData(player).setAuthenticated(newvalue);
	}

	public boolean isPlayerAuthenticated(Player player) {
		return isPlayerInCache(player) ? getPlayerData(player)
				.isAuthenticated() : false;
	}

	public long getLastAlert(Player player) {
		return getPlayerData(player).getLastAlert();
	}

	public void setLastAlertToNow(Player player) {
		getPlayerData(player).setLastAlertToNow();
	}

	public boolean isAlertNeeded(Player player, int intervall) {
		long lastAlert = getLastAlert(player);
		long timeDiff = System.currentTimeMillis() - lastAlert;

		if (lastAlert == 0 || timeDiff > (intervall * 1000)) {
			setLastAlertToNow(player);
			return true;
		}
		return false;
	}

}
