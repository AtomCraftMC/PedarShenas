package ir.alijk.pedarshenas.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PlayerCommand implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommandPre(PlayerCommandPreprocessEvent e) {
        e.setCancelled(true);
    }
}
