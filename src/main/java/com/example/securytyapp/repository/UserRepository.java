package com.example.securytyapp.repository;

import com.example.securytyapp.domaines.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
