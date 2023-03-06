package com.productdesc.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import com.productdesc.entities.User;
import com.productdesc.helper.Message;
import com.productdesc.repositories.UserRepository;

@Controller
public class HomeController {

	
	//dropdown list
	static List<String> role=null;
	static {
		role=new ArrayList<>();
		role.add("ROLE_ADMIN");
		role.add("ROLE_MANAGER");
		role.add("ROLE_USER");
	}

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;

	// home handler
	@GetMapping("/")
	public String home(Model model) {
		System.out.println("Inside home Handler................");
		model.addAttribute("title", "Home-Product Desc");
		return "home";
	}

	// about handler
	@GetMapping("/about")
	public String about(Model model) {
		System.out.println("Inside about Handler................");
		model.addAttribute("title", "About-Product Desc");
		return "about";
	}
	
	// signup handler
	@GetMapping("/signup")
	public String signup(Model model) {
		System.out.println("Inside signup Handler................");
		model.addAttribute("title", "Register-Product Desc");
		model.addAttribute("user", new User());
		model.addAttribute("role", role);
		return "signup";
	}

	// handler for registering user
	@PostMapping("/register")
	public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, Model model,
			HttpSession session) {
		System.out.println("Inside registerUser Handler................");
		try {
			if (bindingResult.hasErrors()) {
				System.out.println("ERROR : " + bindingResult.toString());
				model.addAttribute("user", user);
				return "signup";
			}

			// assign role
			//user.setRole("ROLE_USER");
			//user.setRole("ROLE_ADMIN");
			//user.setRole("ROLE_MANAGER");
			//user.setPassword("a");
			user.setPassword(passwordEncoder.encode(user.getPassword()));

			System.out.println("user : " + user);
			
			this.userRepository.save(user);

			model.addAttribute("user", new User());
			model.addAttribute("role", role);
			
			session.setAttribute("message", new Message("Successfully Registered!!", "alert-success"));
			return "signup";

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Something went wrong!! " + e.getMessage(), "alert-danger"));
			return "signup";
		}
	}

	// my login handler 
	@GetMapping("/signin")
	public String login(Model model) {
		System.out.println("Inside login Handler................");
		model.addAttribute("title", "Login-Product Desc");
		model.addAttribute("role", role);
		return "login";
	}
}
