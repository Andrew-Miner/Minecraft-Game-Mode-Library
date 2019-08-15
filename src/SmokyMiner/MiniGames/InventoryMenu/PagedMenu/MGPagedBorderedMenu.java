package SmokyMiner.MiniGames.InventoryMenu.PagedMenu;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.plugin.java.JavaPlugin;

import SmokyMiner.MiniGames.InventoryMenu.MGMenuItem;

public class MGPagedBorderedMenu extends MGPagedMenu
{
	private final MGMenuItem borderItem;

	public MGPagedBorderedMenu(JavaPlugin plugin, String name, int maxSlots, Material border) 
	{
		super(plugin, name, maxSlots);

		borderItem = new MGMenuItem(plugin, border, 0, " ", null, (Sound) null);
		fillBorder(0, borderItem);
	}

	@Override
	public void clearMenu()
	{
		clearMenu();
		
		for(int i = 0; i < pages.size(); i++)
			fillBorder(i, borderItem);
	}
	
	@Override
	public MGMenuItem addItem(MGMenuItem item, int slot)
	{
		if(item != borderItem && (slot%9 == 0 || slot%9 == 8))
			throw new IllegalArgumentException("Slot parameter cannot be located at border");
		
		return super.addItem(item, slot);
	}
	
	@Override
	public MGMenuItem addItem(MGMenuItem item, int slot, int page)
	{
		if(item != borderItem && (slot%9 == 0 || slot%9 == 8))
			throw new IllegalArgumentException("Slot parameter cannot be located at border");
		
		return super.addItem(item, slot, page);
	}
	
	@Override
	@Deprecated
	public MGMenuItem removeItem(int slot)
	{
		if(slot%9 == 0 || slot%9 == 8)
			throw new IllegalArgumentException("Slot parameter cannot be located at border");
		
		return super.removeItem(slot);
	}
	
	@Override
	public MGMenuItem removeItem(MGMenuItem item)
	{
		int page = getPage(item);
		
		if(page == -1)
			return null;
		
		int slot = getSlot(item);
		
		if(slot%9 == 0 || slot%9 == 8)
			throw new IllegalArgumentException("Cannot remove border item");

		return removeItem(page, slot);
	}
	
	@Override
	public MGMenuItem removeItem(int page, int slot)
	{
		if(slot%9 == 0 || slot%9 == 8)
			throw new IllegalArgumentException("Slot parameter cannot be located at border");
		
		return super.removeItem(page, slot);
	}
	
	@Override
	@Deprecated
	public MGMenuItem moveItem(int oldSlot, int newSlot)
	{
		if(oldSlot%9 == 0 || oldSlot%9 == 8 || newSlot%9 == 0 || newSlot%9 == 8)
			throw new IllegalArgumentException("Slot parameters cannot be located at border");
		
		return super.moveItem(oldSlot, newSlot);
	}
	
	@Override
	@Deprecated
	public MGMenuItem moveItem(MGMenuItem item, int newSlot)
	{
		if(newSlot%9 == 0 || newSlot%9 == 8)
			throw new IllegalArgumentException("New Slot parameter cannot be located at border");
		
		return super.moveItem(item, newSlot);
	}
	
	@Override
	public MGMenuItem moveItem(int oldSlot, int oldPage, int newSlot, int newPage)
	{
		if(newSlot%9 == 0 || newSlot%9 == 8)
			throw new IllegalArgumentException("New Slot parameter cannot be located at border");
		
		if(oldSlot%9 == 0 || oldSlot%9 == 8)
			throw new IllegalArgumentException("Old Slot parameter cannot be located at border");
		
		return super.moveItem(oldSlot, oldPage, newSlot, newPage);
	}
	
	@Override
	public MGMenuItem moveItem(MGMenuItem item, int newSlot, int newPage)
	{
		if(newSlot%9 == 0 || newSlot%9 == 8)
			throw new IllegalArgumentException("New Slot parameter cannot be located at border");

		return super.moveItem(item,  newSlot, newPage);
	}
	
	@Override
	public int getNextSlot(int page)
	{
		if(page >= pages.size())
			return -1;
		
		int lastRow = maxSlots/9 - 1;
		HashMap<Integer, MGMenuItem> map = pages.get(page);
		
		for(int i = 0; i < maxSlots; i++)
		{
			int row = i/9;
			int col = i%9;
			
			if(row == 0 || row == lastRow || col == 0 || col == 8)
				continue;
			
			if(!map.containsKey(i))
				return i;
		}
		
		return -1;
	}
	
	@Override
	protected void addPage(int pageNumber)
	{
		while (pageNumber >= pages.size())
		{
			pages.add(new HashMap<Integer, MGMenuItem>());
			buildInventory(name, (pageNumber == 0) ? -1 : pageNumber);
			
			if(pages.size() > 1)
				addPrevPageItem(pages.size() - 1);
			
			fillBorder(pages.size() - 1, borderItem);
			

			if(pages.size() > 1)
				addNextPageItem(pages.size() - 2);
		} 
	}
	
	@Override
	protected void addPrevPageItem(int page) 
	{
		int slot = maxSlots - 17;
		MGMenuItem prevItem = new MGMenuItem(plugin, Material.MAP, PREV_PAGE_NAME + ": " + page, prevListener);
		
		MGMenuItem oldItem = addItem(prevItem, slot, page);
		
		if(oldItem != null)
		{
			try
			{
				addItem(page, oldItem);
			}
			catch(IllegalStateException e)
			{
				try { addItem(page + 1, oldItem); }
				catch(IllegalStateException e2) { addItem(oldItem); }
			}
		}
	}

	@Override
	protected void addNextPageItem(int page) 
	{
		int slot = maxSlots - 11;
		MGMenuItem nextItem = new MGMenuItem(plugin, Material.MAP, NEXT_PAGE_NAME + ": " + (int)(page + 2), nextListener);
		
		MGMenuItem oldItem = addItem(nextItem, slot, page);
		
		if(oldItem != null)
		{
			try
			{
				addItem(page, oldItem);
			}
			catch(IllegalStateException e)
			{
				try { addItem(page + 1, oldItem); }
				catch(IllegalStateException e2) { addItem(oldItem); }
			}
		}
	}
	
	protected void fillBorder(int page, MGMenuItem filler)
	{
		int lastRow = maxSlots/9 - 1;
		
		for(int i = 0; i < maxSlots; i++)
		{
			if(i/9 == 0 || i/9 == lastRow)
				addItem(filler, i, page);
			else if(i%9 == 0 || i%9 == 8)
				addItem(filler, i, page);
		}
	}
}