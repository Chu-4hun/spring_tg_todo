package com.belocurov.telegram_manager.repositories;

import com.belocurov.telegram_manager.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
