package SmokyMiner.MiniGames.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import SmokyMiner.MiniGames.Lobby.MGMatchMaker;
import SmokyMiner.Minigame.Main.MGManager;

public class MGCommandJoin implements CommandExecutor
{
	private MGMatchMaker manager;
	private MGManager mgManager;
	
	public MGCommandJoin()
	{
		manager = null;
		mgManager = null;
	}
	
	public MGCommandJoin(MGManager mgManager, MGMatchMaker manager)
	{
		this.manager = manager;
		this.mgManager = mgManager;
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
    				if(mgManager != null && mgManager.plugin() != null)
    					p.sendMessage(ChatColor.YELLOW + "[" + mgManager.plugin().getName() + "] " + ChatColor.RED + "Error: You must leave the lobby before you can join another match!");
    			}
    			else if(!manager.joinLobby(p.getUniqueId()))
    					if(mgManager !=null && mgManager.plugin() != null)
    						p.sendMessage(ChatColor.YELLOW + "[" + mgManager.plugin().getName() + "] " + ChatColor.RED + "Error: Failed to join match!" );
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
