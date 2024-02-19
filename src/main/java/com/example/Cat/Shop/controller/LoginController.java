package com.example.Cat.Shop.controller;

import java.util.ArrayList;
import java.util.List;

import com.example.Cat.Shop.model.Role;
import com.example.Cat.Shop.model.User;
import com.example.Cat.Shop.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.example.Cat.Shop.repository.RoleRepo;
import com.example.Cat.Shop.repository.UserRepo;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {
//	@Autowired
//	private AuthenticationManager authenticationManager;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	UserRepo userRepo;
	@Autowired
	RoleRepo roleRepo;
	@Autowired
	UserService userService;
	
	@GetMapping("/login")
	public String login() {
		return "login";
	}
	
	@GetMapping("/register")
	public String register() {
		return "register";
	}

	@GetMapping("/forgot-password")
	public String forgotPassWord() {
		return "forgetPassword";
	}

	@PostMapping("/forgot-password")
	public String getNewPassWord(@RequestParam String email, RedirectAttributes redirectAttributes) {
		int result = userService.getNewPassword(email);
		if (result==0) {
			redirectAttributes.addFlashAttribute("message","wrong-email");
			return "redirect:/forgetPassword";
		} else if (result==2) {
			redirectAttributes.addFlashAttribute("message","email-not-exist");
			return "redirect:/forgetPassword";
		} else {
			redirectAttributes.addFlashAttribute("forgotpassword","success");
			return "redirect:/login";
		}

	}

	@PostMapping("/register")
	public String registerPost(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes) throws Exception{
		if (userRepo.findUserByEmail(user.getEmail()).isPresent()) {
			redirectAttributes.addFlashAttribute("message", "failRegister");
			return "redirect:/register";
		} else {
			String password = user.getPassword();
			user.setPassword(bCryptPasswordEncoder.encode(password));
			List<Role> roles = new ArrayList<>();
			roles.add(roleRepo.findById(2).get());
			user.setRoles(roles);
			userRepo.save(user);
			redirectAttributes.addFlashAttribute("message", "successRegister");
			return "redirect:/login";
		}
	}

}
