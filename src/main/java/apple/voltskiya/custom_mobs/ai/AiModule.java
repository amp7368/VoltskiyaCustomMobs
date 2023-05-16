package apple.voltskiya.custom_mobs.ai;

import apple.voltskiya.custom_mobs.ai.aggro.LineOfSightAggroListener;
import apple.voltskiya.custom_mobs.ai.aggro.NoAlertOthers;
import apple.voltskiya.custom_mobs.ai.bowlike.BowlikeMoveManager;
import com.voltskiya.lib.AbstractModule;

public class AiModule extends AbstractModule {

    public static final String EXTENSION_TAG = "ai";

    @Override
    public void enable() {
        new BowlikeMoveManager();
        new LineOfSightAggroListener();
        new NoAlertOthers();
    }

    @Override
    public String getName() {
        return "Ai";
    }
}
