package fr.viazel.speedrunnervshunter.listeners;

import fr.viazel.speedrunnervshunter.utils.GameManager;
import fr.viazel.speedrunnervshunter.utils.GameManagerEnum;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitJoinListener implements Listener {

    private final GameManagerEnum gameManager;

    public PlayerQuitJoinListener() {
        gameManager = GameManager.getGameManager();
    }

    @EventHandler
    public void event(PlayerQuitEvent e) {

        if(gameManager != GameManagerEnum.START) {
            e.setQuitMessage(null);
        }

    }

    @EventHandler
    public void event(PlayerJoinEvent e) {

        MainListener.setP(e.getPlayer());

        MainListener.getP().getInventory().clear();

        if(!MainListener.getP().isOp()) MainListener.getP().setGameMode(GameMode.ADVENTURE);

        if(gameManager != GameManagerEnum.START) {
            MainListener.getP().kickPlayer("§cLa partie est déjà lancée !\n\n§bNous sommes désolée...\n§fSpeedRunner VS Hunter | JeromeGame");
            return;
        }

        if(Bukkit.getOnlinePlayers().size() < 4) {
            e.setJoinMessage("§eBienvenue à §b" + MainListener.getP().getName() + " §e! §c(§e" + Bukkit.getOnlinePlayers().size() + "§c/4)");
        }else {
            e.setJoinMessage("§eBienvenue à §b" + MainListener.getP().getName() + " §e! §a(§e" + Bukkit.getOnlinePlayers().size() + "§a/4)");
        }

    }

}
