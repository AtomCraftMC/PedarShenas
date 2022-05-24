package ir.alijk.pedarshenas.events;

import ir.alijk.pedarshenas.Encryption;
import ir.alijk.pedarshenas.PedarShenasSpigot;
import ir.alijk.pedarshenas.PlayerStages;
import ir.alijk.pedarshenas.SMSManager;
import ir.alijk.pedarshenas.config.Messages;
import ir.alijk.pedarshenas.data.AtomPlayer;
import ir.jeykey.megacore.utils.Common;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.io.IOException;
import java.sql.SQLException;

public class PlayerChat implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e) {
        // Making sure that chat wouldn't actually get sent
        e.setCancelled(true);

        // Making sure that the player is in our player list
        if (!PedarShenasSpigot.getAtomPlayers().containsKey(e.getPlayer())) return;

        // Receiving the atom player object from our mapping
        AtomPlayer atomPlayer = PedarShenasSpigot.getAtomPlayers().get(e.getPlayer());

        if (atomPlayer.getStage().equals(PlayerStages.PENDING_INPUT_PHONE)) {
            // Doing the phone number checks
            if (!e.getMessage().startsWith("09")) {
                Common.send(e.getPlayer(), Messages.INVALID_PHONE_ERROR);
                return;
            } else if (e.getMessage().length() != 11) {
                Common.send(e.getPlayer(), Messages.INVALID_PHONE_LENGTH_ERROR);
                return;
            }

            // Checking for similar phone number in the database (checking if it's unique)
            int similarNumbers;

            try {
                // We will check the sha256 hash phone number in the database
                similarNumbers = PedarShenasSpigot.getAtomPlayersDao().queryForEq("phone", Encryption.sha256(e.getMessage())).size();
            } catch (SQLException ex) {
                e.getPlayer().kickPlayer(Messages.DATABASE_ISSUE);
                return;
            }

            if (similarNumbers > 0) {
                Common.send(e.getPlayer(), Messages.DUPLICATE_NUMBER_ERROR);
            } else {
                atomPlayer.setPhone(e.getMessage());
                atomPlayer.setStage(PlayerStages.PENDING_VERIFICATION_CODE);

                String playerAddress = e.getPlayer().getAddress().getHostString();
                boolean isAllowed = PedarShenasSpigot.getRecordManager().newRecord(playerAddress);

                if (!isAllowed) {
                    int cooldown = PedarShenasSpigot.getRecordManager().getRecordCooldown(playerAddress);
                    Bukkit.getScheduler().runTask(PedarShenasSpigot.getInstance(), () -> {
                        e.getPlayer().kickPlayer(Common.colorize(Messages.VERIFY_COOLDOWN_ERROR.replace("%cooldown%", Integer.toString(cooldown))));
                    });
                    return;
                }

                // Sending the SMS containing verification code to the user
                Bukkit.getScheduler().runTaskAsynchronously(PedarShenasSpigot.getInstance(), () -> {
                    String verificationCode;
                    try {
                        verificationCode = SMSManager.sendVerificationCode(atomPlayer.getPhone(), e.getPlayer().getName());
                    } catch (IOException ex) {
                        e.getPlayer().kickPlayer(Messages.SMS_FAILED_ERROR);
                        return;
                    }

                    atomPlayer.setVerificationCode(verificationCode);
                });

                Common.send(e.getPlayer(), Messages.SMS_SENT);
            }
        } else if (atomPlayer.getStage().equals(PlayerStages.PENDING_VERIFICATION_CODE)) {
            // Checking the input verification code from the player
            if (e.getMessage().equalsIgnoreCase(atomPlayer.getVerificationCode())) {
                atomPlayer.setVerified(true);

                try {
                    atomPlayer.save();
                } catch (SQLException ex) {
                    e.getPlayer().kickPlayer(Messages.DATABASE_ISSUE);
                }

                Common.send(e.getPlayer(), Messages.VERIFY_SUCCESSFUL);

                // We'll send the user back to auth after the verification so the LoginSender plugin sends him to blobby[1-4]
                // PedarShenasSpigot.getBungeeApi().connect(e.getPlayer(), "auth");
                Bukkit.getScheduler().runTask(
                        PedarShenasSpigot.getInstance(),
                        () -> Bukkit.getServer().dispatchCommand(
                                Bukkit.getServer().getConsoleSender(),
                                "forward console lobby " + e.getPlayer().getName()
                        )
                );
            } else {
                String errorMessages = Messages.WRONG_VERIFICATION_CODE_ERROR.replace("%count%", "&4(" + atomPlayer.getWrongCodeCounter() + "/3)");
                if (atomPlayer.getWrongCodeCounter() < 3) {
                    Common.send(e.getPlayer(), errorMessages);
                    atomPlayer.setWrongCodeCounter(atomPlayer.getWrongCodeCounter() + 1);
                } else {
                    Bukkit.getScheduler().runTask(PedarShenasSpigot.getInstance(), () -> {
                        e.getPlayer().kickPlayer(Common.colorize(errorMessages));
                    });
                }
            }
        }

    }
}
