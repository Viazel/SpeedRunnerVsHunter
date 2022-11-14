package fr.viazel.speedrunnervshunter.utils;

import org.bukkit.entity.Player;

public class PlayerRunner {

    private Player p;

    public PlayerRunner(Player p) {
        this.p = p;
    }

    public boolean haveToBeOP() {
        if(!p.isOp()) {
            SpeedRunnerLogger.sendMessage(p, "§cVous devez être OP pour exécuter cette commande !");
            return false;
        }
        return true;
    }

    public String getName() {
        return p.getName();
    }

    public Player getPlayer() {
        return p;
    }

}
