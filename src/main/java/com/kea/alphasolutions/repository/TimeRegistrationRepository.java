package com.kea.alphasolutions.repository;

import com.kea.alphasolutions.model.TimeRegistration;
import com.kea.alphasolutions.repository.rowmapper.TimeRegistrationRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TimeRegistrationRepository {

    private final JdbcTemplate jdbcTemplate;

    public TimeRegistrationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<TimeRegistration> findAllByTaskId(int taskId) {
        String sql = "SELECT * FROM TimeRegistration WHERE task_id = ? ORDER BY date DESC";
        return jdbcTemplate.query(sql, new TimeRegistrationRowMapper(), taskId);
    }

    public List<TimeRegistration> findAllByResourceId(int resourceId) {
        String sql = "SELECT * FROM TimeRegistration WHERE resource_id = ? ORDER BY date DESC";
        return jdbcTemplate.query(sql, new TimeRegistrationRowMapper(), resourceId);
    }

    public TimeRegistration findById(int id) {
        String sql = "SELECT * FROM TimeRegistration WHERE time_id = ?";
        List<TimeRegistration> results = jdbcTemplate.query(sql, new TimeRegistrationRowMapper(), id);
        return results.isEmpty() ? null : results.get(0);
    }

    public void save(TimeRegistration timeRegistration) {
        String sql = "INSERT INTO TimeRegistration (task_id, resource_id, date, hours) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                timeRegistration.getTaskId(),
                timeRegistration.getResourceId(),
                timeRegistration.getDate(),
                timeRegistration.getHours());
    }

    public void update(TimeRegistration timeRegistration) {
        String sql = "UPDATE TimeRegistration SET task_id = ?, resource_id = ?, date = ?, hours = ? WHERE time_id = ?";
        jdbcTemplate.update(sql,
                timeRegistration.getTaskId(),
                timeRegistration.getResourceId(),
                timeRegistration.getDate(),
                timeRegistration.getHours(),
                timeRegistration.getTimeId());
    }

    public void delete(int id) {
        String sql = "DELETE FROM TimeRegistration WHERE time_id = ?";
        jdbcTemplate.update(sql, id);
    }

    public double getTotalHoursByTaskId(int taskId) {
        String sql = "SELECT COALESCE(SUM(hours), 0) FROM TimeRegistration WHERE task_id = ?";
        Double result = jdbcTemplate.queryForObject(sql, Double.class, taskId);
        return result != null ? result : 0.0;
    }

    public double getTotalHoursBySubprojectId(int subprojectId) {
        String sql = "SELECT COALESCE(SUM(tr.hours), 0) FROM TimeRegistration tr " +
                "JOIN Task t ON tr.task_id = t.task_id " +
                "WHERE t.subproject_id = ?";
        Double result = jdbcTemplate.queryForObject(sql, Double.class, subprojectId);
        return result != null ? result : 0.0;
    }

    public double getTotalHoursByProjectId(int projectId) {
        String sql = "SELECT COALESCE(SUM(tr.hours), 0) FROM TimeRegistration tr " +
                "JOIN Task t ON tr.task_id = t.task_id " +
                "JOIN Subproject s ON t.subproject_id = s.subproject_id " +
                "WHERE s.project_id = ?";
        Double result = jdbcTemplate.queryForObject(sql, Double.class, projectId);
        return result != null ? result : 0.0;
    }
}