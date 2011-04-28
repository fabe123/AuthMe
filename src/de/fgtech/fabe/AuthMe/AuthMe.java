package de.fgtech.fabe.AuthMe;

import java.util.List;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import de.fgtech.fabe.AuthMe.DataController.DataController;
import de.fgtech.fabe.AuthMe.DataController.DataSource.DataSource;
import de.fgtech.fabe.AuthMe.DataController.DataSource.FlatfileData;
import de.fgtech.fabe.AuthMe.DataController.DataSource.MySQLData;
import de.fgtech.fabe.AuthMe.InventoryCache.FlatfileCache;
import de.fgtech.fabe.AuthMe.InventoryCache.InventoryArmour;
import de.fgtech.fabe.AuthMe.Listener.AuthMeBlockListener;
import de.fgtech.fabe.AuthMe.Listener.AuthMeEntityListener;
import de.fgtech.fabe.AuthMe.Listener.AuthMePlayerListener;
import de.fgtech.fabe.AuthMe.LoginTimeout.LoginTimeout;
import de.fgtech.fabe.AuthMe.MessageHandler.MessageHandler;
import de.fgtech.fabe.AuthMe.Parameters.Messages;
import de.fgtech.fabe.AuthMe.Parameters.Settings;
import de.fgtech.fabe.AuthMe.PlayerCache.PlayerCache;
import de.fgtech.fabe.AuthMe.Sessions.SessionHandler;

/**
 * AuthMe for Bukkit
 * 
 * @author fabe
 */
public class AuthMe extends JavaPlugin {

	private final AuthMePlayerListener playerListener = new AuthMePlayerListener(
			this);
	private final AuthMeBlockListener blockListener = new AuthMeBlockListener(
			this);
	private final AuthMeEntityListener entityListener = new AuthMeEntityListener(
			this);

	public Settings settings;
	public Messages messages;
	public PlayerCache playercache;
	public DataController datacontroller;
	public FlatfileCache invcache;
	public SessionHandler sessionhandler;
	public DataSource datas;

