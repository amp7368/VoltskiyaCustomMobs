package apple.voltskiya.custom_mobs.mobs.abilities.tick.reviver.mob;

import apple.mc.utilities.world.vector.VectorUtils;
import apple.nms.decoding.entity.DecodeEntity;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.reviver.config.ReviverConfigBasic;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.reviver.dead.DeadRecordedMob;
import apple.voltskiya.custom_mobs.mobs.abilities.tick.reviver.dead.ReviveDeadManager;
import apple.voltskiya.custom_mobs.pathfinders.utilities.PathfinderGoalMoveToTarget;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.util.Vector;
import voltskiya.apple.utilities.action.ActionMeta;
import voltskiya.apple.utilities.action.ActionReturn;
import voltskiya.apple.utilities.action.OneOffAction;
import voltskiya.apple.utilities.action.RepeatableActionImpl;
import voltskiya.apple.utilities.action.RepeatingActionManager;

public class MobReviverBasic extends MobReviver<ReviverConfigBasic> {

    public static final int SUMMON_TICKING_INTERVAL = 3;
    private static final String DO_INIT = "init";
    private static final String DO_SUMMON1 = "summon1";
    private static final String DO_START = "start";
    private static final String DO_START_RITUAL = "start_ritual";
    private final RepeatingActionManager action;
    private DeadRecordedMob reviveMe;

    public MobReviverBasic(Entity reviver, ReviverConfigBasic config) {
        super(reviver, config);
        action = new RepeatingActionManager(VoltskiyaPlugin.get()).registerInit(this::initSummon)
            .registerAction(new OneOffAction(DO_SUMMON1, this::summon1))
            .registerAction(new OneOffAction(DO_START, this::summonStart)).registerAction(
                new RepeatableActionImpl(DO_START_RITUAL, this::doReviveRitual,
                    config.reviveRitualTime, SUMMON_TICKING_INTERVAL))
            .registerFinally(this::quitRevive);
    }

    private void initSummon() {
        setIsDoingAction(true);
    }

    private void summon1() {
        reviveMe = ReviveDeadManager.getNearestMob(config.deadTooLong, getLocation());
        if (reviveMe == null) {
            return;
        }
        ReviveDeadManager.removeMob(reviveMe);
        double distance = VectorUtils.distance(reviveMe.getLocation(), getLocation());
        if (distance > config.searchRadius) {
            return;
        }
        Location targetLocation = reviveMe.getLocation();
        while (targetLocation.getBlock().getType().isAir() && targetLocation.getY() >= 0) {
            targetLocation.subtract(0, 1, 0);
        }
        targetLocation = targetLocation.getBlock().getLocation().add(0.5, 1, 0.5);
        this.reviveMe.setLocation(targetLocation);
        DecodeEntity.getGoalSelector(getMob()).addGoal(-1,
            new PathfinderGoalMoveToTarget(getMob(), targetLocation, (int) 1.6, config.giveUpTick,
                () -> action.startActionAndStart(DO_START)));
    }

    @Override
    protected void doAbility() {
        action.startActionAndStart(DO_SUMMON1);
    }

    private void summonStart() {
        if (reviveMe == null || VectorUtils.distance(reviveMe.getLocation(), getLocation()) > 2) {
            // say we failed
            ReviveDeadManager.addMob(reviveMe);
            return;
        }
        Mob reviver = getBukkitMob();
        reviver.setAI(false);

        final Location reviverLocation = reviver.getLocation();
        final Location reviveMeLocation = reviveMe.getLocation();
        double x = reviveMeLocation.getX() - reviverLocation.getX();
        double z = reviveMeLocation.getZ() - reviverLocation.getZ();
        double magnitude = x * x + z * z;

        Location newLoc = reviverLocation.setDirection(
            new Vector(x / magnitude, -.5, z / magnitude));
        reviver.teleport(newLoc);

        final World world = reviverLocation.getWorld();
        world.playSound(reviveMe.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE,
            SoundCategory.HOSTILE, 35, .7f);
        action.startAction(DO_START_RITUAL);
    }

    private void quitRevive() {
        getBukkitMob().setAI(true);
        setIsDoingAction(false);
    }

    private ActionReturn doReviveRitual(ActionMeta meta) {
        // if the mob was just spawned or it was hurt a while ago
        if (wasHit(meta.currentTick()) || isDead()) {
            reviveMe.resetCooldown(config.deadCooldown);
            return ActionReturn.stop();
        }
        if (meta.isLastRun()) {
            doReviveSummon(reviveMe);
            return ActionReturn.stop();
        }
        Location reviverLocation = getLocation();
        World world = getWorld();
        double xLoc = reviverLocation.getX();
        double yLoc = reviverLocation.getY();
        double zLoc = reviverLocation.getZ();
        for (int i = 0; i < 15; i++) {
            double xi = random.random().nextDouble() - .5;
            double yi = random.random().nextDouble() * 2;
            double zi = random.random().nextDouble() - .5;
            world.spawnParticle(Particle.REDSTONE, xLoc + xi, yLoc + yi, zLoc + zi, 1,
                new Particle.DustOptions(Color.RED, 1f));
        }
        return ActionReturn.go();
    }
}
