package fr.viazel.speedrunnervshunter;

import fr.viazel.speedrunnervshunter.commands.EndCommand;
import fr.viazel.speedrunnervshunter.commands.SetDefaultSpawnCommand;
import fr.viazel.speedrunnervshunter.commands.StartCommand;
import fr.viazel.speedrunnervshunter.listeners.MainListener;
import fr.viazel.speedrunnervshunter.utils.ConfigFile;
import fr.viazel.speedrunnervshunter.utils.GameManager;
import fr.viazel.speedrunnervshunter.utils.PlayerRunner;
import fr.viazel.speedrunnervshunter.utils.SpeedRunnerLogger;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main extends JavaPlugin {

    private static Main INSTANCE;

    public ArrayList<PlayerRunner> speedrunners;

    private GameManager gameManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        speedrunners = new ArrayList<>();
        INSTANCE = this;
        this.gameManager = GameManager.START;
        getServer().getLogger().info("Plugin activated !");
        getServer().getPluginManager().registerEvents(new MainListener(), this);
        getCommand("start").setExecutor(new StartCommand());
        getCommand("end").setExecutor(new EndCommand());
        getCommand("setspawn").setExecutor(new SetDefaultSpawnCommand());
    }

    @Override
    public void onDisable() {

        if(gameManager.equals(GameManager.GAME)){
            endGame();
        }

    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public void changeGameManager(GameManager gameManager) {

        switch (gameManager) {
            case GAME:
                this.gameManager = GameManager.GAME;
                SpeedRunnerLogger.broadcastMessage("La partie commence !");
                launchGame();
                break;
            case START:
                this.gameManager = GameManager.START;
                SpeedRunnerLogger.broadcastMessage("En attente de joueur...");
                break;
            case ENDSPEEDRUNER:
                this.gameManager = GameManager.ENDSPEEDRUNER;
                SpeedRunnerLogger.broadcastMessage("La partie est finie ! §aLes speedrunners §font gagnés !");
                endGame();
                break;
            case ENDHUNTER:
                this.gameManager = GameManager.ENDHUNTER;
                SpeedRunnerLogger.broadcastMessage("La partie est finie ! §cLes Hunters §font gagnés !");
                endGame();
                break;
        }

    }

    private void launchGame() {

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                Bukkit.getWorld("world").getBlockAt(i, 80, j).setType(Material.GLASS);
            }
        }

        ConfigFile configFile = new ConfigFile();

        Bukkit.getOnlinePlayers().stream().forEach(player -> {
            if(!containsInAnArrayList(speedrunners, player)) {
                player.setPlayerListName("§cHunter " + player.getName());
                player.teleport(new Location(Bukkit.getWorld("world"), 2, 81, 2, 0.0f, 0.0f));
            }else {
                player.teleport(configFile.getDefaultSpawnLocation());
                player.setPlayerListName("§aSpeedRunner " + player.getName());
                SpeedRunnerLogger.sendMessage(player, "§aVous êtes SpeedRunner !");
            }
        });

        Bukkit.getOnlinePlayers().forEach(player -> {
            player.setGameMode(GameMode.SURVIVAL);
            player.getInventory().clear();
        });

        MainListener.setHunterNotMoove();
    }

    private void endGame() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.setPlayerListName(player.getName());
            player.setGameMode(GameMode.CREATIVE);
            player.setCompassTarget(new Location(Bukkit.getWorld("world"), 0, 0, 0));
            player.getInventory().clear();
            player.teleport(new Location(Bukkit.getWorld("world"), 0 , 80, 0));
        });
        speedrunners.clear();
        changeGameManager(GameManager.START);
    }

    public static boolean containsInAnArrayList(ArrayList<PlayerRunner> list, Player thing) {

        AtomicBoolean can = new AtomicBoolean(false);

        list.stream().forEach(anything -> {
            if(anything.getPlayer().getUniqueId().equals(thing.getUniqueId())) can.set(true);
        });

        return can.get();
    }

    public static Main getInstance() { return INSTANCE; }
}