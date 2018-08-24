package com.gmail.andrew.miner3.plugins.Paintball.Commands;

import com.gmail.andrew.miner3.plugins.Paintball.Commands.CommandParser.Command;

public interface PaintballCommand 
{
	public boolean onCommand(ParsedCommand command);
	public Command getCommand();
}
