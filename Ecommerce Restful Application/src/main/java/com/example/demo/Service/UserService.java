package com.example.demo.Service;

import com.example.demo.AuthenticatingCurrentUser;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.demo.Validations;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private  Validations validator;

    @Autowired
    private AuthenticatingCurrentUser authenticatingCurrentUser;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        User user = optionalUser.get();
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
    }

    // Retrieving the All the users ( ADMIN )
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Retrieving user by id
    public User getUserById(Long userId) {
        User currentUser =  authenticatingCurrentUser.getCurrentUser();
        if (!currentUser.getId().equals(userId)) {
            throw new AccessDeniedException("You are not allowed to access this resource");
        }
        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found" + userId));
    }

    // Registering User
    public String registerUser(User user) {

        // validating the email
        if(!validator.isEmailValid(user.getEmail())) return "Invalid email " + user.getEmail();

        // Checking whether the email is present or not
        if (userRepository.existsByEmail(user.getEmail())) return "Email is already in use!";

        String role = user.getRole().toUpperCase();
        // Validating the role
        if(role.equals("ADMIN")){
            boolean isAdminExist = userRepository.findByRole("ADMIN").isPresent();
            if (isAdminExist) return "Admin already exists!";
        }

        // Validating the mobile number
        if (!validator.isMobileNumberValid(user.getMobileNumber())) return "Invalid mobile number: " + user.getMobileNumber();

        // Validating the password
        if (!validator.isPasswordValid(user.getPassword())) return "Password must be 8-15 characters long, contain one uppercase letter, and one special character. " + user.getPassword();

        // Validating the role
        if(!validator.isRoleValid(user.getRole())) return "Invalid role " + user.getRole();

        // setting the username to uppercase
        user.setUsername(user.getUsername().toUpperCase());
        // Encoding password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // setting the role to uppercase
        user.setRole(user.getRole().toUpperCase());
        userRepository.save(user);
         return  "User registered successfully";
    }

    // Updating User details
    public User updateUser(Long userId, User userDetails) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id " + userId));
        User currentUser =  authenticatingCurrentUser.getCurrentUser();
        if (!currentUser.getId().equals(userId)) {
            throw new AccessDeniedException("You are not allowed to access this resource");
        }
        existingUser.setUsername(userDetails.getUsername());
        existingUser.setEmail(userDetails.getEmail());
        existingUser.setMobileNumber(userDetails.getMobileNumber());
        existingUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        return userRepository.save(existingUser);
    }

    // Deleting the user by passing user_id
    public void deleteUserById(long userId) {
        if(userRepository.existsById(userId)) userRepository.deleteById(userId);
        throw new NoSuchElementException("User doesn't exist with id " + userId);
    }

    // While registering the user if the user email is redundant then it not going to register the user with the redundant email
    public Boolean UserExistsByEmail(String email){
        return userRepository.existsByEmail(email);
    }
}

