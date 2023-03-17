package fr.viazel.speedrunnervshunter.commands;

import fr.viazel.speedrunnervshunter.utils.ConfigFile;
import fr.viazel.speedrunnervshunter.utils.GameManager;
import fr.viazel.speedrunnervshunter.utils.PlayerRunner;
import fr.viazel.speedrunnervshunter.utils.SpeedRunnerLogger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetDefaultSpawnCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] strings) {

        PlayerRunner p = new PlayerRunner((Player) sender);

        if(!p.hasToBeOP()) return false;

        ConfigFile configFile = new ConfigFile();
        configFile.setDefaultSpawn(p.getPlayer().getLocation());
        SpeedRunnerLogger.sendMessage(p.getPlayer(), "§aLe spawn des SpeedRunners a bien été défini !");

        return false;
    }
}
