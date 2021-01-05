package ksp.vilius.usersupport.service;

import ksp.vilius.usersupport.Payload.UserRequest;
import ksp.vilius.usersupport.exceptions.EmailExistsException;
import ksp.vilius.usersupport.exceptions.EmailNotFoundException;
import ksp.vilius.usersupport.exceptions.UsernameExistsException;
import ksp.vilius.usersupport.models.User;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;


public interface UserService {

    User register(String firstName, String lastName, String username, String email) throws EmailExistsException, UsernameExistsException, MessagingException;

    List<User> getUsers();

    User findByUsername(String username);

    User findUserByEmail(String email);

    //Create new user by admin
    User addNewUser(UserRequest request) throws EmailExistsException, UsernameExistsException, IOException;

    User updateUser(String currentUsername, UserRequest request) throws EmailExistsException, UsernameExistsException, IOException;

    void deleteUser(Long id);

    void resetPassword(String email) throws EmailNotFoundException, MessagingException;

    User updateProfileImage(String username, MultipartFile profile_image) throws IOException;
}
