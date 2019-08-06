package com.lms.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.lms.models.User;
import com.lms.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService{

	@Autowired
	private UserRepository repository;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = repository.findByUsername(username);
		CustomUserDetails userDetails = null;
		if(user!=null){
//			System.out.println("USER role::: " + user.getRoles().stream().findAny().get().getRole());
//			System.out.println("USERNAME::: " + user.getRoles().size());
			userDetails = new CustomUserDetails(user);
//			System.out.println("USER AUTHORITY::: " + userDetails.getAuthorities().stream().findAny().get().getAuthority());
		}else{
			throw new UsernameNotFoundException("User does not exist the name: " + username);
		}
		return userDetails;
	}

}
