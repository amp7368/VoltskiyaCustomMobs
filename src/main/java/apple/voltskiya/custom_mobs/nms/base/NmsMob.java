package apple.voltskiya.custom_mobs.nms.base;

import net.minecraft.world.entity.Entity;

public class NmsMob<Self extends Entity> {

    private final Self self;
    private final NmsSpawner<Self, ?> spawner;
    private final NmsMobSupers<Self> supers;

    public NmsMob(Self self, NmsSpawner<Self, ?> spawner, NmsMobSupers<Self> supers) {
        this.self = self;
        this.spawner = spawner;
        this.supers = supers;
    }


    public NmsMobSupers<Self> supers() {
        return this.supers;
    }

    public Self self() {
        return self;
    }

    public NmsSpawner<Self, ?> spawner() {
        return spawner;
    }
}
