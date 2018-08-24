package com.gmail.andrew.miner3.plugins.InventoryMenu;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.plugin.java.JavaPlugin;

public class BorderedMenu extends InventoryMenu
{
	private final MenuItem borderItem;

	public BorderedMenu(JavaPlugin plugin, String name, int maxSlots, int color) 
	{
		super(plugin, name, maxSlots);

		borderItem = new MenuItem(plugin, Material.STAINED_GLASS_PANE, color, " ", null, (Sound) null);
		fillBorder(borderItem);
	}

	@Override
	public MenuItem addItem(MenuItem item, int slot)
	{
		if(item != borderItem && (slot%9 == 0 || slot%9 == 8))
			throw new IllegalArgumentException("Slot parameter cannot be located at border");
		
		return super.addItem(item, slot);
	}
	
	@Override
	public MenuItem removeItem(int slot)
	{
		if(slot%9 == 0 || slot%9 == 8)
			return null;
		
		return super.removeItem(slot);
	}
	
	@Override
	public MenuItem moveItem(int oldSlot, int newSlot)
	{
		if(oldSlot%9 == 0 || oldSlot%9 == 8 || newSlot%9 == 0 || newSlot%9 == 8)
			return null;
		
		return super.moveItem(oldSlot, newSlot);
	}
	
	@Override
	public MenuItem moveItem(MenuItem item, int newSlot)
	{
		if(newSlot%9 == 0 || newSlot%9 == 8)
			return null;
		
		return super.moveItem(item, newSlot);
	}
	
	protected void fillBorder(MenuItem filler)
	{
		int lastRow = maxSlots/9 - 1;
		
		for(int i = 0; i < maxSlots; i++)
		{
			if(i/9 == 0 || i/9 == lastRow)
				addItem(filler, i);
			else if(i%9 == 0 || i%9 == 8)
				addItem(filler, i);
		}
	}

}
