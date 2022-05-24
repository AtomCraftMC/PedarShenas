package ir.alijk.pedarshenas.events;

import ir.alijk.pedarshenas.PedarShenasSpigot;
import ir.alijk.pedarshenas.PlayerStages;
import ir.alijk.pedarshenas.config.Config;
import ir.alijk.pedarshenas.data.AtomPlayer;
import ir.jeykey.megacore.utils.Common;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;

public class PlayerJoin implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerJoin(PlayerJoinEvent e) {
        AtomPlayer atomPlayer = null;
        try {
            atomPlayer = AtomPlayer.findByUsername(e.getPlayer().getName());
        } catch (SQLException ex) {
            e.getPlayer().kickPlayer("Database Error");
            return;
        }

        if (atomPlayer == null) {
            atomPlayer = new AtomPlayer(e.getPlayer().getName(), e.getPlayer().getUniqueId().toString());
            try {
                atomPlayer.save();
            } catch (SQLException ex) {
                e.getPlayer().kickPlayer("Database Error");
                return;
            }
        }

        PedarShenasSpigot.getAtomPlayers().put(e.getPlayer(), atomPlayer);

        atomPlayer.setStage(PlayerStages.PRE_VERIFY);

        // IF plugin mode is not fully enabled won't verify the user in current server (ACTING AS API)
        if (!Config.ENABLED)
            return;

        // This won't happen if the server is doing normal, so we'll hardcode a lobby to send the user to
        if (atomPlayer.isVerified()) {
//            PedarShenasSpigot.getBungeeApi().connect(e.getPlayer(), "blobby1");
            Bukkit.getScheduler().runTaskLater(
                    PedarShenasSpigot.getInstance(),
                    () -> Bukkit.getServer().dispatchCommand(
                            Bukkit.getServer().getConsoleSender(),
                            "forward console lobby " + e.getPlayer().getName()),
                    5L);
        } else {
            atomPlayer.setStage(PlayerStages.PENDING_INPUT_PHONE);
            Common.send(e.getPlayer(), "&aBaraye taeid hesab karbari khodetoon shomare hamrah khodetoon ro dar chat vared konid");
        }

    }
}
