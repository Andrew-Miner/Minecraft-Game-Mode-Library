package SmokyMiner.MiniGames.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import SmokyMiner.MiniGames.Lobby.MGLobby;
import SmokyMiner.MiniGames.Lobby.MGMatchMaker;
import SmokyMiner.Minigame.Main.MGManager;

public class MGCommandAllChat implements CommandExecutor
{
	private MGMatchMaker manager;
	private MGManager mgManager;
	
	public MGCommandAllChat()
	{
		manager = null;
		mgManager = null;
	}
	
	public MGCommandAllChat(MGManager mgManager, MGMatchMaker manager)
	{
		this.manager = manager;
		this.mgManager = mgManager;
	}
	
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) 
    {
    	if(manager == null)
    	{
    		mgManager.plugin().getLogger().warning("CommandAllChat failed to load instance of LobbyManager!");
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
    			
    			MGLobby lobby = manager.getLobbyByPlayer(p.getUniqueId());
    			
    			if(lobby == null)
    				p.chat(msg);
    			else
    				lobby.sendChatMessege(p.getUniqueId(), msg, true);
    			
    			if(msg.equalsIgnoreCase("start"))
    				lobby.nextStage();
    			
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