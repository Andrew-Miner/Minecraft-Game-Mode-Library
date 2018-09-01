package SmokyMiner.MiniGames.InventoryMenu;

import java.util.EventObject;

import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;

@SuppressWarnings("serial")
public class MGItemClickEvent extends EventObject
{
	private MGMenuItem item;
	private ClickType clickType;
	private HumanEntity player;
	private Sound sound;
	
	boolean cancelled;

	public MGItemClickEvent(Object source, MGMenuItem item, ClickType clickType, HumanEntity player, Sound clickSound) 
	{
		super(source);
		
		this.item = item;
		this.clickType = clickType;
		this.player = player;
		this.sound = clickSound;
		
		cancelled = false;
	}
	
	public MGInventoryMenu getMenu() { return (MGInventoryMenu) super.getSource(); }
	public MGMenuItem getItem() { return item; }
	public ClickType getClickType() { return clickType; }
	public HumanEntity getWhoClicked() { return player; }
	
	public Sound getClickSound()
	{
		return sound;
	}
	
	public void setClickSound(Sound sound)
	{
		this.sound = sound;
	}
}
