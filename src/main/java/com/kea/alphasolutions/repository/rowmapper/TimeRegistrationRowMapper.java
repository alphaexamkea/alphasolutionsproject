package com.kea.alphasolutions.repository.rowmapper;

import com.kea.alphasolutions.model.TimeRegistration;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TimeRegistrationRowMapper implements RowMapper<TimeRegistration> {
    @Override
    public TimeRegistration mapRow(ResultSet rs, int rowNum) throws SQLException {
        TimeRegistration timeRegistration = new TimeRegistration();
        timeRegistration.setTimeId(rs.getInt("time_id"));
        timeRegistration.setTaskId(rs.getInt("task_id"));
        timeRegistration.setResourceId(rs.getInt("resource_id"));
        if (rs.getDate("date") != null) {
            timeRegistration.setDate(rs.getDate("date").toLocalDate());
        }
        timeRegistration.setHours(rs.getDouble("hours"));
        return timeRegistration;
    }
}