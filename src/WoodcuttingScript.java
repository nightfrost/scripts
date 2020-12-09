import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.utility.ConditionalSleep;
import java.util.function.BooleanSupplier;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.lang.reflect.InvocationTargetException;

@ScriptManifest(author = "Nightfrost", info = "SimpleWC", logo = "https://static.wikia.nocookie.net/2007scape/images/e/ef/Woodcutting_cape_equipped.png/revision/latest?cb=20180308223316",
        name = "SimpleWC", version = 1)
public class WoodcuttingScript extends Script {
    private GUIWC guiwc = new GUIWC();
    private Tree tree;

    @Override
    public void onStart() {
        try {
            SwingUtilities.invokeAndWait(() -> {
                guiwc = new GUIWC();
                guiwc.open();
            });
        } catch (InterruptedException | InvocationTargetException e) {
            e.printStackTrace();
            stop();
            return;
        }

        // If the user closed the dialog and didn't click the Start button
        if (!guiwc.isStarted()) {
            stop();
            return;
        }

        tree = guiwc.getSelectedTree();
    }

    @Override
    public void onExit() {
        log("Thx for running the script!");

    }

    @Override
    public int onLoop() throws InterruptedException {
        RS2Object treeObject = getObjects().closest(tree.toString()); //Get closest object with given name.

        if (getInventory().contains("Logs") && getInventory().isFull()) {
            getInventory().dropAll("Logs");
            log("Dropping logs...");
            sleep(500);
        }
        else if (getInventory().contains(tree.toString() + " logs") && getInventory().isFull()) {
            getInventory().dropAll(tree.toString() + " logs");
            log("Dropping all " + tree.toString() + " logs");
            int randomTime = random(400,800);
            sleep(randomTime);
        }
        else if (tree != null && !myPlayer().isMoving() && !myPlayer().isAnimating() ) {
            treeObject.interact("Chop down");
            log("Chopping " + tree.toString());
            new ConditionalSleep(5000) {
                @Override
                public boolean condition() {
                    return myPlayer().isAnimating() || !treeObject.exists();
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

//Sleep class from OSBot forums - not used. Only present in this script for reference.
final class Sleep extends ConditionalSleep {

    private final BooleanSupplier condition;

    public Sleep(final BooleanSupplier condition, final int timeout) {
        super(timeout);
        this.condition = condition;
    }

    @Override
    public final boolean condition() throws InterruptedException {
        return condition.getAsBoolean();
    }

    public static boolean sleepUntil(final BooleanSupplier condition, final int timeout) {
        return new Sleep(condition, timeout).sleep();
    }
}

//GUI for tree selection
final class GUIWC {
    private final JDialog mainDialog;
    private final JComboBox<Tree> treeSelector;

    private boolean started;

    public GUIWC() {
        mainDialog = new JDialog();
        mainDialog.setTitle("Nightfrost's Woodcutter");
        mainDialog.setModal(true);
        mainDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainDialog.getContentPane().add(mainPanel);

        JPanel treeSelectionPanel = new JPanel();
        treeSelectionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel treeSelectionLabel = new JLabel("Select tree:");
        treeSelectionPanel.add(treeSelectionLabel);

        treeSelector = new JComboBox<>(Tree.values());
        treeSelectionPanel.add(treeSelector);

        mainPanel.add(treeSelectionPanel);

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

    public Tree getSelectedTree() {
        return (Tree) treeSelector.getSelectedItem();
    }

    public void open() {
        mainDialog.setVisible(true);
    }

    public void close() {
        mainDialog.setVisible(false);
        mainDialog.dispose();
    }
}

//Enum of different types of trees.
enum Tree {
    Tree,
    Oak,
    Willow,
    Teak,
    Maple,
    Mahogany,
    Yew,
    Magic;

    @Override
    public String toString() {
        return name();
    }
}