	public void onEnable() {
		// Creating dir, if it doesn't exist
		final File folder = new File(Settings.PLUGIN_FOLDER);
		if (!folder.exists()) {
			folder.mkdirs();
		}

		// Loading config
		File configFile = new File(Settings.PLUGIN_FOLDER, "config.yml");
		if (!configFile.exists()) {
			extractDefaultFile("config.yml");
			configFile = new File(Settings.PLUGIN_FOLDER, "config.yml");
		}
		settings = new Settings(configFile);

		// Loading messages
		File messagesFile = new File(Settings.PLUGIN_FOLDER, "messages.yml");
		if (!messagesFile.exists())
			messagesFile = new File(Settings.PLUGIN_FOLDER, "messages.yml");
		messages = new Messages(messagesFile);

		// Create a new cache for player stuff
		playercache = new PlayerCache();

		// Create the cache that's needed for inventory backups
		invcache = new FlatfileCache();

		// Create a session handler, that manages player sessions
		int maxTimePeriod = settings.MaximalTimePeriod();
		boolean IPCheck = settings.SessionIPCheckEnabled();
		sessionhandler = new SessionHandler(maxTimePeriod, IPCheck);

		// Save the current time to use it later
		long before = System.currentTimeMillis();

		// Create the wished DataSource
		if (settings.DataSource().equals("mysql")) {
			MessageHandler.showInfo("Using MySQL as datasource!");

			String host = settings.MySQLConnectionHost();
			int port = settings.MySQLConnectionPort();
			String database = settings.MySQLConnectionDatabase();
			String username = settings.MySQLConnectionUsername();
			String password = settings.MySQLConnectionPassword();
			String tableName = settings.MySQLCustomTableName();
			String columnName = settings.MySQLCustomColumnName();
			String columnPassword = settings.MySQLCustomColumnPassword();
			datas = new MySQLData(host, port, database, username, password,
					tableName, columnName, columnPassword);
		} else {
			MessageHandler.showInfo("Using flatfile as datasource!");

			datas = new FlatfileData();
		}

		// Setting up the DataController
		boolean caching = settings.CachingEnabled();
		datacontroller = new DataController(datas, caching);

		// Outputs the time that was needed for loading the registrations
		float timeDiff = (float) (System.currentTimeMillis() - before);
		timeDiff = timeDiff / 1000;

		if (caching) {
			MessageHandler.showInfo("Cache for registrations is enabled!");
			MessageHandler
					.showInfo(datacontroller.getRegisteredPlayerAmount()
							+ " registered players loaded in " + timeDiff
							+ " seconds!");
		} else {
			MessageHandler.showInfo("Cache for registrations is disabled!");
			MessageHandler.showInfo("There are "
					+ datacontroller.getRegisteredPlayerAmount()
					+ " registered players in database!");
		}

		MessageHandler.showInfo("Version " + this.getDescription().getVersion()
				+ " was successfully loaded!");

		// Check if the plugin was loaded under runtime or was reloaded
		if (getServer().getOnlinePlayers().length > 0) {
			onAuthMeReload();
		}

		// Setting up the listeners
		PluginManager pm = getServer().getPluginManager();

		pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener,
				Priority.Lowest, this);
		pm.registerEvent(Event.Type.PLAYER_CHAT, playerListener,
				Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener,
				Priority.Lowest, this);
		pm.registerEvent(Event.Type.PLAYER_LOGIN, playerListener,
				Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener,
				Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener,
				Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_KICK, playerListener,
				Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_PICKUP_ITEM, playerListener,
				Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener,
				Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener,
				Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener,
				Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener,
				Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_TARGET, entityListener,
				Priority.Normal, this);
	}

	public void onAuthMeReload() {
		for (Player player : getServer().getOnlinePlayers()) {
			// Is player really registered?
			boolean regged = datacontroller
					.isPlayerRegistered(player.getName());

			// Create PlayerCache
			playercache.createCache(player, regged, false);
			player.sendMessage(messages.getMessage("Alert.PluginReloaded"));
		}

		MessageHandler.showInfo("AuthMe restored the player cache!");
	}

	public void onDisable() {
		MessageHandler.showInfo("The plugin was unloaded!");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {

		if (commandLabel.equalsIgnoreCase("register")) {
			if (!(sender instanceof Player)) {
				return false;
			}
			Player player = (Player) sender;

			if (!settings.RegisterEnabled()) {
				player.sendMessage("Registrations are currently disabled!");
				return false;
			}

			if (playercache.isPlayerRegistered(player)) {
				player.sendMessage(messages
						.getMessage("Error.AlreadyRegistered"));
				return false;
			}

			Map<String, String> customFields = settings
					.getCustomInformationFields();
			Map<String, String> customInformation = new HashMap<String, String>();

			// Do we have custom tables and mysql as datasource?
			if (customFields.size() > 0
					&& settings.DataSource().equals("mysql")) {

				// Check if the player typed the right amount of fields
				if (args.length != customFields.size() + 1) {
					String usageCustomFields = "";
					for (String key : customFields.keySet()) {
						usageCustomFields = usageCustomFields + "<" + key
								+ "> ";
					}
					player.sendMessage("Usage: /register <password> "
							+ usageCustomFields);
					return false;
				}

				// Check the custom fields, if they comply to the RegEx'es and
				// extract the informations and save them in a hashtable
				int counter = 1;
				for (String key : customFields.keySet()) {
					if (!args[counter].matches(customFields.get(key))) {
						player.sendMessage(messages.getMessage(
								"Command.RegisterExtraInfoCheckFailed", key));
						return false;
					}
					customInformation.put(key, args[counter]);
					counter++;
				}

			} else {
				// Check if the player typed the right amount of fields
				if (args.length != 1) {
					player.sendMessage("Usage: /register <password>");
					return false;
				}
			}

			String password = args[0];

			boolean executed = datacontroller.saveAuth(player.getName(),
					encrypt(password), customInformation);

			if (!executed) {
				player.sendMessage(messages.getMessage("Error.DatasourceError"));
				MessageHandler
						.showError("Failed to save an auth due to an error in the datasource!");
				return false;
			}

			playercache.setPlayerAuthenticated(player, true);
			playercache.setPlayerRegistered(player, true);

			player.sendMessage(messages.getMessage("Command.RegisterResponse",
					password));
			MessageHandler.showInfo("Player " + player.getName()
					+ " is now registered!");

			return true;
		}

		if (commandLabel.equalsIgnoreCase("login")
				|| commandLabel.equalsIgnoreCase("l")) {
			if (!(sender instanceof Player)) {
				return false;
			}
			Player player = (Player) sender;

			if (!settings.LoginEnabled()) {
				player.sendMessage("Logins are currently disabled!");
				return false;
			}

			if (args.length != 1) {
				player.sendMessage("Usage: /login <password>");
				return false;
			}

			String playername = player.getName();
			String password = args[0];

			if (!playercache.isPlayerRegistered(player)) {
				player.sendMessage(messages.getMessage("Error.NotRegistered"));
				return false;
			}

			if (playercache.isPlayerAuthenticated(player)) {
				player.sendMessage(messages.getMessage("Error.AlreadyLoggedIn"));
				return false;
			}

			final String realPassword = datacontroller.getHash(playername);

			if (!realPassword.equals(encrypt(password))) {
				if (settings.KickOnWrongPassword()) {
					player.kickPlayer(messages
							.getMessage("Error.InvalidPassword"));
				} else {
					player.sendMessage(messages
							.getMessage("Error.InvalidPassword"));
				}
				MessageHandler.showInfo("Player " + player.getName()
						+ " tried to login with a wrong password!");
				return false;
			}

			LoginTimeout.removeLoginTimeout(this, player);
			performPlayerLogin(player);

			player.sendMessage(messages.getMessage("Command.LoginResponse"));
			MessageHandler.showInfo("Player " + player.getName()
					+ " logged in!");

			return true;
		}

		if (commandLabel.equalsIgnoreCase("changepassword")) {
			if (!(sender instanceof Player)) {
				return false;
			}
			Player player = (Player) sender;

			if (!settings.ChangePasswordEnabled()) {
				player.sendMessage("Changing passwords is currently disabled!");
				return false;
			}
			if (!playercache.isPlayerRegistered(player)) {
				player.sendMessage(messages.getMessage("Error.NotRegistered"));
				return false;
			}
			if (!playercache.isPlayerAuthenticated(player)) {
				player.sendMessage(messages.getMessage("Error.NotLogged"));
				return false;
			}
			if (args.length != 2) {
				player.sendMessage("Usage: /changepassword <oldpassword> <newpassword>");
				return false;
			}
			if (!datacontroller.getHash(player.getName()).equals(
					encrypt(args[0]))) {
				player.sendMessage(messages.getMessage("Error.WrongPassword"));
				return false;
			}

			boolean executed = datacontroller.updateAuth(player.getName(),
					encrypt(args[1]));

			if (!executed) {
				player.sendMessage(messages.getMessage("Error.DatasourceError"));
				MessageHandler
						.showError("Failed to update an auth due to an error in the datasource!");
				return false;
			}

			player.sendMessage(messages
					.getMessage("Command.ChangePasswordResponse"));
			MessageHandler.showInfo("Player " + player.getName()
					+ " changed his password!");
		}

		if (commandLabel.equalsIgnoreCase("logout")) {
			if (!(sender instanceof Player)) {
				return false;
			}
			Player player = (Player) sender;

			if (!settings.LogoutEnabled()) {
				player.sendMessage("Logging out is currently disabled!");
				return false;
			}

			if (!playercache.isPlayerAuthenticated(player)) {
				player.sendMessage(messages.getMessage("Error.NotLogged"));
				return false;
			}

			playercache.setPlayerAuthenticated(player, false);

			player.sendMessage(messages.getMessage("Command.LogoutResponse"));
			MessageHandler.showInfo("Player " + player.getName()
					+ " logged out!");
		}

		if (commandLabel.equalsIgnoreCase("unregister")) {
			if (!(sender instanceof Player)) {
				return false;
			}
			Player player = (Player) sender;

			if (!settings.UnregisterEnabled()) {
				player.sendMessage("Unregistering is currently disabled!");
				return false;
			}
			if (!playercache.isPlayerRegistered(player)) {
				player.sendMessage(messages.getMessage("Error.NotRegistered"));
				return false;
			}
			if (!playercache.isPlayerAuthenticated(player)) {
				player.sendMessage(messages.getMessage("Error.NotLogged"));
				return false;
			}
			if (args.length != 1) {
				player.sendMessage("Usage: /unregister <password>");
				return false;
			}
			if (!datacontroller.getHash(player.getName()).equals(
					encrypt(args[0]))) {
				player.sendMessage(messages.getMessage("Error.WrongPassword"));
				return false;
			}

			boolean executed = datacontroller.removeAuth(player.getName());

			if (!executed) {
				player.sendMessage(messages.getMessage("Error.DatasourceError"));
				MessageHandler
						.showError("Failed to remove an auth due to an error in the datasource!");
				return false;
			}

			playercache.recreateCache(player);

			player.sendMessage(messages
					.getMessage("Command.UnregisterResponse"));
			MessageHandler.showInfo("Player " + player.getName()
					+ " is now unregistered!");
		}

		if (commandLabel.equalsIgnoreCase("authme")) {

			String pre = "";

			if (sender instanceof Player) {
				Player player = (Player) sender;

				if (!player.isOp()) {
					player.sendMessage("You dont have permission to do this!");
					return false;
				}
				if (!playercache.isPlayerAuthenticated(player)) {
					player.sendMessage(messages.getMessage("Error.NotLogged"));
					return false;
				}

				pre = "/";
			}

			if (args.length == 0) {
				sender.sendMessage("Usage: "
						+ pre
						+ "authme <reloadconfig | reloadcache | toggleregs | deleteauth>");
				return false;
			}

			if (args[0].equals("deleteauth")) {
				if (args.length != 2) {
					sender.sendMessage("Usage: " + pre
							+ "authme deleteauth <playername>");
					return false;
				}
			}

			// /authme reloadcache Command
			if (args[0].equals("reloadcache")) {
				if (!settings.ReloadEnabled()) {
					sender.sendMessage("Reloading authentications is currently disabled!");
					return false;
				}
				if (!settings.CachingEnabled()) {
					sender.sendMessage(ChatColor.RED
							+ "Error: There is no need to reload the authentication cache. Caching is disabled in config anyway!");
					return false;
				}

				datacontroller = new DataController(datas, true);

				sender.sendMessage(ChatColor.GREEN
						+ "AuthMe has successfully reloaded all authentications!");

				MessageHandler
						.showInfo("Authentication cache reloaded by command!");
			}

			// /authme reloadconfig Command
			if (args[0].equals("reloadconfig")) {
				File configFile = new File(Settings.PLUGIN_FOLDER, "config.yml");
				if (!configFile.exists()) {
					extractDefaultFile("config.yml");
					configFile = new File(Settings.PLUGIN_FOLDER, "config.yml");
				}
				settings = new Settings(configFile);

				sender.sendMessage(ChatColor.GREEN
						+ "AuthMe has successfully reloaded it's config file!");

				MessageHandler.showInfo("Config file reloaded by command!");
			}

			// /authme toggleregs Command
			if (args[0].equals("toggleregs")) {
				String key = "Commands.Users.RegisterEnabled";
				if (settings.getBoolean(key, true)) {
					settings.setProperty(key, false);
					sender.sendMessage(ChatColor.GREEN
							+ "AuthMe has successfully disabled registrations!");
				} else {
					settings.setProperty(key, true);
					sender.sendMessage(ChatColor.GREEN
							+ "AuthMe has successfully enabled registrations!");
				}
			}

			// /authme deleteauth Command
			if (args[0].equals("deleteauth")) {
				if (!settings.ResetEnabled()) {
					sender.sendMessage("Reseting a authentication is currently disabled!");
					return false;
				}
				if (!datacontroller.isPlayerRegistered(args[1])) {
					sender.sendMessage(messages
							.getMessage("Error.PlayerNotRegistered"));
					return false;
				}

				boolean executed = datacontroller.removeAuth(args[1]);

				if (!executed) {
					sender.sendMessage(messages
							.getMessage("Error.DatasourceError"));
					MessageHandler
							.showError("Failed to remove an auth due to an error in the datasource!");
					return false;
				}

				// If the player is online, recreate his cache
				Player delPlayer = getServer().getPlayer(args[1]);
				if (delPlayer != null) {
					playercache.recreateCache(delPlayer);
				}

				sender.sendMessage(ChatColor.GREEN
						+ "This player is now unregistered!");
				MessageHandler.showInfo("Account of " + args[1]
						+ " got deleted by command!");
			}

		}

		return false;
	}

	public boolean checkAuth(Player player) {

		if (playercache.isPlayerAuthenticated(player)) {
			return true;
		}

		int alertInterval = settings.alertInterval();
		if (playercache.isPlayerRegistered(player)) {
			if (playercache.isAlertNeeded(player, alertInterval)) {
				player.sendMessage(messages.getMessage("Alert.Login"));
			}
			return false;
		}

		if (settings.ForceRegistration()) {
			if (playercache.isAlertNeeded(player, alertInterval)) {
				player.sendMessage(messages.getMessage("Alert.Registration"));
			}
			return false;
		} else {
			return true;
		}
	}

	// Checks if the player doesn't have to log in to do stuff(Bots?)
	public boolean checkUnrestrictedAccess(Player player) {
		List<Object> players = settings.AllowPlayerUnrestrictedAccess();
		if (!players.isEmpty()) {
			for (Object playernameObj : players) {
				String playername = (String) playernameObj;
				if (player.getName().equalsIgnoreCase(playername)
						|| player.getDisplayName().equalsIgnoreCase(playername)) {
					return true;
				}
			}
		}
		return false;
	}

	public void performPlayerLogin(Player player) {
		playercache.setPlayerAuthenticated(player, true);

		if (invcache.doesCacheExist(player.getName())) {
			InventoryArmour invarm = invcache.readCache(player.getName());

			ItemStack[] invstackbackup = invarm.getInventory();
			player.getInventory().setContents(invstackbackup);

			ItemStack[] armStackBackup = invarm.getArmour();

			if (armStackBackup[3] != null) {
				if (armStackBackup[3].getAmount() != 0) {
					player.getInventory().setHelmet(armStackBackup[3]);
				}
			}
			if (armStackBackup[2] != null) {
				if (armStackBackup[2].getAmount() != 0) {
					player.getInventory().setChestplate(armStackBackup[2]);
				}
			}
			if (armStackBackup[1] != null) {
				if (armStackBackup[1].getAmount() != 0) {
					player.getInventory().setLeggings(armStackBackup[1]);
				}
			}
			if (armStackBackup[0] != null) {
				if (armStackBackup[0].getAmount() != 0) {
					player.getInventory().setBoots(armStackBackup[0]);
				}
			}

			invcache.removeCache(player.getName());
		}
	}

	public String encrypt(String string) {
		try {
			final MessageDigest m = MessageDigest.getInstance("MD5");
			final byte[] bytes = string.getBytes();
			m.update(bytes, 0, bytes.length);
			final BigInteger i = new BigInteger(1, m.digest());

			return String.format("%1$032X", i).toLowerCase();
		} catch (final Exception e) {
		}

		return "";
	}

	public void extractDefaultFile(String name) {
		File actual = new File(Settings.PLUGIN_FOLDER, name);
		if (!actual.exists()) {

			InputStream input = this.getClass().getResourceAsStream(
					"/default/" + name);
			if (input != null) {
				FileOutputStream output = null;

				try {
					output = new FileOutputStream(actual);
					byte[] buf = new byte[8192];
					int length = 0;

					while ((length = input.read(buf)) > 0) {
						output.write(buf, 0, length);
					}

					MessageHandler.showInfo("Default file written: " + name);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						if (input != null)
							input.close();
					} catch (Exception e) {
					}

					try {
						if (output != null)
							output.close();
					} catch (Exception e) {

					}
				}
			}
		}
	}

}
