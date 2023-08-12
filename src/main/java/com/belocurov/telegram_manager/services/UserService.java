package com.belocurov.telegram_manager.services;

import com.belocurov.telegram_manager.models.User;
import com.belocurov.telegram_manager.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User createNewUserIfUnique(Long userId) {
        if (!userRepository.existsById(userId)) {
            User newUser = new User();
            newUser.setId(userId);
            return userRepository.save(newUser);
        }
        return userRepository.findById(userId).orElse(null);
    }

//    //Uncomment if user have additional fields except id
//    @Transactional
//    public User updateUser(Long userId, String newName) {
//        User user = userRepository.findById(userId).orElse(null);
//        if (user != null) {
//            //add something
//            return userRepository.save(user);
//        }
//        return null; // User not found
//    }

}