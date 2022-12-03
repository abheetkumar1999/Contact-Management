package com.smart.scm.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.websocket.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.scm.dao.ContactRepository;
import com.smart.scm.dao.UserRepository;
import com.smart.scm.entites.Contact;
import com.smart.scm.entites.User;
import com.smart.scm.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;

	// method for adding the common Data
	@ModelAttribute
	public void addCommonData(Model m, Principal p) {
		String username = p.getName();
		System.out.println("welcome " + username);
		User user = this.userRepository.getUserByUser(username);
		System.out.println("welcome " + user);

		m.addAttribute("user", user);
	}

	// home Dashboard
	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) {

		return "normal/user_dashboard";
	}

	// open add form handler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());

		return "normal/add_contact_form";

	}

	// processing add contact form
	// message will be sent in the session
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Principal principal, HttpSession session) {

		try {
			String name = principal.getName();

			User user = this.userRepository.getUserByUser(name);

			// processing Image
			if (file.isEmpty()) {
				System.out.println("No Image provided");
				contact.setImage("contact.png");
			} else {
				// upload the file to the folder and update the name
				contact.setImage(file.getOriginalFilename());

				File saveFile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				System.out.println("Image is Uploaded");
			}

			user.getContacts().add(contact);

			contact.setUser(user);

			this.userRepository.save(user);

			System.out.println("Contact Added");

			System.out.println("DATA" + contact);
			// print message ...
			session.setAttribute("message", new Message("Your Contact is successfully Added", "success"));

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.getMessage());
			e.printStackTrace();
			// error message
			session.setAttribute("message", new Message("Something went wrong!! try again.", "danger"));

		}
		return "normal/add_contact_form";
	}

	// show contact handler
	// per page 5 contacts
	// current page
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model m, Principal principal) {
		m.addAttribute("title", "Show user contacts");
		String currentUser = principal.getName();
		User currentUserDetails = this.userRepository.getUserByUser(currentUser);
		Pageable pageable = PageRequest.of(page, 5);

		Page<Contact> contact = this.contactRepository.findContactsByUserId(currentUserDetails.getId(), pageable);
		m.addAttribute("contacts", contact);
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPages", contact.getTotalPages());
		return "normal/show_contacts";
	}

	// showing particular contact Details
	@RequestMapping("/{cId}/contact")
	public String showContactDetails(@PathVariable("cId") int id, Model m, Principal p) {
		System.out.println(id);
		String currentUser = p.getName();

		User currentUserDetails = this.userRepository.getUserByUser(currentUser);
		// authorized contacts

		Optional<Contact> contactoptional = this.contactRepository.findById(id);
		Contact contact = contactoptional.get();

		if (currentUserDetails.getId() == contact.getUser().getId()) {
			m.addAttribute("contact", contact);
			m.addAttribute("title", contact.getFirstName());
		}

		return "normal/contact_details";
	}

	// delete contact handler
	@GetMapping("/delete/{cId}")
	public String deleteContact(@PathVariable("cId") Integer id, Principal p, HttpSession session) {
		Optional<Contact> contactOptional = this.contactRepository.findById(id);
		Contact contact = contactOptional.get();

		String currentUser = p.getName();

		User currentUserDetails = this.userRepository.getUserByUser(currentUser);
		// check
		currentUserDetails.getContacts().remove(contact);
		this.userRepository.save(currentUserDetails);
//		if (currentUserDetails.getId() == contact.getUser().getId()) {
//			contact.setUser(null);
//			this.contactRepository.delete(contact);
		session.setAttribute("message", new Message("contact deleted successfully", "success"));
//		}
		return "redirect:/user/show-contacts/0";
	}

	// open update form handler
	@PostMapping("/update-contact/{cId}")
	public String updateForm(@PathVariable("cId") Integer id, Model m) {
		m.addAttribute("title", "Update Contact");
		Contact contact = this.contactRepository.findById(id).get();
		m.addAttribute("contact", contact);
		return "normal/update_contact";
	}

	@RequestMapping(value = "/process-update", method = RequestMethod.POST)
	public String updatehandler(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Principal principal, HttpSession session) {
		
		try {
			
			Contact oldcontact =this.contactRepository.findById(contact.getcId()).get();
			if(!file.isEmpty()) {
				
				
				
				
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Image is Uploaded");
				contact.setImage(file.getOriginalFilename());
			}
			else {
				contact.setImage(oldcontact.getImage());
			}
			User user = this.userRepository.getUserByUser(principal.getName());
			contact.setUser(user);
			this.contactRepository.save(contact);
			session.setAttribute("message", new Message("you contact has been updated", "success"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Contact name" + contact.getFirstName());
		System.out.println("Contact Id" + contact.getcId());
		return "redirect:"+contact.getcId()+"/contact";
	}

	// your profile
	@GetMapping("/profile")
	public String yourProfile(Model m) {
		m.addAttribute("title", "My profile");
		return "normal/profile";

	}

}
