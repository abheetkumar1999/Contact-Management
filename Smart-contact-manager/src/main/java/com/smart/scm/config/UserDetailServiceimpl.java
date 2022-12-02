package com.smart.scm.config;

import org.springframework.beans.factory.annotation.Autowired;
import com.smart.scm.entites.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.smart.scm.dao.UserRepository;

public class UserDetailServiceimpl implements UserDetailsService {
	@Autowired
	private UserRepository userRepository;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// fetching user from the DB
		User user = userRepository.getUserByUserName(username);
		if(user == null) {
			throw new UsernameNotFoundException("could not found the User!");
		}
		customUserDetails customuserdetails = new customUserDetails(user);
		return customuserdetails;
	}
	
}
