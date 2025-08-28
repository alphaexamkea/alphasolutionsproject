package com.kea.alphasolutions.repository;

import com.kea.alphasolutions.repository.rowmapper.ProjectRowMapper;
import com.kea.alphasolutions.model.Project;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProjectRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProjectRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Project> findAll() {
        String sql = "SELECT * FROM Project";
        return jdbcTemplate.query(sql, new ProjectRowMapper());
    }

    public Project findById(int id) {
        String sql = "SELECT * FROM Project WHERE project_id = ?";
        List<Project> results = jdbcTemplate.query(sql, new ProjectRowMapper(), id);

        if (results.isEmpty()) {
            return null;
        } else {
            return results.get(0);
        }
    }

    public void save(Project project) {
        String sql = "INSERT INTO Project (name, description, start_date, end_date) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                project.getName(),
                project.getDescription(),
                project.getStartDate(),
                project.getEndDate());
    }

    public void update(Project project) {
        String sql = "UPDATE Project SET name = ?, description = ?, start_date = ?, end_date = ? WHERE project_id = ?";
        jdbcTemplate.update(sql,
                project.getName(),
                project.getDescription(),
                project.getStartDate(),
                project.getEndDate(),
                project.getProjectId());
    }

    public void delete(int id) {
        String sql = "DELETE FROM Project WHERE project_id = ?";
        jdbcTemplate.update(sql, id);
    }

    public int countSubprojectsByProjectId(int projectId) {
        String sql = "SELECT COUNT(*) FROM Subproject WHERE project_id = ?";
        Integer result = jdbcTemplate.queryForObject(sql, Integer.class, projectId);
        return result != null ? result : 0;
    }

    public int countTasksByProjectId(int projectId) {
        String sql = "SELECT COUNT(*) FROM Task t JOIN Subproject s ON t.subproject_id = s.subproject_id WHERE s.project_id = ?";
        Integer result = jdbcTemplate.queryForObject(sql, Integer.class, projectId);
        return result != null ? result : 0;
    }

    public int countResourcesByProjectId(int projectId) {
        String sql = "SELECT COUNT(DISTINCT tr.resource_id) FROM TimeRegistration tr JOIN Task t ON tr.task_id = t.task_id JOIN Subproject s ON t.subproject_id = s.subproject_id WHERE s.project_id = ?";
        Integer result = jdbcTemplate.queryForObject(sql, Integer.class, projectId);
        return result != null ? result : 0;
    }
}
