package org.uninstal.seasons.database;

public enum UserParameter {
    EXP("exp", null, "exp"),
    MOB_KILLS("mob_kills", "mob-kills", "mobs"),
    PLAYER_KILLS("player_kills", "player-kills", "player"),
    PLAY_TIME("play_time", "play-time", "time"),
    CLAN(null, "clan", null);

    private final String columnId;
    private final String configId;
    private final String placeholderId;

    UserParameter(String columnId, String configId, String placeholderId) {
        this.columnId = columnId;
        this.configId = configId;
        this.placeholderId = placeholderId;
    }

    public String getColumnId() {
        return columnId;
    }

    public String getConfigId() {
        return configId;
    }

    public String getPlaceholderId() {
        return placeholderId;
    }

    public static UserParameter getByPlaceholderId(String placeholderId) {
        for (UserParameter parameter : values()) {
            if (parameter.getPlaceholderId().equalsIgnoreCase(placeholderId)) {
                return parameter;
            }
        }
        return null;
    }
}