package fr.viazel.speedrunnervshunter;

import fr.viazel.speedrunnervshunter.commands.EndCommand;
import fr.viazel.speedrunnervshunter.commands.SetDefaultSpawnCommand;
import fr.viazel.speedrunnervshunter.commands.StartCommand;
import fr.viazel.speedrunnervshunter.listeners.*;
import fr.viazel.speedrunnervshunter.utils.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.ArrayList;

public class Main extends JavaPlugin {

    private static Main INSTANCE;

    private GameManager gameManager;

    @Override
    public void onEnable() {

        INSTANCE = this;

        this.gameManager = new GameManager();

        saveDefaultConfig();
        getServer().getLogger().info("Plugin activated !");
        getServer().getPluginManager().registerEvents(new MainListener(), this);
        getServer().getPluginManager().registerEvents(new MoveListener(), this);
        getServer().getPluginManager().registerEvents(new DamageListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitJoinListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerInventoryListener(), this);
        getCommand("start").setExecutor(new StartCommand());
        getCommand("end").setExecutor(new EndCommand());
        getCommand("setspawn").setExecutor(new SetDefaultSpawnCommand());
    }

    @Override
    public void onDisable() {

        if(gameManager.equals(GameManagerEnum.GAME)){
            GameManager.endGame();
        }

    }

    public static Main getInstance() { return INSTANCE; }
}