package apple.voltskiya.custom_mobs.mobs.abilities.ai_changes.flamethrower;

import net.minecraft.world.entity.EntityInsentient;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftMob;
import org.bukkit.util.Vector;
import voltskiya.apple.utilities.util.VectorUtils;

import java.util.Random;

public class FlameThrowerShot {
    private static final double DENSITY = 1;
    private final EntityInsentient handle;
    private final CraftMob bukkitEntity;
    private final double radius;
    private final FlameThrowerCaster me;
    private final double radiansWide;
    private double length;
    private final Random random = new Random();

    public FlameThrowerShot(FlameThrowerCaster me, double currentLength, int degreesWide) {
        this.me = me;
        this.handle = me.getEntity();
        this.length = currentLength;
        this.bukkitEntity = (CraftMob) this.handle.getBukkitEntity();
        this.radiansWide = Math.toRadians(degreesWide);
        // tan(theta) = opp/adj
        this.radius = Math.atan(this.radiansWide) * this.length;
    }

    public void shoot() {
        Particle particle = Particle.FLAME;
        Location startLocation = bukkitEntity.getLocation();
        final World world = startLocation.getWorld();
        CraftLivingEntity target = bukkitEntity.getTarget();
        Vector direction = target == null ?
                startLocation.getDirection() :
                target.getEyeLocation().subtract(startLocation).toVector();
        direction.normalize();
        double volume = volume(this.radius, this.length) * DENSITY;
        for (int i = 0; i < volume; i++) {
            double ilength = this.length * random.nextDouble(); //length of the vector
            double iTheta = this.radiansWide * random.nextDouble() * 2 - this.radiansWide; // left-right
            double iPhi = this.radiansWide * random.nextDouble() * 2 - this.radiansWide; // up-down
            double ix = random.nextDouble() * ilength;
            double iy = Math.acos(iPhi) * ilength;
            double iz = Math.acos(iTheta) * ilength;

            // rotate it back to the real world
            // find the angle in the xz
            double angleXZ = VectorUtils.dot(ix, iz, direction.getX(), direction.getZ()) / (VectorUtils.magnitude(ix, iz) + VectorUtils.magnitude(direction.getX(), direction.getZ()));
            double angleXY = VectorUtils.dot(ix, iy, direction.getX(), direction.getY()) / (VectorUtils.magnitude(ix, iy) + VectorUtils.magnitude(direction.getX(), direction.getY()));
            Vector idirection = VectorUtils.rotateVector(direction.getX(), direction.getZ(), direction.getY(), random.nextBoolean() ? angleXZ : -angleXZ);
            idirection = VectorUtils.rotateVector(idirection.getX(), idirection.getY(), idirection.getZ(), random.nextBoolean() ? angleXY : -angleXY);
            idirection.multiply(ilength);
            world.spawnParticle(particle,
                    startLocation.getX() + idirection.getX(),
                    startLocation.getY() + idirection.getY(),
                    startLocation.getZ() + idirection.getZ(),
                    0
            );
        }
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    private static double volume(double radius, double height) {
        return Math.PI * radius * radius * height / 3;
    }
}
