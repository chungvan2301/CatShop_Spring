	package com.example.Cat.Shop.Configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.Cat.Shop.model.Role;
import com.example.Cat.Shop.model.User;
import com.example.Cat.Shop.repository.RoleRepo;
import com.example.Cat.Shop.repository.UserRepo;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class GoogleOauth2SuccessHandler implements AuthenticationSuccessHandler {

	@Autowired
	RoleRepo roleRepo;
	@Autowired
	UserRepo userRepo;

	private RedirectStrategy redirectStragety = new DefaultRedirectStrategy();
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
										Authentication authentication) throws IOException, ServletException {
		// TODO Auto-generated method stub
		OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
		String email = token.getPrincipal().getAttributes().get("email").toString();
		if(userRepo.findUserByEmail(email).isPresent()) {
			Optional <User> user = userRepo.findUserByEmail(email);
			storeUserInSession(request.getSession(), user.orElse(null).getEmail());
		} else {
			User user = new User();
			user.setFirstName(token.getPrincipal().getAttributes().get("given_name").toString());
			user.setLastName(token.getPrincipal().getAttributes().get("family_name").toString());
			user.setEmail(email);
			String password = RandomNumberGenerator.generateRandomPassword();
			user.setPassword(password);
			List <Role> roles = new ArrayList<>();
			roles.add(roleRepo.findById(2).get());
			user.setRoles(roles);
			userRepo.save(user);
			storeUserInSession(request.getSession(), user.getEmail());
		}
		redirectStragety.sendRedirect(request, response, "/");
	}
	private void storeUserInSession(HttpSession session, String email) {
			session.setAttribute("email", email);
	}
	
}
