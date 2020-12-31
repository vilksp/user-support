package ksp.vilius.usersupport.service.impl;

import ksp.vilius.usersupport.models.User;
import ksp.vilius.usersupport.models.UserPrincipal;
import ksp.vilius.usersupport.repository.UserRepository;
import ksp.vilius.usersupport.service.UserService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UserDetailsService, UserService {

    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userToFind = repository.findByUsername(username);

        if (userToFind != null) {
            userToFind.setLastLoginDateDisplay(userToFind.getLastLoginDate());
            userToFind.setLastLoginDate(new Date());
            repository.save(userToFind);
            return new UserPrincipal(userToFind);
        }

        throw new UsernameNotFoundException("User not found with username: " + username);
    }

    @Override
    public User register(String firstName, String lastName, String username, String email) {

        validateNewUsernameAndEmail(username, email);

        return null;
    }

    private void validateNewUsernameAndEmail(String username, String email) {
        if(StringUtils.isNotBlank(username)){
            User currentUser = findByUsername(username);
            if(currentUser ==null){
                throw new UsernameNotFoundException("No user found with username: '"+username+"'");
            }
        }
    }

    @Override
    public List<User> getUsers() {
        return null;
    }

    @Override
    public User findByUsername(String username) {
        return null;
    }

    @Override
    public User findUserByEmail(String email) {
        return null;
    }
}
