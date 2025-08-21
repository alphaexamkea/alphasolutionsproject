package com.kea.alphasolutions.repository.rowmapper;

import com.kea.alphasolutions.model.Resource;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ResourceRowMapper implements RowMapper<Resource> {
    @Override
    public Resource mapRow(ResultSet rs, int rowNum) throws SQLException {
        Resource resource = new Resource();
        resource.setResourceId(rs.getInt("resource_id"));
        resource.setName(rs.getString("name"));
        resource.setRole(rs.getString("role"));
        resource.setSystemRole(rs.getString("system_role"));
        resource.setUsername(rs.getString("username"));
        resource.setPassword(rs.getString("password"));
        return resource;
    }
}
