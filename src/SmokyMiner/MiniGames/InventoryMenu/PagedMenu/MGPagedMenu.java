package SmokyMiner.MiniGames.InventoryMenu.PagedMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import SmokyMiner.MiniGames.InventoryMenu.MGInventoryMenu;
import SmokyMiner.MiniGames.InventoryMenu.MGMenuItem;
import SmokyMiner.MiniGames.InventoryMenu.MGMenuTools;

public class MGPagedMenu extends MGInventoryMenu
{
	// TODO: Use Different Inventories for each page
	// 		 Otherwise if someone changes a page it changes for everyone
	// TODO: Open correct inventory in PageListener using next/prev name
	protected final String PREV_PAGE_NAME = ChatColor.GRAY + "" + ChatColor.ITALIC + "Previous Page";
	protected final String NEXT_PAGE_NAME = ChatColor.GRAY + "" + ChatColor.ITALIC + "Next Page";
	
	protected MGPrevPageListener prevListener;
	protected MGNextPageListener nextListener;

	protected ArrayList<Inventory> inventories;
	protected ArrayList<HashMap<Integer, MGMenuItem>> pages;
	
	protected HashMap<UUID, Integer> curPages;
	protected String name;
	
	public MGPagedMenu(JavaPlugin plugin, String name, int maxSlots)
	{
		super(plugin, name, maxSlots);

		this.name = name;
		
		prevListener = new MGPrevPageListener();
		nextListener = new MGNextPageListener();
		
		curPages = new HashMap<UUID, Integer>();
		
		inventories = new ArrayList<Inventory>();
		inv = null;
		
		pages = new ArrayList<HashMap<Integer, MGMenuItem>>();
		menuItems = null;
		
		addPage(0);
	}
	
	@EventHandler
	public void playerLeft(PlayerKickEvent e)
	{
		curPages.remove(e.getPlayer().getUniqueId());
	}
	
	@EventHandler
	public void playerQuit(PlayerQuitEvent e)
	{
		curPages.remove(e.getPlayer().getUniqueId());
	}
	
//	@EventHandler
//	public void inventoryCloseEvent(InventoryCloseEvent e)
//	{
//		if(inventories.contains(e.getInventory()))
//			curPages.remove(e.getPlayer().getUniqueId());
//	}
	
	@Override
	@EventHandler
	public void onItemClick(InventoryClickEvent e)
	{
		int page = getPage(e.getInventory());
		
		if(page != -1)
		{
			e.setCancelled(true);
			
			MGMenuItem item = getItem(e.getSlot(), page);
			
			if(item != null)
				item.clicked(this, e.getClick(), e.getWhoClicked());
		}
	}
	
