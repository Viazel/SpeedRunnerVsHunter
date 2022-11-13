package fr.viazel.speedrunnervshunter.listeners;

import fr.viazel.speedrunnervshunter.Main;
import fr.viazel.speedrunnervshunter.utils.GameManager;
import fr.viazel.speedrunnervshunter.utils.PlayerRunner;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

public class MainListener implements Listener {

    @EventHandler
    public void event(PlayerQuitEvent e) {

        if(Main.getInstance().getGameManager() != GameManager.START) {
            e.setQuitMessage(null);
        }

    }

    @EventHandler
    public void event(PlayerDeathEvent e){

        if(Main.getInstance().getGameManager() != GameManager.GAME) return;

        Player player = e.getEntity();

        PlayerRunner p = new PlayerRunner(player);

        e.setDeathMessage("§eLe joueur §b" + p.getName() + " §eest mort !");

        PlayerRunner wait = Main.getInstance().speedrunners.stream().filter(runner -> runner.getPlayer().getUniqueId() == p.getPlayer().getUniqueId()).findFirst().get();

        if(wait == null) return;

        Main.getInstance().speedrunners.remove(wait);

        if(Main.getInstance().speedrunners.isEmpty()) {
            Main.getInstance().changeGameManager(GameManager.ENDHUNTER);
            return;
        }

        p.getPlayer().spigot().respawn();
        p.getPlayer().setGameMode(GameMode.SPECTATOR);
        p.getPlayer().teleport(Main.getInstance().speedrunners.stream().findFirst().get().getPlayer());

    }

    @EventHandler
    public void event(PlayerInteractEvent e){
        if(Main.getInstance().getGameManager() != GameManager.GAME) return;

        if(!e.getAction().equals(Action.RIGHT_CLICK_AIR) && !e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

        if(e.getItem() == null) return;

        if(!e.getItem().getType().equals(Material.COMPASS)) return;

        Player p = e.getPlayer();

        p.getPlayer().setCompassTarget(Bukkit.getPlayer("Layttos").getLocation());

        p.sendMessage("Updated !");

    }

    @EventHandler
    public void event(SmithItemEvent e) {

        if(!(e.getWhoClicked() instanceof Player)) return;

        ItemStack item = e.getCurrentItem();

        if(Main.getInstance().getGameManager() != GameManager.GAME) return;

        PlayerRunner p = new PlayerRunner((Player) e.getWhoClicked());

        PlayerRunner wait = Main.getInstance().speedrunners.stream().filter(runner -> runner.getPlayer().getUniqueId() == p.getPlayer().getUniqueId()).findFirst().get();

        if(wait == null) return;

        assert item != null;
        if(item.getType().equals(Material.NETHERITE_SWORD)) {

            Main.getInstance().speedrunners.forEach(player -> {
                player.getPlayer().sendMessage("Vous avez fini !");
            });

            e.setCancelled(true);
            e.setCurrentItem(null);
            e.setResult(null);

            Main.getInstance().changeGameManager(GameManager.ENDSPEEDRUNER);

        }

    }

    @EventHandler
    public void event(PlayerJoinEvent e) {

        PlayerRunner p = new PlayerRunner(e.getPlayer());

        p.getPlayer().getInventory().clear();

        if(!p.getPlayer().isOp()) p.getPlayer().setGameMode(GameMode.ADVENTURE);

        if(Main.getInstance().getGameManager() != GameManager.START) {
            p.getPlayer().kickPlayer("§cLa partie est déjà lancée !\n\n§bNous sommes désolée...\n§fSpeedRunner VS Hunter | JeromeGame");
            return;
        }

        if(Bukkit.getOnlinePlayers().size() < 4) {
            e.setJoinMessage("§eBienvenue à §b" + p.getName() + " §e! §c(§e" + Bukkit.getOnlinePlayers().size() + "§c/4)");
        }else {
            e.setJoinMessage("§eBienvenue à §b" + p.getName() + " §e! §a(§e" + Bukkit.getOnlinePlayers().size() + "§a/4)");
        }

    }
}