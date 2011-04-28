package de.fgtech.fabe.AuthMe.DataController.DataSource;


import de.fgtech.fabe.AuthMe.DataController.RegistrationCache.RegistrationCache;
import de.fgtech.fabe.AuthMe.DataController.RegistrationCache.RegistrationData;
import de.fgtech.fabe.AuthMe.MessageHandler.MessageHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

public class MySQLData extends DataSource {

	private Connection connection = null;
	private Statement statement = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;
	private String host;
	private int port;
	private String database;
	private String username;
	private String password;
	private String tableName;
	private String columnName;
	private String columnPassword;

	public MySQLData(String host, int port, String database, String username,
			String password, String tableName, String columnName,
			String columnPassword) {
		this.host = host;
		this.port = port;
		this.database = database;
		this.username = username;
		this.password = password;
		this.tableName = tableName;
		this.columnName = columnName;
		this.columnPassword = columnPassword;

		// Setup MySQL tables
		connect();
		setup();
	}

	public void connect() {
		try {
			// This will load the MySQL driver
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			;
			// Setup the connection with the DB
			connection = DriverManager.getConnection("jdbc:mysql://" + host
					+ ":" + port + "/" + database + "?" + "user=" + username
					+ "&password=" + password);

			// Statements allow to issue SQL queries to the database
			statement = connection.createStatement();
		} catch (Exception e) {
			MessageHandler
					.showError("[MySQL] Unable to establish a connection to your MySQL database!");
			System.out.println(e);
		}
	}

	// Setup up the tables, if they don't exist already
	public void setup() {
		try {
			if (connection.isClosed())
				connect();

			preparedStatement = connection
					.prepareStatement("CREATE TABLE IF NOT EXISTS " + tableName
							+ " (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
							+ columnName + " VARCHAR(20) NOT NULL, "
							+ columnPassword + " VARCHAR(36) NOT NULL);");
			preparedStatement.executeUpdate();

		} catch (Exception e) {
			MessageHandler.showError("[MySQL] Couldn't create a '" + tableName
					+ "' table in your MySQL database!");
		}
	}

	// You need to close the resultSet
	@SuppressWarnings("unused")
	private void close() {
		try {
			if (resultSet != null) {
				resultSet.close();
			}

			if (statement != null) {
				statement.close();
			}

			if (connection != null) {
				connection.close();
			}
		} catch (Exception e) {

		}
	}

	@Override
	public RegistrationCache loadAllAuths() {
		RegistrationCache regcache = new RegistrationCache();
		try {
			if (connection.isClosed())
				connect();

			preparedStatement = connection.prepareStatement("SELECT * FROM "
					+ tableName);
			resultSet = preparedStatement.executeQuery();

			String user = "";
			String password = "";
			while (resultSet.next()) {
				user = resultSet.getString(columnName);
				password = resultSet.getString(columnPassword);

				if (user != "" && password != "") {
					regcache.insert(new RegistrationData(user, password));
					user = "";
					password = "";
				}
			}

		} catch (Exception e) {
			MessageHandler.showError("[MySQL] Unable to load authentications!");
		}
		return regcache;
	}

	@Override
	public boolean saveAuth(String playername, String hash,
			Map<String, String> customInformation) {
		try {
			if (connection.isClosed())
				connect();

			String addCustomInfo = "";
			if (!customInformation.isEmpty()) {
				for (String key : customInformation.keySet()) {
					addCustomInfo = ", " + key + "='"
							+ customInformation.get(key) + "'";
				}
			}

			preparedStatement = connection.prepareStatement("INSERT INTO "
					+ tableName + " SET " + columnName + "=?, "
					+ columnPassword + "=?" + addCustomInfo);
			// VALUES (null, ?, ?)
			preparedStatement.setString(1, playername);
			preparedStatement.setString(2, hash);
			preparedStatement.executeUpdate();

		} catch (Exception e) {
			MessageHandler
					.showError("[MySQL] Unable to save an authentication!");
			return false;
		}
		return true;
	}

	@Override
	public boolean updateAuth(String playername, String hash) {
		try {
			if (connection.isClosed())
				connect();

			preparedStatement = connection.prepareStatement("UPDATE "
					+ tableName + " SET " + columnPassword + " = ? WHERE "
					+ columnName + " = ?");
			preparedStatement.setString(1, hash);
			preparedStatement.setString(2, playername);
			preparedStatement.executeUpdate();

		} catch (Exception e) {
			MessageHandler
					.showError("[MySQL] Unable to update an authentication!");
			return false;
		}
		return true;
	}

	@Override
	public boolean removeAuth(String playername) {
		try {
			if (connection.isClosed())
				connect();

			preparedStatement = connection.prepareStatement("DELETE FROM "
					+ tableName + " WHERE " + columnName + " = ?");
			preparedStatement.setString(1, playername);
			preparedStatement.executeUpdate();

		} catch (Exception e) {
			MessageHandler
					.showError("[MySQL] Unable to remove an authentication!");
			return false;
		}
		return true;
	}

	@Override
	public String loadHash(String playername) {
		try {
			if (connection.isClosed())
				connect();

			preparedStatement = connection.prepareStatement("SELECT "
					+ columnPassword + " FROM " + tableName + " WHERE "
					+ columnName + " = ?");
			preparedStatement.setString(1, playername);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				return resultSet.getString(1);
			}
		} catch (Exception e) {
			MessageHandler
					.showError("[MySQL] Unable to load an authentication!");
		}
		return null;
	}

	@Override
	public boolean isPlayerRegistered(String playername) {
		try {
			if (connection.isClosed())
				connect();

			preparedStatement = connection
					.prepareStatement("SELECT COUNT(*) FROM " + tableName
							+ " WHERE " + columnName + " = ?");
			preparedStatement.setString(1, playername);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				return (resultSet.getInt(1) <= 0 ? false : true);
			}
		} catch (Exception e) {
			MessageHandler
					.showError("[MySQL] Unable to check an authentication!");
		}
		return false;
	}

	@Override
	public int getRegisteredPlayerAmount() {
		try {
			if (connection.isClosed())
				connect();

			preparedStatement = connection
					.prepareStatement("SELECT COUNT(*) FROM " + tableName);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				return resultSet.getInt(1);
			}

		} catch (Exception e) {
			MessageHandler
					.showError("[MySQL] Unable to count authentications!");
		}
		return 0;
	}

}
