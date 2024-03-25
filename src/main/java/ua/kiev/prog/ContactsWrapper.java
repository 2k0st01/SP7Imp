package ua.kiev.prog;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "contacts")
public class ContactsWrapper {
    private List<Contact> contact;

    @XmlElement(name = "contact")
    public List<Contact> getContacts() {
        return contact;
    }

    public void setContacts(List<Contact> contact) {
        this.contact = contact;
    }
}
