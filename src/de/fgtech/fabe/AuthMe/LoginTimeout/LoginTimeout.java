package de.fgtech.fabe.AuthMe.LoginTimeout;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;


import de.fgtech.fabe.AuthMe.AuthMe;
import de.fgtech.fabe.AuthMe.MessageHandler.MessageHandler;

public class LoginTimeout {

	private static Map<Player, Integer> taskidTable = new HashMap<Player, Integer>();

	public static void createLoginTimeout(final AuthMe plugin,
			final Player player) {

		if (!taskidTable.containsKey(player)) {
			if (!(plugin.settings.loginTimeout() > 0)) {
				return;
			}

			Runnable test = new Runnable() {
				@Override
				public void run() {
					if (player != null) {
						if (!plugin.playercache.isPlayerAuthenticated(player)
								&& plugin.playercache
										.isPlayerRegistered(player)) {
							player.kickPlayer("You took too long to login!");
							MessageHandler.showInfo(player.getName()
									+ " took to long to login!");
						}
					}
					removeLoginTimeout(plugin, player);
				}
			};
			int taskid = plugin
					.getServer()
					.getScheduler()
					.scheduleSyncDelayedTask(plugin, test,
							(plugin.settings.loginTimeout() * 20L));

			taskidTable.put(player, taskid);
		}
	}

	public static void removeLoginTimeout(final AuthMe plugin,
			final Player player) {
		if (taskidTable.containsKey(player)) {
			int taskid = taskidTable.get(player);
			plugin.getServer().getScheduler().cancelTask(taskid);
			taskidTable.remove(player);
		}
	}
}