package com.gmail.andrew.miner3.plugins.Paintball.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.andrew.miner3.plugins.Paintball.PluginManager;
import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.LobbyManager;

public class CommandJoin implements CommandExecutor
{
	private LobbyManager manager;
	private PluginManager plugin;
	
	public CommandJoin()
	{
		manager = null;
		plugin = null;
	}
	
	public CommandJoin(PluginManager plugin, LobbyManager manager)
	{
		this.manager = manager;
		this.plugin = plugin;
	}
	
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) 
    {
    	if(manager == null)
    	{
    		Bukkit.getServer().getLogger().warning("CommandJoin failed to load instance of LobbyManager!");
    		return false;
    	}
    	
    	if(sender instanceof Player)
    	{
    		Player p = (Player) sender;
    		
    		if(args.length == 0)
    		{
    			if(manager.getLobbyByPlayer(p.getUniqueId()) != null)
    			{
    				if(plugin != null)
    					p.sendMessage(ChatColor.YELLOW + "[" + plugin.getName() + "] " + ChatColor.RED + "Error: You must leave the lobby before you can join another match!");
    			}
    			else if(!manager.JoinMatch(p.getUniqueId()))
    					if(plugin != null)
    						p.sendMessage(ChatColor.YELLOW + "[" + plugin.getName() + "] " + ChatColor.RED + "Error: Failed to join match!" );
    			return true;
    		}
    		else if(args.length == 1 && !isNumeric(args[1]))
    		{
    			
    		}
    		else
    			return false;
    	}
    	else
    		Bukkit.getServer().getLogger().info("Command /" + label + " can only be used by a player!");
    	
        return true;
    }
    
    public boolean isNumeric(String s) 
    {
        int len = s.length();
        for (int i = 0; i < len; ++i) 
        {
            if (!Character.isDigit(s.charAt(i)))
                return false;
        }
      
        return true;
    }
}
