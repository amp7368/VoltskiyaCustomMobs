package apple.voltskiya.custom_mobs.nms.parent.register;

import apple.voltskiya.custom_mobs.sql.MobListSql;
import net.minecraft.world.entity.Mob;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftMob;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public interface RegisteredEntityEater {

    /**
     * eat all the mobs in the database
     */
    default void eatMobs() {
        for (Entity entityBukkit : getMobs()) {
            eatEntity(entityBukkit);
            if (entityBukkit instanceof Mob) {
                eatEntity((Mob) entityBukkit);
                eatEntity(((CraftMob) entityBukkit).getHandle());
            }
        }
    }

    /**
     * remove any entities that don't exist anymore along the way
     *
     * @return the entities that exist in the database
     */
    default List<Entity> getMobs() {
        List<UUID> uuids = null;
        try {
            uuids = MobListSql.getMobs(getName());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        if (uuids == null) return Collections.emptyList();
        List<org.bukkit.entity.Entity> mobs = new ArrayList<>(uuids.size());
        for (UUID uuid : uuids) {
            org.bukkit.entity.Entity mob = Bukkit.getEntity(uuid);
            if (mob == null) MobListSql.removeMob(uuid);
            else mobs.add(mob);
        }
        return mobs;
    }

    /**
     * eat an entity please override one of the eatEntity events (there's 3 cause it makes things easier and lets the programmer choose
     * what they need)
     *
     * @param entity the entity to eat
     */
    default void eatEntity(Entity entity) {
    }

    /**
     * eat an entity please override one of the eatEntity events (there's 3 cause it makes things easier and lets the programmer choose
     * what they need)
     *
     * @param entity the entity to eat
     */
    default void eatEntity(Mob entity) {
    }

    /**
     * eat an entity please override one of the eatEntity events (there's 3 cause it makes things easier and lets the programmer choose
     * what they need)
     *
     * @param entity the entity to eat
     */
    default void eatEntity(Mob entity) {
    }

    /**
     * @return the name of this mob type
     */
    String getName();

    /**
     * register this mob type with the database
     */
    default void registerInDB() {
        try {
            MobListSql.registerName(getName());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    default void eatAndRegisterEvent(CreatureSpawnEvent event) {
        addMob(event.getEntity().getUniqueId());
        eatEvent(event);
    }

    /**
     * add the following mob to the database
     *
     * @param uuid the uuid of the mob to add
     */
    default void addMob(UUID uuid) {
        try {
            MobListSql.addMob(getName(), uuid);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * eat a mob spawn event
     *
     * @param event the event to eat
     */
    default void eatEvent(CreatureSpawnEvent event) {
        final LivingEntity entity = event.getEntity();
        eatEntity(entity);
        if (entity instanceof Mob) {
            eatEntity((Mob) entity);
            eatEntity(((CraftMob) entity).getHandle());
        }
    }
}
