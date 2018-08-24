package com.gmail.andrew.miner3.plugins.Paintball.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.andrew.miner3.plugins.Paintball.PluginManager;
import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.Lobby;
import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.LobbyManager;

public class CommandAllChat implements CommandExecutor
{
	private LobbyManager manager;
	private PluginManager plugin;
	
	public CommandAllChat()
	{
		manager = null;
		plugin = null;
	}
	
	public CommandAllChat(PluginManager plugin, LobbyManager manager)
	{
		this.manager = manager;
		this.plugin = plugin;
	}
	
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) 
    {
    	if(manager == null)
    	{
    		plugin.getLogger().warning("CommandAllChat failed to load instance of LobbyManager!");
    		return false;
    	}
    	
    	if(sender instanceof Player)
    	{
    		Player p = (Player) sender;
    		
    		if(args.length == 0)
    		{
    			manager.getBrowser().openBrowser(p);
    			return false;
    		}
    		else
    		{
    			String msg = new String();
    			
    			for(String s : args)
    				msg += s + " ";
    			
    			Lobby lobby = manager.getLobbyByPlayer(p.getUniqueId());
    			
    			if(lobby == null)
    				p.chat(msg);
    			else
    				lobby.sendChatMessege(p.getUniqueId(), msg, true);
    			
    			if(msg.equalsIgnoreCase("start "))
    				lobby.startMatch();
    			
    			return true;
    		}
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