package com.smart.scm.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.scm.entites.Contact;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
	// implement the method for the pagination
	@Query("from Contact as c where c.user.id=:userid")
	public Page<Contact> findContactsByUserId(@Param("userid")int userid, Pageable page);
	//search
	//public List<Contact> findByNameContainingAndUser(String keywords, User user);
}
