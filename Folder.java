import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Folder class represents an email folder containing the list of emails and the name of the folder
 *
 * @author Kenny
 **/

public class Folder implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private ArrayList<Email> emails;
    private String name;
    private String currentSortingMethod;

    /**
     * Constructor for folder with a given name and sets default sorting method to date descending.
     * @param name the name of the folder
     */
    public Folder(String name) {
        this.emails = new ArrayList<>();
        this.name = name;
        this.currentSortingMethod = "dateDescending";
    }

    // Getter and setter for emails
    public ArrayList<Email> getEmails() {
        return emails;
    }

    public void setEmails(ArrayList<Email> emails) {
        this.emails = emails;
    }

    // Getter and setter for name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and setter for sorting method
    public String getCurrentSortingMethod() {
        return currentSortingMethod;
    }

    public void setCurrentSortingMethod(String currentSortingMethod) {
        this.currentSortingMethod = currentSortingMethod;
    }

    /**
     * Adds an email to the folder according to the current sorting method
     * @param email the email to add
     */
    public void addEmail(Email email) {
        emails.add(email);
        System.out.println("Email added successfully.");
    }

    /**
     * Removes an email from the folder by index
     * @param index the index of the email to remove
     * @return the removed email
     */
    public Email removeEmail(int index) {
        // Check index is not less than zero or greater than the size of the list
        if (index >= 0 && index < emails.size()) {
            return emails.remove(index);
        }
        return null; // Return null if index is invalid
    }

    // Sorting methods using comparators

    /**
     * Sorts emails alphabetically by subject in ascending order.
     */
    public void sortBySubjectAscending() {
        emails.sort(Comparator.comparing(Email::getSubject));
        currentSortingMethod = "subjectAscending";
    }

    /**
     * Sorts emails alphabetically by subject in descending order.
     */
    public void sortBySubjectDescending() {
        // Reverse comparison order for descending order; method is compare(T o1, T o2) -> compare(T o2, T o1)
        emails.sort((e1, e2) -> e2.getSubject().compareTo(e1.getSubject()));
        currentSortingMethod = "subjectDescending";
    }

    /**
     * Sorts emails by date in ascending order.
     */
    public void sortByDateAscending() {
        emails.sort(Comparator.comparing(Email::getTimestamp));
        currentSortingMethod = "dateAscending";
    }

    /**
     * Sorts emails by date in descending order.
     */
    public void sortByDateDescending() {
        // Reverse comparison order for descending order; method is compare(T o1, T o2) -> compare(T o2, T o1)
        emails.sort((e1, e2) -> e2.getTimestamp().compareTo(e1.getTimestamp()));
        currentSortingMethod = "dateDescending";
    }
}