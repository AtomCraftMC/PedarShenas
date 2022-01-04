package ir.alijk.pedarshenas.commands;

import ir.alijk.pedarshenas.Encryption;
import ir.alijk.pedarshenas.PedarShenasSpigot;
import ir.alijk.pedarshenas.config.Config;
import ir.alijk.pedarshenas.config.Messages;
import ir.alijk.pedarshenas.data.AtomPlayer;
import ir.jeykey.megacore.utils.Common;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.sql.SQLException;

public class VerifyCommand extends Command {
    public String PERMISSION = "pedarshenas.admin.verify";

    public VerifyCommand() {
        super("pedarverify");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            Common.send(sender, Messages.COMMAND_NO_PERMISSION);
            return true;
        }

        if (args.length != 3) {
            Common.send(sender, Messages.COMMAND_WRONG_USAGE);
            return true;
        }

        String targetName = args[0];
        String verificationType = args[1];
        String verificationValue = args[2];

        AtomPlayer atomPlayer = null;
        try {
            atomPlayer = AtomPlayer.findByUsername(targetName);
        } catch (SQLException ex) {
            Common.send(sender, Messages.DATABASE_ISSUE);
            return true;
        }

        if (atomPlayer == null) {
            Common.send(sender, Messages.COMMAND_PLAYER_NOT_FOUND);
            return true;
        }

        if (verificationType.equalsIgnoreCase("discord")) {
            if (17 <= verificationValue.length()) {
                // Checking for similar phone number in the database (checking if it's unique)
                int similarValues;

                try {
                    // We will check the sha256 hash phone number in the database
                    similarValues = PedarShenasSpigot.getAtomPlayersDao().queryForEq("discordId", verificationValue).size();
                } catch (SQLException ex) {
                    Common.send(sender, Messages.DATABASE_ISSUE);
                    return true;
                }

                if (similarValues > 0) {
                    Common.send(sender, Messages.COMMAND_DUPLICATE_VERIFY_VALUE);
                    return true;
                } else {
                    atomPlayer.setDiscordId(Long.parseLong(verificationValue));
                    atomPlayer.setVerified(true);
                    try {
                        atomPlayer.save();
                        Common.send(
                                sender,
                                Messages.COMMAND_PLAYER_VERIFIED
                                        .replace("%name%", targetName)
                                        .replace("%type%", verificationType)
                                        .replace("%value%", verificationValue)
                        );
                    } catch (SQLException e) {
                        Common.send(sender, Messages.DATABASE_ISSUE);
                    }
                }
            } else {
                Common.send(sender, Messages.COMMAND_WRONG_DISCORD_ID);
            }
        } else {
            Common.send(sender, Messages.COMMAND_WRONG_USAGE);
        }

        return true;
    }
}
