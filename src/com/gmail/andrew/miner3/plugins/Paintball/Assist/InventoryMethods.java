package com.gmail.andrew.miner3.plugins.Paintball.Assist;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class InventoryMethods 
{
	public static enum PlayerInvIndex
	{

	    HotBar0(0),
	    HotBar1(1),
	    HotBar2(2),
	    HotBar3(3),
	    HotBar4(4),
	    HotBar5(5),
	    HotBar6(6),
	    HotBar7(7),
	    HotBar8(8) {
	    	@Override
	    	public PlayerInvIndex next() {
	    		return values()[0];
	    	};
	    };

	    private int _value;

	    PlayerInvIndex(int Value) 
	    {
	        this._value = Value;
	    }

	    public int getValue() 
	    {
	            return _value;
	    }

	    public static PlayerInvIndex fromInt(int i) 
	    {
	        for (PlayerInvIndex b : PlayerInvIndex.values()) 
	        {
	            if (b.getValue() == i) { return b; }
	        }
	        return null;
	    }
	    
	    public PlayerInvIndex next() 
	    {
	        // No bounds checking required here, because the last instance overrides
	        return values()[ordinal() + 1];
	    }
	}
	
	public static void buildLeatherArmor(ItemStack [] armor, Color color) 
	{
		armor[3] = new ItemStack(Material.LEATHER_HELMET, 1);
		armor[2] = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
		armor[1] = new ItemStack(Material.LEATHER_LEGGINGS, 1);
		armor[0] = new ItemStack(Material.LEATHER_BOOTS, 1);
		
		
		
		for(int i = 0; i < 4; i++)
		{
			LeatherArmorMeta meta = (LeatherArmorMeta) armor[i].getItemMeta();
			meta.setColor(color);
			armor[i].setItemMeta(meta);
		}
		
	}
	
	public static Color convertChatColor(ChatColor color)
	{
		return convertChatColor("" + color);
	}
	
	public static Color convertChatColor(String chatColor)
	{
		String color = chatColor.replace("" + ChatColor.COLOR_CHAR, "");
		char c = color.charAt(0);
		
		switch(c)
		{
		case '0':
			return Color.BLACK;
		case '1':
			return Color.fromRGB(0, 0, 170);
		case '2':
			return Color.fromRGB(0, 170, 0);
		case '3':
			return Color.fromRGB(0, 170, 170);
		case '4':
			return Color.fromRGB(170, 0, 0);
		case '5':
			return Color.fromRGB(170, 0, 170);
		case '6':
			return Color.fromRGB(255, 170, 0);
		case '7':
			return Color.fromRGB(170, 170, 170);
		case '8':
			return Color.fromRGB(85, 85, 85);
		case '9':
			return Color.fromRGB(85, 85, 255);
		case 'a':
			return Color.fromRGB(85, 255, 85);
		case 'b':
			return Color.fromRGB(85, 255, 255);
		case 'c':
			return Color.fromRGB(255, 85, 85);
		case 'd':
			return Color.fromRGB(255, 85, 255);
		case 'e':
			return Color.fromRGB(255, 255, 85);
		case 'f':
			return Color.WHITE;
		default:
			return Color.WHITE;
		}
	}
}
