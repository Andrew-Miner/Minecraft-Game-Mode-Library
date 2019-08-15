package SmokyMiner.MiniGames.InventoryMenu;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.plugin.java.JavaPlugin;

public class MGBorderedMenu extends MGInventoryMenu
{
	private final MGMenuItem borderItem;

	public MGBorderedMenu(JavaPlugin plugin, String name, int maxSlots, Material border) 
	{
		super(plugin, name, maxSlots);

		borderItem = new MGMenuItem(plugin, border, 0, " ", null, (Sound) null);
		fillBorder(borderItem);
	}

	@Override
	public MGMenuItem addItem(MGMenuItem item, int slot)
	{
		if(item != borderItem && (slot%9 == 0 || slot%9 == 8))
			throw new IllegalArgumentException("Slot parameter cannot be located at border");
		
		return super.addItem(item, slot);
	}
	
	@Override
	public MGMenuItem removeItem(int slot)
	{
		if(slot%9 == 0 || slot%9 == 8)
			return null;
		
		return super.removeItem(slot);
	}
	
	@Override
	public MGMenuItem moveItem(int oldSlot, int newSlot)
	{
		if(oldSlot%9 == 0 || oldSlot%9 == 8 || newSlot%9 == 0 || newSlot%9 == 8)
			return null;
		
		return super.moveItem(oldSlot, newSlot);
	}
	
	@Override
	public MGMenuItem moveItem(MGMenuItem item, int newSlot)
	{
		if(newSlot%9 == 0 || newSlot%9 == 8)
			return null;
		
		return super.moveItem(item, newSlot);
	}
	
	protected void fillBorder(MGMenuItem filler)
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
