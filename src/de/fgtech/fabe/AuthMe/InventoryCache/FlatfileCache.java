package de.fgtech.fabe.AuthMe.InventoryCache;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

import org.bukkit.inventory.ItemStack;

import de.fgtech.fabe.AuthMe.Parameters.Settings;

public class FlatfileCache {

	public FlatfileCache() {
		final File folder = new File(Settings.CACHE_FOLDER);
		if (!folder.exists()) {
			folder.mkdirs();
		}
	}

	public void createCache(String playername, InventoryArmour invarm) {
		final File file = new File(Settings.CACHE_FOLDER + "/" + playername
				+ ".cache");

		if (file.exists()) {
			return;
		}

		FileWriter writer = null;
		try {
			file.createNewFile();

			writer = new FileWriter(file);

			ItemStack[] invstack = invarm.getInventory();

			for (int i = 0; i < invstack.length; i++) {

				int itemid = 0;
				int amount = 0;
				int durability = 0;

				if (invstack[i] != null) {
					itemid = invstack[i].getTypeId();
					amount = invstack[i].getAmount();
					durability = invstack[i].getDurability();
				}

				writer.write("i" + ":" + itemid + ":" + amount + ":"
						+ durability + "\r\n");
				writer.flush();
			}

			ItemStack[] wearstack = invarm.getArmour();

			for (int i = 0; i < wearstack.length; i++) {
				int itemid = 0;
				int amount = 0;
				int durability = 0;

				if (wearstack[i] != null) {
					itemid = wearstack[i].getTypeId();
					amount = wearstack[i].getAmount();
					durability = wearstack[i].getDurability();
				}

				writer.write("w" + ":" + itemid + ":" + amount + ":"
						+ durability + "\r\n");
				writer.flush();
			}

			writer.close();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public InventoryArmour readCache(String playername) {
		final File file = new File(Settings.CACHE_FOLDER + "/" + playername
				+ ".cache");

		ItemStack[] stacki = new ItemStack[36];
		ItemStack[] stacka = new ItemStack[4];

		if (!file.exists()) {
			return new InventoryArmour(stacki, stacka);
		}

		Scanner reader = null;
		try {
			reader = new Scanner(file);

			int i = 0;
			int a = 0;
			while (reader.hasNextLine()) {
				final String line = reader.nextLine();

				if (!line.contains(":")) {
					continue;
				}

				final String[] in = line.split(":");

				if (in.length != 4) {
					continue;
				}
				if (!in[0].equals("i") && !in[0].equals("w")) {
					continue;
				}

				if (in[0].equals("i")) {
					stacki[i] = new ItemStack(Integer.parseInt(in[1]),
							Integer.parseInt(in[2]), Short.parseShort((in[3])));
					i++;
				} else {
					stacka[a] = new ItemStack(Integer.parseInt(in[1]),
							Integer.parseInt(in[2]), Short.parseShort((in[3])));
					a++;
				}

			}
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return new InventoryArmour(stacki, stacka);
	}

	public void removeCache(String playername) {
		final File file = new File(Settings.CACHE_FOLDER + "/" + playername
				+ ".cache");

		if (file.exists()) {
			file.delete();
		}
	}

	public boolean doesCacheExist(String playername) {
		final File file = new File(Settings.CACHE_FOLDER + "/" + playername
				+ ".cache");

		if (file.exists()) {
			return true;
		}
		return false;
	}

}
