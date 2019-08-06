package com.lms.controllers;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.PostRemove;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.lms.dao.LmsRepository;
import com.lms.models.Book;
import com.lms.models.Role;
import com.lms.models.User;
import com.lms.repository.UserRepository;
import com.lms.services.LmsService;

@RestController
//@PreAuthorize("hasAnyRole('ADMIN')")
public class MainController {

	@Autowired
	private LmsService service;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	LmsRepository repo;

	@Autowired
	private BCryptPasswordEncoder passEncoder;

	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	@GetMapping("/secured/user/add")
	public ModelAndView addUser() {
		ModelAndView mv = new ModelAndView("adduser");
		mv.addObject("mode", "ADDUSER");
		mv.addObject("user", new User());
		return mv;
	}

	@PreAuthorize("hasAnyRole('ADMIN')")
	@PostMapping("/secured/user/save")
	public String saveUser(@RequestBody User user, HttpServletResponse response) {
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
		return "index";
	}

	@GetMapping(value = "/")
	public ModelAndView index(Model model, @RequestParam(defaultValue="0") int page, HttpServletRequest req) {
		ModelAndView mv = new ModelAndView("index");
		mv.addObject("mode", "BOOK_VIEW");
		mv.addObject("books", repo.findAll(PageRequest.of(page, 4)));
		mv.addObject("currentPage",page);
		return mv;
	}

	// @PreAuthorize("hasAnyRole('USER')")
	@GetMapping(value = "/secured/edit")
	public ModelAndView edit(@RequestParam long id, HttpServletRequest req) {
		ModelAndView mv = new ModelAndView("index");
		mv.addObject("mode", "BOOK_EDIT");
		mv.addObject("book", service.findById(id).get());
		return mv;
	}

	// @PreAuthorize("hasAnyRole('USER')")
	@PostMapping(value = "/secured/update")
	public void update(Book book, HttpServletRequest req, HttpServletResponse resp) {
		service.save(book);
		req.setAttribute("mode", "BOOK_VIEW");
		req.setAttribute("books", repo.findAll());
		try {
			resp.sendRedirect("/");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@GetMapping(value = "/secured/create")
	public ModelAndView create(HttpServletRequest req) {
		ModelAndView mv = new ModelAndView("index");
		mv.addObject("mode", "NEW_BOOK");
		mv.addObject("book", new Book());
		return mv;
	}

	@GetMapping(value = "/login")
	public ModelAndView login(HttpServletRequest req) {
		ModelAndView mv = new ModelAndView("index");
		mv.addObject("mode", "LOGIN");
		return mv;
	}
	
	@GetMapping(value = "/register")
	public ModelAndView register(HttpServletRequest req) {
		ModelAndView mv = new ModelAndView("index");
		mv.addObject("mode", "REGISTER");
		mv.addObject("user", new User());
		return mv;
	}
	
	@PostMapping(value = "/saveuser")
	public String completeUserRegistration(@ModelAttribute User user, HttpServletResponse response) {
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
		return "index";
	}


	@GetMapping(value = "/secured/delete")
	public void delete(@RequestParam long id, HttpServletResponse resp) {
		service.delete(id);
		try {
			resp.sendRedirect("/");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
