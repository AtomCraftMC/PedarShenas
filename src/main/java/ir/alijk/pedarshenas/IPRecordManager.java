package ir.alijk.pedarshenas;

import ir.alijk.pedarshenas.data.IPRecord;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IPRecordManager {
    @Getter private final HashMap<String, IPRecord> activeRecords = new HashMap<>();
    @Getter private final HashMap<String, IPRecord> limitedRecords = new HashMap<>();

    public IPRecordManager() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(PedarShenasSpigot.getInstance(), () -> {
            List<String> limitFinishedRecords = new ArrayList<>();

            getLimitedRecords().entrySet().stream()
                    .filter(entry -> System.currentTimeMillis() - entry.getValue().getLastTryAt() > 5 * 60 * 1000)
                    .forEach(entry -> limitFinishedRecords.add(entry.getKey()));

            limitFinishedRecords.forEach(record -> getLimitedRecords().remove(record));

        }, 0, 100);
    }

    /**
     * Add new record for  an ip address
     * @param address The ip address of the client
     * @return Will return if the address is allowed for another record or not
     */
    public boolean newRecord(String address) {
        if (getLimitedRecords().containsKey(address)) return false;

        IPRecord record;
        if (getActiveRecords().containsKey(address)) {
            record = getActiveRecords().get(address);
            record.newTry();
            getActiveRecords().replace(address, record);
        } else {
            record = new IPRecord(address);
            getActiveRecords().put(address, record);
        }

        if (record.getTries() > 3) {
            getActiveRecords().remove(record);
            getLimitedRecords().put(address, record);
            return false;
        }

        return true;
    }

    /**
     * Will return user cooldown by ip address
     * @param address User ip address in string
     * @return Cooldown of the address (we'll be 0 is the address is not limited)
     */
    public int getRecordCooldown(String address) {
        if (getLimitedRecords().containsKey(address)) {
            int diffInSeconds = Math.round((System.currentTimeMillis() - getLimitedRecords().get(address).getLastTryAt()) / 1000F);
            return 300 - diffInSeconds;
        } else {
            return 0;
        }
    }
}
