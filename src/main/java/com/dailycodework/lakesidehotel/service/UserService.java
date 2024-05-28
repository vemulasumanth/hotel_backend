package com.dailycodework.lakesidehotel.service;

import com.dailycodework.lakesidehotel.exception.UserAlreadyExistsException;
import com.dailycodework.lakesidehotel.model.Role;
import com.dailycodework.lakesidehotel.model.User;
import com.dailycodework.lakesidehotel.repository.RoleRepository;
import com.dailycodework.lakesidehotel.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService implements IUserService {
	@Autowired
    private UserRepository userRepository;
	@Autowired
    private  PasswordEncoder passwordEncoder;
	@Autowired
    private RoleRepository roleRepository;

    @Override
    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException(user.getEmail() + " already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));


        Role defaultRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName("ROLE_USER");
                    return roleRepository.save(newRole);
                });

        Set<Role> userRoles = new HashSet<>();
        userRoles.add(defaultRole);

        user.setRoles(userRoles);

        return userRepository.save(user);
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Transactional
    @Override
    public void deleteUser(String email) {
        User theUser = getUser(email);
        if (theUser != null){
            userRepository.deleteByEmail(email);
        }

    }

    @Override
    public User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
