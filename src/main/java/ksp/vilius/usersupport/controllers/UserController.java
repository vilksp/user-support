package ksp.vilius.usersupport.controllers;

import ksp.vilius.usersupport.exceptions.EmailExistsException;
import ksp.vilius.usersupport.exceptions.UsernameExistsException;
import ksp.vilius.usersupport.models.User;
import ksp.vilius.usersupport.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) throws EmailExistsException, UsernameExistsException {

        User registeredUser = userService.register(user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail());
        return new ResponseEntity<>(registeredUser, HttpStatus.OK);
    }

}
