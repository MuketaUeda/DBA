package pr2mapAgent;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.ContainerController;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) {
        JFileChooser fileChooser = new JFileChooser();

        File defaultDirectory = new File("maps"); // Replace with your desired directory
        if (defaultDirectory.exists() && defaultDirectory.isDirectory()) {
            fileChooser.setCurrentDirectory(defaultDirectory);
        } else {
            // Fallback to user's home directory if the specified directory doesn't exist
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        }

        fileChooser.setDialogTitle("Choose a map file");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            try {
                //if a map is chosen
                Map map = new Map(selectedFile.getAbsolutePath());
                Environment env = new Environment(map.getWidth(), map.getHeight(), map);

                GridLayoutManager glm = new GridLayoutManager(map);

                while (!glm.positionsSet()) {
                    try {
                        Thread.sleep(1); // Wait for 1 millisecond
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt(); // Handle interrupt
                        break;
                    }
                }

                System.out.println("Start [" + glm.getStartPos()[0] + ", " + glm.getStartPos()[1] + "]");
                System.out.println("Target [" + glm.getEndPos()[0] + ", " + glm.getEndPos()[1] + "]");

                Object[] arguments = {glm.getStartPos(), glm.getEndPos(), env, glm};
                Scout raccoon = new Scout();

                Runtime jadeRuntime = Runtime.instance();
                Profile profile = new ProfileImpl();
                ContainerController mainContainer = jadeRuntime.createMainContainer(profile);

                raccoon.startAgent(arguments, mainContainer);

            }
            catch (Exception e) {
                // Handle exceptions such as file not found or invalid file format
                JOptionPane.showMessageDialog(null, "An error occurred while processing the map file:\n" + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }

        } else {
            // if the user chancel the choice the program is finished
            System.out.println("No file has been selected.");
            System.exit(0);
        }
    }
}