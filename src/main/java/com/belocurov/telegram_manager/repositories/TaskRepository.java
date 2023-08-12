package com.belocurov.telegram_manager.repositories;

import com.belocurov.telegram_manager.models.Task;
import com.belocurov.telegram_manager.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserId(Long userId);
}
