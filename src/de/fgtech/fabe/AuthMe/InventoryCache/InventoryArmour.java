package de.fgtech.fabe.AuthMe.InventoryCache;

import org.bukkit.inventory.ItemStack;

public class InventoryArmour {

	private ItemStack[] inventory;
	private ItemStack[] armour;	
	
	public InventoryArmour(ItemStack[] inventory, ItemStack[] armour){
		this.inventory = inventory;
		this.armour = armour;
	}
	
	public ItemStack[] getInventory(){
		return inventory;
	}
	
	public ItemStack[] getArmour(){
		return armour;
	}
}
