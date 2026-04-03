package com.example.demo.servicesImplementation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.DTO.TaskDTO;
import com.example.demo.entities.Task;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.repositories.TaskRepository;
import com.example.demo.services.TaskService;

@Service
public class TaskServiceImpl implements TaskService {

    private static final String IAM_SERVICE = "Identity-Access-Management";

    private final TaskRepository repo;
    private final RestTemplate restTemplate;

    public TaskServiceImpl(TaskRepository repo, RestTemplate restTemplate) {
        this.repo = repo;
        this.restTemplate = restTemplate;
    }

    @Override
    public TaskDTO create(TaskDTO dto) {

        // Validate assigned user
        Boolean exists = restTemplate.getForObject(
            "http://Identity-Access-Management/internal/users/" + dto.getAssignedTo()+ "/exists",
            Boolean.class
        );

        if (exists == null || !exists) {
            throw new ResourceNotFoundException(
                "User not found with id: " + dto.getAssignedTo()
            );
        }

        Task task = new Task();
        task.setAssignedTo(dto.getAssignedTo());
        task.setRelatedEntityID(dto.getRelatedEntityID());
        task.setDescription(dto.getDescription());
        task.setDueDate(dto.getDueDate());
        task.setStatus(dto.getStatus());

        return mapToDTO(repo.save(task));
    }

    @Override
    public List<TaskDTO> getAll() {
        List<TaskDTO> list = new ArrayList<>();
        for (Task t : repo.findAll()) {
            list.add(mapToDTO(t));
        }
        return list;
    }

    @Override
    public TaskDTO getById(Long id) {
        return mapToDTO(repo.findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException("Task not found with id: " + id)));
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("Task not found with id: " + id);
        }
        repo.deleteById(id);
    }

    private TaskDTO mapToDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setTaskID(task.getTaskID());
        dto.setAssignedTo(task.getAssignedTo());
        dto.setRelatedEntityID(task.getRelatedEntityID());
        dto.setDescription(task.getDescription());
        dto.setDueDate(task.getDueDate());
        dto.setStatus(task.getStatus());
        return dto;
    }
}