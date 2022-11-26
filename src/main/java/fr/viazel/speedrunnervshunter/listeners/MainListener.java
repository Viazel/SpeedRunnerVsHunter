package fr.viazel.speedrunnervshunter.listeners;

import fr.viazel.speedrunnervshunter.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainListener implements Listener {

    private static Inventory inv;

    private static Map<Player, Player> compassTracker;
    private static boolean hunterCanMoove;

    private static Player p;

    private static Player target;

    public MainListener() {
        inv = Bukkit.createInventory(null, 27, "§eChoose your runner !");
        hunterCanMoove = true;
        compassTracker = new HashMap<>();
    }

    public static Inventory getInv() {
        return inv;
    }

    public static Map<Player, Player> getCompassTracker() {
        return compassTracker;
    }

    public static boolean isHunterCanMoove() {
        return hunterCanMoove;
    }

    public static void setHunterCanMoove(boolean hunterCanMoove) {
        MainListener.hunterCanMoove = hunterCanMoove;
    }

    public static Player getP() {
        return p;
    }

    public static void setP(Player p) {
        MainListener.p = p;
    }

    public static Player getTarget() {
        return target;
    }

    public static void setTarget(Player target) {
        MainListener.target = target;
    }

    @EventHandler
    public void eventEnd(EntityDamageEvent e){

        if(!(e.getEntity() instanceof EnderDragon)) return;

        EnderDragon enderDragon = (EnderDragon) e.getEntity();

        if(enderDragon.getHealth() > e.getDamage()) return;

        GameManager.changeGameManager(GameManagerEnum.ENDSPEEDRUNER);

    }

}