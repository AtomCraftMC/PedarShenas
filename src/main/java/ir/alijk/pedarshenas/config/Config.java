package ir.alijk.pedarshenas.config;

import ir.jeykey.megacore.MegaPlugin;
import ir.jeykey.megacore.config.Configurable;

public class Config extends Configurable {
        public static String PREFIX;
        public static boolean ENABLED;
        public static String GHASEDAK_API_KEY;
        public static String GHASEDAK_TEMPLATE_NAME;

        public Config(MegaPlugin plugin) {
                super(plugin, "config.yml");
        }

        @Override
        public void init() {
                PREFIX = getConfig().getString("prefix");
                ENABLED = getConfig().getBoolean("enabled");
                GHASEDAK_API_KEY = getConfig().getString("ghasedak-api-key");
                GHASEDAK_TEMPLATE_NAME = getConfig().getString("ghasedak-template-name");
        }
}
