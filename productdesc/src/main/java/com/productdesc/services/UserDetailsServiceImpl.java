package com.productdesc.services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.productdesc.entities.User;
import com.productdesc.repositories.UserRepository;

@Service 
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

	//Autowired UserRepository dao to use getUserByUserName method by implementing JpaRepository
	 @Autowired
	 private UserRepository userRepository;
	
	 @Override public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	  
	 // fetching user from database User user =
	 User user = userRepository.getUserByUserName(username);
	 
	 if (user==null) { 
		 throw new UsernameNotFoundException("Could not found user!!"); 
	 }
	 
	 UserDetailsImpl userDetailsImpl=new UserDetailsImpl(user);
	 
	 return userDetailsImpl; }
	 
}
