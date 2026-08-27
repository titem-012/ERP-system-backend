package com.echomenswear.repository;

import com.echomenswear.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    
    Optional<User> findByUsername(String username);
    
    // NEW: Added this method so the UserService can find a user by their email
    Optional<User> findByEmail(String email); 
    
    List<User> findByRole(String role);
    
}