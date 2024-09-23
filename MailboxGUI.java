import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.io.*;

/**
 * MailboxGUI class creates the graphical user interface (GUI) for the mailbox simulation.
 * It allows users to view, delete, and move emails between folders.
 *
 * @author Kenny
 */

public class MailboxGUI extends JFrame {
    private final JTable emailTable; // A table that will display emails
    private final EmailTableModel emailTableModel; // Custom table defines how email data is displayed in JTable

    private boolean sortAscending = true; // Sorting algorithms
    private String sortBy = "Date"; // Default sort by "Date"

    public MailboxGUI(Mailbox mailbox, Folder folder) {
        this.emailTableModel = new EmailTableModel(folder.getEmails()); // Initializes custom table with a list of email
        this.emailTable = new JTable(emailTableModel); // Creates the JTable using a custom table model

        // Set up JFrame
        setTitle("Mailbox"); // Sets the title to Mailbox
        setSize(800, 600); // Just the size of the JFrame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Terminates upon hitting the "x" button to close program
        setLayout(new BorderLayout()); // Defines a grid and locations (north/south/west/east/center)

        // Set up table
        emailTable.setAutoCreateRowSorter(true); // Enables the automatic sorting functionality built in
        JScrollPane scrollPane = new JScrollPane(emailTable); // Allows scrolling in the event we have many emails
        add(scrollPane, BorderLayout.CENTER); // Centered

        // Add buttons
        JPanel buttonPanel = new JPanel(); // Creates a panel that holds the buttons
        JButton deleteButton = new JButton("Delete");
        JButton moveButton = new JButton("Move");
        buttonPanel.add(deleteButton); // Adds the deleted button to the panel
        buttonPanel.add(moveButton); // Adds the move button to the panel
        add(buttonPanel, BorderLayout.SOUTH);

        // Header click listeners for sorting
        JTableHeader header = emailTable.getTableHeader(); // Grabs the column names
        header.addMouseListener(new MouseAdapter() { // Mouse listener to detect clicks on header
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = emailTable.columnAtPoint(e.getPoint()); // Gets the column index that was clicked
                String columnName = emailTable.getColumnName(column); // Gets the name of the clicked column
                // Set sorting algorithm based on what column name was clicked
                if (columnName.equals("Subject")) {
                    sortBy = "Subject";
                } else if (columnName.equals("Date")) {
                    sortBy = "Date";
                }
                sortAscending = !sortAscending;
                sortTable();
            }
        });

        // Delete button action
        deleteButton.addActionListener(e -> {
            int selectedRow = emailTable.getSelectedRow(); // Gets the currently selected row in the table
            if (selectedRow >= 0) {
                Email email = emailTableModel.getEmailAt(selectedRow); // Get the email at the selected row
                mailbox.deleteEmail(email); // Delete the email from the mailbox
                emailTableModel.removeEmail(email); // Remove the email from the table model (refresh the table)
                saveMailbox(); // Save the mailbox after deleting the email
            } else {
                // Show an error message if no row is selected
                JOptionPane.showMessageDialog(this, "No email selected to delete.");
            }
        });

        // Move button action
        moveButton.addActionListener(e -> {
            int selectedRow = emailTable.getSelectedRow(); // Gets the currently selected row in the table
            if (selectedRow >= 0) { // If a row is selected
                Email email = emailTableModel.getEmailAt(selectedRow); // Get the email at the selected row
                // Prompt user for target folder name
                String targetFolderName = JOptionPane.showInputDialog(this, "Enter target folder name:");
                if (targetFolderName != null && !targetFolderName.trim().isEmpty()) { // If a valid folder name is provided
                    Folder targetFolder = mailbox.getFolder(targetFolderName); // Get the folder object for the provided name
                    if (targetFolder != null) { // If the folder exists
                        mailbox.moveEmail(email, targetFolder); // Move the email to the target folder
                        emailTableModel.removeEmail(email); // Remove the email from the table model (refresh the table)
                        saveMailbox(); // Save the mailbox state after moving the email
                    } else {
                        // Show an error message if the folder is not found
                        JOptionPane.showMessageDialog(this, "Folder not found.");
                    }
                } else {
                    // Show an error message if the folder name is invalid
                    JOptionPane.showMessageDialog(this, "Invalid folder name.");
                }
            } else {
                // Show an error message if no row is selected
                JOptionPane.showMessageDialog(this, "No email selected to move.");
            }
        });

