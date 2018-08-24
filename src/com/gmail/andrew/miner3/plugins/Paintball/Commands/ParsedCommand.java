package com.gmail.andrew.miner3.plugins.Paintball.Commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.CommandSender;

import com.gmail.andrew.miner3.plugins.Paintball.Commands.CommandParser.Command;

public class ParsedCommand 
{
	private HashMap<String, ArrayList<String>> parseTree;
	private Command type;
	private CommandSender sender;
	private int size;
	
	public ParsedCommand(Command command)
	{
		parseTree = new HashMap<String, ArrayList<String>>();
		type = command;
		sender = null;
		size = 0;
	}
	
	public ParsedCommand(Command command, CommandSender sender)
	{
		parseTree = new HashMap<String, ArrayList<String>>();
		type = command;
		this.sender = sender;
		size = 0;
	}
	
	public ParsedCommand(Command command, ArrayList<String> identifiers)
	{
		parseTree = new HashMap<String, ArrayList<String>>();
		type = command;
		sender = null;
		size = 0;
		
		for(String str : identifiers)
		{
			addIdentifier(str);
		}
	}
	
	public ParsedCommand(Command command, ArrayList<String> identifiers, CommandSender sender)
	{
		parseTree = new HashMap<String, ArrayList<String>>();
		type = command;
		this.sender = sender;
		
		for(String str : identifiers)
		{
			addIdentifier(str);
		}
	}
	
	public Command getCommand()
	{
		return type;
	}
	
	public void addIdentifier(String identifier)
	{
		parseTree.put(identifier, new ArrayList<String>());
	}
	
	public void removeIdentifier(String identifier)
	{
		parseTree.remove(identifier);
	}
	
	public boolean add(String identifier, String item)
	{
		if(!parseTree.containsKey(identifier))
			return false;
		
		parseTree.get(identifier).add(item);
		size++;
		return true;
	}
	
	public boolean remove(String identifier, String item)
	{
		if(!parseTree.containsKey(identifier))
			return false;
		
		parseTree.get(identifier).remove(item);
		size--;
		return true;
	}
	
	public ArrayList<String> getArgs(String identifier)
	{
		if(parseTree.containsKey(identifier))
			return (ArrayList<String>) parseTree.get(identifier).clone();
		
		return null;
	}
	
	public boolean isEmpty()
	{
		for(Map.Entry<String, ArrayList<String>> pair : parseTree.entrySet())
		{
			if(pair.getValue().size() > 0)
				return false;
		}
		
		return true;
	}
	
	public void clear()
	{
		parseTree.clear();
	}

	public void setSender(CommandSender sender) 
	{
		this.sender = sender;
	}
	
	public CommandSender getSender()
	{
		return sender;
	}
	
	public int size()
	{
		return size;
	}
}
