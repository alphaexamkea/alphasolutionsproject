package com.kea.alphasolutions.repository;

import com.kea.alphasolutions.model.Subproject;
import com.kea.alphasolutions.repository.rowmapper.SubprojectRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SubprojectRepository {

    private final JdbcTemplate jdbcTemplate;

    public SubprojectRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Subproject> findAllByProjectId(int projectId) {
        String sql = "SELECT * FROM Subproject WHERE project_id = ?";
        return jdbcTemplate.query(sql, new SubprojectRowMapper(), projectId);
    }

    public Subproject findById(int id) {
        String sql = "SELECT * FROM Subproject WHERE subproject_id = ?";
        List<Subproject> results = jdbcTemplate.query(sql, new SubprojectRowMapper(), id);
        return results.isEmpty() ? null : results.get(0);
    }

    public void save(Subproject subproject) {
        String sql = "INSERT INTO Subproject (project_id, name, description, start_date, end_date) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                subproject.getProjectId(),
                subproject.getName(),
                subproject.getDescription(),
                subproject.getStartDate(),
                subproject.getEndDate());
    }

    public void update(Subproject subproject) {
        String sql = "UPDATE Subproject SET name = ?, description = ?, start_date = ?, end_date = ? WHERE subproject_id = ?";
        jdbcTemplate.update(sql,
                subproject.getName(),
                subproject.getDescription(),
                subproject.getStartDate(),
                subproject.getEndDate(),
                subproject.getSubprojectId());
    }

    public void delete(int id) {
        String sql = "DELETE FROM Subproject WHERE subproject_id = ?";
        jdbcTemplate.update(sql, id);
    }
}