	@Override
	public void clearMenu()
	{
		inventories.forEach((invt) -> invt.clear());
		curPages.forEach((id, page) -> page = 0);
		
		pages.clear();
		pages.add(new HashMap<Integer, MGMenuItem>());
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public HashMap<Integer, MGMenuItem> getItems()
	{
		return (HashMap<Integer, MGMenuItem>) pages.get(0).clone();
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<HashMap<Integer, MGMenuItem>> getAllItems()
	{
		return (ArrayList<HashMap<Integer, MGMenuItem>>) pages.clone();
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<Integer, MGMenuItem> getPage(int page)
	{
		if(page >= pages.size())
			return null;
		return (HashMap<Integer, MGMenuItem>) pages.get(page).clone();
	}
	
	@Override
	@Deprecated
	public MGMenuItem getItem(int slot)
	{
		return null;
	}
	
	public int getPage(Inventory inventory)
	{
		if(inventories.contains(inventory))
			return inventories.indexOf(inventory);
		
		return -1;
	}
	
	public MGMenuItem getItem(int slot, int page)
	{
		if(page >= pages.size())
			return null;
		return pages.get(page).get(slot);
	}
	
	@Override
	public int getSlot(MGMenuItem menuItem)
	{
		for(HashMap<Integer, MGMenuItem> page : pages)
		{
			for(Map.Entry<Integer, MGMenuItem> item : page.entrySet())
			{
				if(item.getValue().equals(menuItem))
					return item.getKey();
			}
		}
		
		return -1;
	}
	
	public int getPage(MGMenuItem menuItem)
	{
		for(int i = 0; i < pages.size(); i++)
		{
			if(pages.get(i).containsValue(menuItem))
				return i;
		}
		
		return -1;
	}
	
	@Override
	public int itemCount()
	{
		int count = 0;
		
		for(HashMap<Integer, MGMenuItem> page : pages)
		{
			count += page.size();
		}
		
		return count;
	}
	
	@Override
	public int size()
	{
		return maxSlots;
	}
	
	public int getPageSize(int page)
	{
		int slots = maxSlots;
		
		if(page != 0)
			slots--;
		
		if(page < pages.size() - 1)
			slots--;
		
		return slots;
	}
	
	public int pageCount()
	{
		return pages.size();
	}
	
	public int getCurrentPage(UUID playerId)
	{
		if(curPages.containsKey(playerId))
			return curPages.get(playerId);
		return 0;
	}
	
	@Override
	public void openMenu(Player p)
	{
		curPages.put(p.getUniqueId(), 0);
		p.openInventory(inventories.get(0));
	}
	
	public void openMenu(Player p, int page)
	{
		if(page >= pages.size())
			throw new IllegalArgumentException("Page index out of bounds");
		
		curPages.put(p.getUniqueId(), page);
		p.openInventory(inventories.get(page));
	}
	
	@Override
	public void closeMenu(Player p)
	{
		if(inventories.get(curPages.get(p.getUniqueId())).equals(p.getOpenInventory().getTopInventory()))
			p.closeInventory();
	}
	
	@Override
	public MGMenuItem addItem(MGMenuItem item, int slot)
	{
		int pageNumber = MGMenuTools.getOpenPage(pages, maxSlots);
		HashMap<Integer, MGMenuItem> page = pages.get(pageNumber);
		
		if(slot < 0 || slot >= maxSlots)
			throw new IllegalArgumentException("Slot parameter must be less than maximum slots value");
		
		MGMenuItem oldItem = getItem(slot);
		
		if(oldItem != null)
		{
			if(oldItem.getListeners().contains(nextListener))
				throw new IllegalArgumentException("Cannot place MenuItem over Next Page item");
			else if(oldItem.getListeners().contains(prevListener))
				throw new IllegalArgumentException("Cannot place MenuItem over Previous Page item");
		}
		
		page.put(slot, item);
		
		if(item != null)
			inventories.get(pageNumber).setItem(slot, item.getItemStack());
		else
			inventories.get(pageNumber).setItem(slot, null);
		
		return oldItem;
	}
	
	public MGMenuItem addItem(int page, MGMenuItem item)
	{
		if(page >= pages.size())
			addPage(page);

		HashMap<Integer, MGMenuItem> map = pages.get(page);
		int nextSlot = getNextSlot(page);
		
		if(nextSlot == -1)
			throw new IllegalStateException("Page #" + page + " is already full");
		
		MGMenuItem oldItem = getItem(nextSlot, page);
		
		map.put(nextSlot, item);
		
		if(item != null)
			inventories.get(page).setItem(nextSlot, item.getItemStack());
		else
			inventories.get(page).setItem(nextSlot, null);
		
		return oldItem;
	}
	
	public MGMenuItem addItem(MGMenuItem item, int slot, int page)
	{
		
		if(page >= pages.size())
			addPage(page);
		
		if(slot < 0 || slot >= maxSlots)
			throw new IllegalArgumentException("Slot parameter must be less than maximum slots value");
		
		MGMenuItem oldItem = getItem(slot, page);
		
		if(oldItem != null)
		{
			if(oldItem.getListeners().contains(nextListener))
				throw new IllegalArgumentException("Cannot place MenuItem over Next Page item");
			else if(oldItem.getListeners().contains(prevListener))
				throw new IllegalArgumentException("Cannot place MenuItem over Previous Page item");
		}
		
		pages.get(page).put(slot, item);
		
		if(item != null)
			inventories.get(page).setItem(slot, item.getItemStack());
		else
			inventories.get(page).setItem(slot, null);
		
		return oldItem;
	}
	
	@Override
	public MGMenuItem addItem(MGMenuItem item)
	{
		int page = MGMenuTools.getOpenPage(pages, maxSlots);
		
		if(page == -1)
		{
			page = pages.size();
			addPage(page);
		}
		
		int nextSlot = getNextSlot(page);
		MGMenuItem oldItem = getItem(nextSlot, page);
		
		pages.get(page).put(nextSlot, item);
		
		if(item != null)
			inventories.get(page).setItem(nextSlot, item.getItemStack());
		else
			inventories.get(page).setItem(nextSlot, null);
		
		return oldItem;
	}
	
	@Override
	public MGMenuItem removeItem(MGMenuItem item)
	{
		int page = getPage(item);
		
		if(page == -1)
			return null;
		
		return removeItem(page, getSlot(item));
	}
	
	@Override
	@Deprecated
	public MGMenuItem removeItem(int slot)
	{
		return null;
	}
	
	public MGMenuItem removeItem(int page, int slot)
	{
		if(page >= pages.size())
			return null;
		
		if(slot < 0 || slot >= maxSlots)
			return null;
		
		HashMap<Integer, MGMenuItem> pageItems = pages.get(page);
		
		MGMenuItem item = pageItems.remove(slot);
		
		if(item != null)
		{
			inventories.get(page).setItem(slot, null);
			item.close();
		}
		
		if(pageItems.isEmpty())
			removePage(page);
		
		return item;
	}

	@Override
	@Deprecated
	public MGMenuItem moveItem(int oldSlot, int newSlot)
	{
//		if(oldSlot < 0 || oldSlot >= maxSlots)
//			return null;
//		
//		if(newSlot < 0 || newSlot >= maxSlots)
//			return null;
//		
//		MenuItem item = getItem(oldSlot);
//		
//		if(item == null)
//			return item;
//		
//		pages.get(curPage).remove(oldSlot);
//		pages.get(curPage).put(newSlot, item);
//		
//		inv.setItem(oldSlot, null);
//		inv.setItem(newSlot, item.getItemStack());
//		
//		return item;
		return null;
	}
	
	public MGMenuItem moveItem(int oldSlot, int oldPage, int newSlot, int newPage)
	{
		if(oldPage >= pages.size())
			return null;
		
		if(newPage >= pages.size())
			addPage(newPage);
		
		if(oldSlot < 0 || oldSlot >= maxSlots)
			return null;
		
		if(newSlot < 0 || newSlot >= maxSlots)
			return null;
	
		
		MGMenuItem item = getItem(oldSlot);
		
		if(item == null)
			return item;
		
		
		menuItems.remove(oldSlot);
		menuItems.put(newSlot, item);


		inventories.get(oldPage).setItem(oldSlot, null);
		inventories.get(newPage).setItem(newSlot, item.getItemStack());

		
		return item;
	}
	
	@Override
	@Deprecated
	public MGMenuItem moveItem(MGMenuItem item, int newSlot)
	{
//		if(newSlot < 0 || newSlot >= maxSlots)
//			return null;
//		
//		
//		int page = getPage(item);
//		
//		if(page != curPage)
//			return null;
//		
//		
//		int slot = getSlot(item);
//		
//		if(slot < 0 || slot >= maxSlots)
//			return null;
//		
//		MenuItem oldItem = pages.get(curPage).get(newSlot);
//		
//		pages.get(curPage).remove(slot);
//		pages.get(curPage).put(newSlot, item);
//		
//		inv.setItem(slot, null);
//		inv.setItem(newSlot, item.getItemStack());
//		
//		
//		return oldItem;
		
		return null;
	}

	public MGMenuItem moveItem(MGMenuItem item, int newSlot, int newPage)
	{
		if(newPage >= pages.size())
			addPage(newPage);
		
		if(newSlot < 0 || newSlot >= maxSlots)
			return null;
		
		int page = getPage(item);
		
		if(page == -1)
			return null;
		
		
		int slot = getSlot(item);
		
		if(slot < 0 || slot >= maxSlots)
			return null;
		
		
		MGMenuItem oldItem = pages.get(page).get(newSlot);
		
		pages.get(page).remove(slot);
		pages.get(newPage).put(newSlot, item);
		
		
		inventories.get(page).setItem(slot, null);
		inventories.get(newPage).setItem(newSlot, item.getItemStack());
		
		
		return oldItem; 
	}
	
	@Override
	public void rebuildInventory()
	{
		inventories.forEach(invt -> invt.clear());
		
		for(int i = 0; i < pages.size(); i++)
		{
			for(Map.Entry<Integer, MGMenuItem> entry : pages.get(i).entrySet())
			{
				inventories.get(i).setItem(entry.getKey(), entry.getValue().getItemStack());
			}
		}
	}

	@Override
	public void refreshInventory(ArrayList<HumanEntity> players) 
	{
		for(int i = 0; i < players.size(); i++)
			if(curPages.containsKey(players.get(i).getUniqueId()))
				players.get(i).openInventory(inventories.get(curPages.get(players.get(i).getUniqueId())));
	}
	
	public int getNextSlot(int page)
	{
		if(page >= pages.size())
			return -1;
			
		return MGMenuTools.getNextSlot(pages.get(page), maxSlots);
	}

	private void removePage(int page) 
	{
		if(page == pages.size() - 1 && pages.size() > 1)
		{
			pages.remove(page);
			inventories.remove(page);
			
			for(Map.Entry<UUID, Integer> entry : curPages.entrySet())
			{
				if(entry.getValue().equals(page))
					openMenu(Bukkit.getPlayer(entry.getKey()), page);
			}
		}
		else if(!pages.get(page).isEmpty())
		{
			pages.get(page).clear();
			inventories.get(page).clear();
		}
	}
	
	protected void addPage(int pageNumber)
	{
		while (pageNumber >= pages.size())
		{
			pages.add(new HashMap<Integer, MGMenuItem>());
			buildInventory(name, (pageNumber == 0) ? -1 : pageNumber);
			
			if(pages.size() > 1)
				addPrevPageItem(pages.size() - 1);
			
			if(pages.size() > 1)
				addNextPageItem(pages.size() - 2);
		}
	}
	
	protected void buildInventory(String name, int page)
	{
		
		if(page != -1)
		{
			name += ChatColor.RESET;
			String endStr = "" + (int)(page + 1);
			int spaces = 32 - name.length() - endStr.length();
			
			for(int i = 0; i < spaces; i++)
				name += " ";
			name += endStr;
		}
		
		inventories.add(Bukkit.createInventory(null, maxSlots, name));
	}

	protected void addPrevPageItem(int page) 
	{
		int slot = maxSlots - 9;
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

	protected void addNextPageItem(int page) 
	{
		int slot = maxSlots - 1;
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
}