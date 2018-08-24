package com.gmail.andrew.miner3.plugins.InventoryMenu;

import java.util.List;

import org.bukkit.Material;

public interface MenuItemAnimator 
{
	public default String updateName(String name)
	{
		return name;
	}
	
	public default List<String> updateLore(List<String> lore)
	{
		return lore;
	}
	
	public default Material updateMaterial(Material material)
	{
		return material;
	}
	
	public default void itemClicked(ItemClickEvent e)
	{
		
	}
	
	public default void updateMenuSlot(MenuItem item)
	{
		
	}
}
