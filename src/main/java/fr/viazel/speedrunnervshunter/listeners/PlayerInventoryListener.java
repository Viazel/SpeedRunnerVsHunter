package fr.viazel.speedrunnervshunter.listeners;

import fr.viazel.speedrunnervshunter.utils.SpeedRunnerLogger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class PlayerInventoryListener implements Listener {

    @EventHandler
    public void event(InventoryClickEvent e){

        if(!(e.getWhoClicked() instanceof Player)) return;

        if(e.getCurrentItem() == null) return;

        MainListener.setP((Player) e.getWhoClicked());

        if(!e.getInventory().equals(MainListener.getInv())) return;

        e.setCancelled(true);

        MainListener.setTarget(Bukkit.getPlayer(e.getCurrentItem().getItemMeta().getDisplayName()));

        MainListener.getCompassTracker().put(MainListener.getP(), MainListener.getTarget());

        MainListener.getP().closeInventory();

        SpeedRunnerLogger.sendMessage(MainListener.getP(), "Vous avez choisi §b" + MainListener.getTarget().getName() + " §f!");

    }

}
