package ua.kiev.prog;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.List;

@Controller
public class MyController {
    static final int DEFAULT_GROUP_ID = -1;
    static final int ITEMS_PER_PAGE = 6;
    private String pattern = null;
    private final ContactService contactService;
    private final ContactRepository contactRepository;

    public MyController(ContactService contactService, ContactRepository contactRepository) {
        this.contactService = contactService;
        this.contactRepository = contactRepository;
    }

    @GetMapping("/")
    public String index(Model model,
                        @RequestParam(required = false, defaultValue = "0") Integer page) {
        if (page < 0) page = 0;

        List<Contact> contacts = contactService
                .findAll(PageRequest.of(page, ITEMS_PER_PAGE, Sort.Direction.DESC, "id"));

        model.addAttribute("groups", contactService.findGroups());
        model.addAttribute("deleteByGroups", contactService.findGroups());
        model.addAttribute("contacts", contacts);
        model.addAttribute("allPages", getPageCount());

        return "index";
    }


    @PostMapping(value = "/search")
    public String postSearch(@RequestParam String pattern,
                             @RequestParam(required = false, defaultValue = "0") Integer page,
                             Model model) {
        if (page < 0) page = 0;
        this.pattern = pattern;
        model.addAttribute("groups", contactService.findGroups());
        model.addAttribute("contacts", contactService.findByPattern(
                pattern, PageRequest.of(page, ITEMS_PER_PAGE, Sort.Direction.DESC, "id")));
        model.addAttribute("searchPages", getPageCount(pattern));

        return "index";
    }

    @GetMapping(value = "/search")
    public String getSearch(@RequestParam(required = false, defaultValue = "0") Integer page,
                            Model model) {
        if (page < 0) page = 0;
        model.addAttribute("groups", contactService.findGroups());
        model.addAttribute("contacts", contactService.findByPattern(
                pattern, PageRequest.of(page, ITEMS_PER_PAGE, Sort.Direction.DESC, "id")));
        model.addAttribute("searchPages", getPageCount(pattern));

        return "index";
    }

    @GetMapping("/reset")
    public String reset() {
        contactService.reset();
        return "redirect:/";
    }

    @PostMapping(value = "/contact/add", consumes = "application/xml")
    public ResponseEntity<String> addContactFromXml(@RequestBody Contact contactDTO) {

        contactService.addContact(contactDTO);
        return new ResponseEntity<>("Contact added successfully", HttpStatus.CREATED);
    }

    @GetMapping("/contact_add_page")
    public String contactAddPage(Model model) {
        model.addAttribute("groups", contactService.findGroups());
        return "contact_add_page";
    }

    @GetMapping("/group_add_page")
    public String groupAddPage() {
        return "group_add_page";
    }


    @GetMapping("/group/{id}")
    public String listGroup(
            @PathVariable(value = "id") long groupId,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            Model model) {
        Group group = (groupId != DEFAULT_GROUP_ID) ? contactService.findGroup(groupId) : null;
        if (page < 0) page = 0;

        List<Contact> contacts = contactService
                .findByGroup(group, PageRequest.of(page, ITEMS_PER_PAGE, Sort.Direction.DESC, "id"));

        model.addAttribute("groups", contactService.findGroups());
        model.addAttribute("contacts", contacts);
        model.addAttribute("byGroupPages", getPageCount(group));
        model.addAttribute("groupId", groupId);

        return "index";
    }

    @PostMapping("/group/delete/{id}")
    public String deleteGroup(@PathVariable(value = "id") long groupId) {
        contactService.deleteById(groupId);
        return "redirect:/";
    }


    @PostMapping(value = "/contact/delete")
    public ResponseEntity<Void> delete(
            @RequestParam(value = "toChangeGroup[]", required = false) long[] toDelete) {
        if (toDelete != null && toDelete.length > 0)
            contactService.deleteContacts(toDelete);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/contact/change-group")
    public String move(
            @RequestParam(value = "toChangeGroup[]", required = false) long[] toMove,
            @RequestParam(value = "groupId") long toGroup) {
        if (toMove != null && toMove.length > 0)
            contactService.moveToOtherGroup(toMove, toGroup);

        return "redirect:/";
    }

    @PostMapping(value = "/contact/add")
    public String contactAdd(@RequestParam(value = "group") long groupId,
                             @RequestParam String name,
                             @RequestParam String surname,
                             @RequestParam String phone,
                             @RequestParam String email) {
        Group group = (groupId != DEFAULT_GROUP_ID) ? contactService.findGroup(groupId) : null;

        Contact contact = new Contact(group, name, surname, phone, email);
        contactService.addContact(contact);

        return "redirect:/";
    }

    @PostMapping(value = "/contact/add/xml")
    public String contactAddByXml(@RequestParam("file") MultipartFile multipartFile){
        if (multipartFile.isEmpty()) {
            return "redirect:/error";
        }

        contactService.addContactByXML(multipartFile);
        return "redirect:/";
    }

    @PostMapping(value = "/group/add")
    public String groupAdd(@RequestParam String name) {
        contactService.addGroup(new Group(name));
        return "redirect:/";
    }

    private long getPageCount() {
        long totalCount = contactService.count();
        return (totalCount / ITEMS_PER_PAGE) + ((totalCount % ITEMS_PER_PAGE > 0) ? 1 : 0);
    }

    private long getPageCount(Group group) {
        long totalCount = contactService.countByGroup(group);
        return (totalCount / ITEMS_PER_PAGE) + ((totalCount % ITEMS_PER_PAGE > 0) ? 1 : 0);
    }

    private long getPageCount(String pattern) {
        long totalCount = contactService.countByPattern(pattern);
        return (totalCount / ITEMS_PER_PAGE) + ((totalCount % ITEMS_PER_PAGE > 0) ? 1 : 0);
    }
}
