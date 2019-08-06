package com.lms.controllers;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.lms.dao.LmsRepository;
import com.lms.jwt.config.JwtTokenUtil;
import com.lms.models.Book;
import com.lms.models.JwtRequest;
import com.lms.models.JwtResponse;
import com.lms.models.Role;
import com.lms.models.User;
import com.lms.repository.UserRepository;
import com.lms.services.CustomUserDetailsService;

//@CrossOrigin("http://localhost:9000") 
@CrossOrigin("*") 
@RestController
@RequestMapping("/v2/")
public class ApiControllerV2 {

	@Autowired
	private BCryptPasswordEncoder passEncoder;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	LmsRepository repo;

	@ResponseBody
	@PostMapping("signup")
	public String saveUser(@RequestBody User user, HttpServletResponse response) {
		Role role = new Role();
		role.setRole("USER");
		Set<Role> roles = new HashSet<Role>();
		roles.add(role);
		user.setRoles(roles);
		user.setPassword(passEncoder.encode(user.getPassword()));
		try {
			userRepository.save(user);
		} catch (Exception e) {
			try {
				response.sendError(500);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		return "successful";
	}

	@RequestMapping(value = "authenticate", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {

		authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

		final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

		final String token = jwtTokenUtil.generateToken(userDetails);

		return ResponseEntity.ok(new JwtResponse(token));
	}

	@Autowired
	private AuthenticationManager authenticationManager;

	private void authenticate(String username, String password) throws Exception {
		try {
//			System.out.println("User::" + username + ":" + password);
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}
	}

	@PreAuthorize("hasAnyRole('USER')")
	@RequestMapping(value = "/rest/apiHealth", method = RequestMethod.GET)
	public String testAuth() throws Exception {
		return "Successful";
	}

	@PreAuthorize("hasAnyRole('USER')")
	@RequestMapping(value = "/rest/books", method = RequestMethod.GET)
	@ResponseBody 
	public List<Book> getBooks() throws Exception {
		return repo.findAll();
	}

}
