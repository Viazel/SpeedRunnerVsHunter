package fr.viazel.speedrunnervshunter.listeners;

import fr.viazel.speedrunnervshunter.Main;
import fr.viazel.speedrunnervshunter.utils.GameManager;
import fr.viazel.speedrunnervshunter.utils.GameManagerEnum;
import fr.viazel.speedrunnervshunter.utils.SpeedRunnerLogger;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MoveListener implements Listener {

    @EventHandler
    public void event(PlayerMoveEvent e){

        if(!GameManager.getSpeedrunners().contains(e.getPlayer())) {
            if(GameManager.getGameManager().equals(GameManagerEnum.GAME)) {
                if(!MainListener.isHunterCanMoove()) {
                    e.setCancelled(true);
                }
            }
        }
    }

    public static void setHunterNotMove() {

        MainListener.setHunterCanMoove(false);
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                if(GameManager.getGameManager().equals(GameManagerEnum.GAME)) {
                    MainListener.setHunterCanMoove(true);
                    Bukkit.getOnlinePlayers().stream().filter(player -> !GameManager.getSpeedrunners().contains(player)).forEach(player -> {
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

}
