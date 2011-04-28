package de.fgtech.fabe.AuthMe.Listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;

import de.fgtech.fabe.AuthMe.AuthMe;

/**
 * AuthMe block listener
 * 
 * @author fabe
 */
public class AuthMeEntityListener extends EntityListener {
	private final AuthMe plugin;

	public AuthMeEntityListener(final AuthMe plugin) {
		this.plugin = plugin;
	}

	public void onEntityDamage(EntityDamageEvent event) {
		Entity entity = event.getEntity();
		if (!(entity instanceof Player)) {
			return;
		}
		
		Player player = (Player) entity;
		if (!plugin.checkAuth(player)) {
			event.setCancelled(true);
		}
	}

	public void onEntityTarget(EntityTargetEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof Player) {
			return;
		}
		
		Entity target = event.getTarget();
		if (!(target instanceof Player)) {
			return;
		}
		Player targetPlayer = (Player) target;
		
		if (!plugin.checkAuth(targetPlayer)) {
			event.setCancelled(true);
		}
	}

}