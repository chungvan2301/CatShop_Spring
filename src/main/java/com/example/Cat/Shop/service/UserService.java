package com.example.Cat.Shop.service;

import com.example.Cat.Shop.model.User;
import com.example.Cat.Shop.repository.UserRepo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class UserService {
    @Autowired
    UserRepo userRepo;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~!@#$%^&*()_+-=";
    private static final int PASSWORD_LENGTH = 10;
    private static final SecureRandom RANDOM = new SecureRandom();

    public User userLoggedIn(HttpServletRequest request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (userRepo.findUserByEmail(authentication.getName()).isPresent()) {
            return userRepo.findUserByEmail(authentication.getName()).orElse(null);
        } else {
            HttpSession session = request.getSession();
            String userEmailInSession = session.getAttribute("email").toString();
            return userRepo.findUserByEmail(userEmailInSession).orElse(null);
        }
    }

    public int getNewPassword (String email) {
        if (userRepo.findUserByEmail(email).orElse(null)==null) {
            System.out.println(email);
            return 0;
        } else {
            System.out.println("tesst2");
            User user = userRepo.findUserByEmail(email).orElse(null);
            String newPassword = this.generatePassword();
            user.setPassword(bCryptPasswordEncoder.encode(newPassword));
            userRepo.save(user);
            return sendNewPasswordEmail(email,newPassword);
        }

    }

    public int sendNewPasswordEmail(String to, String newPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your new password");
        message.setText("Your new password is: " + newPassword);
        try {
            System.out.println("tesst");
            javaMailSender.send(message);
            return 1;
        } catch (MailException e) {
            // Log the exception for debugging purposes
            e.printStackTrace();
            // Return a custom message
            return 2;
        }
    }

    public String generatePassword() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }
        return password.toString();
    }
}
