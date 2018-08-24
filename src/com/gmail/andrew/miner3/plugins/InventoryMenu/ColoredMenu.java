package com.gmail.andrew.miner3.plugins.InventoryMenu;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.plugin.java.JavaPlugin;

public class ColoredMenu extends InventoryMenu
{
	private final MenuItem fillerItem;
	
	public ColoredMenu(JavaPlugin plugin, String name, int maxSlots, int color) 
	{
		super(plugin, name, maxSlots);
		fillerItem = new MenuItem(plugin, Material.STAINED_GLASS_PANE, color, " ", null, (Sound) null);
		fillMenu(fillerItem);
	}
	
	@Override
	public MenuItem removeItem(int slot)
	{
		if(slot < 0 || slot >= maxSlots)
			return null;
		
		MenuItem item = menuItems.remove(slot);
		
		if(item == fillerItem)
			return null;
		
		inv.setItem(slot, fillerItem.getItemStack());
		item.close();
		
		return item;
	}
	
	@Override
	public MenuItem moveItem(int oldSlot, int newSlot)
	{
		if(oldSlot < 0 || oldSlot >= maxSlots)
			return null;
		
		if(newSlot < 0 || newSlot >= maxSlots)
			return null;
		
		MenuItem item = getItem(oldSlot);
		
		if(item == fillerItem)
			return null;
		
		menuItems.remove(oldSlot);
		menuItems.put(newSlot, item);
		
		inv.setItem(oldSlot, fillerItem.getItemStack());
		inv.setItem(newSlot, item.getItemStack());
		
		return item;
	}
	
	@Override
	public MenuItem moveItem(MenuItem item, int newSlot)
	{
		if(item == fillerItem)
			return null;
		
		if(newSlot < 0 || newSlot >= maxSlots)
			return null;
		
		int slot = getSlot(item);
		
		if(slot < 0 || slot >= maxSlots)
			return null;
		
		MenuItem oldItem = menuItems.get(newSlot);
		
		menuItems.remove(slot);
		menuItems.put(newSlot, item);
		
		inv.setItem(slot, null);
		inv.setItem(newSlot, item.getItemStack());
		
		return oldItem; 
	}
	
	protected void fillMenu(MenuItem filler) 
	{
		for(int i = 0; i < maxSlots; i++)
		{
			addItem(filler, i);
		}
	}
}
