package com.example.Cat.Shop.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.Cat.Shop.model.CustomUserDetail;
import com.example.Cat.Shop.model.User;
import com.example.Cat.Shop.repository.UserRepo;

@Service
public class CustomUserDetailService implements UserDetailsService {
	@Autowired
	UserRepo userRepo;
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		Optional <User> user = userRepo.findUserByEmail(email);
		user.orElseThrow(()-> new UsernameNotFoundException("Lỗi email"));
		return user.map(CustomUserDetail::new).get();
	}

}
