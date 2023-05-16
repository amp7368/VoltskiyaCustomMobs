package apple.voltskiya.custom_mobs.nms.parent.config;

import com.voltskiya.lib.AbstractModule;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ConfigManager {

    private static final String defaultConfig = "config";
    private final Map<String, YamlConfiguration> ymls = new HashMap<>();
    private File folder = null;

    /**
     * @return the name of the sub_module (a step below a module)
     */
    abstract public String getName();

    /**
     * @return the default values for the config file
     */
    abstract public YmlSettings[] getSettings();

    /**
     * initializes the .yml file with default values
     *
     * @throws IOException if something goes wrong with the value insertion
     */
    public void initializeYml() throws IOException {
        for (YmlSettings settings : getSettings()) {
            setValueIfNotExists(settings.getPath(), settings.getValue());
        }
    }

    /**
     * @return the module associated with this config
     */
    protected abstract AbstractModule getPlugin();

    /**
     * sets the value of a field if that field doesn't already have a value
     *
     * @param path  the path of the variable to set
     * @param value the value of the variable
     * @throws IOException if something goes wrong with the value insertion
     */
    public void setValueIfNotExists(String path, Object value) throws IOException {
        setValueIfNotExists(defaultConfig, path, value);
    }

    /**
     * sets the value of a field if that field doesn't already have a value this allows for deeper folders and allows you to set the
     * file name
     *
     * @param fileName the name of the yml file
     * @param path     the path of the variable to set
     * @param value    the value of the variable
     * @param parents  and parent folders of the file
     * @throws IOException if something goes wrong with the value insertion
     */
    public void setValueIfNotExists(String fileName, String path, Object value, String... parents) throws IOException {
        File file = new File(getDatafolder() + (
            parents.length == 0 ? "" : File.separator) + String.join(File.separator, parents));
        if (!file.exists()) file.mkdirs();
        file = new File(getDatafolder() + (
            parents.length == 0 ? "" : File.separator) + String.join(File.separator, parents), fileName + ".yml");
        if (!file.exists()) file.createNewFile();
        YamlConfiguration yml = getConfig(fileName, file);
        @Nullable ConfigurationSection config = yml.getConfigurationSection(defaultConfig);
        if (config == null) {
            yml.createSection(defaultConfig);
            config = yml.getConfigurationSection(defaultConfig);
            if (config == null) {
                initializeYml();
                config = yml.getConfigurationSection(defaultConfig);
                if (config == null) throw new IOException("Error creating the config for " + getName());
            }
        }
        if (config.get(path) == null) {
            config.set(path, value);
            yml.save(file);
        }
    }


    /**
     * sets the value of a variable regardless of what already exists there
     *
     * @param path  the path of where to set the varialbe
     * @param value the value to set the variable to
     * @throws IOException if there was an issue setting the value
     */
    public void setValue(String path, Object value) throws IOException {
        setValue(defaultConfig, path, value);
    }


    /**
     * sets the value of a variable regardless of what already exists there
     *
     * @param fileName the file name of the yml
     * @param path     the path of where to set the variable
     * @param value    the value to set the variable to
     * @throws IOException if there  was an error with the writing of the value
     */
    public void setValue(String fileName, String path, Object value) throws IOException {
        File file = new File(getDatafolder(), fileName + ".yml");
        if (!file.exists()) file.createNewFile();
        YamlConfiguration yml = getConfig(fileName, file);
        @Nullable ConfigurationSection config = yml.getConfigurationSection(defaultConfig);
        if (config == null) {
            initializeYml();
            config = yml.getConfigurationSection(defaultConfig);
            if (config == null) throw new IOException("Error creating the config for " + getName());
        }
        config.set(path, value);
        yml.save(file);
    }

    /**
     * gets the value at the specified path
     *
     * @param path the path to get the value at
     * @return the value described
     * @throws IOException if there was an issue retrieving the file info
     */
    @Nullable
    public Object getValue(String path) throws IOException {
        return getValue(defaultConfig, path);
    }

    /**
     * gets the value at the specified path
     *
     * @param fileName the yml file
     * @param path     the  path to get the value at
     * @param parents  any parent folders
     * @return the value described
     * @throws IOException if there was an issue retrieving the file
     */
    @Nullable
    protected Object getValue(String fileName, String path, String... parents) throws IOException {
        File file = new File(getDatafolder() + (
            parents.length == 0 ? "" : File.separator) +
            String.join(File.separator, parents), fileName + ".yml");
        if (!file.exists()) file.createNewFile();
        YamlConfiguration yml = getConfig(fileName, file);
        @Nullable ConfigurationSection config = yml.getConfigurationSection(defaultConfig);
        if (config == null) {
            config = yml.createSection(defaultConfig);
            yml.save(file);
        }
        return config.get(path);
    }

    /**
     * gets the configuration object for the specified filename in this directory
     *
     * @param fileName the name of the possible yml if I already have the yml name cached
     * @param file     the file of the yml file
     * @return the configuration object decribed in the file
     * @throws IOException if there was an issue retrieving the yml
     */
    @NotNull
    public YamlConfiguration getConfig(String fileName, File file) throws IOException {
        YamlConfiguration yml = ymls.get(fileName);
        if (yml == null || !file.exists()) {
            // create the yml
            if (!file.exists()) file.createNewFile();
            yml = YamlConfiguration.loadConfiguration(file);
            ymls.put(fileName, yml);
        }
        return yml;
    }

    /**
     * gets the value at the path, and if the variable doesn't exist, initialize the yml with default values
     *
     * @param path the path for the value
     * @return the value at the specified path
     * @throws IOException if there was an issue reading the file
     */
    @NotNull
    public Object getValueOrInit(String path) throws IOException {
        return getValueOrInit(defaultConfig, path);
    }

    /**
     * gess the value at the specified path and if the variable doesn't exist, initialize the yml with default values
     *
     * @param fileName the name of the yml file
     * @param path     the path for the value
     * @param parents  any parent folders
     * @return the value at the specified path
     * @throws IOException if there was an issue reading or writing to the file
     */
    @NotNull
    public Object getValueOrInit(String fileName, String path, String... parents) throws IOException {
        final String pathname = getDatafolder() + (
            parents.length == 0 ? "" : File.separator
        ) + String.join(File.separator, parents);
        File file = new File(pathname);
        if (!file.exists()) file.mkdirs();
        @Nullable Object value = getValue(fileName, path, parents);
        if (value == null) initializeYml();
        value = getValue(fileName, path, parents);
        if (value == null) throw new IOException("Error initializing config " + getName());
        return value;
    }

    /**
     * @return the datafolder for this sub sub plugin
     */
    public File getDatafolder() {
        if (this.folder == null) {
            File folder = new File(getPlugin().getDataFolder(), getName());
            if (!folder.exists()) folder.mkdirs();
            this.folder = folder;
        }
        return this.folder;
    }
}
