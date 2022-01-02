package ir.alijk.pedarshenas.events;

import ir.alijk.pedarshenas.PedarShenasSpigot;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent e) {
        PedarShenasSpigot.getAtomPlayers().remove(e.getPlayer());
    }
}
