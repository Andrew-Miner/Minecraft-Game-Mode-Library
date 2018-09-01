package SmokyMiner.MiniGames.Commands.CommandHandler;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import SmokyMiner.MiniGames.Commands.MGCommandInfo;
import SmokyMiner.MiniGames.Commands.MGCommandMap;
import SmokyMiner.MiniGames.Lobby.MGLobbyManager;
import SmokyMiner.Minigame.Main.MGManager;

public class MGCommandHandler implements CommandExecutor, TabCompleter
{
	private MGLobbyManager manager;
	private MGManager mgManager;
	private MGCommandParser parser;
	private MGCommandInfo infoCmd;
	private MGCommandMap arenaCmd;
	
	public MGCommandHandler(MGManager mgManager, MGLobbyManager manager)
	{
		this.manager = manager;
		this.mgManager = mgManager;
		parser = new MGCommandParser();
		
		infoCmd = new MGCommandInfo(mgManager);
		arenaCmd = new MGCommandMap(mgManager, manager);
	}
	
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) 
    {
    	if(manager == null)
    	{
    		Bukkit.getServer().getLogger().warning("MGCommands failed to load instance of LobbyManager!");
    		return true;
    	}
    	else if(mgManager.plugin() == null)
    	{
    		Bukkit.getServer().getLogger().warning("MGCommands failed to load instance of JavaPlugin!");
    		return true;
    	}
    	
    	MGParsedCommand pbCommand = parser.parseCommand(args);
    	
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
