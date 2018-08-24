package com.gmail.andrew.miner3.plugins.Paintball.MapClasses;

import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.configuration.file.FileConfiguration;

import com.gmail.andrew.miner3.plugins.Paintball.PluginManager;

public class MapManager 
{
	private ArrayList<MapMetadata> maps;
	private ArrayList<String> activeRotation;
	private FileConfiguration mapRegistry;
	private PluginManager plugin;
	
	
	public MapManager(PluginManager plugin, FileConfiguration mapRegistry)
	{
		this.plugin = plugin;
		maps = new ArrayList<MapMetadata>();
		activeRotation = new ArrayList<String>();
		this.mapRegistry = mapRegistry;
	}
	
	public MapManager(PluginManager plugin, FileConfiguration mapRegistry, ArrayList<MapMetadata> maps)
	{
		this.plugin = plugin;
		this.maps = maps;
		activeRotation = new ArrayList<String>();
		this.mapRegistry = mapRegistry;
	}
	
	public MapManager(PluginManager plugin, FileConfiguration mapRegistry, ArrayList<MapMetadata> maps, ArrayList<String> activeRotation)
	{
		this.plugin = plugin;
		this.maps = maps;
		this.activeRotation = activeRotation;
		this.mapRegistry = mapRegistry;
	}
	
	public int getMapCount()
	{
		return maps.size();
	}
	
	public ArrayList<MapMetadata> getMaps()
	{
		return maps;
	}
	
	public ArrayList<MapMetadata> getActiveAvailableMaps()
	{
		ArrayList<MapMetadata> maps = new ArrayList<MapMetadata>();
		
		for(String name : activeRotation)
		{
			MapMetadata map = findMap(name);
			
			if(map != null && !map.isInUse())
				maps.add(map);
		}
		
		return maps;
	}
	
	public MapMetadata getRandomMap(boolean notUsed)
	{
		ArrayList<MapMetadata> maps = getActiveAvailableMaps();
		
		if(!maps.isEmpty())
			return maps.get((int)(Math.random() * maps.size()));
		
		return null;
	}
	
	public MapMetadata getRandomMap(boolean notUsed, int PlayerCount)
	{
		ArrayList<MapMetadata> compatable = getCompatableMaps(notUsed, PlayerCount);
		
		if(compatable != null && !compatable.isEmpty())
			return compatable.get((int)(Math.random() * compatable.size()));
		
		return null;
	}
	
	public ArrayList<MapMetadata> getRandomSet(boolean notUsed, int setCount)
	{
		ArrayList<MapMetadata> maps = getActiveAvailableMaps();
		ArrayList<MapMetadata> set = new ArrayList<MapMetadata>();
		
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
	
	public ArrayList<MapMetadata> getRandomSet(boolean notUsed, int setCount, int PlayerCount)
	{
		ArrayList<MapMetadata> compatable = getCompatableMaps(notUsed, PlayerCount);
		ArrayList<MapMetadata> set = new ArrayList<MapMetadata>();
		
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
	
	public ArrayList<MapMetadata> getCompatableMaps(boolean notUsed, int PlayerCount)
	{
		ArrayList<MapMetadata> maps = getActiveAvailableMaps();
		ArrayList<MapMetadata> compatable = new ArrayList<MapMetadata>();
		
		Iterator<MapMetadata> it = maps.iterator();
		
		while(it.hasNext())
		{
			MapMetadata temp = it.next();
			if(temp.getMaxPlayers() <= PlayerCount)
				compatable.add(temp);
		}

		if(!compatable.isEmpty())
			return compatable;
		
		return null;
	}
	
	public void addMap(MapMetadata map, boolean addToRegistry)
	{
		Iterator<MapMetadata> it = findMapIterator(map.getMapName());
		
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
	
	public void removeMap(MapMetadata map)
	{
		int index = maps.indexOf(map);
		String name = map.getMapName().toLowerCase();
		
		if(index != -1)
			maps.remove(index);
		
		removeFromRotation(name);
	}
	
	public MapMetadata findMap(String mapName)
	{
		Iterator<MapMetadata> it = maps.iterator();
		
		while(it.hasNext())
		{
			MapMetadata temp = it.next();
			if(temp.getMapName().equalsIgnoreCase(mapName))
				return temp;
		}
		
		return null;
	}
	
	private Iterator<MapMetadata> findMapIterator(String mapName)
	{
		Iterator<MapMetadata> it = maps.iterator();
		
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
		MapMetadata map = findMap(lowerCase);
		try
		{
			if(map != null)
				MapMetadataMethods.validateSpawns(plugin, map);
		}
		catch (IllegalArgumentException e)
		{
			throw new IllegalStateException(e.getMessage());
		}
		
		if(map != null && !activeRotation.contains(lowerCase))
		{
			for(String str : activeRotation)
				plugin.getLogger().info(str);
			ArrayList<String> reg = (ArrayList<String>) mapRegistry.getStringList("Active Rotation");
			reg.add(lowerCase + ".yml");
			mapRegistry.set("Active Rotation", reg);
			plugin.saveMapRegistry();
			
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
			plugin.saveMapRegistry();
			
			activeRotation.remove(lowerCase);
			return true;
		}
		
		return false;
	}

	public ArrayList<MapMetadata> getActiveMaps() 
	{
		ArrayList<MapMetadata> maps = new ArrayList<MapMetadata>();
		
		for(String name : activeRotation)
		{
			MapMetadata map = findMap(name);
			
			if(map != null)
				maps.add(map);
		}
		
		return maps;
	}

	public boolean isActive(MapMetadata map) 
	{
		ArrayList<MapMetadata> activeMaps = getActiveMaps();
		
		for(MapMetadata m : activeMaps)
		{
			if(m.getMapName().equals(map.getMapName()))
				return true;
		}
		
		return false;
	}
}
