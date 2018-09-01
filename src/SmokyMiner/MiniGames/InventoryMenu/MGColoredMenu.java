package SmokyMiner.MiniGames.InventoryMenu;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.plugin.java.JavaPlugin;

public class MGColoredMenu extends MGInventoryMenu
{
	private final MGMenuItem fillerItem;
	
	public MGColoredMenu(JavaPlugin plugin, String name, int maxSlots, int color) 
	{
		super(plugin, name, maxSlots);
		fillerItem = new MGMenuItem(plugin, Material.LEGACY_STAINED_GLASS_PANE, color, " ", null, (Sound) null);
		fillMenu(fillerItem);
	}
	
	@Override
	public MGMenuItem removeItem(int slot)
	{
		if(slot < 0 || slot >= maxSlots)
			return null;
		
		MGMenuItem item = menuItems.remove(slot);
		
		if(item == fillerItem)
			return null;
		
		inv.setItem(slot, fillerItem.getItemStack());
		item.close();
		
		return item;
	}
	
	@Override
	public MGMenuItem moveItem(int oldSlot, int newSlot)
	{
		if(oldSlot < 0 || oldSlot >= maxSlots)
			return null;
		
		if(newSlot < 0 || newSlot >= maxSlots)
			return null;
		
		MGMenuItem item = getItem(oldSlot);
		
		if(item == fillerItem)
			return null;
		
		menuItems.remove(oldSlot);
		menuItems.put(newSlot, item);
		
		inv.setItem(oldSlot, fillerItem.getItemStack());
		inv.setItem(newSlot, item.getItemStack());
		
		return item;
	}
	
	@Override
	public MGMenuItem moveItem(MGMenuItem item, int newSlot)
	{
		if(item == fillerItem)
			return null;
		
		if(newSlot < 0 || newSlot >= maxSlots)
			return null;
		
		int slot = getSlot(item);
		
		if(slot < 0 || slot >= maxSlots)
			return null;
		
		MGMenuItem oldItem = menuItems.get(newSlot);
		
		menuItems.remove(slot);
		menuItems.put(newSlot, item);
		
		inv.setItem(slot, null);
		inv.setItem(newSlot, item.getItemStack());
		
		return oldItem; 
	}
	
	protected void fillMenu(MGMenuItem filler) 
	{
		for(int i = 0; i < maxSlots; i++)
		{
			addItem(filler, i);
		}
	}
}