package de.fgtech.fabe.AuthMe.DataController.DataSource;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;


import de.fgtech.fabe.AuthMe.DataController.RegistrationCache.RegistrationCache;
import de.fgtech.fabe.AuthMe.DataController.RegistrationCache.RegistrationData;
import de.fgtech.fabe.AuthMe.Parameters.Settings;

public class FlatfileData extends DataSource {

	public FlatfileData() {
		String authFolder = Settings.AUTH_FILE.substring(0,
				Settings.AUTH_FILE.lastIndexOf("/"));
		final File folder = new File(authFolder);
		if (!folder.exists()) {
			folder.mkdirs();
		}
	}

	public RegistrationCache loadAllAuths() {
		RegistrationCache regcache = new RegistrationCache();

		final File file = new File(Settings.AUTH_FILE);

		if (!file.exists()) {
			return new RegistrationCache();
		}

		Scanner reader = null;
		try {
			reader = new Scanner(file);
			while (reader.hasNextLine()) {
				final String line = reader.nextLine();

				if (!line.contains(":")) {
					continue;
				}

				final String[] in = line.split(":");

				if (in.length != 2) {
					continue;
				}
				regcache.insert(new RegistrationData(in[0].toLowerCase(), in[1]));

			}
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return regcache;
	}

	@Override
	public boolean saveAuth(String username, String hash, Map<String, String> customInformation) {
		BufferedWriter bw = null;

		try {
			bw = new BufferedWriter(new FileWriter(Settings.AUTH_FILE, true));
			bw.write(username + ":" + hash + "\r\n");
			bw.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (bw != null)
				try {
					bw.close();
				} catch (IOException e) {
					return false;
				}
		}
		return true;
	}

	@Override
	public boolean removeAuth(String username) {

		File file = new File(Settings.AUTH_FILE);
		File tempFile = new File(Settings.AUTH_FILE + ".tmp");

		if (!file.exists()) {
			return false;
		}

		BufferedReader reader = null;
		BufferedWriter writer = null;
		try {
			reader = new BufferedReader(new FileReader(file));

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}

		try {
			writer = new BufferedWriter(new FileWriter(tempFile));
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		String currentLine;
		try {
			while ((currentLine = reader.readLine()) != null) {
				String line = currentLine.trim();

				if (!line.contains(":")) {
					continue;
				}

				final String[] in = line.split(":");

				if (in.length != 2) {
					continue;
				}

				if (in[0].equals(username))
					continue;
				writer.write(currentLine + "\r\n");
			}

			writer.close();
			reader.close();

			// Delete the original file
			if (!file.delete()) {
				System.out.println("Could not delete file");
				return false;
			}

			// Rename the new file to the filename the original file had.
			if (!tempFile.renameTo(file)){
				System.out.println("Could not rename file");
				return false;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean updateAuth(String username, String hash) {
		boolean checkRem = removeAuth(username);
		boolean checkSave = saveAuth(username, hash, null);
		if(checkRem && checkSave){
			return true;
		}
		return false;
	}

	@Override
	public String loadHash(String playername) {
		final File file = new File(Settings.AUTH_FILE);

		if (!file.exists()) {
			return null;
		}

		Scanner reader = null;
		try {
			reader = new Scanner(file);
			while (reader.hasNextLine()) {
				final String line = reader.nextLine();

				if (!line.contains(":")) {
					continue;
				}

				final String[] in = line.split(":");

				if (in.length != 2) {
					continue;
				}

				if (in[0].equals(playername)) {
					return in[1];
				}
			}
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return null;
	}

	@Override
	public boolean isPlayerRegistered(String playername) {
		return (loadHash(playername) != null ? true : false);
	}

	@Override
	public int getRegisteredPlayerAmount() {
		int counter = 0;
		final File file = new File(Settings.AUTH_FILE);

		if (!file.exists()) {
			return 0;
		}

		Scanner reader = null;
		try {
			reader = new Scanner(file);
			while (reader.hasNextLine()) {
				final String line = reader.nextLine();

				if (!line.contains(":")) {
					continue;
				}

				final String[] in = line.split(":");

				if (in.length != 2) {
					continue;
				}

				counter++;
			}
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return counter;
	}

}
