package de.fgtech.fabe.AuthMe.Parameters;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.util.config.Configuration;

public class Messages extends Configuration {

	private static final ConcurrentHashMap<String, String> defaults = new ConcurrentHashMap<String, String>();

	public Messages(File f) {
		super(f);

		load();
		loadDefaults();
		setDefaults();
		save();
	}

	private void loadDefaults() {
		defaults.put("Command.RegisterResponse",
				"&aRegistered using the password: &e%1");
		defaults.put("Command.UnregisterResponse",
				"&aYou are now unregistered!");
		defaults.put("Command.LoginResponse", "&aPassword accepted. Welcome.");
		defaults.put("Command.LogoutResponse", "&aSuccessfully logged out!");
		defaults.put("Command.ChangePasswordResponse",
				"&aPassword successfully changed!");
		defaults.put("Command.RegisterExtraInfoCheckFailed",
				"&cYour %1 does not match our rules!");

		defaults.put("Sessions.Hint",
				"&aYou were automatically identified and logged in. Welcome.");

		defaults.put("Alert.Login",
				"&cPlease identify yourself with /login <password>");
		defaults.put("Alert.Registration",
				"&cPlease register yourself with /register <password>");
		defaults.put("Alert.PluginReloaded",
				"&cThe server was reloaded! Please log in again...");

		defaults.put("JoinMessage.Command",
				"&eTo register, type /register <password>");
		defaults.put("JoinMessage.ForceRegistration",
				"&dYou have to register an account before playing!");
		defaults.put("JoinMessage.FreeRegistration",
				"&dYou are free to register an account for best protection of your account!");

		defaults.put("Error.NotRegistered", "&cError: You are not registered!");
		defaults.put("Error.NotLogged", "&cError: You are not logged in!");
		defaults.put("Error.WrongPassword",
				"&cError: You inserted a wrong password!");
		defaults.put("Error.PlayerNotRegistered",
				"&cError: This player is not registered!");
		defaults.put("Error.AlreadyRegistered",
				"&cError: You are already registered!");
		defaults.put("Error.AlreadyLoggedIn",
				"&cError: You are already logged in!");
		defaults.put("Error.InvalidPassword", "&cInvalid password");
		defaults.put("Error.DatasourceError",
				"&cInternal error! Please notify an admin about this issue!");
		defaults.put("Error.DatasourceError",
				"&cInternal error! Please notify an admin about this issue!");
		defaults.put("Kick.DisallowedCharacters",
				"Your playername (%1) is not set, contains invalid characters or is too short/long!");
		defaults.put("Kick.OtherUserLoggedIn",
				"There is already a player logged in with the same name! Try another one!");
		defaults.put("Kick.NotRegistered",
		"Please visit our site http://fg-tech.de/registration to register your playername!");
	}

	private void setDefaults() {
		for (String key : defaults.keySet()) {
			if (getString(key) == null)
				setProperty(key, defaults.get(key));
		}

		// clear defaults to free memory
		defaults.clear();
	}

	public String getMessage(String key) {
		return this.getString(key).replace("&", "\u00a7");
	}

	public String getMessage(String key, Object replacement) {
		return getMessage(key).replace("%1", replacement.toString());
	}
}