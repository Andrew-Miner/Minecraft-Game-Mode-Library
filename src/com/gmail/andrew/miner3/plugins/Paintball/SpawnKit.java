package com.gmail.andrew.miner3.plugins.Paintball;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.gmail.andrew.miner3.plugins.Paintball.Assist.InventoryMethods;

public class SpawnKit 
{
	private Color color;
	private ItemStack[] armor;
	private HashMap<Integer, ItemStack> items;
	private ArrayList<ItemStack> standAloneItems;
	
	public SpawnKit(ChatColor armorColor)
	{
		this.color = InventoryMethods.convertChatColor(armorColor);
		armor = new ItemStack[4];
		
		InventoryMethods.buildLeatherArmor(armor, this.color);
		
		items = new HashMap<Integer, ItemStack>();
		standAloneItems = new ArrayList<ItemStack>();
	}
	
	public SpawnKit(ChatColor armorColor, HashMap<Integer, ItemStack> items)
	{
		this.color = InventoryMethods.convertChatColor(armorColor);
		armor = new ItemStack[4];
		
		this.items = items;
		standAloneItems = new ArrayList<ItemStack>();
		
		InventoryMethods.buildLeatherArmor(armor, this.color);
		
		this.items = items;
	}
	
	public SpawnKit(Color armorColor)
	{
		this.color = armorColor;
		armor = new ItemStack[4];
		
		items = new HashMap<Integer, ItemStack>();
		standAloneItems = new ArrayList<ItemStack>();
		
		InventoryMethods.buildLeatherArmor(armor, this.color);
	}
	
	public SpawnKit(Color armorColor, HashMap<Integer, ItemStack> items)
	{
		this.color = armorColor;
		armor = new ItemStack[4];
		
		this.items = items;
		standAloneItems = new ArrayList<ItemStack>();
		
		InventoryMethods.buildLeatherArmor(armor, this.color);
		
		this.items = items;
	}
	
	public void setItem(int slot, ItemStack item)
	{
		items.put(slot, item);
	}
	
	public void addItem(ItemStack items[])
	{
		for(int i = 0; i < items.length; i++)
		{
			standAloneItems.add(items[i]);
		}
	}
	
	public void addItem(ItemStack item)
	{
		standAloneItems.add(item);
	}
	
	public void giveKit(Player p)
	{
		PlayerInventory inv = p.getInventory();
		inv.setArmorContents(armor);
		
		for(Map.Entry<Integer, ItemStack> entry : items.entrySet())
		{
			inv.setItem(entry.getKey(), entry.getValue());
		}
		
		for(ItemStack item : standAloneItems)
		{
			inv.addItem(item);
		}
	}
}
