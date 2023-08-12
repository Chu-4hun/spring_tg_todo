package com.belocurov.telegram_manager.repositories;

import com.belocurov.telegram_manager.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

     boolean existsByChatId(Long id);
     Optional<User> findByChatId(Long id);

    default User createIfNotExists(Long id) {
        if (existsByChatId(id)) {
            return findByChatId(id).orElse(null); // Return existing user
        } else {
            User newUser = new User();
            newUser.setChatId(id);
            return save(newUser); // Create and save new user
        }
    }
}
