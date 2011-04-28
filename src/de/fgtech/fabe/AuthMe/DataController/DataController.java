package de.fgtech.fabe.AuthMe.DataController;

import java.util.Map;


import de.fgtech.fabe.AuthMe.DataController.DataSource.DataSource;
import de.fgtech.fabe.AuthMe.DataController.RegistrationCache.RegistrationCache;
import de.fgtech.fabe.AuthMe.DataController.RegistrationCache.RegistrationData;

public class DataController {

	private DataSource datas;
	private RegistrationCache regcache;
	private boolean caching = false;

	public DataController(DataSource dataf, boolean caching) {
		this.datas = dataf;
		this.caching = caching;

		if (caching) {
			this.regcache = dataf.loadAllAuths();
		}
	}

	public boolean saveAuth(String playername, String hash,
			Map<String, String> customInformation) {
		boolean executed = datas.saveAuth(playername.toLowerCase(), hash,
				customInformation);

		if (caching && executed) {
			regcache.insert(new RegistrationData(playername.toLowerCase(), hash));
		}
		return executed;
	}

	public boolean removeAuth(String playername) {
		boolean executed = datas.removeAuth(playername.toLowerCase());

		if (caching && executed) {
			regcache.remove(playername.toLowerCase());
		}
		return executed;
	}

	public boolean updateAuth(String playername, String hash) {
		boolean executed = datas.updateAuth(playername.toLowerCase(), hash);

		if (caching && executed) {
			regcache.getPlayerData(playername.toLowerCase()).setHash(hash);
		}
		return executed;
	}

	public String getHash(String playername) {
		if (caching) {
			return regcache.getHash(playername.toLowerCase());
		}
		return datas.loadHash(playername.toLowerCase());
	}

	public boolean isPlayerRegistered(String playername) {
		if (caching) {
			return regcache.isPlayerRegistered(playername.toLowerCase());
		}
		return datas.isPlayerRegistered(playername.toLowerCase());
	}

	public int getRegisteredPlayerAmount() {
		if (caching) {
			return regcache.getRegisteredPlayerAmount();
		}
		return datas.getRegisteredPlayerAmount();
	}

}
