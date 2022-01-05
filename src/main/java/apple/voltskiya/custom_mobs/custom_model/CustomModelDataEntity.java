package apple.voltskiya.custom_mobs.custom_model;

import apple.nms.decoding.nbt.DecodeNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import voltskiya.apple.utilities.util.data_structures.XYZ;

import java.util.Map;

public class CustomModelDataEntity {
    private static final String ROTATION_NBT = "Rotation";
    private static final String UUID_NBT = "UUID";
    public final String nameInYml;
    public final double facingX;
    public final double facingY;
    public final double facingZ;
    public final EntityType type;
    public final NBTTagCompound nbt;
    public final Map<String, Object> otherData;
    private final XYZ<Double> posVec;
    private final XYZ<Double> facingVec;
    public double x;
    public double y;
    public double z;

    public CustomModelDataEntity(String nameInYml, double x, double y, double z, double facingX, double facingY, double facingZ, EntityType type, NBTTagCompound nbt, Map<String, Object> otherData) {
        this.nameInYml = nameInYml;
        this.x = x;
        this.y = y;
        this.z = z;
        this.posVec = new XYZ<>(x, y, z);
        if (DecodeNBT.hasKey(nbt, ROTATION_NBT)) {
            NBTTagList rotation = DecodeNBT.getFloatList(nbt, ROTATION_NBT); //float list
            if (rotation.size() == 2) {
                float yaw = Float.parseFloat(rotation.get(0).toString());
                float pitch = Float.parseFloat(rotation.get(1).toString());

                Location l = new Location(null, 0, 0, 0);
                l.setYaw(yaw);
                l.setPitch(pitch);
                this.facingX = l.getDirection().getX();
                this.facingY = l.getDirection().getY();
                this.facingZ = l.getDirection().getZ();
                DecodeNBT.removeKey(nbt, ROTATION_NBT);
            } else {
                this.facingX = facingX;
                this.facingY = facingY;
                this.facingZ = facingZ;
            }
        } else {
            this.facingX = facingX;
            this.facingY = facingY;
            this.facingZ = facingZ;
        }
        this.facingVec = new XYZ<>(this.facingX, this.facingY, this.facingZ);
        this.type = type;
        this.nbt = nbt;
        DecodeNBT.removeKey(nbt, UUID_NBT);
        this.otherData = otherData;
    }

    public XYZ<Double> getPosVec() {
        return posVec;
    }

    public XYZ<Double> getFacingVec() {
        return facingVec;
    }
}
