package fr.viazel.speedrunnervshunter.listeners;

import fr.viazel.speedrunnervshunter.utils.GameManager;
import fr.viazel.speedrunnervshunter.utils.GameManagerEnum;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {

    private final GameManagerEnum gameManager;

    public PlayerDeathListener() {
        gameManager = GameManager.getGameManager();
    }

    @EventHandler
    public void event(PlayerDeathEvent e){

        if(gameManager != GameManagerEnum.GAME) return;

        MainListener.setP(e.getEntity());

        e.setDeathMessage("§eLe joueur §b" + MainListener.getP().getName() + " §eest mort !");

    }

}
