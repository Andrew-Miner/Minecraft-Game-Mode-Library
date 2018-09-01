package SmokyMiner.MiniGames.InventoryMenu;

import java.util.List;

import org.bukkit.Material;

public interface MGMenuItemAnimator 
{
	public default String updateName(String name)
	{
		return name;
	}
	
	public default List<String> updateLore(List<String> lore)
	{
		return lore;
	}
	
	public default Material updateMaterial(Material material)
	{
		return material;
	}
	
	public default void itemClicked(MGItemClickEvent e)
	{
		
	}
	
	public default void updateMenuSlot(MGMenuItem item)
	{
		
	}
}