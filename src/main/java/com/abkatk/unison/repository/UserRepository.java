package com.abkatk.unison.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.abkatk.unison.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>{

}
