package com.kea.alphasolutions.repository;

import com.kea.alphasolutions.model.Task;
import com.kea.alphasolutions.repository.rowmapper.TaskRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TaskRepository {

    private final JdbcTemplate jdbcTemplate;

    public TaskRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Task> findAllBySubprojectId(int subprojectId) {
        String sql = "SELECT * FROM Task WHERE subproject_id = ?";
        return jdbcTemplate.query(sql, new TaskRowMapper(), subprojectId);
    }

    public Task findById(int id) {
        String sql = "SELECT * FROM Task WHERE task_id = ?";
        List<Task> results = jdbcTemplate.query(sql, new TaskRowMapper(), id);
        return results.isEmpty() ? null : results.get(0);
    }

    public void save(Task task) {
        String sql = "INSERT INTO Task (subproject_id, name, description, estimated_hours, deadline, status) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                task.getSubprojectId(),
                task.getName(),
                task.getDescription(),
                task.getEstimatedHours(),
                task.getDeadline(),
                task.getStatus());
    }

    public void update(Task task) {
        String sql = "UPDATE Task SET name = ?, description = ?, estimated_hours = ?, deadline = ?, status = ? WHERE task_id = ?";
        jdbcTemplate.update(sql,
                task.getName(),
                task.getDescription(),
                task.getEstimatedHours(),
                task.getDeadline(),
                task.getStatus(),
                task.getTaskId());
    }

    public void delete(int id) {
        String sql = "DELETE FROM Task WHERE task_id = ?";
        jdbcTemplate.update(sql, id);
    }
}