        setVisible(true); // Make the GUI visible / show up to user on the computer screen
    }

    /**
     * Saves the mailbox state to "mailbox.obj" so that moving/deleting emails in GUI reflects actual file
     * Note: This was not stated in instructions but was requested during office hours (implementing to be safe)
     */
    private void saveMailbox() {
        try (FileOutputStream fos = new FileOutputStream("mailbox.obj");
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(Mailbox.mailbox); // Save the mailbox state
            System.out.println("Mailbox saved.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Sorts the table based on the current sorting method
     */
    private void sortTable() {
        TableRowSorter<EmailTableModel> sorter = (TableRowSorter<EmailTableModel>) emailTable.getRowSorter();
        // Sort by either date or subject
        if (sortBy.equals("Date")) {
            // Swing class sorter that sorts the rows and takes in a custom sorting method (ascending/descending)
            // SingletonList is used to ensure that when sorting, it gets a proper list of keys
            sorter.setSortKeys(Collections.singletonList
                    // Sorts the column index 1 or date
                    // If true then sort by ascending, otherwise sort by descending
                            (new RowSorter.SortKey(1, sortAscending ? SortOrder.ASCENDING : SortOrder.DESCENDING)));
        } else if (sortBy.equals("Subject")) {
            // Swing class sorter that sorts the rows and takes in a custom sorting method (ascending/descending)
            // SingletonList is used to ensure that when sorting, it gets a proper list of keys
            sorter.setSortKeys(Collections.singletonList
                    // Sorts column index 0 or subject
                    // If true then sort by ascending, otherwise sort by descending
                            (new RowSorter.SortKey(0, sortAscending ? SortOrder.ASCENDING : SortOrder.DESCENDING)));
        }
    }

    /**
     * Main method to run MailboxGUI
     * @param args command-line arguments (not used here)
     */
    public static void main(String[] args) {
        Mailbox.initializeMailbox(); // Initialize the mailbox
        Mailbox mailbox = Mailbox.mailbox; // Get the instance

        if (mailbox != null) {
            Folder folder = mailbox.getInbox();
            if (folder != null) {
                new MailboxGUI(mailbox, folder); // Pass mailbox and inbox folder to GUI
            } else {
                System.out.println("Error: Inbox folder not found.");
            }
        } else {
            System.out.println("Error: Mailbox not initialized.");
        }
    }

    /**
     * Custom table model for displaying emails in a JTable
     */
    private static class EmailTableModel extends AbstractTableModel {
        private final String[] columnNames = {"Subject", "Date"}; // Column names for the table
        private final List<Email> emails; // List to store email objects

        /**
         * Constructor for EmailTableModel. Initializes the list of emails.
         * @param emails the list of emails to display.
         */
        public EmailTableModel(List<Email> emails) {
            this.emails = new ArrayList<>(emails);
        }

        /**
         * Gets the number of rows in the table
         * Overrides the getRowCount method from AbstractTableModel
         * @return the number of rows in the table which is just the number of emails
         */
        @Override
        public int getRowCount() {
            return emails.size();
        }

        /**
         * Gets the number of columns in the table
         * Overrides the getColumnCount method from AbstractTableModel
         * @return the number of columns in the table which is just the column names (subject/date; so it is always 2)
         */
        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        /**
         * Gets the value at a specified row and column index
         * Overrides the getValueAt method in AbstractTableModel
         * @param rowIndex the row whose value is to be queried
         * @param columnIndex the column whose value is to be queried
         * @return returns the data at the specific row and column index
         */
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Email email = emails.get(rowIndex); // Retrieve email at the specified row index
            return switch (columnIndex) {
                case 0 -> email.getSubject(); // Return email if column index is 0
                // Return date format if column index is 1 (SimpleDateFormat is used to make format cleaner)
                case 1 -> new SimpleDateFormat("h:mma M/d/yyyy").format(email.getTimestamp().getTime());
                // Otherwise return null if column index out of bound
                default -> null;
            };
        }

        /**
         * Returns the name of the column based on the column index
         * Overrides the getColumnName method in AbstractTableModel
         * @param column the column being queried
         * @return the name of the column at the index
         */
        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        /**
         * Retrieve the email object at a specified row index
         * @param rowIndex the index of the row to retrieve the email at
         * @return the email at the specified row
         */
        public Email getEmailAt(int rowIndex) {
            return emails.get(rowIndex);
        }

        /**
         * Removes the specified email
         * @param email the name of the email to be removed
         */
        public void removeEmail(Email email) {
            emails.remove(email);
            // fireTableDataChanged is a method from AbstractTableModel
            // It notifies that the table has been changed (as we removed an email) and will refresh / update the table
            fireTableDataChanged();
        }
    }
}