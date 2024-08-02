package com.example.demo;

import org.springframework.stereotype.Component;
import java.util.regex.Pattern;

//  A Class for validating mobile number, password, role and email
@Component
public class Validations {

    // Validating mobile number
    public Boolean isMobileNumberValid(String mobileNumber) {
        String mobileNumberPattern = "[6-9]{1}[0-9]{9}";
        if (mobileNumber == null) return false;
        return Pattern.matches(mobileNumberPattern, mobileNumber);
    }

    // Validating password
    public Boolean isPasswordValid(String password) {
        if (password == null || password.length() < 8 || password.length() >15) return false;
        // Password has to contain at least one uppercase letter and one special character
        String passwordPattern = "^(?=.*[A-Z])(?=.*[!@#&()\\-\\[\\]{}:;',?/*~$^+=<>]).+$";
        return Pattern.matches(passwordPattern, password);
    }

    // Validating role
    public Boolean isRoleValid(String role){
        return role.equalsIgnoreCase("ADMIN") || role.equalsIgnoreCase("USER");
    }

    // Validating role
    public Boolean isEmailValid(String email){
        return email.contains("@");
    }
}
