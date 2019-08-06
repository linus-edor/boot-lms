package com.lms.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A controller implemented to demonstrate custom access denied page.
 * **/

@RestController
@PreAuthorize("hasAnyRole('ADMIN')")
@RequestMapping("/admin/")
public class SecuredController {

	@GetMapping("adduser")
	public void userAdd(HttpServletResponse resp) throws IOException{
		resp.sendRedirect("/");
	}
}
