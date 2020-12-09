import org.osbot.rs07.api.model.Entity;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.utility.ConditionalSleep;

import java.util.Arrays;

@ScriptManifest(author = "Nightfrost", info = "SimpleCooking", logo = "https://static.wikia.nocookie.net/2007scape/images/c/c9/Cooking_cape_equipped.png/revision/latest?cb=20180308195129", version = 1.0,
        name = "SimpleCooking")
public class CookingScript extends Script {

    @Override
    public void onStart() {
        getExperienceTracker().start(Skill.COOKING);
    }

    @Override
    public int onLoop() throws InterruptedException {
        String food = "Raw shrimps";
        int starting = getExperienceTracker().getGainedXP(Skill.COOKING);
        RS2Object fire = getObjects().closest("Fire");
        RS2Widget cookAll = getWidgets().get(270, 14);
        if (getInventory().contains(food)) {
            if (!myPlayer().isAnimating())
                getInventory().interact("Use", food);
                fire.interact("Use");
                cookAll.interact(); //Bugs out on widget - works but keeps spamming.
        }else {

        }
        return random(200, 300);
    }
}