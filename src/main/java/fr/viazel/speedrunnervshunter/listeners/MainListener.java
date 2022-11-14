package fr.viazel.speedrunnervshunter.listeners;

import fr.viazel.speedrunnervshunter.Main;
import fr.viazel.speedrunnervshunter.utils.GameManager;
import fr.viazel.speedrunnervshunter.utils.PlayerRunner;
import fr.viazel.speedrunnervshunter.utils.SpeedRunnerLogger;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class MainListener implements Listener {

    private Map<Player, Integer> compassTracker;
    private static boolean hunterCanMoove;

    public MainListener() {
        hunterCanMoove = true;
        compassTracker = new HashMap<>();
        Bukkit.getOnlinePlayers().forEach(player -> {
            compassTracker.put(player, 0);
        });
    }

    public static void setHunterNotMoove() {
        hunterCanMoove = false;
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                if(Main.getInstance().getGameManager().equals(GameManager.GAME)) {
                    hunterCanMoove = true;
                    Bukkit.getOnlinePlayers().stream().filter(player -> !Main.containsInAnArrayList(Main.getInstance().speedrunners, player)).forEach(player -> {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 20 * 7, 1));
                    });
                    SpeedRunnerLogger.broadcastMessage("Les §cHunters §fsont lachés !");
                    for (int i = 0; i < 4; i++) {
                        for (int j = 0; j < 4; j++) {
                            Bukkit.getWorld("world").getBlockAt(i, 80, j).breakNaturally();
                        }
                    }
                }
            }

        }, (20 * 10));
    }

    @EventHandler
    public void event(EntityDamageByEntityEvent e) {
        if(!(e.getEntity() instanceof Player)) return;
        if(!(e.getDamager() instanceof Player)) return;

        Player p = (Player) e.getEntity();
        Player target = (Player) e.getDamager();

        if(Main.containsInAnArrayList(Main.getInstance().speedrunners, p) && Main.containsInAnArrayList(Main.getInstance().speedrunners, target)) {
            SpeedRunnerLogger.sendMessage(p, "§cVous ne pouvez pas frapper votre coéquipier !");
            e.setCancelled(true);
            return;
        }

        if(!Main.containsInAnArrayList(Main.getInstance().speedrunners, p) && !Main.containsInAnArrayList(Main.getInstance().speedrunners, target)) {
            SpeedRunnerLogger.sendMessage(p, "§cVous ne pouvez pas frapper votre coéquipier !");
            e.setCancelled(true);
        }

    }

    @EventHandler
    public void event(PlayerMoveEvent e){
        if(!Main.containsInAnArrayList(Main.getInstance().speedrunners, e.getPlayer())) {
            if(Main.getInstance().getGameManager().equals(GameManager.GAME)) {
                if(!hunterCanMoove) {
                    e.setCancelled(true);
                }
            }
        }
    }

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

        if(Main.containsInAnArrayList(Main.getInstance().speedrunners, e.getPlayer())) return;

        if(!e.getAction().equals(Action.RIGHT_CLICK_AIR) && !e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

        if(e.getItem() == null) return;

        if(!e.getItem().getType().equals(Material.COMPASS)) return;

        Player p = e.getPlayer();

        p.getPlayer().setCompassTarget(Bukkit.getPlayer("Layttos").getLocation());

        SpeedRunnerLogger.sendMessage(p, "§aLa position de la boussole a été mise à jour !");

    }

    @EventHandler
    public void event(SmithItemEvent e) {

        if(!(e.getWhoClicked() instanceof Player)) return;

        if(Main.getInstance().getGameManager() != GameManager.GAME) return;

        if(Main.containsInAnArrayList(Main.getInstance().speedrunners, (Player) e.getWhoClicked())) return;

        ItemStack item = e.getCurrentItem();

        PlayerRunner p = new PlayerRunner((Player) e.getWhoClicked());

        PlayerRunner wait = Main.getInstance().speedrunners.stream().filter(runner -> runner.getPlayer().getUniqueId() == p.getPlayer().getUniqueId()).findFirst().get();

        if(wait == null) return;

        assert item != null;
        if(item.getType().equals(Material.NETHERITE_SWORD)) {

            e.setCancelled(true);
            e.setCurrentItem(null);
            e.setResult(null);

            Main.getInstance().changeGameManager(GameManager.ENDSPEEDRUNER);

        }

    }

    @EventHandler
    public void event(CraftItemEvent e){

        if(!(e.getWhoClicked() instanceof Player)) return;

        if(!Main.getInstance().getGameManager().equals(GameManager.GAME)) return;

        if(e.getCurrentItem().getType().equals(Material.COMPASS)) {
            ItemStack item = new ItemStack(Material.COMPASS);
            ItemMeta itemMeta = item.getItemMeta();

            Player p = (Player) e.getWhoClicked();

            itemMeta.setDisplayName("§l§e» §l§e" + Main.getInstance().speedrunners.get(compassTracker.get(p)).getName());
            item.setItemMeta(itemMeta);

            e.setCurrentItem(item);
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