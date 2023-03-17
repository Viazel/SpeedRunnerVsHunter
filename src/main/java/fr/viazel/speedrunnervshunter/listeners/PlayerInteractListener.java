package fr.viazel.speedrunnervshunter.listeners;

import fr.viazel.speedrunnervshunter.Main;
import fr.viazel.speedrunnervshunter.utils.GameManager;
import fr.viazel.speedrunnervshunter.utils.GameManagerEnum;
import fr.viazel.speedrunnervshunter.utils.SpeedRunnerLogger;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class PlayerInteractListener implements Listener {

    @EventHandler
    public void event(PlayerInteractEvent e){
        if(GameManager.getGameManager() != GameManagerEnum.GAME) return;

        if(GameManager.getSpeedrunners().contains(e.getPlayer())) return;

        if(!e.getAction().equals(Action.RIGHT_CLICK_AIR) && !e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

        if(e.getPlayer().isSneaking()) return;

        if(e.getItem() == null) return;

        if(!e.getItem().getType().equals(Material.COMPASS)) return;

        MainListener.setP(e.getPlayer());

        int max = GameManager.getSpeedrunners().size() - 1;
        int min = 0;
        int range = max - min + 1;
        int r = (int) ((Math.random() * range) + min);

        if(MainListener.getCompassTracker().get(MainListener.getP()) == null) {
            SpeedRunnerLogger.sendMessage(MainListener.getP(), "Vous devez chosir une cible en sneak + click droit !");
            return;
        }

        MainListener.getP().setCompassTarget(MainListener.getCompassTracker().get(MainListener.getP()).getLocation());

        ItemStack itemStack = new ItemStack(Material.COMPASS);

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("§l§e» §l§e" + MainListener.getCompassTracker().get(MainListener.getP()).getName());
        itemStack.setItemMeta(itemMeta);

        MainListener.getP().getInventory().setItemInMainHand(itemStack);

    }

    @EventHandler
    public void eventSneak(PlayerInteractEvent e){
        if(GameManager.getGameManager() != GameManagerEnum.GAME) return;

        if(GameManager.getSpeedrunners().contains(e.getPlayer())) return;

        if(!e.getAction().equals(Action.RIGHT_CLICK_AIR) && !e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

        if(!e.getPlayer().isSneaking()) return;

        if(e.getItem() == null) return;

        if(!e.getItem().getType().equals(Material.COMPASS)) return;

        MainListener.setP(e.getPlayer());

        ItemStack[] items = new ItemStack[27];

        AtomicInteger i = new AtomicInteger();

        GameManager.getSpeedrunners().forEach(player ->  {
            items[i.get()] = getHead(player);
            i.set(i.get() + 1);
        });

        MainListener.getInv().setContents(items);

        MainListener.getP().openInventory(MainListener.getInv());

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