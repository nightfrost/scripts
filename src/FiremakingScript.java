
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.Script;

import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.utility.ConditionalSleep;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

@ScriptManifest(name = "SimpleFiremaking", author = "Nightfrost", version = 1.0, info = "",
        logo = "https://static.wikia.nocookie.net/2007scape/images/f/f1/Firemaking_cape_equipped.png/revision/latest?cb=20180308195132")

public class FiremakingScript extends Script {
    private GUIFM guifm = new GUIFM();
    private TreeLogs logs;

    @Override
    public void onStart() {
        getExperienceTracker().start(Skill.FIREMAKING);
        log("Welcome to SimpleFiremaking");
        try {
            SwingUtilities.invokeAndWait(() -> {
                guifm = new GUIFM();
                guifm.open();
            });
        } catch (InterruptedException | InvocationTargetException e) {
            e.printStackTrace();
            stop();
            return;
        }

        // If the user closed the dialog and didn't click the Start button
        if (!guifm.isStarted()) {
            stop();
            return;
        }

        logs = guifm.getSelectedTree();

    }

    @Override
    public void onExit() {

        //Code here will execute after the script ends

    }

    @Override
    public int onLoop() {
        int starting = getExperienceTracker().getGainedXP(Skill.FIREMAKING);
        if (logs != null) {
            if (logs.toString() == "Logs" && getInventory().contains(logs.toString()) && getInventory().contains("Tinderbox")) {
                getInventory().interact("Use", "Tinderbox");
                getInventory().interact("Use", logs.toString());
                new ConditionalSleep(10000) {
                    @Override
                    public boolean condition() {
                        return getExperienceTracker().getGainedXP(Skill.FIREMAKING) > starting;
                    }
                }.sleep();
            }else{
                if(getInventory().contains(logs.toString() + " logs")) {
                    getInventory().interact("Use", "Tinderbox");
                    getInventory().interact("Use", logs.toString() + " logs");
                    new ConditionalSleep(10000) {
                        @Override
                        public boolean condition() {
                            return getExperienceTracker().getGainedXP(Skill.FIREMAKING) > starting;
                        }
                    }.sleep();
                }
            }
        }
        return 100; //The amount of time in milliseconds before the loop starts over

    }

    @Override
    public void onPaint(Graphics2D g) {

        //This is where you will put your code for paint(s)

    }

}

enum TreeLogs {
    Logs,
    Oak,
    Willow,
    Maple,
    Yew;
    @Override
    public String toString() {
        return name();
    }
}

final class GUIFM {
    private final JDialog mainDialog;
    private final JComboBox<TreeLogs> logsSelector;

    private boolean started;

    public GUIFM() {
        mainDialog = new JDialog();
        mainDialog.setTitle("Nightfrost's Firemaking");
        mainDialog.setModal(true);
        mainDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainDialog.getContentPane().add(mainPanel);

        JPanel logsSelectionPanel = new JPanel();
        logsSelectionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel treeSelectionLabel = new JLabel("Select tree:");
        logsSelectionPanel.add(treeSelectionLabel);

        logsSelector = new JComboBox<>(TreeLogs.values());
        logsSelectionPanel.add(logsSelector);

        mainPanel.add(logsSelectionPanel);

        JButton startButton = new JButton("Start");
        startButton.addActionListener(e -> {
            started = true;
            close();
        });
        mainPanel.add(startButton);

        mainDialog.pack();
    }

    public boolean isStarted() {
        return started;
    }

    public TreeLogs getSelectedTree() {
        return (TreeLogs) logsSelector.getSelectedItem();
    }

    public void open() {
        mainDialog.setVisible(true);
    }

    public void close() {
        mainDialog.setVisible(false);
        mainDialog.dispose();
    }
}