package apple.voltskiya.custom_mobs.leaps.pounce;

import apple.voltskiya.custom_mobs.ConfigManager;
import apple.voltskiya.custom_mobs.VoltskiyaModule;
import apple.voltskiya.custom_mobs.YmlSettings;
import apple.voltskiya.custom_mobs.leaps.LeapPlugin;
import apple.voltskiya.custom_mobs.leaps.LeapType;
import apple.voltskiya.custom_mobs.sql.MobListSql;
import net.minecraft.server.v1_16_R3.Entity;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class LeapPounceNormal extends ConfigManager {
    private static LeapPounceNormal instance;

    public LeapPounceNormal() {
        instance = this;
        for (UUID mob : getMobs()) {
            final CraftEntity entityBukkit = (CraftEntity) Bukkit.getEntity(mob);
            if (entityBukkit != null) {
                @Nullable Entity entity = entityBukkit.getHandle();
                if (entity instanceof EntityInsentient) {
                    eatEntity((EntityInsentient) entity);
                    continue;
                }
            }
            MobListSql.removeMob(mob);
        }
    }

    private void eatEntity(EntityInsentient entity) {
        LeapPounce.eatEntity(entity, LeapType.POUNCE_LEAP_NORMAL);
        instance.addMobs(entity.getUniqueID());
    }

    public static void eatSpawnEvent(CreatureSpawnEvent event) {
        LeapPounce.eatSpawnEvent(event, LeapType.POUNCE_LEAP_NORMAL);
        instance.addMobs(event.getEntity().getUniqueId());
    }

    /**
     * @return the name of the sub_module (a step below a module)
     */
    @Override
    public String getName() {
        return "pounce_normal_leap";
    }

    /**
     * @return the default values for the config file
     */
    @Override
    public YmlSettings[] getSettings() {
        return new YmlSettings[0];
    }

    /**
     * @return the module associated with this config
     */
    @Override
    protected VoltskiyaModule getPlugin() {
        return LeapPlugin.get();
    }
}
