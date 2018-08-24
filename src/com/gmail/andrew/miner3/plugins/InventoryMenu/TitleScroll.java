package com.gmail.andrew.miner3.plugins.InventoryMenu;

import java.util.ArrayList;
import java.util.regex.Matcher;

import org.bukkit.ChatColor;

public class TitleScroll implements MenuAnimation
{
	private final int colorLength = 3;
	
	private String title = null;
	private ChatColor scrollColor;
	private int colorPos;
	
	public TitleScroll(ChatColor scrollColor)
	{
		this.scrollColor = scrollColor;
		colorPos = -colorLength;
	}
	
	@Override
	public String updateTitle(String string)
	{
		if(title == null)
			title = String.copyValueOf(string.toCharArray());

		int curPos = colorPos;
		colorPos++;
		
		int length = colorLength;
		
		if(curPos < 0 && curPos + colorLength > 0)
		{
			length += curPos;
			curPos = 0;
		}
		
		if(curPos < 0)
			return string;
		
		String fullTitle = ChatColor.stripColor(title);
		
		if(curPos < fullTitle.length())
		{
			String[] fullSplit = ItemAnimation.STRIP_COLOR_PATTERN.split(title);

			ArrayList<String> colorSplit = new ArrayList<String>();
			Matcher m = ItemAnimation.STRIP_COLOR_PATTERN.matcher(title);

			while(m.find())
				colorSplit.add(m.group());

			String tempTitle = "";
			int colorIndex = 0;
			int charPos = 0;
			String lastColor = "";
			
			for(int i = 0; i < fullSplit.length; i++)
			{
				String chunk = fullSplit[i];
				int chunkPos = charPos;
				
				if(!chunk.isEmpty())
				{
					while(charPos < chunkPos + chunk.length())
					{
						if(charPos == curPos || (charPos == chunkPos && charPos >= curPos && charPos < curPos + length))
							tempTitle += scrollColor;
						else if(charPos == curPos + length)
							tempTitle += lastColor;
						
						tempTitle += chunk.charAt(charPos++);
					}
				}
				
				if(colorIndex < colorSplit.size())
					tempTitle += lastColor = colorSplit.get(colorIndex++);
			}
			
			return tempTitle;
		}
		else
		{
			colorPos = -colorLength;
			return title;
		}
	}
}
