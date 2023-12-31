package brad.tillmann;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.swing.filechooser.FileFilter;

// TODO: Clean up this code lol
// TODO: We need to save all of our mod pack definitions to the application data folder. Likely a folder containing each pack as a separate json.
// TODO: We currently have no way to actually search for individual mods & build a modpack within the UI.
public class ModPacksForm {
    private JPanel panel1;
    private JList<LCModPack> modPackList;
    private JButton newModPackButton; // TODO: Implement once we implement mod pack editing
    private JButton importModPackButton;
    private JButton exportModPackButton;
    private JButton removeModPackButton; // TODO: Implement once we implement mod pack persistence (i.e. saving what packs we have so we dont need to import them every time)
    private JButton installModPackButton;
    private JButton uninstallModsButton;
    private Map<String, LCModPack> modPacksByName;

    public ModPacksForm() throws IOException {
        modPacksByName = new HashMap<>();
        importModPackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                fileChooser.addChoosableFileFilter(new FileFilter() {
                    public String getDescription() {
                        return "JSON Files (*.json)";
                    }

                    public boolean accept(File f) {
                        if (f.isDirectory()) {
                            return false;
                        } else {
                            return f.getName().toLowerCase().endsWith(".json");
                        }
                    }
                });

                int result = fileChooser.showOpenDialog(panel1);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    LCModPack modPack = null;

                    try {
                        modPack = LCModPack.fromFile(selectedFile);
                    } catch (IOException ex)
                    {
                        JOptionPane.showMessageDialog(
                                new JFrame(),
                                "Could not load the selected file. Please select a valid mod pack json file.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }

                    if(modPack != null)
                    {
                        // If a mod pack with the same name already exists, replace it
                        if(modPacksByName.containsKey(modPack.getName()))
                            modPacksByName.replace(modPack.getName(), modPack);
                        else
                            modPacksByName.put(modPack.getName(), modPack);

                        Vector<LCModPack> modPackListElements = new Vector<>();
                        modPacksByName.values().forEach(modPackListElements::addElement);
                        modPackList.setListData(modPackListElements);
                    }
                }
            }
        });
        exportModPackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LCModPack selectedModPack = (LCModPack) modPackList.getSelectedValue();
                if(selectedModPack != null)
                {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle("Save as");
                    fileChooser.addChoosableFileFilter(new FileFilter() {
                        public String getDescription() {
                            return "JSON Files (*.json)";
                        }

                        public boolean accept(File f) {
                            if (f.isDirectory()) {
                                return true;
                            } else {
                                return f.getName().toLowerCase().endsWith(".json");
                            }
                        }
                    });

                    int userSelection = fileChooser.showSaveDialog(panel1);
                    if (userSelection == JFileChooser.APPROVE_OPTION) {
                        File fileToSave = fileChooser.getSelectedFile();
                        try {
                            selectedModPack.toFile(fileToSave);
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(
                                    new JFrame(),
                                    "An error occurred while saving the mod pack json file.",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });
        uninstallModsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LCModPack selectedModPack = (LCModPack) modPackList.getSelectedValue();

                if(selectedModPack == null)
                {
                    JOptionPane.showMessageDialog(
                            new JFrame(),
                            "No mod pack is selected!",
                            "Notification",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                //TODO: Check if this modpack is actually installed using persistent config
                LCModManager.getInstance().uninstallModPack(selectedModPack);

                JOptionPane.showMessageDialog(
                        new JFrame(),
                        String.format("Modpack uninstalled!", selectedModPack.getModDescriptors().size()),
                        "Notification",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
        installModPackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LCModPack selectedModPack = (LCModPack) modPackList.getSelectedValue();

                if(selectedModPack == null)
                {
                    JOptionPane.showMessageDialog(
                            new JFrame(),
                            "No mod pack is selected!",
                            "Notification",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                try {
                    // TODO: If a mod pack is already installed, uninstall it first.
                    LCModManager.getInstance().installModPack(selectedModPack);

                    // Lets remember what's installed
                    PersistentConfig.getInstance().setValue("InstalledModpack.Name", selectedModPack.getName());
                    PersistentConfig.getInstance().setValue("InstalledModPack.Author", selectedModPack.getAuthor());
                    PersistentConfig.getInstance().setValue("InstalledModPack.Version", selectedModPack.getVersion());
                    PersistentConfig.getInstance().flush();

                    JOptionPane.showMessageDialog(
                            new JFrame(),
                            String.format("%d mods installed!", selectedModPack.getModDescriptors().size()),
                            "Notification",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex)
                {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(
                            new JFrame(),
                            String.format("Something went wrong!", selectedModPack.getModDescriptors().size()),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void createUIComponents() {

    }
}
