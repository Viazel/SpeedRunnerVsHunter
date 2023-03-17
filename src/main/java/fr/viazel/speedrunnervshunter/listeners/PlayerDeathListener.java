package fr.viazel.speedrunnervshunter.listeners;

import fr.viazel.speedrunnervshunter.utils.ConfigFile;
import fr.viazel.speedrunnervshunter.utils.GameManager;
import fr.viazel.speedrunnervshunter.utils.GameManagerEnum;
import fr.viazel.speedrunnervshunter.utils.SpeedRunnerLogger;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerDeathListener implements Listener {

    @EventHandler
    public void event(EntityDamageEvent e){

        if(GameManager.getGameManager() != GameManagerEnum.GAME) return;

        if(!(e.getEntity() instanceof Player)) return;

        MainListener.setP((Player) e.getEntity());

        if(MainListener.getP().getHealth() > e.getDamage()) return;

        e.setCancelled(true);
        MainListener.getP().setHealth(20);

        if(!GameManager.getSpeedrunners().contains(MainListener.getP())) {
            MainListener.getP().teleport(new ConfigFile().getDefaultSpawnLocation());
            SpeedRunnerLogger.broadcastMessage("§eLe joueur §b" + MainListener.getP().getName() + " §eest mort !");
            return;
        };

        GameManager.getSpeedrunners().remove(MainListener.getP());

        if(GameManager.getSpeedrunners().isEmpty()) {
            GameManager.changeGameManager(GameManagerEnum.ENDHUNTER);
            return;
        }

        MainListener.getP().setGameMode(GameMode.SPECTATOR);
        MainListener.getP().teleport(GameManager.getSpeedrunners().stream().findFirst().get());
        SpeedRunnerLogger.broadcastMessage("§eLe speedrunner §b" + MainListener.getP().getName() + " §eest mort ! (§cil reste " + GameManager.getSpeedrunners().size() + " speedrunner(s))");
    }

}