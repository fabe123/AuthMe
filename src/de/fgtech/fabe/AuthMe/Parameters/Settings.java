package de.fgtech.fabe.AuthMe.Parameters;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.util.config.Configuration;

public class Settings extends Configuration {

	public static String PLUGIN_FOLDER = "./plugins/AuthMe";
	public static String CACHE_FOLDER = Settings.PLUGIN_FOLDER + "/cache";
	public static String AUTH_FILE = Settings.PLUGIN_FOLDER + "/auths.db";

	public Settings(File f) {
		super(f);
		if (f.exists()) {
			load();
		} else {
			writeFile();
		}
	}

	public void writeFile() {

		MySQLConnectionHost();
		MySQLConnectionPort();
		MySQLConnectionDatabase();
		MySQLConnectionUsername();
		MySQLConnectionPassword();
		MySQLCustomTableName();
		MySQLCustomColumnName();
		MySQLCustomColumnPassword();

		RegisterEnabled();
		LoginEnabled();
		ChangePasswordEnabled();
		UnregisterEnabled();
		ResetEnabled();
		ReloadEnabled();
		ForceRegistration();
		LoginSessionsEnabled();
		MaximalTimePeriod();
		PlayerNameMinLength();
		PlayerNameMaxLength();
		PlayerNameRegex();
		WalkAroundSpawnEnabled();
		WalkAroundSpawnRadius();
		AllowUnregisteredChat();
		alertInterval();
		CachingEnabled();
		SessionIPCheckEnabled();
		KickOnWrongPassword();
		loginTimeout();
		getCustomInformationKeys();
		AllowAllowNonLoggedInCommand();
		AllowAllowNonRegisteredCommand();
		AllowPlayerUnrestrictedAccess();
		KickNonRegistered();

		DataSource();

		this.save();
	}

	public boolean RegisterEnabled() {
		String key = "Commands.Users.RegisterEnabled";
		if (this.getString(key) == null) {
			this.setProperty(key, true);
		}
		return this.getBoolean(key, true);
	}

	public boolean LoginEnabled() {
		String key = "Commands.Users.LoginEnabled";
		if (this.getString(key) == null) {
			this.setProperty(key, true);
		}
		return this.getBoolean(key, true);
	}
	
	public boolean LogoutEnabled() {
		String key = "Commands.Users.LogoutEnabled";
		if (this.getString(key) == null) {
			this.setProperty(key, true);
		}
		return this.getBoolean(key, true);
	}

	public boolean ChangePasswordEnabled() {
		String key = "Commands.Users.ChangePasswordEnabled";
		if (this.getString(key) == null) {
			this.setProperty(key, true);
		}
		return this.getBoolean(key, true);
	}

	public boolean UnregisterEnabled() {
		String key = "Commands.Users.UnregisterEnabled";
		if (this.getString(key) == null) {
			this.setProperty(key, true);
		}
		return this.getBoolean(key, true);
	}

	public boolean ResetEnabled() {
		String key = "Commands.Ops.ResetAuthEnabled";
		if (this.getString(key) == null) {
			this.setProperty(key, true);
		}
		return this.getBoolean(key, true);
	}

	public boolean ReloadEnabled() {
		String key = "Commands.Ops.ReloadAuthsEnabled";
		if (this.getString(key) == null) {
			this.setProperty(key, true);
		}
		return this.getBoolean(key, true);
	}

	public boolean ForceRegistration() {
		String key = "Misc.ForceRegistration";
		if (this.getString(key) == null) {
			this.setProperty(key, true);
		}
		return this.getBoolean(key, true);
	}

	public int PlayerNameMinLength() {
		String key = "PlayerNameRestriction.PlayerNameMinLength";
		if (this.getString(key) == null) {
			this.setProperty(key, 3);
		}
		return this.getInt(key, 3);
	}

	public int PlayerNameMaxLength() {
		String key = "PlayerNameRestriction.PlayerNameMaxLength";
		if (this.getString(key) == null) {
			this.setProperty(key, 20);
		}
		return this.getInt(key, 20);
	}

	public String PlayerNameRegex() {
		String key = "PlayerNameRestriction.PlayerNameRegex";
		if (this.getString(key) == null) {
			this.setProperty(key, "[a-zA-Z0-9_?]*");
		}
		return this.getString(key, "[a-zA-Z0-9_?]*");
	}

	public boolean LoginSessionsEnabled() {
		String key = "LoginSessions.Enabled";
		if (this.getString(key) == null) {
			this.setProperty(key, false);
		}
		return this.getBoolean(key, false);
	}

	public int MaximalTimePeriod() {
		String key = "LoginSessions.MaximalTimePeriod";
		if (this.getProperty(key) == null) {
			this.setProperty(key, 300);
		}
		return this.getInt(key, 300);
	}

	public boolean WalkAroundSpawnEnabled() {
		String key = "Misc.AllowNonRegistered.WalkAroundSpawn.Enabled";
		if (this.getString(key) == null) {
			this.setProperty(key, false);
		}
		return this.getBoolean(key, false);
	}
	
	public boolean KickNonRegistered() {
		String key = "Misc.KickNonRegistered";
		if (this.getString(key) == null) {
			this.setProperty(key, false);
		}
		return this.getBoolean(key, false);
	}

