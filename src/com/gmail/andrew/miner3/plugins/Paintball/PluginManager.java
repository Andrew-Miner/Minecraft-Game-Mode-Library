package com.gmail.andrew.miner3.plugins.Paintball;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.andrew.miner3.plugins.Paintball.Assist.ConfigMethods;
import com.gmail.andrew.miner3.plugins.Paintball.Commands.CommandAllChat;
import com.gmail.andrew.miner3.plugins.Paintball.Commands.CommandHandler;
import com.gmail.andrew.miner3.plugins.Paintball.Commands.CommandJoin;
import com.gmail.andrew.miner3.plugins.Paintball.MapClasses.Bound;
import com.gmail.andrew.miner3.plugins.Paintball.MapClasses.MapMetadata;
import com.gmail.andrew.miner3.plugins.Paintball.MapClasses.MapMetadataMethods;
import com.gmail.andrew.miner3.plugins.Paintball.MapClasses.MapManager;

// Last Count ~5973

public class PluginManager extends JavaPlugin 
{
	public ConsoleCommandSender console;
	public static final String logPrefix = "[Paintball] ";
	
	
	private File configFile, mapRegistryFile;
	private FileConfiguration config, registryConfig; 
	private ArrayList<Map.Entry<File, FileConfiguration>> mapConfigs;
	
	private final String configStr = "config.yml";
	private final String mapRegStr = "mapreg.yml";
	private final String defMapConfig = "defmap.yml";
	
	private GameManager manager;
	private MapManager maps;
	private CommandHandler cmdHandler;
	
	private PBPlayerManager playerManager;
	
	@Override
	public void onEnable()
	{
		console = Bukkit.getServer().getConsoleSender();
		mapConfigs = new ArrayList<Map.Entry<File, FileConfiguration>>();
		
		try
		{
			if(!getDataFolder().exists())
				getDataFolder().mkdirs();
		} catch (Exception e)
		{
			getLogger().severe("Failed To Make Plugin Directory!");
			e.printStackTrace();
		}
		
		loadConfig();
		loadMapRegistry();
		loadMapConfigs();
		
		if(registryConfig != null)
			maps = new MapManager(this, getMapRegistry(), getMapMetadata(), getRegistryMapNames());
		else
			maps = new MapManager(this, getMapRegistry());

		playerManager = new PBPlayerManager(this);
		
		manager = new GameManager(this);
		getServer().getPluginManager().registerEvents(manager, this);
		
		getCommand("join").setExecutor(new CommandJoin(this, manager.getLobbyManager()));
		getCommand("all").setExecutor(new CommandAllChat(this, manager.getLobbyManager()));
		
		cmdHandler = new CommandHandler(this, manager.getLobbyManager());
		getCommand("pb").setExecutor(cmdHandler);
		getCommand("pb").setTabCompleter(cmdHandler);
		
		for(MapMetadata map : maps.getMaps())
			MapMetadataMethods.printMapMetadata(this, map);
		
	} 
	
	@Override
	public void onDisable()
	{
		playerManager.updatePlayersSync();
		playerManager.close();
	}
	
	public FileConfiguration getMapConfig(String fileName)
	{
		for(Map.Entry<File, FileConfiguration> entry : mapConfigs)
		{
			if(entry.getKey().getName().equalsIgnoreCase(fileName))
				return entry.getValue();
		}
		
		return null;
	}
	
	public boolean saveMapConfig(String fileName)
	{
		for(Map.Entry<File, FileConfiguration> entry : mapConfigs)
		{
			if(entry.getKey().getName().equalsIgnoreCase(fileName))
			{
				try {
					entry.getValue().save(entry.getKey());
					return true;
				} catch (IOException e) {
					getLogger().warning("Failed to save " + fileName + "!");
				}
			}
		}
		
		return false;
	}
	
	public FileConfiguration getMapRegistry()
	{
		return registryConfig;
	}
	
	public void saveMapRegistry()
	{
		try {
			registryConfig.save(mapRegistryFile);
		} catch (IOException e) {
			getLogger().severe("Failed To Save " + mapRegStr + "!");
			e.printStackTrace();
		}
	}
	
	private void loadMapRegistry()
	{
		mapRegistryFile = ConfigMethods.createFile(this, mapRegStr);
		registryConfig = ConfigMethods.loadFileConfiguration(this, mapRegistryFile);
	}
	
	private void loadConfig()
	{
		configFile = ConfigMethods.createFile(this, configStr);
		config = ConfigMethods.loadFileConfiguration(this, configFile);
	}
	
	private void loadMapConfigs()
	{
		ArrayList<String> mapRegistry = (ArrayList<String>) registryConfig.getStringList("Map Registry");
		
		for(String map : mapRegistry)
		{
			File mapFile = ConfigMethods.createFile(this, map);
			FileConfiguration mapConfig = ConfigMethods.loadFileConfiguration(this, mapFile, defMapConfig);
			
			mapConfigs.add(new AbstractMap.SimpleEntry<File, FileConfiguration>(mapFile, mapConfig));
		}
	}
	
	public PBPlayerManager getPlayerManager()
	{
		return playerManager;
	}
	
	public MapManager getMaps()
	{
		return maps;
	}
	
	public void addMapConfig(File mapFile, FileConfiguration mapConfig)
	{
		mapConfigs.add(new AbstractMap.SimpleEntry<File, FileConfiguration>(mapFile, mapConfig));
		
		try
		{
			MapMetadata map = new MapMetadata(this, mapConfig);
			
			ArrayList<String> mapReg = (ArrayList<String>) registryConfig.getStringList("Active Rotation");
			
			for(String str : mapReg)
			{
				if(str.equalsIgnoreCase(mapFile.getName()))
				{
					maps.addMap(map, true);
					return;
				}
			}

			maps.addMap(map, false);
		} 
		catch(IllegalStateException e)
		{
			getLogger().warning(e.getMessage());
		}
	}
	
	public ArrayList<String> getRegistryMapNames()
	{
		ArrayList<String> mapReg = (ArrayList<String>) registryConfig.getStringList("Active Rotation");
		ArrayList<String> mapNames = new ArrayList<String>();
		
		for(String fileName : mapReg)
		{
			FileConfiguration mapConfig = getMapConfig(fileName);
			
			if(mapConfig != null && mapConfig.contains(MapMetadataMethods.MAP_NAME))
			{
				String name = mapConfig.getString(MapMetadataMethods.MAP_NAME);
				
				if(name.length() > 0)
					mapNames.add(name.toLowerCase());
			}
		}
		
		return mapNames;
	}
	
	private ArrayList<MapMetadata> getMapMetadata() 
	{
		ArrayList<MapMetadata> metadata = new ArrayList<MapMetadata>();
		
		for(Map.Entry<File, FileConfiguration> entry : mapConfigs)
		{
			try
			{
				metadata.add(new MapMetadata(this, entry.getValue()));
			}
			catch (IllegalStateException e)
			{
				this.getLogger().severe(e.getMessage());
			}
		}
		
		return metadata;
	}
}
