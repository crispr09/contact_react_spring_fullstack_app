package in.revivifyai.service;

import in.revivifyai.model.Contact;
import in.revivifyai.repository.ContactRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
@Transactional(rollbackOn = Exception.class)
@AllArgsConstructor
@Slf4j
public class ContactService {

    private final ContactRepository contactRepo;
    public Page<Contact> getAllContact(int page, int size) {
        return contactRepo.findAll(PageRequest.of(page, size, Sort.by("name")));

    }
    public Contact getContact(String id) {
        return contactRepo.findById(id).orElseThrow(()-> new RuntimeException("Contact Not found"));
    }

    public Contact createContact(Contact contact) {
        return contactRepo.save(contact);
    }
    public void deleteContact(Contact contact) {
         contactRepo.delete(contact);
    }
    public String uploadPhoto(String id, MultipartFile file) {
        log.info("saving picture for ID: {}", id);
        Contact contact = getContact(id);
        String photoUrl = photoFunction.apply(id, file);
        contact.setPhotoUrl(photoUrl);
        contactRepo.save(contact);
        return photoUrl;

    }

    private final Function<String, String> fileExtention = filename -> Optional.of(filename).filter(name ->name.contains("."))
            .map(name -> "."+name.substring(filename.lastIndexOf(".") +1)).orElse(".png");
    private final BiFunction<String, MultipartFile, String> photoFunction = (id, image) -> {

        try {
            Path fileStorageLocation = Paths.get("C://pics").toAbsolutePath().normalize();
            if(!Files.exists(fileStorageLocation)) {
                Files.createDirectories(fileStorageLocation);
            }
            Files.copy(image.getInputStream(), fileStorageLocation.resolve(id+fileExtention.apply(image.getOriginalFilename())),REPLACE_EXISTING);
            return ServletUriComponentsBuilder.fromCurrentContextPath().path("/contacts/image/" + id + fileExtention.apply(image.getOriginalFilename())).toUriString();
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to save image");

        }
    };
}
