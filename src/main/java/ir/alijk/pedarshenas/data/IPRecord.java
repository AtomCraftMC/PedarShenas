package ir.alijk.pedarshenas.data;

import lombok.Getter;
import lombok.Setter;

public class IPRecord {
    @Getter private final String address;
    @Getter @Setter private int tries = 1;
    @Getter @Setter private long lastTryAt;

    public IPRecord(String address) {
        this.address = address;
    }

    public void newTry() {
        setTries(getTries() + 1);
        setLastTryAt(System.currentTimeMillis());
    }
}
