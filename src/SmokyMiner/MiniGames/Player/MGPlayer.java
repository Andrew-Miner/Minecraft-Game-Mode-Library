package SmokyMiner.MiniGames.Player;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import SmokyMiner.MiniGames.Maps.MGBound;

import To.Be.Purged.SnowBowMethods;

public class MGPlayer 
{
	private int kills;
	private int deaths;
	private int currentScore;
	private int totalScore;
	private int wins;
	private int losses;
	private long lastShot;
	private long fireRate;
	private long lastReload;
	private boolean isReloading;
	private int shotsFired;
	private int currency;
	
	private boolean isDead;
	private boolean spectating;
	private boolean pregame;
	private boolean ingame;
	private UUID id;
	
	private Location storedLoc;
	
	private MGBound selection;
	
	private Inventory playerInv;
	
	public MGPlayer(UUID id)
	{
		this.id = id;
		this.kills = 0;
		this.deaths = 0;
		this.currentScore = 0;
		this.totalScore = 0;
		
		this.lastShot = 0;
		this.fireRate = SnowBowMethods.firingDelay0;
		this.lastReload = lastShot;
		this.isReloading = false;
		shotsFired = 0;
		
		
		pregame = true;
		ingame = false;
		spectating = false;
		isDead = false;
		
		storedLoc = null;	
		
		selection = new MGBound();
		
		playerInv = Bukkit.createInventory(Bukkit.getPlayer(id), 36);
	}
	
	public MGPlayer(UUID id, int kills, int deaths, int totalScore, int currentScore)
	{
		this.id = id;
		this.kills = kills;
		this.deaths = deaths;
		this.currentScore = currentScore;
		this.totalScore = totalScore;
		
		this.lastShot = 0;
		this.fireRate = SnowBowMethods.firingDelay0;
		this.lastReload = lastShot;
		this.isReloading = false;
		shotsFired = 0;
		
		
		pregame = true;
		ingame = false;
		spectating = false;
		isDead = false;
		
		storedLoc = null;	
		
		selection = new MGBound();
		
		playerInv = Bukkit.createInventory(Bukkit.getPlayer(id), 36);
	}
	
	@Override
	public int hashCode()
	{
		return id.hashCode();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof MGPlayer)
		{
			MGPlayer other = (MGPlayer) obj;
			
			if(other.id.equals(id))
				return true;
		}
		else if(obj instanceof UUID)
		{
			if(id.equals(obj))
				return true;
		}
		else
			return super.equals(obj);
		
		return false;
	}
	
	public UUID getID()
	{
		return id;
	}
	
	public void addKill()
	{
		kills++;
	}
	
	public void addDeath()
	{
		deaths++;
	}
	
	public int getKills()
	{
		return kills;
	}
	
	public int getDeaths()
	{
		return deaths;
	}
	
	public int getCurrentScore()
	{
		return currentScore;
	}
	
	public void setCurrentScore(int score)
	{
		this.currentScore = score;
	}
	
	public void addToScore(int val)
	{
		currentScore += val;
		totalScore += val;
	}
	
	public int getTotalScore()
	{
		return totalScore;
	}
	
	public void setTotalScore(int score)
	{
		totalScore = score;
	}
	
	public boolean isSpectating()
	{
		return spectating;
	}
	
	public boolean isInPregame()
	{
		return pregame;
	}
	
	public boolean isInGame()
	{
		return ingame;
	}
	
	public void setPregame()
	{
		ingame = false;
		spectating = false;
		pregame = true;
		isDead = false;
	}
	
	public void setInGame()
	{
		pregame = false;
		spectating = false;
		ingame = true;
		isDead = false;
	}
	
	public void setSpectating()
	{
		pregame = false;
		ingame = false;
		spectating = true;
		isDead = true;
	}
	
	public void storeLocation()
	{
		storedLoc = Bukkit.getPlayer(id).getLocation();
	}
	
	public Location getStoredLoc()
	{
		return storedLoc;
	}
	
	public void spawn(Location loc, MGSpawnKit kit)
	{
		if(pregame)
			spawnPregame(loc, kit);
		else if(spectating)
			spawnSpectating(loc, kit);
		else if(ingame)
			spawnInGame(loc, kit);
	}
	
	public void spawnPregame(Location loc, MGSpawnKit kit)
	{
		Player p = Bukkit.getServer().getPlayer(id);
		p.setHealth(20);
		p.setFoodLevel(100);
		p.teleport(loc);
		p.getInventory().clear();
		p.setGameMode(GameMode.CREATIVE);
		
		if(kit != null)
			kit.giveKit(p);
	}
	
	public void spawnSpectating(Location loc, MGSpawnKit kit)
	{
		Player p = Bukkit.getServer().getPlayer(id);
		p.setHealth(20);
		p.setFoodLevel(100);
		p.teleport(loc);
		p.getInventory().clear();
		p.setGameMode(GameMode.SPECTATOR);
		
		if(kit != null)
			kit.giveKit(p);
	}
	
	public void spawnInGame(Location loc, MGSpawnKit kit)
	{
		Player p = Bukkit.getServer().getPlayer(id);
		p.setHealth(20);
		p.setFoodLevel(100);
		p.teleport(loc);
		p.getInventory().clear();
		p.setGameMode(GameMode.ADVENTURE);
		
		if(kit != null)
			kit.giveKit(p);
	}
	
	public boolean isDead()
	{
		return isDead;
	}
	
	public void setDead(boolean dead)
	{
		isDead = dead;
	}

	public int getWins() 
	{
		return wins;
	}
	
	public void setWins(int wins)
	{
		this.wins = wins;
	}

	public void addWin() 
	{
		this.wins++;
	}

	public int getLosses() 
	{
		return losses;
	}
	
	public void setLosses(int losses)
	{
		this.losses = losses;
	}

	public void addLoss() 
	{
		this.losses++;
	}
	
	public int getCurrency()
	{
		return currency;
	}
	
	public void setCurrency(int currency)
	{
		this.currency = currency;
	}
	
	public void addCurrency(int amount)
	{
		currency += amount;
	}
	
	public void setSelection(MGBound selection)
	{
		this.selection = selection;
	}
	
	public MGBound getSelection()
	{
		return selection;
	}
	
	public void storeLastShotTime()
	{
		lastShot = System.currentTimeMillis();
	}
	
	public long getLastShotTime() 
	{
		return lastShot;
	}
	
	public void setFireRate(long rate)
	{
		this.fireRate = rate;
	}
	
	public long getFireRate()
	{
		return fireRate;
	}
	
	public void setReloading(boolean rld)
	{
		if(rld)
		{
			storeReloadTime();
			isReloading = true;
		}
		else
			isReloading = false;
	}
	
	public boolean isReloading()
	{
		return isReloading;
	}
	
	public void storeReloadTime()
	{
		lastReload = System.currentTimeMillis();
	}
	
	public long getLastReload()
	{
		return lastReload;
	}
	
	public void setShotsFired(int shots)
	{
		shotsFired = shots;
	}
	
	public int getShotsFired()
	{
		return shotsFired;
	}

	public Inventory getInventory()
	{
		return playerInv;
	}

	public void setInventory(Inventory inventory)
	{
		if(inventory != null)
			playerInv = inventory;
	}
	
	public void resetBow() 
	{
		setShotsFired(0);
		setReloading(false);
		lastShot = 0;
		lastReload = 0;
	}
}