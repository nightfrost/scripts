import org.osbot.rs07.api.model.Entity;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.utility.ConditionalSleep;

import java.util.Arrays;

@ScriptManifest(author = "Nightfrost", info = "SimpleAgility", logo = "https://oldschool.runescape.wiki/images/thumb/1/1b/Agility_cape%28t%29_equipped.png/137px-Agility_cape%28t%29_equipped.png?95739", version = 1.0, name = "SimpleAgility")
public class AgilityScript extends Script {
    private String[] actions = {"Climb", "Cross", "Balance", "Jump-up", "Jump", "Climb-down"};
    private String[] names = {"Rough wall", "Tightrope", "Narrow wall", "Wall", "Gap", "Crate"};
    Entity previous;

    @Override
    public void onStart() {
        getExperienceTracker().start(Skill.AGILITY);
    }

    @Override
    public int onLoop() throws InterruptedException {
        int starting = getExperienceTracker().getGainedXP(Skill.AGILITY);
        Entity nextObj = getObjects().closest(obj -> Arrays.asList(names).contains(obj.getName()) &&
                Arrays.asList(actions).contains(obj.getActions()[0]) &&
                (getMap().canReach(obj) || obj.getName().equals("Crate")) &&
                !obj.equals(previous));

        if (nextObj != null  && !myPlayer().isMoving()) {
            if (nextObj.interact(nextObj.getActions()[0])) {
                new ConditionalSleep(10000) {
                    @Override
                    public boolean condition() throws InterruptedException {
                        return getExperienceTracker().getGainedXP(Skill.AGILITY) > starting;
                    }
                }.sleep();
            } if (getExperienceTracker().getGainedXP(Skill.AGILITY) > starting) {
                previous = nextObj;
            }
        }
        return 250;
    }
}