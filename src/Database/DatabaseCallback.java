package Database;

import java.sql.ResultSet;

public interface DatabaseCallback 
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
