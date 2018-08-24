package com.gmail.andrew.miner3.plugins.Paintball.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.gmail.andrew.miner3.plugins.Paintball.PluginManager;
import com.gmail.andrew.miner3.plugins.Paintball.Commands.CommandParser.Command;

public class CommandInfo implements PaintballCommand
{
	PluginManager plugin;
	
	public CommandInfo(PluginManager plugin)
	{
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(ParsedCommand command) 
	{
		if(command.getCommand() != Command.INFO)
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
	public Command getCommand() 
	{
		return Command.INFO;
	}
}
