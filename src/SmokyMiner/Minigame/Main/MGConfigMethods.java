package SmokyMiner.Minigame.Main;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class MGConfigMethods 
{
	public static File createFile(JavaPlugin plugin, String fileName)
	{
		try
		{
			File temp = new File(plugin.getDataFolder(), fileName);
			
			if(!temp.exists())
				plugin.getLogger().info(fileName + " File Not Found! Creating...");
			else
				plugin.getLogger().info("Loading " + fileName + " File!");
			
			return temp;
		} 
		catch (Exception e) 
		{ 
			plugin.getLogger().severe("Failed To Create " + fileName + " File!");
			e.printStackTrace(); 
		}
		
		return null;
	}
	
	public static FileConfiguration loadFileConfiguration(JavaPlugin plugin, File configFile)
	{
		if(!configFile.exists())
			plugin.saveResource(configFile.getName(), false);
		
		return YamlConfiguration.loadConfiguration(configFile);
	}
	
	public static FileConfiguration loadFileConfiguration(JavaPlugin plugin, File configFile, String defResource)
	{
		FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
		Reader defConfigStream = null;
		
		try {
			defConfigStream = new InputStreamReader(plugin.getResource(defResource),"UTF8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		if(defConfigStream != null)
		{
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			config.setDefaults(defConfig);
		}
		
		if(!configFile.exists())
		{
			try {
				config.options().copyDefaults(true);
				config.save(configFile);
				plugin.getLogger().info("Default config for " + configFile.getName() + " created from: " + defResource);
			} catch (IOException e) {
				plugin.getLogger().severe("Could not save config to " + configFile);
				e.printStackTrace();
			}
		}
		
		
		return config;
	}
}