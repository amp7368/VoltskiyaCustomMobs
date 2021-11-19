package apple.voltskiya.custom_mobs.custom_model.handling;

import java.io.File;

public class ModelNameImpl implements ModelName {
    private File file;

    @Override
    public File getFile() {
        return file;
    }
}
