package de.fgtech.fabe.AuthMe.Listener;

import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

import de.fgtech.fabe.AuthMe.AuthMe;

/**
 * AuthMe block listener
 * 
 * @author fabe
 */
public class AuthMeBlockListener extends BlockListener {
	private final AuthMe plugin;

	public AuthMeBlockListener(final AuthMe plugin) {
		this.plugin = plugin;
	}

	public void onBlockPlace(BlockPlaceEvent event) {
		Player players = event.getPlayer();
		if (!plugin.checkAuth(players)) {
			event.setCancelled(true);
		}
	}

	// NEW------------------
	public void onBlockBreak(BlockBreakEvent event) {
		Player players = event.getPlayer();

		if (!plugin.checkAuth(players)) {
			event.setCancelled(true);
		}
	}

}