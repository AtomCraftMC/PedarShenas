package ir.alijk.pedarshenas;

import com.j256.ormlite.dao.Dao;
import ir.alijk.pedarshenas.config.Config;
import ir.alijk.pedarshenas.config.Messages;
import ir.alijk.pedarshenas.config.Storage;
import ir.alijk.pedarshenas.database.DataSource;
import ir.alijk.pedarshenas.data.AtomPlayer;
import ir.alijk.pedarshenas.events.*;
import ir.jeykey.megacore.MegaPlugin;
import ir.jeykey.megacore.utils.Common;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

public final class PedarShenasSpigot extends MegaPlugin {
    @Getter @Setter private static Dao<AtomPlayer,String> atomPlayersDao;
    @Getter private static final HashMap<Player, AtomPlayer> atomPlayers = new HashMap<>();
    @Getter private static IPRecordManager recordManager;

    @Override
    public void onPluginEnable() {
        getConfigManager().register(Config.class);
        getConfigManager().register(Storage.class);
        getConfigManager().register(Messages.class);

        setPrefix(Common.colorize(Config.PREFIX));

        recordManager = new IPRecordManager();

        // Setting up datasource
        try {
            if (Storage.LOCATION.equalsIgnoreCase("sqlite")) {
                DataSource.SQLite();
            } else if (Storage.LOCATION.equalsIgnoreCase("mysql")) {
                DataSource.MySQL();
            } else {
                disablePlugin("&cStorage type defined in config (" + Storage.LOCATION + ") is not valid!");
                return;
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
            disablePlugin("&cPlugin could not work with database! [ Check Stack Trace For More Information ]");
            return;
        } catch (IOException exception) {
            exception.printStackTrace();
            disablePlugin("&cPlugin is unable to create database file, Please check directory permissions [ Check Stack Trace For More Information ]");
            return;
        } catch (ClassNotFoundException exception) {
            exception.printStackTrace();
            disablePlugin("&cIt seems that there's a problem with plugin and it could not be loaded, Please contact plugin developers [ Check Stack Trace For More Information ]");
            return;
        }

        register(new PlayerJoin());

        // Registering events
        if (Config.ENABLED) {
            register(new PlayerChat());
            register(new PlayerCommand());
            register(new PlayerMove());
            register(new PlayerQuit());
        }
    }

    @Override
    public void onPluginDisable() {

    }
}
