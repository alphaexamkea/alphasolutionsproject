package com.kea.alphasolutions.repository.rowmapper;

import com.kea.alphasolutions.model.Task;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TaskRowMapper implements RowMapper<Task> {
    @Override
    public Task mapRow(ResultSet rs, int rowNum) throws SQLException {
        Task task = new Task();
        task.setTaskId(rs.getInt("task_id"));
        task.setSubprojectId(rs.getInt("subproject_id"));
        task.setName(rs.getString("name"));
        task.setDescription(rs.getString("description"));
        task.setEstimatedHours(rs.getDouble("estimated_hours"));
        if (rs.getDate("deadline") != null) {
            task.setDeadline(rs.getDate("deadline").toLocalDate());
        }
        task.setStatus(rs.getString("status"));
        return task;
    }
}
