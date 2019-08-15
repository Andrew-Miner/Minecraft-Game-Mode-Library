package SmokyMiner.MiniGames.Commands;

import SmokyMiner.MiniGames.Commands.CommandHandler.MGCommandParser.command_info;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import SmokyMiner.MiniGames.Commands.CommandHandler.MGParsedCommand;
import SmokyMiner.MiniGames.Effects.MGEffectUtils;
import SmokyMiner.MiniGames.Items.MGSmokeGrenade;
import SmokyMiner.Minigame.Main.MGManager;

public class MGCommandEffect implements MGCommand
{
	private static MGManager manager;
	private MGSmokeGrenade smkGrnd;
	
	public MGCommandEffect(MGManager manager)
	{
		MGCommandEffect.manager = manager;
		//smkGrnd = new MGSmokeGrenade(manager, 15*20, 3);
	}
	
	@Override
	public boolean onCommand(MGParsedCommand command)
	{
		if(command.getCommand() != command_info.EFFECT)
			return false;

		CommandSender sender = command.getSender();
		
		if(!(sender instanceof Player))
			return false;
		
		ArrayList<String> effect = command.getArgs(command_info.DESCRIPTION.string());
		String effectStr = "";
		for(String str : effect)
			effectStr += str + " ";
		effectStr = effectStr.substring(0, effectStr.length() - 1);
		
		return playEffect(effectStr, ((Player) sender).getLocation(), (Player) sender);
	}
	
	public boolean playEffect(String effect, Location loc, Player sender)
	{
		if(effect.equalsIgnoreCase("smoke"))
			MGEffectUtils.drawSmokeSphere(manager.plugin(), loc, 15*20, 8, 3);
		else if(effect.equalsIgnoreCase("smoke grenade"))
			smkGrnd.giveBlock(sender);
		else
			return false;
		return true;
	}
	
	@Override
	public command_info getCommand()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
