package com.shellshellfish.account.repositories;

import com.shellshellfish.account.model.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
	List<User> findByCellPhoneAndPasswordHash(String cellphone, String passwordhash);
	User findById(Long id);
	List<User> findByCellPhone(String cellphone);
}
