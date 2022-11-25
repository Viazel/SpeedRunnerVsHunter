package fr.viazel.speedrunnervshunter.listeners;

import fr.viazel.speedrunnervshunter.Main;
import fr.viazel.speedrunnervshunter.utils.ConfigFile;
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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MainListener implements Listener {

    private Inventory inv;

    private Map<Player, Player> compassTracker;
    private static boolean hunterCanMoove;

    public MainListener() {
        inv = Bukkit.createInventory(null, 27, "§eChoose your runner !");
        hunterCanMoove = true;
        compassTracker = new HashMap<>();
    }

    public static void setHunterNotMoove() {
        hunterCanMoove = false;
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                if(Main.getInstance().getGameManager().equals(GameManager.GAME)) {
                    hunterCanMoove = true;
                    Bukkit.getOnlinePlayers().stream().filter(player -> !Main.getInstance().speedrunners.contains(player)).forEach(player -> {
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

        if(!Main.getInstance().getGameManager().equals(GameManager.GAME)) return;

        Player p = (Player) e.getDamager();
        Player target = (Player) e.getEntity();

        if(Main.getInstance().speedrunners.contains(p) && Main.getInstance().speedrunners.contains(target)) {
            SpeedRunnerLogger.sendMessage(p, "§cVous ne pouvez pas frapper votre coéquipier !");
            e.setCancelled(true);
            return;
        }

        if(!Main.getInstance().speedrunners.contains(p) && !Main.getInstance().speedrunners.contains(target)) {
            SpeedRunnerLogger.sendMessage(p, "§cVous ne pouvez pas frapper votre coéquipier !");
            e.setCancelled(true);
        }

    }

    @EventHandler
    public void event(PlayerMoveEvent e){
        if(!Main.getInstance().speedrunners.contains(e.getPlayer())) {
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

        if(!Main.getInstance().speedrunners.contains(player)) {
            player.spigot().respawn();
            player.teleport(new ConfigFile().getDefaultSpawnLocation());
            return;
        };

        Main.getInstance().speedrunners.remove(p.getPlayer());

        if(Main.getInstance().speedrunners.isEmpty()) {
            Main.getInstance().changeGameManager(GameManager.ENDHUNTER);
            return;
        }

        p.getPlayer().spigot().respawn();
        p.getPlayer().setGameMode(GameMode.SPECTATOR);
        p.getPlayer().teleport(Main.getInstance().speedrunners.stream().findFirst().get());

    }

    @EventHandler
    public void event(PlayerInteractEvent e){
        if(Main.getInstance().getGameManager() != GameManager.GAME) return;

        if(Main.getInstance().speedrunners.contains(e.getPlayer())) return;

        if(!e.getAction().equals(Action.RIGHT_CLICK_AIR) && !e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

        if(e.getPlayer().isSneaking()) return;

        if(e.getItem() == null) return;

        if(!e.getItem().getType().equals(Material.COMPASS)) return;

        Player p = e.getPlayer();

        int max = Main.getInstance().speedrunners.size() - 1;
        int min = 0;
        int range = max - min + 1;
        int r = (int) ((Math.random() * range) + min);

        if(compassTracker.get(p) == null) {
            SpeedRunnerLogger.sendMessage(p, "Vous devez chosir une cible en sneak + click gauche !");
            return;
        }

        p.setCompassTarget(compassTracker.get(p).getLocation());

        SpeedRunnerLogger.sendMessage(p, "La boussole a été mise à jour sur §b" + compassTracker.get(p).getName() + " §f!");

    }

    @EventHandler
    public void eventSneak(PlayerInteractEvent e){
        if(Main.getInstance().getGameManager() != GameManager.GAME) return;

        if(Main.getInstance().speedrunners.contains(e.getPlayer())) return;

        if(!e.getAction().equals(Action.RIGHT_CLICK_AIR) && !e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

        if(!e.getPlayer().isSneaking()) return;

        if(e.getItem() == null) return;

        if(!e.getItem().getType().equals(Material.COMPASS)) return;

        Player p = e.getPlayer();

        ItemStack[] items = new ItemStack[27];

        AtomicInteger i = new AtomicInteger();

        Main.getInstance().speedrunners.forEach(playerRunner ->  {
            items[i.get()] = getHead(playerRunner.getPlayer());
            i.set(i.get() + 1);
        });

        inv.setContents(items);

        p.openInventory(inv);

    }

    @EventHandler
    public void event(SmithItemEvent e) {

        if(!(e.getWhoClicked() instanceof Player)) return;

        if(Main.getInstance().getGameManager() != GameManager.GAME) return;

        if(!Main.getInstance().speedrunners.contains((Player) e.getWhoClicked())) return;

        ItemStack item = e.getCurrentItem();

        PlayerRunner p = new PlayerRunner((Player) e.getWhoClicked());

        if(!Main.getInstance().speedrunners.contains(p.getPlayer())) return;

        assert item != null;
        if(item.getType().equals(Material.NETHERITE_SWORD)) {

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

    @EventHandler
    public void event(InventoryClickEvent e){

        if(!(e.getWhoClicked() instanceof Player)) return;

        if(e.getCurrentItem() == null) return;

        Player p = (Player) e.getWhoClicked();

        if(!e.getInventory().equals(inv)) return;

        e.setCancelled(true);

        Player target = Bukkit.getPlayer(e.getCurrentItem().getItemMeta().getDisplayName());

        compassTracker.put(p, target);

        p.closeInventory();

        SpeedRunnerLogger.sendMessage(p, "Vous avez choisi §b" + target.getName() + " §f!");

    }

    public static ItemStack getHead(Player player) {
        int lifePlayer = (int) player.getHealth();
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);
        SkullMeta skull = (SkullMeta) item.getItemMeta();
        skull.setDisplayName(player.getName());
        ArrayList<String> lore = new ArrayList<String>();
        lore.add("Custom head");
        skull.setLore(lore);
        skull.setOwner(player.getName());
        item.setItemMeta(skull);
        return item;
    }
}