package fr.viazel.speedrunnervshunter.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SpeedRunnerLogger {

    public static void sendMessage(Player player, String message) {
        player.getPlayer().sendMessage("§f[§eS§aV§cH§f] §f" + message);
    }

    public static void broadcastMessage(String message) {
        Bukkit.broadcastMessage("§f[§eS§aV§cH§f] §f" + message);
    }

    public static void sendTitle(String message) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.sendTitle(message,"", 10, 20 * 2 + 10, 10);
        });
    }

}
