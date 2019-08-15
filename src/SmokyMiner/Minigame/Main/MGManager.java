package SmokyMiner.Minigame.Main;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import SmokyMiner.MiniGames.Commands.MGCommandAllChat;
import SmokyMiner.MiniGames.Commands.MGCommandJoin;
import SmokyMiner.MiniGames.Commands.CommandHandler.MGCommandHandler;
import SmokyMiner.MiniGames.InventoryMenu.MGMenuItem;
import SmokyMiner.MiniGames.Items.MGInventoryItem;
import SmokyMiner.MiniGames.Items.ItemShop.MGItemShop;
import SmokyMiner.MiniGames.Lobby.MGMatchMaker;
import SmokyMiner.MiniGames.Maps.MGMapManager;
import SmokyMiner.MiniGames.Maps.MGMapMetadata;
import SmokyMiner.MiniGames.Maps.MGMapMethods;
import SmokyMiner.MiniGames.Player.MGPlayerManager;

public class MGManager 
{
	public ConsoleCommandSender console;
	public static final String logPrefix = "[MiniGames-API] ";
	
	private File configFile, mapRegistryFile;
	private FileConfiguration config, registryConfig; 
	private ArrayList<Map.Entry<File, FileConfiguration>> mapConfigs;
	
	private final String configStr = "mg-config.yml";
	private final String mapRegStr = "mapreg.yml";
	private final String defMapConfig = "defmap.yml";
	
	private JavaPlugin plugin;

	private MGPlayerManager playerManager;
	private MGCommandHandler cmdHandler;
	private MGMatchMaker matchMaker;
	private MGItemShop itemShop;
	private MGInventoryItem invItem;
	private MGMapManager maps;
	
	public MGManager(JavaPlugin plugin, String cmdPrefix)
	{
		this.plugin = plugin;
		
		initConfigs();
		
		playerManager = new MGPlayerManager(this);
		plugin.getServer().getPluginManager().registerEvents(playerManager, plugin);

		itemShop = new MGItemShop(plugin);
		invItem = new MGInventoryItem(this, Material.CHEST);
		matchMaker = new MGMatchMaker(this, 1, MGMapMethods.loadSpawnMap(this));
		
		plugin.getCommand("join").setExecutor(new MGCommandJoin(this, matchMaker));
		plugin.getCommand("all").setExecutor(new MGCommandAllChat(this, matchMaker));
		
		cmdHandler = new MGCommandHandler(this, matchMaker);
		plugin.getCommand("pb").setExecutor(cmdHandler);
		plugin.getCommand("pb").setTabCompleter(cmdHandler);
	}
	
	private void initConfigs()
	{
		try
		{
			if(!plugin.getDataFolder().exists())
				plugin.getDataFolder().mkdirs();
		} catch (Exception e)
		{
			plugin.getLogger().severe(logPrefix + "Failed To Make Plugin Directory!");
			e.printStackTrace();
		}
		
		mapConfigs = new ArrayList<Map.Entry<File, FileConfiguration>>();
		
		config = null;
		loadConfig();
		
		registryConfig = null;
		loadMapRegistry();
		loadMapConfigs();
		
		if(registryConfig != null)
			maps = new MGMapManager(this, getMapRegistry(), getMapMetadata(), getRegistryMapNames());
		else
			maps = new MGMapManager(this, getMapRegistry());
	}
	
	public void close()
	{
		playerManager.updatePlayersSync(true);
		playerManager.close();
	}
	
	public FileConfiguration getConfig()
	{
		return config;
	}
	
	public void saveConfig()
	{
		try {
			config.save(configFile);
		} catch (IOException e) {
			plugin.getLogger().severe("Failed To Save " + configStr + "!");
			e.printStackTrace();
		}
	}
	
	private void loadConfig()
	{
		configFile = MGConfigMethods.createFile(plugin, configStr);
		config = MGConfigMethods.loadFileConfiguration(plugin, configFile);
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
					plugin.getLogger().warning("Failed to save " + fileName + "!");
				}
			}
		}
		
		return false;
	}
	
	private void loadMapConfigs()
	{
		ArrayList<String> mapRegistry = (ArrayList<String>) registryConfig.getStringList("Map Registry");
		
		for(String map : mapRegistry)
		{
			File mapFile = MGConfigMethods.createFile(plugin, map);
			FileConfiguration mapConfig = MGConfigMethods.loadFileConfiguration(plugin, mapFile, defMapConfig);
			
			mapConfigs.add(new AbstractMap.SimpleEntry<File, FileConfiguration>(mapFile, mapConfig));
		}
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
			plugin.getLogger().severe("Failed To Save " + mapRegStr + "!");
			e.printStackTrace();
		}
	}
	
	private void loadMapRegistry()
	{
		mapRegistryFile = MGConfigMethods.createFile(plugin, mapRegStr);
		registryConfig = MGConfigMethods.loadFileConfiguration(plugin, mapRegistryFile);
	}
	
	public MGPlayerManager getPlayerManager()
	{
		return playerManager;
	}
	
	public MGMatchMaker getMatchMaker()
	{
		return matchMaker;
	}
	
	public MGMapManager getMaps()
	{
		return maps;
	}
	
	public JavaPlugin plugin()
	{
		return plugin;
	}
	
	public MGItemShop getItemShop()
	{
		return itemShop;
	}
	
	public void addMapConfig(File mapFile, FileConfiguration mapConfig)
	{
		mapConfigs.add(new AbstractMap.SimpleEntry<File, FileConfiguration>(mapFile, mapConfig));
		
		try
		{
			MGMapMetadata map = new MGMapMetadata(this, mapConfig);
			
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
			plugin.getLogger().warning(e.getMessage());
		}
	}
	
	public ArrayList<String> getRegistryMapNames()
	{
		ArrayList<String> mapReg = (ArrayList<String>) registryConfig.getStringList("Active Rotation");
		ArrayList<String> mapNames = new ArrayList<String>();
		
		for(String fileName : mapReg)
		{
			FileConfiguration mapConfig = getMapConfig(fileName);
			
			if(mapConfig != null && mapConfig.contains(MGMapMethods.MAP_NAME))
			{
				String name = mapConfig.getString(MGMapMethods.MAP_NAME);
				
				if(name.length() > 0)
					mapNames.add(name.toLowerCase());
			}
		}
		
		return mapNames;
	}

	private ArrayList<MGMapMetadata> getMapMetadata() 
	{
		ArrayList<MGMapMetadata> metadata = new ArrayList<MGMapMetadata>();
		
		for(Map.Entry<File, FileConfiguration> entry : mapConfigs)
		{
			try
			{
				metadata.add(new MGMapMetadata(this, entry.getValue()));
			}
			catch (IllegalStateException e)
			{
				plugin.getLogger().severe(e.getMessage());
			}
		}
		
		return metadata;
	}

	public MGInventoryItem getInventoryBlock()
	{
		return invItem;
	}
}
