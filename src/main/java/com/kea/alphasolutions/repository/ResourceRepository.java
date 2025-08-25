package com.kea.alphasolutions.repository;

import com.kea.alphasolutions.model.Resource;
import com.kea.alphasolutions.repository.rowmapper.ResourceRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ResourceRepository {

    private final JdbcTemplate jdbcTemplate;

    public ResourceRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Resource> findAll() {
        String sql = "SELECT * FROM Resource";
        return jdbcTemplate.query(sql, new ResourceRowMapper());
    }

    public Resource findById(int id) {
        String sql = "SELECT * FROM Resource WHERE resource_id = ?";
        List<Resource> results = jdbcTemplate.query(sql, new ResourceRowMapper(), id);
        return results.isEmpty() ? null : results.get(0);
    }

    public void save(Resource resource) {
        String sql = "INSERT INTO Resource (name, role, system_role, username, password) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                resource.getName(),
                resource.getRole(),
                resource.getSystemRole(),
                resource.getUsername(),
                resource.getPassword());
    }

    public void update(Resource resource) {
        String sql = "UPDATE Resource SET name = ?, role = ?, system_role = ?, username = ?, password = ? WHERE resource_id = ?";
        jdbcTemplate.update(sql,
                resource.getName(),
                resource.getRole(),
                resource.getSystemRole(),
                resource.getUsername(),
                resource.getPassword(),
                resource.getResourceId());
    }

    public void delete(int id) {
        String sql = "DELETE FROM Resource WHERE resource_id = ?";
        jdbcTemplate.update(sql, id);
    }

    public Resource findByUsername(String username) {
        String sql = "SELECT * FROM Resource WHERE username = ?";
        List<Resource> results = jdbcTemplate.query(sql, new ResourceRowMapper(), username);
        return results.isEmpty() ? null : results.get(0);
    }
}
