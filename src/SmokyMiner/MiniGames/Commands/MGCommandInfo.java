package SmokyMiner.MiniGames.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import SmokyMiner.MiniGames.Commands.CommandHandler.MGCommandParser.command_info;
import SmokyMiner.MiniGames.Commands.CommandHandler.MGParsedCommand;
import SmokyMiner.Minigame.Main.MGManager;

public class MGCommandInfo implements MGCommand
{
	MGManager manager;
	
	public MGCommandInfo(MGManager manager)
	{
		this.manager = manager;
	}

	@Override
	public boolean onCommand(MGParsedCommand command) 
	{
		if(command.getCommand() != command_info.INFO)
			return false;
		
		CommandSender sender = command.getSender();
		
		if(sender != null)
		{
			sender.sendMessage(ChatColor.GOLD + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "MC Paintball");
			sender.sendMessage("");
			sender.sendMessage(ChatColor.YELLOW + "Developed By: " + ChatColor.GRAY + "" + ChatColor.ITALIC + "SmokyMiner");
			sender.sendMessage(ChatColor.YELLOW + "Map Builders: " + ChatColor.GRAY + "" + ChatColor.ITALIC + "Berrydude Crazyasian777");
			sender.sendMessage(ChatColor.GRAY +   "                 " + ChatColor.ITALIC + "Hazop GummyBearsz SmokyMiner");
		}
		
		return true;
	}

	@Override
	public command_info getCommand() 
	{
		return command_info.INFO;
	}
}
