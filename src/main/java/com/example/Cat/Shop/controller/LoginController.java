package com.example.Cat.Shop.controller;

import java.util.ArrayList;
import java.util.List;

import com.example.Cat.Shop.model.Role;
import com.example.Cat.Shop.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

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
	
	@GetMapping("/login")
	public String login() {
		return "login";
	}
	
	@GetMapping("/register")
	public String register() {
		return "register";
	}

//	@PostMapping("/login")
//	public String authenticateUser(@RequestBody LoginDTO loginDTO, HttpServletRequest request) {
//		Authentication authentication = authenticationManager
//				.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword()));
//		SecurityContextHolder.getContext().setAuthentication(authentication);
//		Optional<User> optionalUser = userRepo.findUserByEmail(loginDTO.getUsername());
//		User user = optionalUser.orElse(null);
//		System.out.println(loginDTO.getUsername());
//		if (user!=null) {
//			storeUserInSession(request.getSession(), user);
//		}
//		return "/";
//	}

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
