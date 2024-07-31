package com.example.demo.Controller;
import com.example.demo.Entity.User;
import com.example.demo.Service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user")
    public String getUserDashboard() {
        return "User Dashboard";
    }

    @PreAuthorize(("hasRole('ADMIN')"))
            @GetMapping("/admin")
            public String getAdminDashBoard(){
        return "Hello Admin..!";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = customUserDetailsService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER') OR hasRole('ADMIN')")
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        User user = customUserDetailsService.getUserById(userId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable Long userId, @RequestBody User user) {
        User updatedUser = customUserDetailsService.updateUser(userId, user);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable int userId) {
        try {
            customUserDetailsService.deleteUser(userId);
            return ResponseEntity.ok("User deleted successfully");
            //not working if id is not present then also it's showing user deleted successfully
        } catch (NoSuchElementException e) {
            return ResponseEntity.ok("User doesn't exist");
        }
    }
}

