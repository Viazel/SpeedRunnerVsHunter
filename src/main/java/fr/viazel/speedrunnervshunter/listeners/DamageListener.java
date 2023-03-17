package fr.viazel.speedrunnervshunter.listeners;

import fr.viazel.speedrunnervshunter.utils.ConfigFile;
import fr.viazel.speedrunnervshunter.utils.GameManager;
import fr.viazel.speedrunnervshunter.utils.GameManagerEnum;
import fr.viazel.speedrunnervshunter.utils.SpeedRunnerLogger;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DamageListener implements Listener {

    @EventHandler
    public void event(EntityDamageByEntityEvent e) {
        if(!(e.getEntity() instanceof Player)) return;
        if(!(e.getDamager() instanceof Player)) return;

        if(!GameManager.getGameManager().equals(GameManagerEnum.GAME)) return;

        MainListener.setP((Player) e.getDamager());
        MainListener.setTarget((Player) e.getEntity());

        if(GameManager.getSpeedrunners().contains(MainListener.getP()) && GameManager.getSpeedrunners().contains(MainListener.getTarget())) {
            SpeedRunnerLogger.sendMessage(MainListener.getP(), "§cVous ne pouvez pas frapper votre coéquipier !");
            e.setCancelled(true);
            return;
        }

        if(!GameManager.getSpeedrunners().contains(MainListener.getP()) && !GameManager.getSpeedrunners().contains(MainListener.getTarget())) {
            SpeedRunnerLogger.sendMessage(MainListener.getP(), "§cVous ne pouvez pas frapper votre coéquipier !");
            e.setCancelled(true);
        }

    }

}
