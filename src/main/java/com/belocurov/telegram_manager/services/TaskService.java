package com.belocurov.telegram_manager.services;

import com.belocurov.telegram_manager.models.Task;
import com.belocurov.telegram_manager.models.User;
import com.belocurov.telegram_manager.repositories.TaskRepository;
import com.belocurov.telegram_manager.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository; // Add this line

    @Autowired
    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Task createTask(Long userId, String text) {
        User user = userRepository.getReferenceById(userId);
        if (user != null) {
            Task newTask = new Task();
            newTask.setUser(user);
            newTask.setText(text);
            return taskRepository.save(newTask);
        }
        return null;
    }

    @Transactional(readOnly = true)
    public List<Task> getTasksByUserId(Long userId) {
        return taskRepository.findByUserId(userId);
    }

    @Transactional
    public Task updateTask(Long taskId, boolean isDone, String newText) {
        Task task = taskRepository.getReferenceById(taskId);
        task.setDone(isDone);
        task.setText(newText);
        return taskRepository.save(task);
    }

    @Transactional
    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }
}