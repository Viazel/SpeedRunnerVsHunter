package fr.viazel.speedrunnervshunter.commands;

import fr.viazel.speedrunnervshunter.Main;
import fr.viazel.speedrunnervshunter.utils.GameManager;
import fr.viazel.speedrunnervshunter.utils.PlayerRunner;
import fr.viazel.speedrunnervshunter.utils.SpeedRunnerLogger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class StartCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        PlayerRunner p = new PlayerRunner((Player) sender);

        if(Main.getInstance().getGameManager() != GameManager.START) {
            SpeedRunnerLogger.sendMessage(p.getPlayer(), "§cLa partie est déjà lancée !");
            return false;
        }

        if(!p.haveToBeOP()) return false;

        if(Bukkit.getOnlinePlayers().size() < 4) {
            SpeedRunnerLogger.sendMessage(p.getPlayer(), "§cVous devez minimum être 4 joueurs !");
            return false;
        }

        ArrayList<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());

        int max = Bukkit.getOnlinePlayers().size() - 1;
        int min = 0;
        int range = max - min + 1;
        int r = (int) ((Math.random() * range) + min);

        Main.getInstance().speedrunners.add(players.get(r));

        Player target = players.get(r);

        while (Main.getInstance().speedrunners.contains(target)) {
            r = (int) ((Math.random() * range) + min);
            target = players.get(r);
        }

        Main.getInstance().speedrunners.add(target);

        Main.getInstance().changeGameManager(GameManager.GAME);

        return false;
    }
}