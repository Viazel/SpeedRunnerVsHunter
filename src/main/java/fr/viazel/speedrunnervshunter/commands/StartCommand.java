package fr.viazel.speedrunnervshunter.commands;

import fr.viazel.speedrunnervshunter.Main;
import fr.viazel.speedrunnervshunter.utils.GameManager;
import fr.viazel.speedrunnervshunter.utils.PlayerRunner;
import org.bukkit.Bukkit;
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
            p.getPlayer().sendMessage("§cLa partie est déjà lancée !");
            return false;
        }

        if(!p.haveToBeOP()) return false;

        if(Bukkit.getOnlinePlayers().size() > 4) {
            p.getPlayer().sendMessage("§cVous devez minimum être 4 joueurs !");
            return false;
        }

        ArrayList<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());

        int max = Bukkit.getOnlinePlayers().size() - 1;
        int min = 0;
        int range = max - min + 1;
        int r = (int) ((Math.random() * range) + min);

        Main.getInstance().speedrunners.add(new PlayerRunner(players.get(r)));

        Main.getInstance().speedrunners.forEach(runner -> {
            runner.getPlayer().sendMessage("Vous êtes SpeedRunner !");
        });

        Main.getInstance().changeGameManager(GameManager.GAME);

        return false;
    }
}