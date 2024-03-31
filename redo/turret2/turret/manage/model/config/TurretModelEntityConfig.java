package apple.voltskiya.custom_mobs.turret.manage.model.config;


public class TurretModelEntityConfig extends CustomModelEntityConfig {

    public TurretModelEntityConfig(CustomModelDataEntity entity) {
        super(entity);
    }

    private boolean is(String tag) {
        return this.getData().otherData.containsKey(tag);
    }

    public boolean isMain() {
        return is("main");
    }

    public boolean isDurability() {
        return is("durability");
    }

    public boolean isBow() {
        return is("bow");
    }

    public boolean isRefilled() {
        return is("refilled");
    }

}
