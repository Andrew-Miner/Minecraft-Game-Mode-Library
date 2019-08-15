package SmokyMiner.MiniGames.Maps;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import SmokyMiner.MiniGames.Player.MGPlayer;
import SmokyMiner.Minigame.Main.MGManager;

public class MGMapManager implements Listener
{
	private ArrayList<MGMapMetadata> maps;
	private ArrayList<String> activeRotation;
	private FileConfiguration mapRegistry;
	private MGManager manager;
	
	
	public MGMapManager(MGManager manager, FileConfiguration mapRegistry)
	{
		this.manager = manager;
		maps = new ArrayList<MGMapMetadata>();
		activeRotation = new ArrayList<String>();
		this.mapRegistry = mapRegistry;
		
    manager.plugin().getServer().getPluginManager().registerEvents(this, manager.plugin());
	}
	
	public MGMapManager(MGManager manager, FileConfiguration mapRegistry, ArrayList<MGMapMetadata> maps)
	{
		this.manager = manager;
		this.maps = maps;
		activeRotation = new ArrayList<String>();
		this.mapRegistry = mapRegistry;
    
    manager.plugin().getServer().getPluginManager().registerEvents(this, manager.plugin());
	}
	
	public MGMapManager(MGManager manager, FileConfiguration mapRegistry, ArrayList<MGMapMetadata> maps, ArrayList<String> activeRotation)
	{
		this.manager = manager;
		this.maps = maps;
		this.activeRotation = activeRotation;
		this.mapRegistry = mapRegistry;
    
    manager.plugin().getServer().getPluginManager().registerEvents(this, manager.plugin());
	}
	
	public int getMapCount()
	{
		return maps.size();
	}
	
	public ArrayList<MGMapMetadata> getMaps()
	{
		return maps;
	}
	
	public ArrayList<MGMapMetadata> getActiveAvailableMaps()
	{
		ArrayList<MGMapMetadata> maps = new ArrayList<MGMapMetadata>();
		
		for(String name : activeRotation)
		{
			MGMapMetadata map = findMap(name);
			
			if(map != null && !map.isInUse())
				maps.add(map);
		}
		
		return maps;
	}
	
	public MGMapMetadata getRandomMap(boolean notUsed)
	{
		ArrayList<MGMapMetadata> maps = getActiveAvailableMaps();
		
		if(!maps.isEmpty())
			return maps.get((int)(Math.random() * maps.size()));
		
		return null;
	}
	
	public MGMapMetadata getRandomMap(boolean notUsed, int PlayerCount)
	{
		ArrayList<MGMapMetadata> compatable = getCompatableMaps(notUsed, PlayerCount);
		
		if(compatable != null && !compatable.isEmpty())
			return compatable.get((int)(Math.random() * compatable.size()));
		
		return null;
	}
	
	public ArrayList<MGMapMetadata> getRandomSet(boolean notUsed, int setCount)
	{
		ArrayList<MGMapMetadata> maps = getActiveAvailableMaps();
		ArrayList<MGMapMetadata> set = new ArrayList<MGMapMetadata>();
		
		if(maps.size() < setCount)
			setCount = maps.size();
		
		int[] added = new int[setCount];
		
		for(int i = 0; i < setCount; i++)
		{
			int index;
			boolean unique = true;
			
			do
			{
				unique = true;
				index = (int)(Math.random() * setCount);
				
				for(int j = 0; j < i; j++)
				{
					if(added[j] == index)
						unique = false;
				}
				
				if(unique)
				{
					added[i] = index;
					set.add(maps.get(index));
				}
					
			} while(!unique);
		}
		
		if(!set.isEmpty())
			return set;
		
		return null;
	}
	
	public ArrayList<MGMapMetadata> getRandomSet(boolean notUsed, int setCount, int PlayerCount)
	{
		ArrayList<MGMapMetadata> compatable = getCompatableMaps(notUsed, PlayerCount);
		ArrayList<MGMapMetadata> set = new ArrayList<MGMapMetadata>();
		
		if(compatable == null)
			return null;
		
		if(compatable.size() < setCount)
			setCount = maps.size();
		
		int[] added = new int[setCount];
		
		for(int i = 0; i < setCount; i++)
		{
			int index;
			boolean unique = true;
			
			do
			{
				unique = true;
				index = (int)(Math.random() * setCount);
				
				for(int j = 0; j < i; j++)
				{
					if(added[j] == index)
						unique = false;
				}
				
				if(unique)
				{
					added[i] = index;
					set.add(compatable.get(index));
				}
					
			} while(!unique);
		}
		
		if(!set.isEmpty())
			return set;
		
		return null;
	}
	
	public ArrayList<MGMapMetadata> getCompatableMaps(boolean notUsed, int PlayerCount)
	{
		ArrayList<MGMapMetadata> maps = getActiveAvailableMaps();
		ArrayList<MGMapMetadata> compatable = new ArrayList<MGMapMetadata>();
		
		Iterator<MGMapMetadata> it = maps.iterator();
		
		while(it.hasNext())
		{
			MGMapMetadata temp = it.next();
			if(temp.getMaxPlayers() <= PlayerCount)
				compatable.add(temp);
		}

		if(!compatable.isEmpty())
			return compatable;
		
		return null;
	}
	
