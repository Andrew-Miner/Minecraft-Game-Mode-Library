package com.gmail.andrew.miner3.plugins.Paintball.Commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class CommandParser 
{
	public static enum Command
	{
	    PB(-1, "pb"),
		RANDOMSTR(0, "randomstr"),
	    ARENA(1, "map"),
	    INFO(2, "info"),
	    ENABLE(3, "enable"),
	    DISABLE(4, "disable"),
	    LIST(5, "list"),
	    WORLD(6, "world"),
	    MAX(7, "max"),
	    Players(8, "players"),
	    Teams(9, "teams"),
	    ADD(10, "add"),
	    CENTER(11, "center"),
	    SPAWN(12, "spawn"),
	    SPECTATOR(13, "spectator"),
	    TEAM(14, "team"),
	    DESCRIPTION(15, "description"),
	    BOUND(16, "bound"),
	    DEFAULT(17, "default"),
	    INGAME(18, "ingame"),
	    REMOVE(19, "clear")
	    {
	    		@Override
	    		public Command next() 
	    		{
	    			return values()[0];
	    		};
	    };

	    private int value;
	    private String cmdStr;
	    private ArrayList<String> identifiers;

	    Command(int Value, String cmdStr) 
	    {
	        this.value = Value;
	        this.cmdStr = cmdStr;
	        identifiers = new ArrayList<String>();
	    }
	    
	    public void addIdentifier(String identifier)
	    {
	    	identifiers.add(identifier);
	    }
	    
	    public void removeIdentifier(String identifier)
	    {
	    	identifiers.remove(identifier);
	    }
	    
	    public void setIdentifiers(ArrayList<String> identifiers)
	    {
	    	this.identifiers = identifiers;
	    }
	    
	    public ArrayList<String> getIdentifiers()
	    {
			return identifiers;
	    }

	    public int value() 
	    {
	    	return value;
	    }
	    
	    public String string()
	    {
	    	return cmdStr;
	    }

	    public static Command fromInt(int i) 
	    {
	        for (Command cmd : Command.values()) 
	        {
	            if (cmd.value() == i) { return cmd; }
	        }
	        return null;
	    }
	    
	    public static Command fromString(String cmdStr) 
	    {
	        for (Command cmd : Command.values()) 
	        {
	            if (cmd.string().equalsIgnoreCase(cmdStr)) { return cmd; }
	        }
	        return RANDOMSTR;
	    }
	    
	    public Command next() 
	    {
	        return values()[ordinal() + 1];
	    }
	}
	
	public static final String CMD_ID = "command";
	public static final String ARENA_ID = "arena";
	public static final String TYPE_ID = "type";
	public static final String WORLD_ID = "world";
	public static final String MAX_PLAYERS_ID = "maxplayers";
	public static final String MAX_TEAMS_ID = "maxteams";
	public static final String WHAT_TYPE_ID = "addtype";
	public static final String WHAT_ID = "addwhat";
	public static final String TEAM_ID = "teamid";
	public static final String DESC_ID = "description";
	
	private static final int MAXROWS = 20;
	private static final int MAXCOLS = 20;
	
	private int cmdStateArray[][] = null;
	private int maxRows;
	private int maxCols;
	
	private ArrayList<Integer> successRows;
	private HashMap<Integer, HashMap<String, Command[]>> rowInsertionIdentifier;
	
	
	public CommandParser()
	{
		successRows = new ArrayList<Integer>();
		rowInsertionIdentifier = new HashMap<Integer, HashMap<String, Command[]>>();
		
		maxRows = MAXROWS;
		maxCols = MAXCOLS;
		cmdStateArray = new int[maxRows][maxCols];

		buildSuccessRows(successRows);
		buildCommandIdentifiers(rowInsertionIdentifier);
		buildStateArray(cmdStateArray, maxRows, maxCols);
	}
	
	protected static void buildCommandIdentifiers(HashMap<Integer, HashMap<String, Command[]>> rowInsertionIdentifier) 
	{
		// TODO Auto-generated method stub
		Command.ARENA.addIdentifier(CMD_ID);
		Command.ARENA.addIdentifier(ARENA_ID);
		Command.ARENA.addIdentifier(TYPE_ID);
		Command.ARENA.addIdentifier(WORLD_ID);
		Command.ARENA.addIdentifier(MAX_PLAYERS_ID);
		Command.ARENA.addIdentifier(MAX_TEAMS_ID);
		Command.ARENA.addIdentifier(WHAT_TYPE_ID);
		Command.ARENA.addIdentifier(WHAT_ID);
		Command.ARENA.addIdentifier(TEAM_ID);
		Command.ARENA.addIdentifier(DESC_ID);
		
		Command.INFO.addIdentifier(CMD_ID);
		
		// Row 0
		
		HashMap<String, Command[]> insertionIdentifier = new HashMap<String, Command[]>();
		
		Command[] commandIds = {Command.ARENA, Command.INFO};
		insertionIdentifier.put(CMD_ID, commandIds);
		
		rowInsertionIdentifier.put(0, insertionIdentifier);
		
		
		//Row 2
		
		insertionIdentifier = new HashMap<String, Command[]>();
		
		Command[] mapNameIds = { Command.RANDOMSTR };
		insertionIdentifier.put(ARENA_ID, mapNameIds);
		
		Command[] typeIds = { Command.LIST };
		insertionIdentifier.put(TYPE_ID, typeIds);
		
		rowInsertionIdentifier.put(2, insertionIdentifier);
		
		
		// Row 3
		
		insertionIdentifier = new HashMap<String, Command[]>();
		
		insertionIdentifier.put(ARENA_ID, mapNameIds);
		
		Command[] typeIds2 = { Command.INFO, Command.ENABLE, Command.DISABLE, Command.WORLD, Command.ADD, Command.DESCRIPTION, Command.REMOVE };
		insertionIdentifier.put(TYPE_ID, typeIds2);
		
		rowInsertionIdentifier.put(3, insertionIdentifier);
		
		
		// Row 4
		
		insertionIdentifier = new HashMap<String, Command[]>();
		
		Command[] worldIds = { Command.RANDOMSTR, Command.WORLD };
		insertionIdentifier.put(WORLD_ID, worldIds);
		
		rowInsertionIdentifier.put(4, insertionIdentifier);
		
		
		// Row 5
		
		insertionIdentifier = new HashMap<String, Command[]>();
		
		Command[] typeIds3 = { Command.Players, Command.Teams };
		insertionIdentifier.put(TYPE_ID, typeIds3);
		
		rowInsertionIdentifier.put(5, insertionIdentifier);
		
		
		// Row 6
		
		insertionIdentifier = new HashMap<String, Command[]>();
		insertionIdentifier.put(MAX_PLAYERS_ID, mapNameIds);
		rowInsertionIdentifier.put(6, insertionIdentifier);
		
		
		// Row 7
		
		insertionIdentifier = new HashMap<String, Command[]>();
		insertionIdentifier.put(MAX_TEAMS_ID, mapNameIds);
		rowInsertionIdentifier.put(7, insertionIdentifier);
		
		
		// Row 8
		
		insertionIdentifier = new HashMap<String, Command[]>();
		
		Command[] addType = { Command.CENTER };
		insertionIdentifier.put(WHAT_ID, addType);
		
		Command[] addRealType = { Command.SPAWN, Command.BOUND, Command.CENTER };
		insertionIdentifier.put(WHAT_TYPE_ID, addRealType);
		
		rowInsertionIdentifier.put(8, insertionIdentifier);
		
		
		// Row 9
		
		insertionIdentifier = new HashMap<String, Command[]>();
		
		Command[] addType1 = { Command.SPECTATOR, Command.TEAM };
		insertionIdentifier.put(WHAT_ID, addType1);
		
		rowInsertionIdentifier.put(9, insertionIdentifier);
		
		
		// Row 10
		
		insertionIdentifier = new HashMap<String, Command[]>();
		
		Command[] teamId = { Command.RANDOMSTR };
		insertionIdentifier.put(TEAM_ID, teamId);
		
		rowInsertionIdentifier.put(10, insertionIdentifier);
		
		
		// Row 11
		
		insertionIdentifier = new HashMap<String, Command[]>();
		
		Command[] descId = { Command.RANDOMSTR };
		insertionIdentifier.put(DESC_ID, descId);
		
		rowInsertionIdentifier.put(11, insertionIdentifier);
		
		
		// Row 12
		
		insertionIdentifier = new HashMap<String, Command[]>();
		
		Command[] addType2 = { Command.SPECTATOR, Command.DEFAULT, Command.INGAME };
		insertionIdentifier.put(WHAT_ID, addType2);
		
		rowInsertionIdentifier.put(12, insertionIdentifier);
	}
	
	private boolean commandListContains(Command[] cmdList, Command cmd)
	{
		for(int i = 0; i < cmdList.length; i++)
		{
			if(cmdList[i].equals(cmd))
				return true;
		}
		
		return false;
	}

	public ParsedCommand parseCommand(String[] args)
	{
		if(args.length == 0)
			return null;
		
		Command command = Command.fromString(args[0].toLowerCase());
		
		if(command.getIdentifiers().size() == 0)
			return null;
		
		ParsedCommand parsedCmd = new ParsedCommand(command, command.getIdentifiers());
		int row = 0;
		int lastRow = 0;
		
		for(int i = 0; i < args.length; i++)
		{
			String arg = args[i];
			Command colCmd = Command.fromString(arg);
			int nextRow = cmdStateArray[row][colCmd.value()];
			
			
			if(nextRow == -1)
				return null;
			
			if(rowInsertionIdentifier.containsKey(row))
			{
				HashMap<String, Command[]> ids = rowInsertionIdentifier.get(row);
				
				for(Map.Entry<String, Command[]> entry : ids.entrySet())
				{
					if(commandListContains(entry.getValue(), colCmd))
						parsedCmd.add(entry.getKey(), arg);
				}
			}
			
			lastRow = row;
			row = nextRow;
		}
		
		if(successRows.contains(lastRow))
			return parsedCmd;
		
		return null;
	}
	
	protected static void buildStateArray(int[][] stateArray, int maxRows, int maxCols)
	{
	    // Current State Array Configuration:
	     /*=====================================================================================================================================================================================
	      *  | Random Str | Arena | Info | Enable | Disable | List | World | Max | Players | Teams | Add | Center | Spawn | Spectator | Team | Description | Bound | Default | Ingame | Remove |
	      * -|------------|-------|------|--------|---------|------|-------|-----|---------|-------|-----|--------|-------|-----------|------|-------------|-------|---------|--------|--------|
SuccessRow* 0|            |   2   |   1  |        |         |      |       |     |         |       |     |        |       |           |      |             |       |         |        |        | Check For Starting Commands (e.g. Arena, Info)
	      * -|------------|-------|------|--------|---------|------|-------|-----|---------|-------|-----|--------|-------|-----------|------|-------------|-------|---------|--------|--------|
Error Row * 1|            |       |      |        |         |      |       |     |         |       |     |        |       |           |      |             |       |         |        |        | Check Command Ends When Necessary 
	      * -|------------|-------|------|--------|---------|------|-------|-----|---------|-------|-----|--------|-------|-----------|------|-------------|-------|---------|--------|--------|
SuccessRow* 2|      3     |       |      |        |         |   1  |       |     |         |       |     |        |       |           |      |             |       |         |        |        | Check For Arena Name
	      * -|------------|-------|------|--------|---------|------|-------|-----|---------|-------|-----|--------|-------|-----------|------|-------------|-------|---------|--------|--------|
SuccessRow* 3|      3     |       |   1  |    1   |    1    |      |   4   |  5  |         |       |  8  |        |       |           |      |      11     |       |         |        |    8   | Check For Continued Arena Name OR Following Command
	      * -|------------|-------|------|--------|---------|------|-------|-----|---------|-------|-----|--------|-------|-----------|------|-------------|-------|---------|--------|--------|
SuccessRow* 4|      4     |       |      |        |         |      |       |     |         |       |     |        |       |           |      |             |       |         |        |        | Check For World Name (Continued)
	      * -|------------|-------|------|--------|---------|------|-------|-----|---------|-------|-----|--------|-------|-----------|------|-------------|-------|---------|--------|--------|                                                                                  
	      * 5|            |       |      |        |         |      |       |     |    6    |   7   |     |        |       |           |      |             |       |         |        |        |
	      * -|------------|-------|------|--------|---------|------|-------|-----|---------|-------|-----|--------|-------|-----------|------|-------------|-------|---------|--------|--------|
SuccessRow* 6|      1     |       |      |        |         |      |       |     |         |       |     |        |       |           |      |             |       |         |        |        |
	      * -|------------|-------|------|--------|---------|------|-------|-----|---------|-------|-----|--------|-------|-----------|------|-------------|-------|---------|--------|--------|
SuccessRow* 7|      1     |       |      |        |         |      |       |     |         |       |     |        |       |           |      |             |       |         |        |        |
	      * -|------------|-------|------|--------|---------|------|-------|-----|---------|-------|-----|--------|-------|-----------|------|-------------|-------|---------|--------|--------|
SuccessRow* 8|            |       |      |        |         |      |       |     |         |       |     |    1   |   9   |           |      |             |  12   |         |        |        |
	      * -|------------|-------|------|--------|---------|------|-------|-----|---------|-------|-----|--------|-------|-----------|------|-------------|-------|---------|--------|--------|
SuccessRow* 9|            |       |      |        |         |      |       |     |         |       |     |        |       |     1     |  10  |             |       |         |        |        |
	      * -|------------|-------|------|--------|---------|------|-------|-----|---------|-------|-----|--------|-------|-----------|------|-------------|-------|---------|--------|--------|
SuccessRow*10|      1     |       |      |        |         |      |       |     |         |       |     |        |       |           |      |             |       |         |        |        |
	      * -|------------|-------|------|--------|---------|------|-------|-----|---------|-------|-----|--------|-------|-----------|------|-------------|-------|---------|--------|--------|
SuccessRow*11|     11     |       |      |        |         |      |       |     |         |       |     |        |       |           |      |             |       |         |        |        |
	      * -|------------|-------|------|--------|---------|------|-------|-----|---------|-------|-----|--------|-------|-----------|------|-------------|-------|---------|--------|--------|
SuccessRow*12|            |       |      |        |         |      |       |     |         |       |     |        |       |     1     |      |             |       |    1    |    1   |        |
	      * -|------------|-------|------|--------|---------|------|-------|-----|---------|-------|-----|--------|-------|-----------|------|-------------|-------|---------|--------|--------|
	      *13|            |       |      |        |         |      |       |     |         |       |     |        |       |           |      |             |       |         |        |        |
	      * -|------------|-------|------|--------|---------|------|-------|-----|---------|-------|-----|--------|-------|-----------|------|-------------|-------|---------|--------|--------|
	      *14|            |       |      |        |         |      |       |     |         |       |     |        |       |           |      |             |       |         |        |        |
	      * -|------------|-------|------|--------|---------|------|-------|-----|---------|-------|-----|--------|-------|-----------|------|-------------|-------|---------|--------|--------|
	      *15|            |       |      |        |         |      |       |     |         |       |     |        |       |           |      |             |       |         |        |        |
	      * -|------------|-------|------|--------|---------|------|-------|-----|---------|-------|-----|--------|-------|-----------|------|-------------|-------|---------|--------|--------|
	      *16|            |       |      |        |         |      |       |     |         |       |     |        |       |           |      |             |       |         |        |        |
	      * -|------------|-------|------|--------|---------|------|-------|-----|---------|-------|-----|--------|-------|-----------|------|-------------|-------|---------|--------|--------|
	     */
		
		Array2DMethods.initializeArray(stateArray, maxRows, maxCols, -1);
		
		// PB Command Array:
		//stateArray[0][Command.PB.value()] = 1;
		
		// Info State Array
		// Success States: 2
		stateArray[0][Command.INFO.value()] = 1;
		
		stateArray[0][Command.ARENA.value()] = 2;
		stateArray[2][Command.RANDOMSTR.value()] = 3;
		stateArray[2][Command.LIST.value()] = 1;
		stateArray[3][Command.RANDOMSTR.value()] = 3;
		
		stateArray[3][Command.ENABLE.value()] = 1;
		stateArray[3][Command.DISABLE.value()] = 1;
		stateArray[3][Command.INFO.value()] = 1;
		
		stateArray[3][Command.WORLD.value()] = 4;
		stateArray[4][Command.RANDOMSTR.value()] = 4;
		stateArray[4][Command.WORLD.value()] = 4;
		
		stateArray[3][Command.MAX.value()] = 5;
		stateArray[5][Command.Players.value()] = 6;
		stateArray[5][Command.Teams.value()] = 7;
		stateArray[6][Command.RANDOMSTR.value()] = 1;
		stateArray[7][Command.RANDOMSTR.value()] = 1;
		
		stateArray[3][Command.ADD.value()] = 8;
		stateArray[3][Command.REMOVE.value()] = 8;
		stateArray[8][Command.CENTER.value()] = 1;
		stateArray[8][Command.SPAWN.value()] = 9;
		stateArray[9][Command.SPECTATOR.value()] = 1;
		stateArray[9][Command.TEAM.value()] = 10;
		stateArray[10][Command.RANDOMSTR.value()] = 1;
		
		stateArray[3][Command.DESCRIPTION.value()] = 11;
		stateArray[11][Command.RANDOMSTR.value()] = 11;
		
		stateArray[8][Command.BOUND.value()] = 12;
		stateArray[12][Command.SPECTATOR.value()] = 1;
		stateArray[12][Command.INGAME.value()] = 1;
		stateArray[12][Command.DEFAULT.value()] = 1;
	}
	
	protected static void buildSuccessRows(ArrayList<Integer> successRows)
	{
		successRows.add(0);
		successRows.add(2);
		successRows.add(3);
		successRows.add(4);
		successRows.add(6);
		successRows.add(7);
		successRows.add(8);
		successRows.add(9);
		successRows.add(10);
		successRows.add(11);
		successRows.add(12);
	}
	
	public List<String> getAutoComplete(String[] arguments)
	{
		String[] args = removeEmpty(arguments);
		
		if(args.length == 0 || (args.length == 1 && args[0].isEmpty()))
			return getCommandsByRow(0);
		
		Command command = Command.fromString(args[0].toLowerCase());
		
		if(command.getIdentifiers().size() == 0)
			return null;
		
		int row = 0;
		
		for(int i = 0; i < args.length; i++)
		{
			String arg = args[i];
			Command colCmd = Command.fromString(arg);
			int nextRow = cmdStateArray[row][colCmd.value()];
			
			if(nextRow == -1)
				return null;
			row = nextRow;
		}
		
		return getCommandsByRow(row);
	}

	private String[] removeEmpty(String[] arguments) 
	{
		ArrayList<String> list = new ArrayList<String>();
		
		for(int i = 0; i < arguments.length; i++)
		{
			if(!arguments[i].isEmpty())
				list.add(arguments[i]);
		}
		
		String[] array = new String[list.size()];
		list.toArray(array);
		
		return array;
	}

	private List<String> getCommandsByRow(int row) 
	{
		List<String> addTo = new ArrayList<String>();
		for(Command cmd : Command.values())
		{
			if(cmd.value() > -1 && cmdStateArray[row][cmd.value()] != -1)
			{
				if(cmd.equals(Command.RANDOMSTR))
				{
					switch(row)
					{
					case 4:
					case 2:
					case 3:
						addTo.add("<Name>");
						// TODO: ADD MAP NAMES HERE!!!
						break;
					case 6:
					case 7:
					case 10:
						addTo.add("#");
						break;
					case 11:
						addTo.add("<Description>");
						break;
					default:
						addTo.add(cmd.string().substring(0, 1).toUpperCase() + cmd.string().substring(1));
					}
				}
				else
					addTo.add(cmd.string().substring(0, 1).toUpperCase() + cmd.string().substring(1));
			}
		}
		
		return addTo;
	}
}
