package apple.voltskiya.custom_mobs.leap.parent;

@FunctionalInterface
public interface CreateLeapConfig<Config> {

    Config create(String prefix, String tag);
}
