package fr.viazel.speedrunnervshunter.commands;

import fr.viazel.speedrunnervshunter.Main;
import fr.viazel.speedrunnervshunter.utils.GameManager;
import fr.viazel.speedrunnervshunter.utils.GameManagerEnum;
import fr.viazel.speedrunnervshunter.utils.PlayerRunner;
import fr.viazel.speedrunnervshunter.utils.SpeedRunnerLogger;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class StartCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(!(sender instanceof Player)) return false;

        Player p = (Player) sender;

        GameManagerEnum gameManager = GameManager.getGameManager();

        if(gameManager != GameManagerEnum.START) {
            SpeedRunnerLogger.sendMessage(p.getPlayer(), "§cLa partie est déjà lancée !");
            return false;
        }

        if(!(new PlayerRunner(p)).hasToBeOP()) return false;

        if(Bukkit.getOnlinePlayers().size() < 4) {
            SpeedRunnerLogger.sendMessage(p.getPlayer(), "§cVous devez minimum être 4 joueurs !");
            return false;
        }

        int max = Bukkit.getOnlinePlayers().size() - 1;
        int min = 0;
        int range = max - min + 1;
        int r = (int) ((Math.random() * range) + min);

        ArrayList<Player> speedrunners = GameManager.getSpeedrunners();

        Player newPlayer = ((ArrayList<Player>) Bukkit.getOnlinePlayers()).get(r);

        speedrunners.add(newPlayer);

        Player target = newPlayer;

        while (speedrunners.contains(target)) {
            r = (int) ((Math.random() * range) + min);
            target = ((ArrayList<Player>) Bukkit.getOnlinePlayers()).get(r);
        }

        speedrunners.add(target);

        GameManager.changeGameManager(GameManagerEnum.GAME);

        return false;
    }
}