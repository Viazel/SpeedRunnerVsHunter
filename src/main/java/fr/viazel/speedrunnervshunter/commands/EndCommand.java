package fr.viazel.speedrunnervshunter.commands;

import fr.viazel.speedrunnervshunter.Main;
import fr.viazel.speedrunnervshunter.utils.GameManager;
import fr.viazel.speedrunnervshunter.utils.GameManagerEnum;
import fr.viazel.speedrunnervshunter.utils.PlayerRunner;
import fr.viazel.speedrunnervshunter.utils.SpeedRunnerLogger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EndCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(!(sender instanceof Player)) return false;

        Player p = (Player) sender;

        GameManagerEnum gameManager = GameManager.getGameManager();

        if(gameManager == GameManagerEnum.START) {
            SpeedRunnerLogger.sendMessage(p.getPlayer(), "§cLa partie n'est pas déjà lancée !");
            return false;
        }

        if(!(new PlayerRunner(p)).hasToBeOP()) return false;

        SpeedRunnerLogger.sendMessage(p.getPlayer(), "§aLa partie a été forcé de se finir !");

        GameManager.changeGameManager(GameManagerEnum.ENDHUNTER);

        return false;
    }
}