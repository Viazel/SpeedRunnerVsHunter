package fr.viazel.speedrunnervshunter.listeners;

import fr.viazel.speedrunnervshunter.utils.ConfigFile;
import fr.viazel.speedrunnervshunter.utils.GameManager;
import fr.viazel.speedrunnervshunter.utils.GameManagerEnum;
import fr.viazel.speedrunnervshunter.utils.SpeedRunnerLogger;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;

public class DamageListener implements Listener {

    private final ArrayList<Player> speedrunners;
    private final GameManagerEnum gameManager;

    public DamageListener() {
        speedrunners = GameManager.getSpeedrunners();
        gameManager = GameManager.getGameManager();
    }

    @EventHandler
    public void event(EntityDamageEvent e){

        if(gameManager != GameManagerEnum.GAME) return;

        if(!(e.getEntity() instanceof Player)) return;

        MainListener.setP((Player) e.getEntity());

        if(MainListener.getP().getHealth() > e.getDamage()) return;

        e.setCancelled(true);
        MainListener.getP().setHealth(20);

        if(!speedrunners.contains(MainListener.getP())) {
            MainListener.getP().teleport(new ConfigFile().getDefaultSpawnLocation());
            return;
        };

        speedrunners.remove(MainListener.getP());

        if(speedrunners.isEmpty()) {
            GameManager.changeGameManager(GameManagerEnum.ENDHUNTER);
            return;
        }

        MainListener.getP().setGameMode(GameMode.SPECTATOR);
        MainListener.getP().teleport(speedrunners.stream().findFirst().get());

    }

    @EventHandler
    public void event(EntityDamageByEntityEvent e) {
        if(!(e.getEntity() instanceof Player)) return;
        if(!(e.getDamager() instanceof Player)) return;

        if(!gameManager.equals(GameManagerEnum.GAME)) return;

        MainListener.setP((Player) e.getDamager());
        MainListener.setTarget((Player) e.getEntity());

        if(speedrunners.contains(MainListener.getP()) && speedrunners.contains(MainListener.getTarget())) {
            SpeedRunnerLogger.sendMessage(MainListener.getP(), "§cVous ne pouvez pas frapper votre coéquipier !");
            e.setCancelled(true);
            return;
        }

        if(!speedrunners.contains(MainListener.getP()) && !speedrunners.contains(MainListener.getTarget())) {
            SpeedRunnerLogger.sendMessage(MainListener.getP(), "§cVous ne pouvez pas frapper votre coéquipier !");
            e.setCancelled(true);
        }

    }

}
