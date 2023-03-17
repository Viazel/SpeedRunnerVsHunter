package fr.viazel.speedrunnervshunter.utils;

import fr.viazel.speedrunnervshunter.Main;
import fr.viazel.speedrunnervshunter.listeners.MainListener;
import fr.viazel.speedrunnervshunter.listeners.MoveListener;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class GameManager {

    private static GameManagerEnum gameManager;

    private static ArrayList<Player> speedrunners;

    private static ItemStack[] kitRunner;

    public GameManager() {

        kitRunner = new ItemStack[40];
        kitRunner[0] = new ItemStack(Material.DIAMOND_SWORD);
        kitRunner[39] = new ItemStack(Material.IRON_HELMET);
        kitRunner[38] = new ItemStack(Material.IRON_CHESTPLATE);
        kitRunner[37] = new ItemStack(Material.IRON_LEGGINGS);
        kitRunner[36] = new ItemStack(Material.IRON_BOOTS);

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

        ConfigFile configFile = new ConfigFile();

        Location la = configFile.getDefaultSpawnLocation();


        Bukkit.getOnlinePlayers().forEach(player -> {
            player.setGameMode(GameMode.SURVIVAL);
            player.setHealth(20);
            player.setFoodLevel(20);
            player.getInventory().clear();
        });

        Bukkit.getOnlinePlayers().stream().forEach(player -> {
            if(!speedrunners.contains(player)) {
                player.setPlayerListName("§cHunter " + player.getName());
                player.teleport(configFile.getDefaultSpawnLocation());
            }else {
                player.teleport(configFile.getDefaultSpawnLocation());
                player.getInventory().setContents(kitRunner);
                player.updateInventory();
                player.setPlayerListName("§aSpeedRunner " + player.getName());
                SpeedRunnerLogger.sendMessage(player, "§aVous êtes SpeedRunner !");
            }
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

    private static void setGameManager(GameManagerEnum gameManagerEnum) {
        gameManager = gameManagerEnum;
    }

    public static void changeGameManager(GameManagerEnum gameManager) {

        setGameManager(gameManager);

        switch (gameManager) {
            case GAME:
                SpeedRunnerLogger.sendTitle("La partie commence !");
                launchGame();
                break;
            case START:
                SpeedRunnerLogger.broadcastMessage("En attente de joueur...");
                break;
            case ENDSPEEDRUNER:
                SpeedRunnerLogger.sendTitle("La partie est finie ! §aLes speedrunners §font gagnés !");
                endGame();
                break;
            case ENDHUNTER:
                SpeedRunnerLogger.sendTitle("La partie est finie ! §cLes Hunters §font gagnés !");
                endGame();
                break;
        }

    }

}