package ua.kiev.prog;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.List;

// c -> s -> r -> DB

@Service
public class ContactService {

    private final ContactRepository contactRepository;
    private final GroupRepository groupRepository;

    public ContactService(ContactRepository contactRepository,
                          GroupRepository groupRepository) {
        this.contactRepository = contactRepository;
        this.groupRepository = groupRepository;
    }

    @Transactional
    public void addContact(Contact contact) {
        contactRepository.save(contact);
    }

    @Transactional
    public void addGroup(Group group) {
        groupRepository.save(group);
    }

    @Transactional
    public void deleteContacts(long[] idList) {
        for (long id : idList)
            contactRepository.deleteById(id);
    }

    @Transactional(readOnly=true)
    public List<Group> findGroups() {
        return groupRepository.findAll();
    }

    @Transactional(readOnly=true)
    public List<Contact> findAll(Pageable pageable) {
        return contactRepository.findAll(pageable).getContent();
    }

    @Transactional
    public void deleteById(Long group_id){
        groupRepository.deleteById(group_id);
    }

    @Transactional
    public void moveToOtherGroup(long[] user_id, long group_id){
        groupRepository.moveToOtherGroup(user_id,group_id);
    }

    @Transactional(readOnly=true)
    public List<Contact> findByGroup(Group group, Pageable pageable) {
        return contactRepository.findByGroup(group, pageable);
    }

    @Transactional(readOnly = true)
    public long countByGroup(Group group) {
        return contactRepository.countByGroup(group);
    }

    @Transactional(readOnly=true)
    public List<Contact> findByPattern(String pattern, Pageable pageable) {
        return contactRepository.findByPattern(pattern, pageable);
    }

    @Transactional(readOnly = true)
    public long countByPattern(String pattern) {
        return contactRepository.countByPattern(pattern);
    }

    @Transactional
    public void addContactByXML(MultipartFile multipartFile)   {
        File xmlFile = null;
        try {
            xmlFile = File.createTempFile("upload_", "_" + multipartFile.getOriginalFilename());
            multipartFile.transferTo(xmlFile);
            if (xmlFile == null) {
                System.out.println("checkPoint service");
                return;
            }
            JAXBContext jaxbContext = JAXBContext.newInstance(ContactsWrapper.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            ContactsWrapper contactList = (ContactsWrapper) unmarshaller.unmarshal(xmlFile);
            List<Contact> contacts = contactList.getContacts();

            contactRepository.saveAll(contacts);
        } catch (IOException | JAXBException e) {
            e.printStackTrace();
        }
        xmlFile.delete();
    }

    @Transactional(readOnly = true)
    public long count() {
        return contactRepository.count();
    }

    @Transactional(readOnly=true)
    public Group findGroup(long id) {
        return groupRepository.findById(id).get();
    }

    @Transactional
    public void reset() {
        groupRepository.deleteAll();
        contactRepository.deleteAll();

        Group group = new Group("Test");
        Contact contact;

        addGroup(group);

        for (int i = 0; i < 13; i++) {
            contact = new Contact(null, "Name" + i, "Surname" + i, "1234567" + i, "user" + i + "@test.com");
            addContact(contact);
        }
        for (int i = 0; i < 10; i++) {
            contact = new Contact(group, "Other" + i, "OtherSurname" + i, "7654321" + i, "user" + i + "@other.com");
            addContact(contact);
        }
    }
}
