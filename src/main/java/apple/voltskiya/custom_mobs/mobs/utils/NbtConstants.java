package apple.voltskiya.custom_mobs.mobs.utils;

public class NbtConstants {
    public static final String ARMOR_ITEMS = "ArmorItems";
    public static final String HAND_ITEMS = "HandItems";
    public static final String ENTITY_LOCATION_RELATIVE_CONFIG = "EntityConfigRelative";

    public static Object toObject(String data) {
        if (data.equalsIgnoreCase("true")) return true;
        else if (data.equalsIgnoreCase("false")) return false;
        try {
            return Integer.parseInt(data);
        } catch (NumberFormatException ignored) {
        }
        try {
            return Double.parseDouble(data);
        } catch (NumberFormatException ignored) {
        }
        return data;
    }

    public static class EntityLocationRelative {
        public static final String IS_MAIN = "IsMain";
        public static final String RELATIVE_LOCATION = "RelativeLocation";
        public static final String NAME_IN_YML = "NameInYml";
        public static final String X_OFFSET = "XOffset";
        public static final String Y_OFFSET = "YOffset";
        public static final String Z_OFFSET = "ZOffset";
        public static final String X_FACING = "XFacing";
        public static final String Y_FACING = "YFacing";
        public static final String Z_FACING = "ZFacing";
        public static final String OTHER_DATA = "OtherData";
    }
}
