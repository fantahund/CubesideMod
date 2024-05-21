package de.fanta.cubeside.permission;

import java.util.HashMap;
import java.util.Map;

public class PermissionHandler {

    private static final int PLAYER = 0;
    private static final int VIP = 100;
    private static final int CREATOR = 100;
    private static final int VETERAN = 1500;
    private static final int BUDDY = 2000;
    private static final int STAFF = 5000;
    private static final int ADMIN = 8000;
    private static final int OWNER = 10000;

    private static final Map<String, Integer> RANK_PRIORITIES;

    static {
        RANK_PRIORITIES = Map.of(
                "player", PLAYER,
                "vip", VIP,
                "creator", CREATOR,
                "veteran", VETERAN,
                "buddy", BUDDY,
                "staff", STAFF,
                "admin", ADMIN,
                "owner", OWNER
        );
    }

    private final Map<String, Integer> minRequiredPermission;

    public PermissionHandler() {
        this.minRequiredPermission = new HashMap<>();
        //Player
        minRequiredPermission.put("xareomap", PLAYER);
        //VIP

        //VETERAN
        minRequiredPermission.put("cubeside.addskulltolore", BUDDY);

        //CREATOR

        //BUDDY


        //STAFF
        minRequiredPermission.put("cubeside.autochat", STAFF);
        minRequiredPermission.put("cubeside.afkcheck", STAFF);

        //ADMIN

        //OWNER
    }

    public boolean hasPermission(String rank, String permission) {
        if (rank == null) {
            return false;
        }


        Integer rankPriority = RANK_PRIORITIES.get(rank.toLowerCase());
        if (rankPriority == null) {
            rankPriority = PLAYER;
        }

        Integer minPriority = minRequiredPermission.get(permission.toLowerCase());
        return minPriority != null && rankPriority >= minPriority;
    }
}
