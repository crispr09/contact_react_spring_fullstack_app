package in.revivifyai.controller;

import in.revivifyai.model.Contact;
import in.revivifyai.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequestMapping("/contacts")
@RequiredArgsConstructor
public class ContactController {
    private final ContactService contactService;

    @PostMapping
    public ResponseEntity<Contact> createContact(@RequestBody Contact contact) {
        return ResponseEntity.created(URI.create("/contacts/userID")).body(contactService.createContact(contact));
    }
    @GetMapping
    public ResponseEntity <Page<Contact>> getContacts(@RequestParam(value = "page" ,defaultValue = "0") int page,
                                                      @RequestParam(value="size", defaultValue = "10") int size) {
        return ResponseEntity.ok().body(contactService.getAllContact(page, size));
    }
    @GetMapping("/{id}")
    public ResponseEntity <Contact> getContacts(@PathVariable(value = "id") String id) {
        return ResponseEntity.ok().body(contactService.getContact(id));
    }
    @PutMapping("/photo")
    public ResponseEntity <String> uploadPhoto(@RequestParam(value = "id") String id, @RequestParam(value = "file" ) MultipartFile file) {
        return ResponseEntity.ok().body(contactService.uploadPhoto(id,file));
    }
    @GetMapping(value = "/image/{filename}", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[]  getPhoto(@PathVariable("filename") String filename) throws IOException {
        return Files.readAllBytes(Paths.get("C://pics/" +filename));
    }
}
