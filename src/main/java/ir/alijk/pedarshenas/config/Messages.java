package ir.alijk.pedarshenas.config;

import ir.jeykey.megacore.MegaPlugin;
import ir.jeykey.megacore.config.Configurable;

public class Messages extends Configurable {
        public static String INVALID_PHONE_ERROR;
        public static String INVALID_PHONE_LENGTH_ERROR;
        public static String DUPLICATE_NUMBER_ERROR;
        public static String VERIFY_COOLDOWN_ERROR;
        public static String SMS_FAILED_ERROR;
        public static String DATABASE_ISSUE;
        public static String WRONG_VERIFICATION_CODE_ERROR;

        public static String COMMAND_NO_PERMISSION;
        public static String COMMAND_WRONG_USAGE;
        public static String COMMAND_PLAYER_NOT_FOUND;
        public static String COMMAND_WRONG_DISCORD_ID;
        public static String COMMAND_PLAYER_VERIFIED;
        public static String COMMAND_DUPLICATE_VERIFY_VALUE;

        public static String SMS_SENT;
        public static String VERIFY_SUCCESSFUL;

        public Messages(MegaPlugin plugin) {
                super(plugin, "messages.yml");
        }

        @Override
        public void init() {
                INVALID_PHONE_ERROR = getConfig().getString("errors.invalid-phone");
                INVALID_PHONE_LENGTH_ERROR = getConfig().getString("errors.invalid-phone-length");
                DUPLICATE_NUMBER_ERROR = getConfig().getString("errors.duplicate-phone");
                VERIFY_COOLDOWN_ERROR = getConfig().getString("errors.verify-cooldown");
                SMS_FAILED_ERROR = getConfig().getString("errors.sms-failed");
                DATABASE_ISSUE = getConfig().getString("errors.database-issue");
                WRONG_VERIFICATION_CODE_ERROR = getConfig().getString("errors.wrong-verification-code");

                COMMAND_NO_PERMISSION = getConfig().getString("errors.commands.no-permission");
                COMMAND_WRONG_USAGE = getConfig().getString("errors.commands.wrong-usage");
                COMMAND_PLAYER_NOT_FOUND = getConfig().getString("errors.commands.player-not-found");
                COMMAND_WRONG_DISCORD_ID = getConfig().getString("errors.commands.wrong-discord-id");
                COMMAND_PLAYER_VERIFIED = getConfig().getString("errors.commands.player-verified");
                COMMAND_DUPLICATE_VERIFY_VALUE = getConfig().getString("errors.commands.duplicate-verify-value");

                SMS_SENT = getConfig().getString("sms-sent");
                VERIFY_SUCCESSFUL = getConfig().getString("verify-successful");
        }
}
