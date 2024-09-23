import java.io.Serial;
import java.io.Serializable;
import java.util.GregorianCalendar;

/**
 * Email class contains the basic information of a standard email (to,cc,bcc,subject, timestamp)
 * This class implements Serializable to allow object serialization
 *
 @author Kenny
 **/
public class Email implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String to;
    private String cc;
    private String bcc;
    private String subject;
    private String body;
    private GregorianCalendar timestamp;

    /**
     * Constructs a new Email with the specified to, cc, bcc, subject, and body.
     * The timestamp is set to the current date and time.
     * @param to the recipient's email address
     * @param cc the cc recipient's email address
     * @param bcc the bcc recipient's email address
     * @param subject the subject of the email
     * @param body the body text of the email
     */
    public Email(String to, String cc, String bcc, String subject, String body) {
        this.to = to;
        this.cc = cc;
        this.bcc = bcc;
        this.subject = subject;
        this.body = body;
        this.timestamp = new GregorianCalendar();
    }

    // Getter and setter for recipient's email address.
    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    // Getter and setter for cc recipient's email address.
    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    // Getter and setter for bcc recipient's email address
    public String getBcc() {
        return bcc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    // Getter and setter of the subject
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    // Getter and setter for body text of the email
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    // Getter and setter for timestamp of when the email was created
    public GregorianCalendar getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(GregorianCalendar timestamp) {
        this.timestamp = timestamp;
    }
}