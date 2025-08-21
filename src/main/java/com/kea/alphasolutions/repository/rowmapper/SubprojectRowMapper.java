package com.kea.alphasolutions.repository.rowmapper;

import com.kea.alphasolutions.model.Subproject;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SubprojectRowMapper implements RowMapper<Subproject> {
    @Override
    public Subproject mapRow(ResultSet rs, int rowNum) throws SQLException {
        Subproject subproject = new Subproject();
        subproject.setSubprojectId(rs.getInt("subproject_id"));
        subproject.setProjectId(rs.getInt("project_id"));
        subproject.setName(rs.getString("name"));
        subproject.setDescription(rs.getString("description"));
        if (rs.getDate("start_date") != null) {
            subproject.setStartDate(rs.getDate("start_date").toLocalDate());
        }
        if (rs.getDate("end_date") != null) {
            subproject.setEndDate(rs.getDate("end_date").toLocalDate());
        }
        return subproject;
    }
}
