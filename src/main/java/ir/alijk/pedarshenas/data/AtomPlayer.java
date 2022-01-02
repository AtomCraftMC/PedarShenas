package ir.alijk.pedarshenas.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import ir.alijk.pedarshenas.Encryption;
import ir.alijk.pedarshenas.PedarShenasSpigot;
import ir.alijk.pedarshenas.PlayerStages;
import lombok.Getter;
import lombok.Setter;

import java.sql.SQLException;
import java.util.List;

@DatabaseTable(tableName = "pedarshenas_players")
public class AtomPlayer {
    @DatabaseField(columnName = "id", generatedId = true) @Getter @Setter
    private int id;

    @DatabaseField(canBeNull = false) @Getter @Setter
    private String username;

    @DatabaseField(canBeNull = false) @Getter @Setter
    private String uuid;

    @DatabaseField @Getter @Setter
    private String phone;

    @DatabaseField @Getter @Setter
    private int discordId;

    @DatabaseField @Getter @Setter
    private String email;

    @DatabaseField @Getter @Setter
    private boolean isVerified;

    @Getter @Setter
    private PlayerStages stage;

    @Getter @Setter
    private String verificationCode;

    @Getter @Setter
    private int wrongCodeCounter = 1;

    public AtomPlayer() {

    }

    public AtomPlayer(String username, String uuid) {
        this(username, uuid, null, -1, "null", false);
    }

    public AtomPlayer(String username, String uuid, String phone, int discordId, String email, boolean isVerified) {
        this(username, uuid, phone, discordId, "null", false, -1);
    }

    public AtomPlayer(String username, String uuid, String phone, int discordId, String email, boolean isVerified, int id) {
        this.username = username;
        this.uuid = uuid;
        this.phone = phone;
        this.discordId = discordId;
        this.email = email;
        this.isVerified = isVerified;
        this.id = id;
    }

    public void save() throws SQLException {
        // We will hash phone number on saving the player to database if the phone is available
        if (getPhone() != null) setPhone(Encryption.sha256(getPhone()));
        PedarShenasSpigot.getAtomPlayersDao().createOrUpdate(this);
    }

    public static AtomPlayer findByUsername(String username) throws SQLException{
        List<AtomPlayer> atomPlayers = PedarShenasSpigot.getAtomPlayersDao().queryForEq("userName", username);
        if (atomPlayers.size() == 0) return null;
        return atomPlayers.get(0);
    }

}
