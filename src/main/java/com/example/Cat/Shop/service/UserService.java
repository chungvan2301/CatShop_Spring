package com.example.Cat.Shop.service;

import com.example.Cat.Shop.model.User;
import com.example.Cat.Shop.repository.UserRepo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserRepo userRepo;

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

}
