package apple.voltskiya.custom_mobs.sql;

public class DBNames {

    public static class MobNames {
        public static final String MOB_TYPE_NAME = "mob_type_name";
        public static final String DATABASE_NAME = "mobs.db";
        public static final String MOB_UID_TABLE = "mob_uuids";
        public static final String MOB_TYPE_UID = "mob_type";
        public static final String MOB_UUID = "uuid";
        public static final String MOB_TYPE_TO_TYPE_UID_TABLE = "mob_type_to_my_uid";
    }

    public static class TurretNames {
        public static final String DATABASE_NAME = "turrets.db";
        public static final String TURRETS_TABLE = "turret";
        public static final String ARROW_TABLE = "arrow";
        public static final String TURRET_TO_ENTITY_TABLE = "turret_to_entity";
        public static final String TURRET_UID = "turret_uid";
        public static final String X = "x";
        public static final String Y = "y";
        public static final String Z = "z";
        public static final String X_FACING = "x_facing";
        public static final String Y_FACING = "y_facing";
        public static final String Z_FACING = "z_facing";
        public static final String DURABILITY_ENTITY = "durability_entity";
        public static final String REFILLED_ENTITY = "refilled_entity";
        public static final String BOW_ENTITY = "bow_entity";
        public static final String BOW = "bow";
        public static final String BOW_DURABILITY = "bow_durability";
        public static final String HEALTH = "health";
        public static final String ARROW_COUNT = "arrow_count";
        public static final String ENTITY_UID = "entity_uid";
        public static final String WORLD_UID = "world_uid";
        public static final String ARROW_SLOT_INDEX = "slot_index";
        public static final String TURRET_TYPE = "turret_type";
    }

    public static class MaterialNames {
        public static final String MATERIAL_UID = "material_uid";
        public static final String MATERIAL_NAME = "material_name";
        public static final String MATERIAL_TABLE = "material";
    }

    public static class ItemNames {
        public static final long AIR_ITEM_STACK_ID = 0;
        public static final String ITEM_UID = "item_uid";
        public static final String ITEM_COUNT = "item_count";
        public static final String DURABILITY = "durability";
        public static final String ENCHANTMENT_UID = "enchantment_uid";
        public static final String ENCHANTMENT_NAMESPACE = "enchantment_namespace";
        public static final String ENCHANTMENT_NAME = "enchantment_name";
        public static final String ENCHANTMENT_LEVEL = "enchantment_level";
        public static final String ITEM_TABLE = "item";
        public static final String ENCHANTMENT_TABLE ="enchantment" ;
        public static final String ENCHANTMENT_ENUM_TABLE = "enchantment_uid_to_name";
    }
}
