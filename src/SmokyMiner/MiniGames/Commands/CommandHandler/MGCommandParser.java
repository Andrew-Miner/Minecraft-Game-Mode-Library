package SmokyMiner.MiniGames.Commands.CommandHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MGCommandParser 
{
	public static enum command_info
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
	    		public command_info next() 
	    		{
	    			return values()[0];
	    		};
	    };

	    private int value;
	    private String cmdStr;
	    private ArrayList<String> identifiers;

	    command_info(int Value, String cmdStr) 
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

	    public static command_info fromInt(int i) 
	    {
	        for (command_info cmd : command_info.values()) 
	        {
	            if (cmd.value() == i) { return cmd; }
	        }
	        return null;
	    }
	    
	    public static command_info fromString(String cmdStr) 
	    {
	        for (command_info cmd : command_info.values()) 
	        {
	            if (cmd.string().equalsIgnoreCase(cmdStr)) { return cmd; }
	        }
	        return RANDOMSTR;
	    }
	    
	    public command_info next() 
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
	private HashMap<Integer, HashMap<String, command_info[]>> rowInsertionIdentifier;
	
	
	public MGCommandParser()
	{
		successRows = new ArrayList<Integer>();
		rowInsertionIdentifier = new HashMap<Integer, HashMap<String, command_info[]>>();
		
		maxRows = MAXROWS;
		maxCols = MAXCOLS;
		cmdStateArray = new int[maxRows][maxCols];

		buildSuccessRows(successRows);
		buildCommandIdentifiers(rowInsertionIdentifier);
		buildStateArray(cmdStateArray, maxRows, maxCols);
	}
	
	protected static void buildCommandIdentifiers(HashMap<Integer, HashMap<String, command_info[]>> rowInsertionIdentifier) 
	{
		// TODO Auto-generated method stub
		command_info.ARENA.addIdentifier(CMD_ID);
		command_info.ARENA.addIdentifier(ARENA_ID);
		command_info.ARENA.addIdentifier(TYPE_ID);
		command_info.ARENA.addIdentifier(WORLD_ID);
		command_info.ARENA.addIdentifier(MAX_PLAYERS_ID);
		command_info.ARENA.addIdentifier(MAX_TEAMS_ID);
		command_info.ARENA.addIdentifier(WHAT_TYPE_ID);
		command_info.ARENA.addIdentifier(WHAT_ID);
		command_info.ARENA.addIdentifier(TEAM_ID);
		command_info.ARENA.addIdentifier(DESC_ID);
		
		command_info.INFO.addIdentifier(CMD_ID);
		
		// Row 0
		
		HashMap<String, command_info[]> insertionIdentifier = new HashMap<String, command_info[]>();
		
		command_info[] commandIds = {command_info.ARENA, command_info.INFO};
		insertionIdentifier.put(CMD_ID, commandIds);
		
		rowInsertionIdentifier.put(0, insertionIdentifier);
		
		
		//Row 2
		
		insertionIdentifier = new HashMap<String, command_info[]>();
		
		command_info[] mapNameIds = { command_info.RANDOMSTR };
		insertionIdentifier.put(ARENA_ID, mapNameIds);
		
		command_info[] typeIds = { command_info.LIST };
		insertionIdentifier.put(TYPE_ID, typeIds);
		
		rowInsertionIdentifier.put(2, insertionIdentifier);
		
		
		// Row 3
		
		insertionIdentifier = new HashMap<String, command_info[]>();
		
		insertionIdentifier.put(ARENA_ID, mapNameIds);
		
		command_info[] typeIds2 = { command_info.INFO, command_info.ENABLE, command_info.DISABLE, command_info.WORLD, command_info.ADD, command_info.DESCRIPTION, command_info.REMOVE };
		insertionIdentifier.put(TYPE_ID, typeIds2);
		
		rowInsertionIdentifier.put(3, insertionIdentifier);
		
		
		// Row 4
		
		insertionIdentifier = new HashMap<String, command_info[]>();
		
		command_info[] worldIds = { command_info.RANDOMSTR, command_info.WORLD };
		insertionIdentifier.put(WORLD_ID, worldIds);
		
		rowInsertionIdentifier.put(4, insertionIdentifier);
		
		
		// Row 5
		
		insertionIdentifier = new HashMap<String, command_info[]>();
		
		command_info[] typeIds3 = { command_info.Players, command_info.Teams };
		insertionIdentifier.put(TYPE_ID, typeIds3);
		
		rowInsertionIdentifier.put(5, insertionIdentifier);
		
		
		// Row 6
		
		insertionIdentifier = new HashMap<String, command_info[]>();
		insertionIdentifier.put(MAX_PLAYERS_ID, mapNameIds);
		rowInsertionIdentifier.put(6, insertionIdentifier);
		
		
		// Row 7
		
		insertionIdentifier = new HashMap<String, command_info[]>();
		insertionIdentifier.put(MAX_TEAMS_ID, mapNameIds);
		rowInsertionIdentifier.put(7, insertionIdentifier);
		
		
		// Row 8
		
		insertionIdentifier = new HashMap<String, command_info[]>();
		
		command_info[] addType = { command_info.CENTER };
		insertionIdentifier.put(WHAT_ID, addType);
		
		command_info[] addRealType = { command_info.SPAWN, command_info.BOUND, command_info.CENTER };
		insertionIdentifier.put(WHAT_TYPE_ID, addRealType);
		
		rowInsertionIdentifier.put(8, insertionIdentifier);
		
		
		// Row 9
		
		insertionIdentifier = new HashMap<String, command_info[]>();
		
		command_info[] addType1 = { command_info.SPECTATOR, command_info.TEAM };
		insertionIdentifier.put(WHAT_ID, addType1);
		
		rowInsertionIdentifier.put(9, insertionIdentifier);
		
		
		// Row 10
		
		insertionIdentifier = new HashMap<String, command_info[]>();
		
		command_info[] teamId = { command_info.RANDOMSTR };
		insertionIdentifier.put(TEAM_ID, teamId);
		
		rowInsertionIdentifier.put(10, insertionIdentifier);
		
		
		// Row 11
		
		insertionIdentifier = new HashMap<String, command_info[]>();
		
		command_info[] descId = { command_info.RANDOMSTR };
		insertionIdentifier.put(DESC_ID, descId);
		
		rowInsertionIdentifier.put(11, insertionIdentifier);
		
		
		// Row 12
		
		insertionIdentifier = new HashMap<String, command_info[]>();
		
		command_info[] addType2 = { command_info.SPECTATOR, command_info.DEFAULT, command_info.INGAME };
		insertionIdentifier.put(WHAT_ID, addType2);
		
		rowInsertionIdentifier.put(12, insertionIdentifier);
	}
	
	private boolean commandListContains(command_info[] cmdList, command_info cmd)
	{
		for(int i = 0; i < cmdList.length; i++)
		{
			if(cmdList[i].equals(cmd))
				return true;
		}
		
		return false;
	}

	public MGParsedCommand parseCommand(String[] args)
	{
		if(args.length == 0)
			return null;
		
		command_info command = command_info.fromString(args[0].toLowerCase());
		
		if(command.getIdentifiers().size() == 0)
			return null;
		
		MGParsedCommand parsedCmd = new MGParsedCommand(command, command.getIdentifiers());
		int row = 0;
		int lastRow = 0;
		
		for(int i = 0; i < args.length; i++)
		{
			String arg = args[i];
			command_info colCmd = command_info.fromString(arg);
			int nextRow = cmdStateArray[row][colCmd.value()];
			
			
			if(nextRow == -1)
				return null;
			
			if(rowInsertionIdentifier.containsKey(row))
			{
				HashMap<String, command_info[]> ids = rowInsertionIdentifier.get(row);
				
				for(Map.Entry<String, command_info[]> entry : ids.entrySet())
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
		
		Array2DFunctions.initializeArray(stateArray, maxRows, maxCols, -1);
		
		// PB Command Array:
		//stateArray[0][Command.PB.value()] = 1;
		
		// Info State Array
		// Success States: 2
		stateArray[0][command_info.INFO.value()] = 1;
		
		stateArray[0][command_info.ARENA.value()] = 2;
		stateArray[2][command_info.RANDOMSTR.value()] = 3;
		stateArray[2][command_info.LIST.value()] = 1;
		stateArray[3][command_info.RANDOMSTR.value()] = 3;
		
		stateArray[3][command_info.ENABLE.value()] = 1;
		stateArray[3][command_info.DISABLE.value()] = 1;
		stateArray[3][command_info.INFO.value()] = 1;
		
		stateArray[3][command_info.WORLD.value()] = 4;
		stateArray[4][command_info.RANDOMSTR.value()] = 4;
		stateArray[4][command_info.WORLD.value()] = 4;
		
		stateArray[3][command_info.MAX.value()] = 5;
		stateArray[5][command_info.Players.value()] = 6;
		stateArray[5][command_info.Teams.value()] = 7;
		stateArray[6][command_info.RANDOMSTR.value()] = 1;
		stateArray[7][command_info.RANDOMSTR.value()] = 1;
		
		stateArray[3][command_info.ADD.value()] = 8;
		stateArray[3][command_info.REMOVE.value()] = 8;
		stateArray[8][command_info.CENTER.value()] = 1;
		stateArray[8][command_info.SPAWN.value()] = 9;
		stateArray[9][command_info.SPECTATOR.value()] = 1;
		stateArray[9][command_info.TEAM.value()] = 10;
		stateArray[10][command_info.RANDOMSTR.value()] = 1;
		
		stateArray[3][command_info.DESCRIPTION.value()] = 11;
		stateArray[11][command_info.RANDOMSTR.value()] = 11;
		
		stateArray[8][command_info.BOUND.value()] = 12;
		stateArray[12][command_info.SPECTATOR.value()] = 1;
		stateArray[12][command_info.INGAME.value()] = 1;
		stateArray[12][command_info.DEFAULT.value()] = 1;
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
		
		command_info command = command_info.fromString(args[0].toLowerCase());
		
		if(command.getIdentifiers().size() == 0)
			return null;
		
		int row = 0;
		
		for(int i = 0; i < args.length; i++)
		{
			String arg = args[i];
			command_info colCmd = command_info.fromString(arg);
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
		for(command_info cmd : command_info.values())
		{
			if(cmd.value() > -1 && cmdStateArray[row][cmd.value()] != -1)
			{
				if(cmd.equals(command_info.RANDOMSTR))
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
