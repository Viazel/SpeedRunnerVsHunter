package fr.viazel.speedrunnervshunter.utils;

import fr.viazel.speedrunnervshunter.Main;
import fr.viazel.speedrunnervshunter.listeners.MainListener;
import fr.viazel.speedrunnervshunter.listeners.MoveListener;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class GameManager {

    private static GameManagerEnum gameManager;

    private static ArrayList<Player> speedrunners;

    public GameManager() {
        speedrunners = new ArrayList<>();
        gameManager = GameManagerEnum.START;
        speedrunners = new ArrayList<>();

        ItemStack item = new ItemStack(Material.COMPASS);
        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.setDisplayName("§l§e» §l§eMagical Compass");
        item.setItemMeta(itemMeta);

        ShapedRecipe expBottle = new ShapedRecipe(item);
        expBottle.shape(" O ", "ODO", " O ");
        expBottle.setIngredient('O', Material.DIAMOND);
        expBottle.setIngredient('D', Material.OBSIDIAN);

        Main.getInstance().getServer().addRecipe(expBottle);

    }

    public static ArrayList<Player> getSpeedrunners() { return speedrunners; }
    public static GameManagerEnum getGameManager() { return gameManager; }

    private static void launchGame() {

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                Bukkit.getWorld("world").getBlockAt(i, 80, j).setType(Material.GLASS);
            }
        }

        ConfigFile configFile = new ConfigFile();
        Location l = new Location(Bukkit.getWorld("world"), 2, 81, 2, 0.0f, 0.0f);

        Bukkit.getOnlinePlayers().stream().forEach(player -> {
            if(!speedrunners.contains(player)) {
                player.setPlayerListName("§cHunter " + player.getName());
                player.teleport(l);
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

        MoveListener.setHunterNotMove();
    }

    public static void endGame() {
        Bukkit.getScheduler().cancelTasks(Main.getInstance());
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.setPlayerListName(player.getName());
            player.setGameMode(GameMode.CREATIVE);
            player.setCompassTarget(new Location(Bukkit.getWorld("world"), 0, 0, 0));
            player.getInventory().clear();
            player.teleport(new Location(Bukkit.getWorld("world"), 0, 80, 0));
        });
        speedrunners.clear();
        changeGameManager(GameManagerEnum.START);
    }

    public static void changeGameManager(GameManagerEnum gameManager) {

        switch (gameManager) {
            case GAME:
                GameManager.gameManager = GameManagerEnum.GAME;
                SpeedRunnerLogger.sendTitle("La partie commence !");
                launchGame();
                break;
            case START:
                GameManager.gameManager = GameManagerEnum.START;
                SpeedRunnerLogger.broadcastMessage("En attente de joueur...");
                break;
            case ENDSPEEDRUNER:
                SpeedRunnerLogger.sendTitle("La partie est finie ! §aLes speedrunners §font gagnés !");
                endGame();
                break;
            case ENDHUNTER:
                GameManager.gameManager = GameManagerEnum.ENDHUNTER;
                SpeedRunnerLogger.sendTitle("La partie est finie ! §cLes Hunters §font gagnés !");
                endGame();
                break;
        }

    }

}