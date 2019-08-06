package com.lms.controllers;

import java.io.IOException;

import javax.annotation.security.PermitAll;
import javax.security.auth.message.config.AuthConfig;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.lms.models.User;

@RestController
@RequestMapping("/v1/")
public class ApiController {

	@PreAuthorize("hasAnyRole('ADMIN')")
	@ResponseBody
	@PostMapping("adduser")
	public String userAdd( HttpServletResponse resp) throws IOException{
		return "Successful";
	}
	@GetMapping("adduser")
	public String addUser( HttpServletResponse resp) throws IOException{
		return "Successful";
	}
}
