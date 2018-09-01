package SmokyMiner.MiniGames.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class MGSpawnKit 
{
	private Color color;
	private ItemStack[] armor;
	private HashMap<Integer, ItemStack> items;
	private ArrayList<ItemStack> standAloneItems;
	
	public MGSpawnKit(ChatColor armorColor)
	{
		this.color = MGInvFunctions.convertChatColor(armorColor);
		armor = new ItemStack[4];
		
		MGInvFunctions.buildLeatherArmor(armor, this.color);
		
		items = new HashMap<Integer, ItemStack>();
		standAloneItems = new ArrayList<ItemStack>();
	}
	
	public MGSpawnKit(ChatColor armorColor, HashMap<Integer, ItemStack> items)
	{
		this.color = MGInvFunctions.convertChatColor(armorColor);
		armor = new ItemStack[4];
		
		this.items = items;
		standAloneItems = new ArrayList<ItemStack>();
		
		MGInvFunctions.buildLeatherArmor(armor, this.color);
		
		this.items = items;
	}
	
	public MGSpawnKit(Color armorColor)
	{
		this.color = armorColor;
		armor = new ItemStack[4];
		
		items = new HashMap<Integer, ItemStack>();
		standAloneItems = new ArrayList<ItemStack>();
		
		MGInvFunctions.buildLeatherArmor(armor, this.color);
	}
	
	public MGSpawnKit(Color armorColor, HashMap<Integer, ItemStack> items)
	{
		this.color = armorColor;
		armor = new ItemStack[4];
		
		this.items = items;
		standAloneItems = new ArrayList<ItemStack>();
		
		MGInvFunctions.buildLeatherArmor(armor, this.color);
		
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