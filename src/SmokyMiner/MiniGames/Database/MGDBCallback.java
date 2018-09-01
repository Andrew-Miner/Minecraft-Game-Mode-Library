package SmokyMiner.MiniGames.Database;

import java.sql.ResultSet;

public interface MGDBCallback 
{
	public default void onQueryDone(ResultSet set)
	{
		
	}
	
	public default void onUpdateDone()
	{
		
	}
	
	public default void onInitializeDone()
	{
		
	}
}
