package com.gmail.andrew.miner3.plugins.Paintball.Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.gmail.andrew.miner3.plugins.Paintball.PluginManager;
import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.LobbyManager;

public class CommandHandler implements CommandExecutor, TabCompleter
{
	private LobbyManager manager;
	private PluginManager plugin;
	private CommandParser parser;
	private CommandInfo infoCmd;
	private CommandMap arenaCmd;
	
	public CommandHandler(PluginManager plugin, LobbyManager manager)
	{
		this.manager = manager;
		this.plugin = plugin;
		parser = new CommandParser();
		
		infoCmd = new CommandInfo(plugin);
		arenaCmd = new CommandMap(plugin, manager);
	}
	
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) 
    {
    	if(manager == null)
    	{
    		Bukkit.getServer().getLogger().warning("PaintballCommands failed to load instance of LobbyManager!");
    		return true;
    	}
    	else if(plugin == null)
    	{
    		Bukkit.getServer().getLogger().warning("PaintballCommands failed to load instance of JavaPlugin!");
    		return true;
    	}
    	
    	ParsedCommand pbCommand = parser.parseCommand(args);
    	
    	if(pbCommand == null)
    		return false;
    	
    	pbCommand.setSender(sender);
    	
    	switch(pbCommand.getCommand())
    	{
    	case INFO:
    		infoCmd.onCommand(pbCommand);
    		break;
    	case ARENA:
    		return arenaCmd.onCommand(pbCommand);
		default:
			return false;
    	}
    	
        return true;
    }

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String CommandLabel, String[] arg)
	{
		List<String> possibleCmds = parser.getAutoComplete(arg);
		
		if(possibleCmds == null)
			return new ArrayList<String>();
		
		return possibleCmds;
	}
    
    public static boolean isNumeric(String s) 
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
