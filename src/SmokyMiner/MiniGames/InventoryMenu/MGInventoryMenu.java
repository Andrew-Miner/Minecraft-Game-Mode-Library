package SmokyMiner.MiniGames.InventoryMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class MGInventoryMenu implements Listener
{
	protected final JavaPlugin plugin;
	
	protected Inventory inv;
	protected int maxSlots;
	protected HashMap<Integer, MGMenuItem> menuItems;
	
	protected MGMenuAnimation animation;
	protected BukkitTask titleAnimation;
	
	protected int lastPos;
	
	public MGInventoryMenu(JavaPlugin plugin, String name, int maxSlots)
	{
		this.plugin = plugin;
		
		this.maxSlots = maxSlots;
		
		while(this.maxSlots % 9 != 0)
			this.maxSlots++;
		
		if(this.maxSlots > 54)
			this.maxSlots = 54;
		
		menuItems = new HashMap<Integer, MGMenuItem>();
		setTitle(name, true);
		
		titleAnimation = null;
		
		lastPos = 0;
		
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}
	
	public void close()
	{
		if(titleAnimation != null && !titleAnimation.isCancelled())
			titleAnimation.cancel();
	}
	
	public void clearMenu()
	{
		inv.clear();
		menuItems.clear();
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<Integer, MGMenuItem> getItems()
	{
		return (HashMap<Integer, MGMenuItem>) menuItems.clone();
	}
	
	public MGMenuItem getItem(int slot)
	{
		return menuItems.get(slot);
	}
	
	public int getSlot(MGMenuItem menuItem)
	{
		for(Map.Entry<Integer, MGMenuItem> item : menuItems.entrySet())
		{
			if(item.getValue().equals(menuItem))
				return item.getKey();
		}
		
		return -1;
	}
	
	public int itemCount()
	{
		return menuItems.size();
	}
	
	public int size()
	{
		return maxSlots;
	}
	
	public void openMenu(Player p)
	{
		p.openInventory(inv);
	}
	
	public void closeMenu(Player p)
	{
		if(inv.equals(p.getOpenInventory().getTopInventory()))
			p.closeInventory();
	}
	
	public MGMenuItem addItem(MGMenuItem item, int slot)
	{
		if(slot < 0 || slot >= maxSlots)
			throw new IllegalArgumentException("Slot parameter must be less than maximum slots value");
		
		MGMenuItem oldItem = getItem(slot);
		
		menuItems.put(slot, item);
		inv.setItem(slot, item.getItemStack());
		
		return oldItem;
	}
	
	public MGMenuItem addItem(MGMenuItem item)
	{
		int slot = getNextSlot();
		
		return addItem(item, slot);
	}

	public MGMenuItem removeItem(MGMenuItem item)
	{
		return removeItem(getSlot(item));
	}
	
	public MGMenuItem removeItem(int slot)
	{
		if(slot < 0 || slot >= maxSlots)
			return null;
		
		MGMenuItem item = menuItems.remove(slot);
		
		if(item != null)
		{
			inv.setItem(slot, null);
			item.close();
		}
		
		return item;
	}
	
	public MGMenuItem moveItem(int oldSlot, int newSlot)
	{
		if(oldSlot < 0 || oldSlot >= maxSlots)
			return null;
		
		if(newSlot < 0 || newSlot >= maxSlots)
			return null;
		
		MGMenuItem item = getItem(oldSlot);
		
		if(item == null)
			return item;
		
		menuItems.remove(oldSlot);
		menuItems.put(newSlot, item);
		
		inv.setItem(oldSlot, null);
		inv.setItem(newSlot, item.getItemStack());
		
		return item;
	}
	
	public MGMenuItem moveItem(MGMenuItem item, int newSlot)
	{
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

	@EventHandler
	public void onItemClick(InventoryClickEvent e)
	{
		Inventory inv = e.getInventory();
		
		if(inv.equals(this.inv))
		{
			e.setCancelled(true);
			
			MGMenuItem item = getItem(e.getSlot());
			
			if(item != null)
				item.clicked(this, e.getClick(), e.getWhoClicked());
		}
	}
	
	public void rebuildInventory()
	{
		for(Map.Entry<Integer, MGMenuItem> entry : menuItems.entrySet())
		{
			inv.setItem(entry.getKey(), entry.getValue().getItemStack());
		}
	}

	public void refreshInventory(ArrayList<HumanEntity> players) 
	{
		for(int i = 0; i < players.size(); i++)
			players.get(i).openInventory(inv);
	}
	
	public void setTitle(String title, boolean centered)
	{
		ArrayList<HumanEntity> players = (inv != null) ? (ArrayList<HumanEntity>) inv.getViewers() : null;
		
		if(title.length() > MGMenuTools.MAX_TITLE_LENGTH)
			title = title.substring(0, MGMenuTools.MAX_TITLE_LENGTH);
		
		if(centered)
			title = MGMenuTools.centerTitle(title);
		
		inv = Bukkit.createInventory(null, maxSlots, title);
		
		if(players != null)
		{
			rebuildInventory();
			refreshInventory(players);
		}
	}
	
	public void setTitleAnimation(MGMenuAnimation animator, long tickDelay)
	{
		close();
		
		this.animation = animator;
		
		titleAnimation = plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable()
		{
			@Override
			public void run() 
			{
				//String newStr = animation.updateTitle(inv.getName());
				
				//if(!inv.getTitle().equals(newStr))
					//setTitle(newStr, true);
			}
			
		}, 1L, tickDelay);
	}
	
	protected int getNextSlot() 
	{
		int slot = inv.firstEmpty();
		
		if(slot == -1)
		{
			if(lastPos < maxSlots)
				return lastPos++;
			else
				return lastPos = 0;
		}
		
		return slot;
	}
}