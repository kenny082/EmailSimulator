import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.text.SimpleDateFormat;

/**
 * Mailbox class represents an email box and contains all the folders along with the inbox and trash
 *
 * @author Kenny
 **/

public class Mailbox implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final Folder inbox;
    private final Folder trash;
    private final ArrayList<Folder> folders;
    public static Mailbox mailbox;

    /**
     * Private constructor and initializes the default folders of Inbox, Trash, and any custom folders
     */
    private Mailbox() {
        this.inbox = new Folder("Inbox");
        this.trash = new Folder("Trash");
        this.folders = new ArrayList<>();
    }

    /**
     * Initializes mailbox if a saved file of "mailbox.obj" exist, otherwise create a new mailbox instance
     */
    public static void initializeMailbox() {
        if (mailbox == null) {
            File file = new File("mailbox.obj");
            if (file.exists()) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                    mailbox = (Mailbox) ois.readObject();
                    System.out.println("Mailbox loaded from previous save.");
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Error loading mailbox: " + e.getMessage());
                    mailbox = new Mailbox();
                }
            } else {
                System.out.println("Previous save not found, starting with an empty mailbox.");
                mailbox = new Mailbox();
            }
        }
    }

    /**
     * Adds a new folder to the mailbox if the folder does not already exist / not duplicate
     * @param folder the folder to be added
     */
    public void addFolder(Folder folder) {
        if (getFolder(folder.getName()) == null) {
            folders.add(folder);
            System.out.println("Folder added: " + folder.getName());
        } else {
            System.out.println("Error: Folder with this name already exists.");
        }
    }

    /**
     * Removes a folder from the mailbox if it exists and is located within a custom folder (not in inbox/trash)
     * @param folderName the name of the folder to delete
     */
    public void removeFolder(String folderName) {
        // Check if the folder is "Inbox" or "Trash"
        if (folderName.equalsIgnoreCase("Inbox") || folderName.equalsIgnoreCase("Trash")) {
            System.out.println("Error: You cannot delete the " + folderName + " folder.");
            return;
        }

        Folder folderToRemove = getFolder(folderName);
        if (folderToRemove != null) {
            folders.remove(folderToRemove);
            System.out.println(folderName + " has been successfully deleted.");
        } else {
            System.out.println("Error: Folder not found.");
        }
    }

    /**
     * Composes a new email and adds it to the inbox by prompting for basic email information
     */
    public void composeEmail() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter recipient (To): ");
        String to = scanner.nextLine();
        System.out.print("Enter carbon copy recipients (CC): ");
        String cc = scanner.nextLine();
        System.out.print("Enter blind carbon copy recipients (BCC): ");
        String bcc = scanner.nextLine();
        System.out.print("Enter subject line: ");
        String subject = scanner.nextLine();
        System.out.print("Enter body: ");
        String body = scanner.nextLine();

        Email email = new Email(to, cc, bcc, subject, body);
        inbox.addEmail(email);
    }

    /**
     * Deletes an email by moving it to the Trash folder
     * @param email the email to be deleted
     */
    public void deleteEmail(Email email) {
        Folder folder = findFolderContainingEmail(email); // Find the folder containing the email
        // Checking if folder was found
        if (folder != null) {
            // Find index of the email within the folder
            int index = folder.getEmails().indexOf(email);
            // Checking if email was found within the folder
            if (index != -1) {
                // If found, remove the email from the folder
                Email removedEmail = folder.removeEmail(index); // Remove email by index
                // Check if email was successfully removed
                if (removedEmail != null) {
                    // If successfully removed, then add email to trash
                    trash.addEmail(removedEmail);
                    System.out.println("\"" + removedEmail.getSubject() + "\" has successfully been moved to the trash.");
                } else {
                    System.out.println("Error: Email not found in " + folder.getName() + ".");
                }
            } else {
                System.out.println("Error: Email not found in " + folder.getName() + ".");
            }
        } else {
            System.out.println("Error: Email not found.");
        }
    }

    /**
     * Clears all the emails from the Trash folder and displays the number of items cleared
     */
    public void clearTrash() {
        int numberOfEmails = trash.getEmails().size(); // Count the number of emails before clearing

        if (numberOfEmails > 0) {
            trash.getEmails().clear(); // Clear the trash
            System.out.println(numberOfEmails + " item(s) successfully deleted.");
        } else {
            System.out.println("Trash folder is empty. There is nothing to delete.");
        }
    }

    /**
     * Moves an email to a specified folder
     * @param email the email to be moved
     * @param target the folder to which the email should be moved to
     */
    public void moveEmail(Email email, Folder target) {
        Folder currentFolder = findFolderContainingEmail(email);

        if (currentFolder == null) {
            System.out.println("Error: Email not found.");
            return;
        }

        // Remove the email from its current folder
        currentFolder.removeEmail(currentFolder.getEmails().indexOf(email));

        // Add the email to the target folder
        target.addEmail(email);

        // Confirmation message
        System.out.println("\"" + email.getSubject() + "\" successfully moved to " + target.getName() + ".");
    }

    /**
     * Retrieves a holder by its name
     * @param name the name of the folder to retrieve
     * @return the folder with the specified name or null if not found
     */
    public Folder getFolder(String name) {
        if (name.equalsIgnoreCase("Inbox")) {
            return inbox;
        }
        if (name.equalsIgnoreCase("Trash")) {
            return trash;
        }
        // Iterate through custom folders to search for name
        for (Folder folder : folders) {
            if (folder.getName().equalsIgnoreCase(name)) {
                return folder;
            }
        }
        return null;
    }

    /**
     * Saves the current state of the mailbox to file (mailbox.obj file) to load on the next run of program
     */
    public void saveMailbox() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("mailbox.obj"))) {
            oos.writeObject(mailbox);
            System.out.println("Mai.lbox saved");
        } catch (IOException e) {
            System.out.println("Error saving mailbox: " + e.getMessage());
        }
    }

    /**
     * Main method to run the application
     * Note: Assuming we are putting the main method in Mailbox (from what was interpreted in the instructions)
     * @param args command-line arguments (not used in this program)
     */
    public static void main(String[] args) {
        initializeMailbox();
        Scanner scanner = new Scanner(System.in);

        // Keeps printing available folders
        while (true) {
            System.out.println("\nMailbox:");
            System.out.println("--------");
            System.out.println("Inbox");
            System.out.println("Trash");
            for (Folder folder : mailbox.folders) {
                System.out.println(folder.getName());
            }
            // List of choices
            System.out.println(); // Cleaner output, better readability
            System.out.println("A – Add folder");
            System.out.println("R – Remove folder");
            System.out.println("C – Compose email");
            System.out.println("F – View folder");
            System.out.println("I – View Inbox");
            System.out.println("T – View Trash");
            System.out.println("E – Empty Trash");
            System.out.println("Q – Quit");
            System.out.print("Enter a user option: ");
            String option = scanner.nextLine().toUpperCase();

            // Switch case for different choices
            switch (option) {
                case "A":
                    System.out.print("Enter folder name: ");
                    String folderName = scanner.nextLine();
                    mailbox.addFolder(new Folder(folderName));
                    break;
                case "R":
                    System.out.print("Enter folder name: ");
                    folderName = scanner.nextLine();
                    mailbox.removeFolder(folderName);
                    break;
                case "C":
                    mailbox.composeEmail();
                    break;
                case "F":
                    System.out.print("Enter folder name: ");
                    folderName = scanner.nextLine();
                    Folder folder = mailbox.getFolder(folderName);
                    if (folder != null) {
                        handleFolderMenu(folder);
                    } else {
                        System.out.println("Folder not found.");
                    }
                    break;
                case "I":
                    handleFolderMenu(mailbox.inbox);
                    break;
                case "T":
                    handleFolderMenu(mailbox.trash);
                    break;
                case "E":
                    mailbox.clearTrash();
                    break;
                case "Q":
                    mailbox.saveMailbox();
                    System.out.println("Program successfully exited and mailbox saved.");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    /**
     * Handles the menu for specific folders and allows user to perform actions within them
     * @param folder the folder to be managed
     */
    private static void handleFolderMenu(Folder folder) {
        Scanner scanner = new Scanner(System.in);
        // SimpleDateFormat is used to make the date look cleaner (as shown in sample output)
        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mma M/d/yyyy");

        // Continuously print header
        while (true) {
            System.out.println(folder.getName());
            System.out.println("Index |        Time       | Subject");
            System.out.println("-----------------------------------");

            // Check if folder is empty -> If empty then just prompt user to return to the main menu
            // Since it is empty then there is nothing to do within the submenu / custom folder, return to the main menu
            if (folder.getEmails().isEmpty()) {
                System.out.println("The folder is empty.");
                System.out.println("R – Return to mailbox");
                System.out.print("Enter a user option: ");
                String option = scanner.nextLine().toUpperCase();

                if ("R".equals(option)) {
                    return;
                } else {
                    System.out.println("Folder is empty, please return to main mailbox by typing 'R'");
                }
                continue;
            }

            int index = 1;
            for (Email email : folder.getEmails()) {
                String formattedDate = dateFormat.format(email.getTimestamp().getTime());
                System.out.printf("%d   |  %s  | %s\n", index++, formattedDate, email.getSubject());
            }
            // Print list of options
            System.out.println();
            System.out.println("M – Move email");
            System.out.println("D – Delete email");
            System.out.println("V – View email contents");
            System.out.println("SA – Sort by subject line in ascending order");
            System.out.println("SD – Sort by subject line in descending order");
            System.out.println("DA – Sort by date in ascending order");
            System.out.println("DD – Sort by date in descending order");
            System.out.println("R – Return to mailbox");
            System.out.print("Enter a user option: ");
            String option = scanner.nextLine().toUpperCase();

            switch (option) {
                case "M":
                    System.out.print("Enter the index of the email to move: ");
                    int emailIndex;
                    try {
                        emailIndex = Integer.parseInt(scanner.nextLine()) - 1;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a valid integer index.");
                        break; // Return to the loop without proceeding further
                    }
                    if (emailIndex >= 0 && emailIndex < folder.getEmails().size()) {
                        Email email = folder.getEmails().get(emailIndex);
                        System.out.println("Folders:");
                        System.out.println("Inbox");
                        System.out.println("Trash");
                        for (Folder f : mailbox.folders) {
                            System.out.println(f.getName());
                        }
                        System.out.print("Select a folder to move \"" + email.getSubject() + "\" to: ");
                        String targetFolderName = scanner.nextLine();
                        Folder targetFolder = mailbox.getFolder(targetFolderName);
                        if (targetFolder != null) {
                            mailbox.moveEmail(email, targetFolder);
                        } else {
                            System.out.println("Folder not found. Moving email to Inbox.");
                            mailbox.moveEmail(email, mailbox.inbox);
                        }
                    } else {
                        System.out.println("Invalid email index.");
                    }
                    break;
                case "D":
                    System.out.print("Enter email index: ");
                    try {
                        emailIndex = Integer.parseInt(scanner.nextLine()) - 1;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a valid integer index.");
                        break; // Return to the loop without proceeding further
                    }
                    if (emailIndex >= 0 && emailIndex < folder.getEmails().size()) {
                        Email email = folder.getEmails().get(emailIndex);
                        mailbox.deleteEmail(email);
                    } else {
                        System.out.println("Invalid email index.");
                    }
                    break;
                case "V":
                    System.out.print("Enter email index: ");
                    try {
                        emailIndex = Integer.parseInt(scanner.nextLine()) - 1;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a valid integer index.");
                        break; // Return to the loop without proceeding further
                    }
                    if (emailIndex >= 0 && emailIndex < folder.getEmails().size()) {
                        Email email = folder.getEmails().get(emailIndex);
                        System.out.println("To: " + email.getTo());
                        System.out.println("CC: " + email.getCc());
                        System.out.println("BCC: " + email.getBcc());
                        System.out.println("Subject: " + email.getSubject());
                        System.out.println("Body: " + email.getBody());
                    } else {
                        System.out.println("Invalid email index.");
                    }
                    break;
                case "SA":
                    folder.sortBySubjectAscending();
                    break;
                case "SD":
                    folder.sortBySubjectDescending();
                    break;
                case "DA":
                    folder.sortByDateAscending();
                    break;
                case "DD":
                    folder.sortByDateDescending();
                    break;
                case "R":
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    /**
     * Helper Function to find the folder containing a specific email
     * @param email the email to find
     * @return the folder containing the email or null if not found
     */
    private Folder findFolderContainingEmail(Email email) {
        if (inbox.getEmails().contains(email)) {
            return inbox;
        }
        if (trash.getEmails().contains(email)) {
            return trash;
        }
        for (Folder folder : folders) {
            for (Email e : folder.getEmails()) {
                if (e.equals(email)) {
                    return folder;
                }
            }
        }
        return null;
    }

    // Additional getter for GUI
    public Folder getInbox() {
        return inbox;
    }
}