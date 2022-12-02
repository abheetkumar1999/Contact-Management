package com.smart.scm.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smart.scm.dao.UserRepository;
import com.smart.scm.entites.Contact;
import com.smart.scm.entites.User;
import com.smart.scm.helper.Message;

@Controller
public class HomeController {
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	
	
	@RequestMapping("/")
	public String home(Model model) {
		model.addAttribute("title","Smart Contact Manager");
		return "home";
	}
	
	@RequestMapping("/about")
	public String about(Model model) {
		model.addAttribute("title","Smart Contact Manager");
		return "about";
	}
	
	
	@RequestMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title","Smart Contact Manager");
		model.addAttribute("user", new User());
		
		return "signup";
	}
	
	
	@RequestMapping( value="/do_register",method = RequestMethod.POST)
	public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult result,@RequestParam(value="agreement" ,defaultValue = "false") boolean agreement,Model model , HttpSession session) {
		
		try {
			if(!agreement) {
				System.out.println("Please Agree Terms and Conditions");
				throw new Exception("Please Agree Terms and Conditions");
			}
			
			if(result.hasErrors()) {
				System.out.println("Error"+result.toString());
				model.addAttribute("user",user);
				return "signup";
			}
			
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageurl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			System.out.println("Agreement"+agreement);
			System.out.println("User"+user.toString());
			
			this.userRepository.save(user);
			model.addAttribute("user",new User());
			session.setAttribute("message", new Message("Successfully Registered!!","alert-success"));
			
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user",user);
			session.setAttribute("message", new Message("Something went wrong!!"+e.getMessage(),"alert-danger"));
			return "signup";
		}
		
		
		return "signup";
	}
	
	@GetMapping("/signin")
	public String customLogin(Model model) {
		model.addAttribute("title" ,"Login Page");
		
		return "login";
		
	}
	
	
	
	
	
	
	
	
	
	
	
	@GetMapping("/test")
	@ResponseBody
	public String test() {
		
		User user = new User();
		user.setId(0);
		user.setName("Abheet Kumar");
		
		Contact contact = new Contact();
		user.getContacts().add(contact);
		userRepository.save(user);
		return "working";
	}
}
