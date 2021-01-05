package ksp.vilius.usersupport.service.impl;

import ksp.vilius.usersupport.Payload.UserRequest;
import ksp.vilius.usersupport.enums.Role;
import ksp.vilius.usersupport.exceptions.EmailExistsException;
import ksp.vilius.usersupport.exceptions.EmailNotFoundException;
import ksp.vilius.usersupport.exceptions.UsernameExistsException;
import ksp.vilius.usersupport.models.User;
import ksp.vilius.usersupport.models.UserPrincipal;
import ksp.vilius.usersupport.repository.UserRepository;
import ksp.vilius.usersupport.service.EmailService;
import ksp.vilius.usersupport.service.LoginAttemptService;
import ksp.vilius.usersupport.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.mail.MessagingException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static ksp.vilius.usersupport.constant.FileConstant.*;
import static ksp.vilius.usersupport.enums.Role.ROLE_USER;
import static org.apache.commons.lang3.StringUtils.EMPTY;


@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserDetailsService, UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final LoginAttemptService attemptService;
    private final EmailService emailService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userToFind = repository.findByUsername(username);

        if (userToFind != null) {
            validateLoginAttempt(userToFind);
            userToFind.setLastLoginDateDisplay(userToFind.getLastLoginDate());
            userToFind.setLastLoginDate(new Date());
            repository.save(userToFind);
            return new UserPrincipal(userToFind);
        }

        throw new UsernameNotFoundException("User not found with username: " + username);
    }

    private void validateLoginAttempt(User userToFind) {
        if (userToFind.isNotLocked()) {
            if (attemptService.hasExceededMaxAttempts(userToFind.getUsername())) {
                userToFind.setNotLocked(false);
            }
            userToFind.setNotLocked(true);
        }
        attemptService.evictUserFromLoginAttemptCache(userToFind.getUsername());
    }

    @Override
    public User register(String firstName, String lastName, String username, String email)
            throws EmailExistsException, UsernameExistsException, MessagingException {

        validateNewUsernameAndEmail(EMPTY, username, email);

        User user = new User();
        user.setUserId(generateUserId());
        String password = generatePassword();
        log.info(password);
        String encodedPassword = encodePassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setEmail(email);
        user.setJoinDate(new Date());
        user.setPassword(encodedPassword);
        user.setActive(true);
        user.setNotLocked(true);
        user.setRole(ROLE_USER.name());
        user.setAuthorities(ROLE_USER.getAuthorities());
        user.setProfileImageUrl(getTemporaryProfileImage(username));
        User userToRegister = repository.save(user);
        emailService.sendNewPasswordEmail(userToRegister.getFirstName(), userToRegister.getEmail(), password);
        return userToRegister;
    }

    @Override
    public User addNewUser(UserRequest request)
            throws EmailExistsException, UsernameExistsException, IOException {

        validateNewUsernameAndEmail(EMPTY, request.getUsername(), request.getEmail());
        User user = new User();
        String password = generatePassword();
        String encodedPassword = encodePassword(password);
        user.setJoinDate(new Date());
        user.setUserId(generateUserId());
        user.setFirstName(request.getFirstname());
        user.setLastName(request.getLastname());
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(encodedPassword);
        user.setActive(request.isActive());
        user.setNotLocked(request.isNonLocked());
        user.setRole(getRoleEnumName(request.getRole()).name());
        user.setAuthorities(getRoleEnumName(request.getRole()).getAuthorities());
        user.setProfileImageUrl(getTemporaryProfileImage(request.getUsername()));
        repository.save(user);
        saveProfileImage(user, request.getProfileImage());
        return user;
    }

    private void saveProfileImage(User user, MultipartFile profileImage) throws IOException {
        if (profileImage != null) {
            Path userFolder = Paths.get(USER_FOLDER + user.getUsername()).toAbsolutePath().normalize();
            if (!Files.exists(userFolder)) {
                Files.createDirectories(userFolder);
                log.info("directory created");
            }
            Files.deleteIfExists(Paths.get(userFolder + user.getUsername() + DOT + JPG_EXTENSION));
            Files.copy(profileImage.getInputStream(), userFolder.resolve(user.getUsername() + DOT + JPG_EXTENSION), REPLACE_EXISTING);
            user.setProfileImageUrl(setProfileImageUrl(user.getUsername()));
            repository.save(user);
        }
    }

    private String setProfileImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(USER_IMAGE_PATH + username + DOT + JPG_EXTENSION).toUriString();
    }


    private Role getRoleEnumName(String role) {

        return Role.valueOf(role.toUpperCase());
    }

    @Override
    public User updateUser(String currentUsername, UserRequest request) throws EmailExistsException, UsernameExistsException, IOException {
        User currentUser = validateNewUsernameAndEmail(currentUsername, request.getUsername(), request.getEmail());

        currentUser.setFirstName(request.getFirstname());
        currentUser.setLastName(request.getLastname());
        currentUser.setEmail(request.getEmail());
        currentUser.setUsername(request.getUsername());
        currentUser.setActive(request.isActive());
        currentUser.setNotLocked(request.isNonLocked());
        currentUser.setRole(getRoleEnumName(request.getRole()).name());
        currentUser.setAuthorities(getRoleEnumName(request.getRole()).getAuthorities());
        repository.save(currentUser);
        saveProfileImage(currentUser, request.getProfileImage());
        return currentUser;
    }

    @Override
    public void deleteUser(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void resetPassword(String email) throws EmailNotFoundException, MessagingException {
        User user = repository.findByEmail(email);
        if (user != null) {
            String newPassword = generatePassword();
            user.setPassword(encodePassword(newPassword));
            repository.save(user);
            emailService.sendNewPasswordEmail(user.getFirstName(), email, newPassword);
        }
        throw new EmailNotFoundException("Email not found: " + email);

    }

    @Override
    public User updateProfileImage(String username, MultipartFile profile_image) throws IOException {
        User user = repository.findByUsername(username);
        if (user != null) {
            saveProfileImage(user, profile_image);
            return user;
        }
        throw new UsernameNotFoundException("User not found with username: " + username);
    }


    @Override
    public List<User> getUsers() {
        return repository.findAll();
    }

    @Override
    public User findByUsername(String username) {
        return repository.findByUsername(username);
    }

    @Override
    public User findUserByEmail(String email) {
        return repository.findByEmail(email);
    }


    private String getTemporaryProfileImage(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/image/profile/" + username).toUriString();
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    private String generateUserId() {

        return RandomStringUtils.randomNumeric(10);
    }

    private User validateNewUsernameAndEmail(String username, String newUsername, String newEmail)
            throws UsernameExistsException, EmailExistsException {

        User userByNewUsername = findByUsername(newUsername);
        User userByNewEmail = findUserByEmail(newEmail);

        if (StringUtils.isNotBlank(username)) {
            User currentUser = findByUsername(username);
            if (currentUser == null) {
                throw new UsernameNotFoundException("No user found with username: '" + username + "'");
            }
            if (userByNewUsername != null && !currentUser.getId().equals(userByNewUsername.getId())) {
                throw new UsernameExistsException("Username is already taken");
            }
            if (userByNewEmail != null && !currentUser.getId().equals(userByNewEmail.getId())) {
                throw new EmailExistsException("Email already exists");
            }
            return currentUser;
        } else {

            if (userByNewUsername != null) {
                throw new UsernameExistsException("Username already exists");
            }

            if (userByNewEmail != null) {
                throw new EmailExistsException("Email already exists");
            }
            return null;
        }
    }
}
