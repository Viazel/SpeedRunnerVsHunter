package fr.viazel.jeromegame;

import fr.viazel.jeromegame.commands.EndCommand;
import fr.viazel.jeromegame.commands.StartCommand;
import fr.viazel.jeromegame.listeners.MainListener;
import fr.viazel.jeromegame.utils.GameManager;
import fr.viazel.jeromegame.utils.PlayerRunner;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class Main extends JavaPlugin {

    private static Main INSTANCE;

    public ArrayList<PlayerRunner> speedrunners;

    private GameManager gameManager;

    @Override
    public void onEnable() {
        speedrunners = new ArrayList<>();
        INSTANCE = this;
        this.gameManager = GameManager.START;
        getServer().getLogger().info("Plugin activated !");
        getServer().getPluginManager().registerEvents(new MainListener(), this);
        getCommand("start").setExecutor(new StartCommand());
        getCommand("end").setExecutor(new EndCommand());
    }

    @Override
    public void onDisable() {
        endHunterGame();
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public void changeGameManager(GameManager gameManager) {

        switch (gameManager) {
            case GAME:
                this.gameManager = GameManager.GAME;
                Bukkit.broadcastMessage("La partie commence !");
                launchGame();
                break;
            case START:
                this.gameManager = GameManager.START;
                Bukkit.broadcastMessage("En attente de joueur...");
                break;
            case ENDSPEEDRUNER:
                this.gameManager = GameManager.ENDSPEEDRUNER;
                Bukkit.broadcastMessage("La partie est finie ! Les speedrunners ont gagnés !");
                endSpeedRunnerGame();
                break;
            case ENDHUNTER:
                this.gameManager = GameManager.ENDHUNTER;
                Bukkit.broadcastMessage("La partie est finie ! Les Hunters ont gagnés !");
                endHunterGame();
                break;
        }

    }

    private void launchGame() {

        Bukkit.getOnlinePlayers().forEach(player -> {
            player.setGameMode(GameMode.SURVIVAL);
            player.getInventory().clear();
        });
    }

    private void endHunterGame() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.setGameMode(GameMode.CREATIVE);
            player.setCompassTarget(new Location(Bukkit.getWorld("world"), 0, 0, 0));
            player.getInventory().clear();
            player.teleport(new Location(Bukkit.getWorld("world"), 0 , 80, 0));
        });
        speedrunners.clear();
        changeGameManager(GameManager.START);
    }

    private void endSpeedRunnerGame() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.setGameMode(GameMode.CREATIVE);
            player.setCompassTarget(new Location(Bukkit.getWorld("world"), 0, 0, 0));
            player.getInventory().clear();
            player.teleport(new Location(Bukkit.getWorld("world"), 0 , 80, 0));
        });
        speedrunners.clear();
        changeGameManager(GameManager.START);
    }

    public static Main getInstance() { return INSTANCE; }
}