package ksp.vilius.usersupport.service;

import ksp.vilius.usersupport.exceptions.EmailExistsException;
import ksp.vilius.usersupport.exceptions.UsernameExistsException;
import ksp.vilius.usersupport.models.User;

import java.util.List;


public interface UserService {

    User register(String firstName, String lastName, String username, String email) throws EmailExistsException, UsernameExistsException;

    List<User> getUsers();

    User findByUsername(String username);

    User findUserByEmail(String email);
}
