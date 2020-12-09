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

@ScriptManifest(author = "Nightfrost", info = "SimpleCombat", logo="https://static.wikia.nocookie.net/2007scape/images/1/19/Attack_cape_equipped.png/revision/latest?cb=20180308195128",
        name = "SimpleCombat", version = 1)
public class CombatScript extends Script {
    private GUICOMBAT guicombat = new GUICOMBAT();
    private NPCPick npc;

    @Override
    public void onStart() {
        try {
            SwingUtilities.invokeAndWait(() -> {
                guicombat = new GUICOMBAT();
                guicombat.open();
            });
        } catch (InterruptedException | InvocationTargetException e) {
            e.printStackTrace();
            stop();
            return;
        }

        // If the user closed the dialog and didn't click the Start button
        if (!guicombat.isStarted()) {
            stop();
            return;
        }

        npc = guicombat.getSelectedNPC();
    }

    @Override
    public void onExit() {
        log("Thx for running the script!");

    }

    @Override
    public int onLoop() throws InterruptedException {
        NPC NPCObject = getNpcs().closest(npc.toString());

        if (npc != null && !myPlayer().isMoving() && !myPlayer().isAnimating() && !myPlayer().isUnderAttack()) {
            NPCObject.interact("Attack");
            sleep(1000);
            log("Attacking " + npc.toString());
            new ConditionalSleep(3000) {
                @Override
                public boolean condition() {
                    return myPlayer().isUnderAttack();
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

enum NPCPick {
    Goblin,
    Man,
    Woman;

    @Override
    public String toString() {
        return name();
    }
}

final class GUICOMBAT {
    private final JDialog mainDialog;
    private final JComboBox<NPCPick> NPCPicker;

    private boolean started;

    public GUICOMBAT() {
        mainDialog = new JDialog();
        mainDialog.setTitle("Nightfrost's Combat");
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

        NPCPicker = new JComboBox<>(NPCPick.values());
        npcSelectionPanel.add(NPCPicker);

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

    public NPCPick getSelectedNPC() {
        return (NPCPick) NPCPicker.getSelectedItem();
    }

    public void open() {
        mainDialog.setVisible(true);
    }

    public void close() {
        mainDialog.setVisible(false);
        mainDialog.dispose();
    }
}