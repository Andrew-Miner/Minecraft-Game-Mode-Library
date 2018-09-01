package SmokyMiner.MiniGames.InventoryMenu;

import java.util.ArrayList;
import java.util.regex.Matcher;

public class MGCharBuilderAnimation extends MGItemAnimation
{
	public MGCharBuilderAnimation(MGInventoryMenu menu)
	{
		super(menu);
	}

	@Override
	public String updateName(String name)
	{
		super.updateName(name);

		String[] nameSplit = MGItemAnimation.STRIP_COLOR_PATTERN.split(this.name);
		String[] tempSplit = MGItemAnimation.STRIP_COLOR_PATTERN.split(name);

		ArrayList<String> colorSplit = new ArrayList<String>();
		Matcher m = MGItemAnimation.STRIP_COLOR_PATTERN.matcher(this.name);

		while(m.find())
			colorSplit.add(m.group());

		String tempName = "";
		int colorIndex = 0;

		if(this.name.equals(name))
		{
			int firstIndex = 0;

			while(firstIndex < nameSplit.length && nameSplit[firstIndex].isEmpty())
			{
				firstIndex++;
				if(colorSplit.size() > colorIndex)
					tempName += colorSplit.get(colorIndex++);
			}

			if(!nameSplit[firstIndex].isEmpty())
				tempName += nameSplit[firstIndex].substring(0, 1);
		}
		else
		{
			for(int i = 0; i < tempSplit.length; i++)
			{
				String curChunk = tempSplit[i];
				String realChunk = nameSplit[i];
				
				if(!curChunk.isEmpty())
				{
					if(curChunk.equals(realChunk))
					{
						tempName += curChunk;
						
						if(i == tempSplit.length - 1 && tempSplit.length < nameSplit.length)
						{
							do
							{
								i++;
								
								if(colorSplit.size() > colorIndex)
									tempName += colorSplit.get(colorIndex++);
								
							} while(nameSplit[i].isEmpty() && i < nameSplit.length-1);
							
							if(nameSplit[i].isEmpty())
								return tempName;
							
							tempName += nameSplit[i].substring(0, 1);
						}
					}
					else
						tempName += realChunk.substring(0, curChunk.length() + 1);
				}
				
				if(colorIndex < colorSplit.size())
					tempName += colorSplit.get(colorIndex++);
			}
		}

		return tempName;
	}
}
