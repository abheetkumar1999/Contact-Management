//package com.smart.scm.controller;
//
//import java.security.Principal;
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.smart.scm.dao.ContactRepository;
//import com.smart.scm.dao.UserRepository;
//import com.smart.scm.entites.Contact;
//import com.smart.scm.entites.User;
//
//@RestController
//public class SearchController {
//	@Autowired
//	private UserRepository userRepository;
//	
//	@Autowired
//	private ContactRepository contactRepository;
//	//search handler
//	@GetMapping("/search/{query}")
//	public ResponseEntity<?> search(@PathVariable("query") String query,Principal principal ){
//		System.out.println("Query: "+query);
//        User user = this.userRepository.getUserByUserName(principal.getName());
//        System.out.println(user);
//        List<Contact> contacts = this.contactRepository.findByNameContainingAndUser(query,user);
//
//        return ResponseEntity.ok(contacts);
//		
//	}
//}
