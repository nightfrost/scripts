import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.utility.ConditionalSleep;
import java.util.function.BooleanSupplier;
import org.osbot.rs07.api.model.NPC;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.lang.reflect.InvocationTargetException;

@ScriptManifest(author = "Nightfrost", info = "SimpleThief", logo = "https://static.wikia.nocookie.net/2007scape/images/4/49/Thieving_cape_equipped.png/revision/latest?cb=20180308223139",
        name = "SimpleThief", version = 1)
public class ThievingScript extends Script {
    private GUITHIEF guithief = new GUITHIEF();
    private NPCNAMES npc;

    @Override
    public void onStart() {
        try {
            SwingUtilities.invokeAndWait(() -> {
                guithief = new GUITHIEF();
                guithief.open();
            });
        } catch (InterruptedException | InvocationTargetException e) {
            e.printStackTrace();
            stop();
            return;
        }

        // If the user closed the dialog and didn't click the Start button
        if (!guithief.isStarted()) {
            stop();
            return;
        }

        npc = guithief.getSelectedNPC();
    }

    @Override
    public void onExit() {
        log("Thx for running the script!");

    }

    @Override
    public int onLoop() throws InterruptedException {
        NPC NPCObject = getNpcs().closest(npc.toString());


        if (getInventory().isFull()) {
            getInventory().dropAll();
            log("Dropping Inventory..");
            sleep(500);
        }
        else if (npc != null && !myPlayer().isMoving() && !myPlayer().isAnimating() ) {
            NPCObject.interact("Pickpocket");
            log("Pickpocketing " + npc.toString());
            new ConditionalSleep(5000) {
                @Override
                public boolean condition() {
                    return myPlayer().isAnimating();
                }
            }.sleep();
        }

        return random(200, 300);

    }

    @Override
    public void onPaint(Graphics2D g) {

        //This is where you will put your code for paint(s)

    }
}

enum NPCNAMES {
    Man,
    Woman;

    @Override
    public String toString() {
        return name();
    }
}

final class GUITHIEF {
    private final JDialog mainDialog;
    private final JComboBox<NPCNAMES> NPCSelector;

    private boolean started;

    public GUITHIEF() {
        mainDialog = new JDialog();
        mainDialog.setTitle("Nightfrost's Thieving");
        mainDialog.setModal(true);
        mainDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainDialog.getContentPane().add(mainPanel);

        JPanel npcSelectionPanel = new JPanel();
        npcSelectionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel npcSelectionLabel = new JLabel("Select NPC:");
        npcSelectionPanel.add(npcSelectionLabel);

        NPCSelector = new JComboBox<>(NPCNAMES.values());
        npcSelectionPanel.add(NPCSelector);

        mainPanel.add(npcSelectionPanel);

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

    public NPCNAMES getSelectedNPC() {
        return (NPCNAMES) NPCSelector.getSelectedItem();
    }

    public void open() {
        mainDialog.setVisible(true);
    }

    public void close() {
        mainDialog.setVisible(false);
        mainDialog.dispose();
    }
}