	public void addMap(MGMapMetadata map, boolean addToRegistry)
	{
		Iterator<MGMapMetadata> it = findMapIterator(map.getMapName());
		
		if(it == null)
			maps.add(map);
		else
		{
			it.remove();
			maps.add(map);
		}
		
		if(addToRegistry)
		{
			if(!activeRotation.contains(map.getMapName().toLowerCase()))
				activeRotation.add(map.getMapName().toLowerCase());
		}
	}
	
	public void removeMap(MGMapMetadata map)
	{
		int index = maps.indexOf(map);
		String name = map.getMapName().toLowerCase();
		
		if(index != -1)
			maps.remove(index);
		
		removeFromRotation(name);
	}
	
	public MGMapMetadata findMap(String mapName)
	{
		Iterator<MGMapMetadata> it = maps.iterator();
		
		while(it.hasNext())
		{
			MGMapMetadata temp = it.next();
			if(temp.getMapName().equalsIgnoreCase(mapName))
				return temp;
		}
		
		return null;
	}
	
	private Iterator<MGMapMetadata> findMapIterator(String mapName)
	{
		Iterator<MGMapMetadata> it = maps.iterator();
		
		while(it.hasNext())
		{
			if(it.next().getMapName().equalsIgnoreCase(mapName))
				return it;
		}
		
		return null;
	}
	
	public boolean addToRotation(String mapName)
	{
		String lowerCase = mapName.toLowerCase();
		MGMapMetadata map = findMap(lowerCase);
		try
		{
			if(map != null)
				MGMapMethods.validateSpawns(manager, map);
		}
		catch (IllegalArgumentException e)
		{
			throw new IllegalStateException(e.getMessage());
		}
		
		if(map != null && !activeRotation.contains(lowerCase))
		{
			for(String str : activeRotation)
				manager.plugin().getLogger().info(str);
			ArrayList<String> reg = (ArrayList<String>) mapRegistry.getStringList("Active Rotation");
			reg.add(lowerCase + ".yml");
			mapRegistry.set("Active Rotation", reg);
			manager.saveMapRegistry();
			
			activeRotation.add(lowerCase);
			return true;
		}
		
		return false;
	}
	
	public boolean removeFromRotation(String mapName)
	{
		String lowerCase = mapName.toLowerCase();
		if(activeRotation.contains(lowerCase))
		{			
			ArrayList<String> reg = (ArrayList<String>) mapRegistry.getStringList("Active Rotation");
			reg.remove(lowerCase + ".yml");
			mapRegistry.set("Active Rotation", reg);
			manager.saveMapRegistry();
			
			activeRotation.remove(lowerCase);
			return true;
		}
		
		return false;
	}

	public ArrayList<MGMapMetadata> getActiveMaps() 
	{
		ArrayList<MGMapMetadata> maps = new ArrayList<MGMapMetadata>();
		
		for(String name : activeRotation)
		{
			MGMapMetadata map = findMap(name);
			
			if(map != null)
				maps.add(map);
		}
		
		return maps;
	}

	public boolean isActive(MGMapMetadata map) 
	{
		ArrayList<MGMapMetadata> activeMaps = getActiveMaps();
		
		for(MGMapMetadata m : activeMaps)
		{
			if(m.getMapName().equals(map.getMapName()))
				return true;
		}
		
		return false;
	}
	
	// Make Map Selections
  @EventHandler
  public void playerInteractEvent(PlayerInteractEvent e)
  {
    Player p = e.getPlayer();
    
    if(p.getInventory().getItemInMainHand().getType().equals(Material.BONE))
    {
      DecimalFormat df = new DecimalFormat("#.#");
      Action action = e.getAction();
      
      MGPlayer player = manager.getPlayerManager().getMGPlayer(p.getUniqueId());
      
      if(player == null)
        return;
      
      Location loc = null;
      MGBound bound = player.getSelection();
      
      switch(action)
      {
      case LEFT_CLICK_BLOCK:
        loc = e.getClickedBlock().getLocation();
        
        if(bound.loc1 != null && bound.loc1.equals(loc))
        {
          e.setCancelled(true);
          return;
        }
        
        bound.loc1 = loc;
        p.sendMessage(ChatColor.GOLD + MGManager.logPrefix + ChatColor.GRAY + ChatColor.ITALIC + " Selection 1 Set:  {" + df.format(bound.loc1.getX()) + " " + df.format(bound.loc1.getY()) + " " + df.format(bound.loc1.getZ()) + "}");
        break;
      case RIGHT_CLICK_BLOCK:
        loc = e.getClickedBlock().getLocation();
        
        if(bound.loc2 != null && bound.loc2.equals(loc))
        {
          e.setCancelled(true);
          return;
        }
        
        bound.loc2 = loc;
        p.sendMessage(ChatColor.GOLD + MGManager.logPrefix + ChatColor.GRAY + ChatColor.ITALIC + " Selection 2 Set:  {" + df.format(bound.loc2.getX()) + " " + df.format(bound.loc2.getY()) + " " + df.format(bound.loc2.getZ()) + "}");
        break;
      default:
        return;
      }
      
      player.setSelection(bound);
      e.setCancelled(true);
    }
  }
}