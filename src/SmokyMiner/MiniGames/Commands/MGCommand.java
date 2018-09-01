package SmokyMiner.MiniGames.Commands;

import SmokyMiner.MiniGames.Commands.CommandHandler.MGParsedCommand;
import SmokyMiner.MiniGames.Commands.CommandHandler.MGCommandParser.command_info;

public interface MGCommand 
{
	public boolean onCommand(MGParsedCommand command);
	public command_info getCommand();
}
