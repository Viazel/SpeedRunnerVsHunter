package fr.viazel.speedrunnervshunter.commands;

import fr.viazel.speedrunnervshunter.Main;
import fr.viazel.speedrunnervshunter.utils.GameManager;
import fr.viazel.speedrunnervshunter.utils.PlayerRunner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EndCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        PlayerRunner p = new PlayerRunner((Player) sender);

        if(Main.getInstance().getGameManager() == GameManager.START) {
            p.getPlayer().sendMessage("§cLa partie n'est déjà lancée !");
            return false;
        }

        if(!p.haveToBeOP()) return false;

        Main.getInstance().changeGameManager(GameManager.ENDHUNTER);

        return false;
    }
}