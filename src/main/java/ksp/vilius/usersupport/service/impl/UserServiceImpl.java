package ksp.vilius.usersupport.service.impl;

import ksp.vilius.usersupport.exceptions.EmailExistsException;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.mail.MessagingException;
import java.util.Date;
import java.util.List;

import static ksp.vilius.usersupport.enums.Role.ROLE_USER;


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

        validateNewUsernameAndEmail(StringUtils.EMPTY, username, email);

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
        user.setProfileImageUrl(getTemporaryProfileImage());
        User userToRegister = repository.save(user);
        emailService.sendNewPasswordEmail(userToRegister.getFirstName(),userToRegister.getEmail(),password);
        return userToRegister;
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


    private String getTemporaryProfileImage() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/image/profile/temp").toUriString();
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
