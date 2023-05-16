package apple.voltskiya.custom_mobs.nms.misc;

import apple.voltskiya.custom_mobs.nms.base.INmsMob;
import apple.voltskiya.custom_mobs.nms.base.NmsMob;
import apple.voltskiya.custom_mobs.nms.base.NmsMobSupers;
import apple.voltskiya.custom_mobs.nms.base.NmsSpawner;
import java.util.List;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

public class MobHealthPack extends ArmorStand implements INmsMob<MobHealthPack> {

    private static NmsSpawner<MobHealthPack, ArmorStand> spawner;
    private NmsMob<MobHealthPack> wrapper;


    public MobHealthPack(EntityType<ArmorStand> type, Level world) {
        super(type, world);
    }

    public static NmsSpawner<MobHealthPack, ?> spawner() {
        if (spawner != null) return spawner;
        return spawner = new MobHealthPackSpawner();
    }

    @Override
    public NmsSpawner<MobHealthPack, ?> getInstSpawner() {
        return spawner();
    }

    @Override
    public NmsMob<MobHealthPack> wrapper() {
        if (this.wrapper != null) return this.wrapper;
        return this.wrapper = createWrapper();
    }

    @Override
    public MobHealthPack getSelf() {
        return this;
    }


    @Override
    public NmsMobSupers<MobHealthPack> makeEntitySupers() {
        return new NmsMobSupers<>(
            super::changeDimension,
            super::move,
            super::load,
            super::save,
            super::saveWithoutId,
            super::remove
        );
    }

    @Override
    public void tick() {
        List<Player> collision = getLevel().getEntities(EntityType.PLAYER, getBoundingBox(), (p) -> true);
        for (Player collide : collision) {
            this.healPlayer(collide);
            this.remove(RemovalReason.KILLED);
            return;
        }
    }


    private void healPlayer(Player player) {
        player.heal(getHealth(), RegainReason.MAGIC);
    }
}
