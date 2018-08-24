package Tossed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.gmail.andrew.miner3.plugins.InventoryMenu.MenuAnimation;
import com.gmail.andrew.miner3.plugins.InventoryMenu.MenuItem;
import com.gmail.andrew.miner3.plugins.InventoryMenu.MenuTools;

//import org.bukkit.Bukkit;
//import org.bukkit.ChatColor;
//import org.bukkit.entity.Player;
//import org.bukkit.plugin.java.JavaPlugin;
//import org.bukkit.scheduler.BukkitTask;
//import org.bukkit.scoreboard.DisplaySlot;
//import org.bukkit.scoreboard.Objective;
//import org.bukkit.scoreboard.Scoreboard;
//import org.bukkit.scoreboard.ScoreboardManager;
//
//import com.gmail.andrew.miner3.plugins.Paintball.PBPlayer;
//import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.Lobby;
//import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.LobbyStates.LobbyState;




public class temp {}
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Map;
//import java.util.UUID;
//
//import org.bukkit.Bukkit;
//import org.bukkit.ChatColor;
//import org.bukkit.entity.Player;
//import org.bukkit.scheduler.BukkitTask;
//import org.bukkit.scoreboard.DisplaySlot;
//import org.bukkit.scoreboard.Objective;
//import org.bukkit.scoreboard.Score;
//import org.bukkit.scoreboard.Scoreboard;
//import org.bukkit.scoreboard.ScoreboardManager;
//
//import com.gmail.andrew.miner3.plugins.Paintball.PBPlayer;
//import com.gmail.andrew.miner3.plugins.Paintball.PluginManager;
//import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.Lobby;
//
//public class PBScoreboard 
//{
//	private final Lobby lobby;
//	private final PluginManager plugin;
//
//	private ScoreboardManager manager;
//	
//	private InGameScoreboard ingame;
//	private PregameScoreboard pregame;
//	
//	//private HashMap<UUID, String> teamListNames;
//	//private HashMap<Integer, Integer> teamScoreLimits;
//
//
//	public PBScoreboard(PluginManager plugin, Lobby lobby, int inGameTimer, int pregameTimer)
//	{
//		this.plugin = plugin;
//		this.lobby = lobby;
//
//		manager = Bukkit.getScoreboardManager();
//		ingame = new InGameScoreboard(plugin, lobby, inGameTimer);
//		pregame = new PregameScoreboard(plugin, lobby, pregameTimer);
//
//		if(lobby.inGame())
//			setInGame();
//		else
//			setPregame();
//
////		inGameSideBar = inGameBoard.registerNewObjective("info" + lobby.getLobbyId(), "dummy");
////		inGameSideBar.setDisplayName(parseTime(this.inGameTimer, true));
////		inGameSideBar.getScore(ChatColor.BOLD + "------------").setScore(5); // large name plus
////		inGameSideBar.getScore(ChatColor.BLUE + "Blue: " + ((char)176) + ((char)176) + ((char)176) + ((char)176) + ((char)176) + ((char)176)).setScore(4);
////		inGameSideBar.getScore(ChatColor.RED + "Red:  " + ((char)176) + ((char)176) + ((char)176) + ((char)176) + ((char)176) + ((char)176)).setScore(3);
////		inGameSideBar.setDisplaySlot(DisplaySlot.SIDEBAR);
//		
////		teamListNames = new HashMap<UUID, String>();
////		teamScoreLimits = new HashMap<Integer, Integer>();
////		teamList = null;
//		
//		
////		tabList = pregameBoard.registerNewObjective("tabList" + lobby.getLobbyId(), "dummy");
////		tabList.setDisplayName(lobbyStr + ChatColor.GRAY + "  0/" + lobby.getMaxPlayerCount());
////		tabList.setDisplaySlot(DisplaySlot.SIDEBAR);
//	}
//
//	public void close()
//	{
//		for(PBPlayer player : this.lobby.getPlayers())
//		{
//			Player p = Bukkit.getPlayer(player.getID());
//			
//			if(p != null)
//				p.setScoreboard(manager.getNewScoreboard());
//		}
//	}
//
//	public void open()
//	{
//		for(PBPlayer player : this.lobby.getPlayers())
//		{
//			Player p = Bukkit.getPlayer(player.getID());
//			
//			if(p != null)
//			{
//				if(lobby.inGame())
//					ingame.setScoreboard(p);
//				else
//					pregame.setScoreboard(p);
//			}
//		}
//	}
//	
//	public void setPregame()
//	{
//		pregame.resetTimer();
//		ingame.resetTimer();
//		
//		if(!pregame.updatePlayerCount())
//			pregame.startLobbyTimer();
//		
//		pregame.refreshPlayerList();
//		
//		open();
//	}
//	
//	public void setInGame()
//	{
//		pregame.resetTimer();
//		ingame.resetTimer();
//		open();
//	}
//	
//	public void startRoundTimer()
//	{
//		if(lobby.inGame())
//			ingame.startRoundTimer();
//	}
//	
//	public void removePlayer(UUID id)
//	{
//		pregame.removePlayer(Bukkit.getPlayer(id));
//	}
//	
//	public void addedPlayer(UUID id)
//	{
//		Player p = Bukkit.getPlayer(id);
//		
//		if(!lobby.inGame())
//		{
//			pregame.setScoreboard(p);
//			pregame.addPlayer(p);
//		}
//		else
//		{
//			ingame.setScoreboard(Bukkit.getPlayer(id));
//		}
//	}
//	
//	public void reset()
//	{
//		pregame.resetTimer();
//		ingame.resetTimer();
//	}
//	
//	public static String parseTime(int time, boolean addColor)
//	{
//		int minutes = time / 60;
//		int seconds = time % 60;
//
//		if(minutes > 59)
//			minutes = 59;
//
//		String rtn = "";
//
//		if(minutes < 10)
//			rtn += "0";
//
//		rtn += minutes + ":";
//
//		if(seconds < 10)
//			rtn += "0";
//		rtn += seconds;
//
//		if(!addColor)
//			return rtn;
//		
//		if(minutes == 0 && seconds <= 30)
//			return ChatColor.RED + rtn;
//
//		return ChatColor.GRAY + rtn;
//	}
//	
////	private void buildTeamList()
////	{
////		if(!lobby.inGame())
////			return;
////		
////		if(teamList != null)
////			teamList.unregister();
////		
////		teamListNames.clear();
////		teamScoreLimits.clear();
////		
////		teamList = inGameBoard.registerNewObjective("teamList" + lobby.lobbyId, "dummy");
////		teamList.setDisplayName("Paintball Lobby #" + lobby.lobbyId + ChatColor.RED + " In-Progress");
////		teamList.setDisplaySlot(DisplaySlot.SIDEBAR);
////		
////		HashMap<Integer, ArrayList<PBPlayer>> teams = lobby.getTeams();
////		
////		Iterator<Map.Entry<Integer, ArrayList<PBPlayer>>> it = teams.entrySet().iterator();
////
////		int scoreCount = 0;
////		
////		while(it.hasNext())
////		{
////			Map.Entry<Integer, ArrayList<PBPlayer>> pair = (Map.Entry<Integer, ArrayList<PBPlayer>>) it.next();
////			Iterator<PBPlayer> it2 = pair.getValue().iterator();
////			
////			teamScoreLimits.put(pair.getKey(), scoreCount);
////			
////			String teamColor = lobby.curMap.getColor(pair.getKey());
////			String teamName = lobby.curMap.getPrefix(pair.getKey());
////			
////			ArrayList<String> names = new ArrayList<String>();
////			
////			while(it2.hasNext())
////			{
////				PBPlayer p = it2.next();
////				String str = teamColor + Bukkit.getPlayer(p.getID()) + ChatColor.WHITE + ": " + scoreToString(p.getScore());
////				names.add(str);
////				teamListNames.put(p.getID(), str);
////			}
////			
////			setScores(names, scoreCount);
////			
////			teamList.getScore(teamColor + teamName + " Team").setScore(scoreCount);
////			scoreCount++;
////		}
////	}
////	
////	public void updateScore(PBPlayer player)
////	{
////		if(!lobby.inGame())
////			return;
////		
////		String str = teamListNames.get(player.getID());
////		String scoreName = str;
////		scoreName = scoreName.substring(0, scoreName.length() - 6);
////		scoreName += scoreToString(player.getScore());
////		
////		int score = teamList.getScore(str).getScore();
////		inGameBoard.resetScores(str);
////		teamList.getScore(scoreName).setScore(score);
////		
////		ArrayList<String> names = new ArrayList<String>();
////		int team = lobby.getTeam(player.getID());
////		
////		for(PBPlayer p : lobby.getTeams().get(team))
////		{
////			names.add(teamListNames.get(p.getID()));
////		}
////		
////		setScores(names, teamScoreLimits.get(team));
////	}
////	
////	private String scoreToString(int score) 
////	{
////		int spaces = 5 - (int) (Math.log(score));
////		
////		String rtn = "";
////		
////		for(int i = 0; i < spaces; i++)
////		{
////			rtn += " ";
////		}
////		
////		rtn += score;
////		return rtn;
////	}
////
////	private void setScores(ArrayList<String> listNames, int minScore)
////	{
////		String[] array = new String[listNames.size()];
////		int size = 0;
////		
////		for(String str : listNames)
////		{
////			insertSort(array, size, str);
////		}
////		
////		for(int i = 0; i < size; i++)
////		{
////			teamList.getScore(array[i]).setScore(i + minScore);
////		}
////		
////		minScore += size;
////	}
////	
////	private void insertSort(String[] array, int size, String nameScore)
////	{
////		for(int i = 0; i < size; i++)
////		{
////			if(parseScore(array[i]) > parseScore(nameScore))
////			{
////				insert(array, size, nameScore, i);
////				return;
////			}
////		}
////		
////		array[size] = nameScore;
////		size++;
////	}
////	
////	private int parseScore(String nameScore)
////	{
////		String score = nameScore.substring(nameScore.length() - 5, nameScore.length() - 1);
////		score = score.replaceAll("\\s", "");
////		return Integer.parseInt(score);
////	}
////	
////	private void insert(String[] array, int size, String nameScore, int pos)
////	{
////		String oldString = null;
////		String newString = nameScore;
////		
////		for(int i = pos; i < size; i++)
////		{
////			oldString = array[i];
////			array[i] = newString;
////			newString = oldString;
////		}
////		
////		array[size] = newString;
////		size++;
////	}
//}
