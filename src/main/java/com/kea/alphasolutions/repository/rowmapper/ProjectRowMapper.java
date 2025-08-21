package com.kea.alphasolutions.repository.rowmapper;

import com.kea.alphasolutions.model.Project;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProjectRowMapper implements RowMapper<Project> {
    @Override
    public Project mapRow(ResultSet rs, int rowNum) throws SQLException {
        Project project = new Project();
        project.setProjectId(rs.getInt("project_id"));
        project.setName(rs.getString("name"));
        project.setDescription(rs.getString("description"));
        if (rs.getDate("start_date") != null) {
            project.setStartDate(rs.getDate("start_date").toLocalDate());
        }
        if (rs.getDate("end_date") != null) {
            project.setEndDate(rs.getDate("end_date").toLocalDate());
        }
        return project;
    }
}