	public int WalkAroundSpawnRadius() {
		String key = "Misc.AllowNonRegistered.WalkAroundSpawn.Radius";
		if (this.getProperty(key) == null) {
			this.setProperty(key, 20);
		}
		return this.getInt(key, 20);
	}

	public boolean AllowUnregisteredChat() {
		String key = "Misc.AllowNonRegistered.Chat";
		if (this.getString(key) == null) {
			this.setProperty(key, false);
		}
		return this.getBoolean(key, false);
	}
	
	public List<Object> AllowAllowNonLoggedInCommand() {
		String key = "Misc.AllowNonLoggedIn.Commands";
		if (this.getString(key) == null) {
			List<String> arg1 = new ArrayList<String>();
			arg1.add("uptime");
			this.setProperty(key, arg1);
		}
		return this.getList(key);
	}
	
	public List<Object> AllowPlayerUnrestrictedAccess() {
		String key = "Misc.AllowPlayerUnrestrictedAccess";
		if (this.getString(key) == null) {
			List<String> arg1 = new ArrayList<String>();
			arg1.add("Bot01");
			this.setProperty(key, arg1);
		}
		return this.getList(key);
	}
	
	public List<Object> AllowAllowNonRegisteredCommand() {
		String key = "Misc.AllowNonRegistered.Commands";
		if (this.getString(key) == null) {
			List<String> arg1 = new ArrayList<String>();
			arg1.add("uptime");
			this.setProperty(key, arg1);
		}
		return this.getList(key);
	}

	public int alertInterval() {
		String key = "Misc.AlertInterval";
		if (this.getProperty(key) == null) {
			this.setProperty(key, 5);
		}
		return this.getInt(key, 5);
	}

	public boolean CachingEnabled() {
		String key = "DataController.CacheEnabled";
		if (this.getString(key) == null) {
			this.setProperty(key, true);
		}
		return this.getBoolean(key, true);
	}

	public boolean SessionIPCheckEnabled() {
		String key = "LoginSessions.IPCheckEnabled";
		if (this.getString(key) == null) {
			this.setProperty(key, true);
		}
		return this.getBoolean(key, true);
	}

	public String MySQLConnectionHost() {
		String key = "MySQL.Connection.Host";
		if (this.getString(key) == null) {
			this.setProperty(key, "localhost");
		}
		return this.getString(key, "localhost");
	}

	public int MySQLConnectionPort() {
		String key = "MySQL.Connection.Port";
		if (this.getProperty(key) == null) {
			this.setProperty(key, 3306);
		}
		return this.getInt(key, 3306);
	}

	public String MySQLConnectionDatabase() {
		String key = "MySQL.Connection.Database";
		if (this.getString(key) == null) {
			this.setProperty(key, "minecraft");
		}
		return this.getString(key, "minecraft");
	}

	public String MySQLConnectionUsername() {
		String key = "MySQL.Connection.Username";
		if (this.getString(key) == null) {
			this.setProperty(key, "minecraft");
		}
		return this.getString(key, "minecraft");
	}

	public String MySQLConnectionPassword() {
		String key = "MySQL.Connection.Password";
		if (this.getString(key) == null) {
			this.setProperty(key, "minecraft");
		}
		return this.getString(key, "minecraft");
	}

	public String MySQLCustomTableName() {
		String key = "MySQL.Custom.TableName";
		if (this.getString(key) == null) {
			this.setProperty(key, "authme");
		}
		return this.getString(key, "authme");
	}

	public String MySQLCustomColumnName() {
		String key = "MySQL.Custom.ColumnUsername";
		if (this.getString(key) == null) {
			this.setProperty(key, "username");
		}
		return this.getString(key, "username");
	}

	public String MySQLCustomColumnPassword() {
		String key = "MySQL.Custom.ColumnPassword";
		if (this.getString(key) == null) {
			this.setProperty(key, "password");
		}
		return this.getString(key, "password");
	}

	public String DataSource() {
		String key = "DataController.Datasource";
		if (this.getString(key) == null) {
			this.setProperty(key, "flatfile");
		}
		return this.getString(key, "flatfile");
	}

	public boolean KickOnWrongPassword() {
		String key = "Misc.KickOnWrongPassword";
		if (this.getString(key) == null) {
			this.setProperty(key, true);
		}
		return this.getBoolean(key, true);
	}

	public int loginTimeout() {
		String key = "Misc.LoginTimeout";
		if (this.getProperty(key) == null) {
			this.setProperty(key, 90);
		}
		return this.getInt(key, 90);
	}

	public List<String> getCustomInformationKeys() {
		String key = "MySQL.Custom.RegistrationInfo";
		if (this.getProperty(key) == null) {
			// this.setProperty(key + ".email", "[abc1]");
			// this.setProperty(key + ".birthday", "[abc2]");
		}
		return this.getKeys(key);
	}

	public Map<String, String> getCustomInformationFields() {
		List<String> keys = getCustomInformationKeys();

		Map<String, String> hashes = new HashMap<String, String>();

		if (keys != null) {
			for (String keyEnd : keys) {
				String key = "MySQL.Custom.RegistrationInfo." + keyEnd;
				if (this.getProperty(key) != null) {
					String getter = this.getString(key);
					hashes.put(keyEnd, getter);
				}
			}
		}
		return hashes;
	}
}
