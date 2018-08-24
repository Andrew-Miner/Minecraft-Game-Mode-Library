package com.gmail.andrew.miner3.plugins.InventoryMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public class ItemAnimation implements MenuItemAnimator
{
	protected static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + String.valueOf(ChatColor.COLOR_CHAR) + "[0-9A-FK-OR]");
	
	protected String name;
	protected ArrayList<String> lore;
	protected Material material;
	protected InventoryMenu menu;
	
	
	public ItemAnimation(InventoryMenu menu)
	{
		this.menu = menu;
		
		name = null;
		lore = null;
		material = null;
	}
	
	public ItemAnimation(InventoryMenu menu, String itemName)
	{
		this.menu = menu;
		
		name = itemName;
		lore = null;
		material = null;
	}
	
	public ItemAnimation(InventoryMenu menu, String itemName, List<String> itemLore)
	{
		this.menu = menu;
		
		name = itemName;
		lore = (ArrayList<String>) itemLore;
		material = null;
	}
	
	public ItemAnimation(InventoryMenu menu, String itemName, List<String> itemLore, Material material)
	{
		this.menu = menu;
		
		name = itemName;
		lore = (ArrayList<String>) itemLore;
		this.material = material;
	}
	
	@Override
	public String updateName(String name)
	{
		if(this.name == null)
			this.name = String.copyValueOf(name.toCharArray());
		
		return name;
	}
	
	@Override
	public List<String> updateLore(List<String> lore)
	{
		if(this.lore == null)
			lore = this.lore;
		
		return lore;
	}
	
	@Override
	public Material updateMaterial(Material material)
	{
		if(this.material == null)
			this.material = material;
		
		return material;
	}
	
	@Override
	public void updateMenuSlot(MenuItem item)
	{
		int slot = menu.getSlot(item);
		
		if(slot == -1)
			return;
		
		menu.moveItem(slot, slot);
	}
}
