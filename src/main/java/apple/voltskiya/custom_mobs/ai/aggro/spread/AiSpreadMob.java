package apple.voltskiya.custom_mobs.ai.aggro.spread;

import apple.voltskiya.custom_mobs.ai.aggro.stare.DelayPathfindingMob;
import apple.voltskiya.mob_manager.mob.MMSpawned;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;

public class AiSpreadMob {

    private static final int DEFAULT_RADIUS = 25;
    private final MMSpawned mm;

    public AiSpreadMob(MMSpawned mm) {
        this.mm = mm;
        new DelayPathfindingMob(mm).doSpawn();
        doSpread();
    }

    private void doSpread() {
        for (String tag : mm.getMob().getScoreboardTags()) {
            if (!tag.startsWith(AiSpread.AI_SPREAD_PREFIX)) continue;

            String meta = tag.substring(AiSpread.AI_SPREAD_PREFIX.length());
            if (meta.isBlank()) {
                doRadiusSpread(DEFAULT_RADIUS);
                return;
            }
            String[] loc = meta.split(",");
            if (loc.length == 3) {
                doCoordsSpread(loc);
                return;
            }
            int radius = Integer.parseInt(loc[0]);
            doRadiusSpread(radius);
            return;
        }
    }

    private void doCoordsSpread(String[] loc) {
        int x = Integer.parseInt(loc[0]);
        int y = Integer.parseInt(loc[1]);
        int z = Integer.parseInt(loc[2]);
        mm.getNmsMob().getNavigation().moveTo(x, y, z, 1d);
    }

    private void doRadiusSpread(int radius) {
        DefaultRandomPos.getPos((PathfinderMob) mm.getNmsMob(), radius, radius /*yRadius*/);
    }
}
