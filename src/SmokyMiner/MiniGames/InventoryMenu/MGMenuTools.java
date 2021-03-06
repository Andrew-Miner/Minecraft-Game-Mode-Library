package SmokyMiner.MiniGames.InventoryMenu;

import java.util.ArrayList;
import java.util.HashMap;

public class MGMenuTools 
{
	public static final int MAX_TITLE_LENGTH = 32;
	
	public static String centerTitle(String title)
	{
		int spaces = (MAX_TITLE_LENGTH/2 - title.length()/2)*6/5;
		
		String tempTitle = "";
		for(int i = 0; i < spaces; i++)
			tempTitle += " ";
		
		return tempTitle + title;
	}

	public static int getNextSlot(HashMap<Integer, MGMenuItem> hashMap, int maxSlots) 
	{
		for(int i = 0; i < maxSlots; i++)
		{
			if(!hashMap.containsKey(i))
				return i;
		}
		
		return -1;
	}

	public static int getOpenPage(ArrayList<HashMap<Integer, MGMenuItem>> pages, int maxPageSlots) 
	{
		for(int i = 0; i < pages.size(); i++)
		{
			if(pages.get(i).size() < maxPageSlots)
				return i;
		}
		
		return -1;
	}
}
