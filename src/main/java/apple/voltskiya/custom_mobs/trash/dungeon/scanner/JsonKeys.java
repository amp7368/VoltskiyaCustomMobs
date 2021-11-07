package apple.voltskiya.custom_mobs.trash.dungeon.scanner;

public class JsonKeys {
    public static final String MOB_CONFIGS = "mobConfigs";
    public static final String MOB_CONFIG_NAME = "name";
    public static final String MOB_CONFIG_MOBS = "mobs";
    public static final String MOB_CONFIG_NBT = "nbt";
    public static final String DUNGEON_MOB_PRIMARY = "mobPrimary";
    public static final String DUNGEON_MOB_CONFIG = "mobConfig";
    public static final String MOB_CONFIG_UUID = "uuid";
    public static final String DUNGEON_CHESTS_LOOTABLE = "lootable";
    public static final String DUNGEON_CHESTS_BLOCK = "blockId";
    public static final String DUNGEON_CHESTS_NBT = "nbt";
    public static final String DUNGEON_CHESTS_TITLE = "title";
    public static final String DUNGEON_REALS = "realDungeons";

    public static class Scanner {
        public static final String SCANNER_NAME = "scanner_name";
        public static final String SCANNER_CENTER = "center";
    }

    public static class Dungeon {
        public static final String SCANNER = "scanner";
        public static final String SCANNED = "scanned";
        public static final String CENTER = "center";
    }

    public static class Layout {
        public static final String DUNGEON_NAME = "dungeon_name";
        public static final String DUNGEON_MOBS = "mobs";
        public static final String DUNGEON_CHESTS = "chests";
        public static final String CENTER = "center";
    }
}
