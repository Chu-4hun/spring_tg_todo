package com.belocurov.telegram_manager.repositories;

import com.belocurov.telegram_manager.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
