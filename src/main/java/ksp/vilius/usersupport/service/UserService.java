package ksp.vilius.usersupport.service;

import ksp.vilius.usersupport.models.User;

import java.util.List;

public interface UserService {

    User register(String firstName, String lastName, String username, String email);

    List<User> getUsers();

    User findByUsername(String username);

    User findUserByEmail(String email);
}
