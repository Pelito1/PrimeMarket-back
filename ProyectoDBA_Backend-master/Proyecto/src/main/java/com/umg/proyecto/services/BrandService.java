package com.umg.proyecto.services;
import com.umg.proyecto.models.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
@Service
public class BrandService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Brand> brandRowMapper = new RowMapper<Brand>() {
        @Override
        public Brand mapRow(ResultSet rs, int rowNum) throws SQLException {
            Brand brand = new Brand();
            brand.setId(rs.getInt("ID"));
            brand.setName(rs.getString("NAME"));
            return brand;
        }
    };

    public List<Brand> findAll() {
        String sql = "SELECT * FROM BRAND";
        return jdbcTemplate.query(sql, brandRowMapper);
    }

    public Brand findById(Integer id) {
        String sql = "SELECT * FROM BRAND WHERE ID = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, brandRowMapper);
    }


    public void save(Brand brand) {
        String sql = "INSERT INTO BRAND (ID, NAME) VALUES (BRAND_SEQ.NEXTVAL, ?)";
        jdbcTemplate.update(sql, brand.getName());
    }

    public void update(Brand brand) {
        String sql = "UPDATE BRAND SET NAME = ? WHERE ID = ?";
        jdbcTemplate.update(sql, brand.getName(), brand.getId());
    }

    public void delete(Integer id) {
        String sql = "DELETE FROM BRAND WHERE ID = ?";
        jdbcTemplate.update(sql, id);
    }
}