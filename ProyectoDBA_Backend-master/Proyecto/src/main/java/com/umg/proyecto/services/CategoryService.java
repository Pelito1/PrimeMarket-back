package com.umg.proyecto.services;

import com.umg.proyecto.models.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Category> categoryRowMapper = new RowMapper<Category>() {
        @Override
        public Category mapRow(ResultSet rs, int rowNum) throws SQLException {
            Category category = new Category();
            category.setId(rs.getInt("ID"));
            category.setName(rs.getString("NAME"));
            category.setParentCategoryId(rs.getObject("PARENT_CATEGORY_ID") != null ? rs.getInt("PARENT_CATEGORY_ID") : null);
            return category;
        }
    };

    public List<Category> findAll() {
        String sql = "SELECT * FROM CATEGORY";
        return jdbcTemplate.query(sql, categoryRowMapper);
    }

    public Category findById(Integer id) {
        String sql = "SELECT * FROM CATEGORY WHERE ID = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, categoryRowMapper);
    }

    public void save(Category category) {
        String sql = "INSERT INTO CATEGORY (ID, NAME, PARENT_CATEGORY_ID) " +
                "VALUES (CATEGORY_SEQ.NEXTVAL, ?, ?)";
        jdbcTemplate.update(sql, category.getName(), category.getParentCategoryId());
    }

    public void update(Category category) {
        String sql = "UPDATE CATEGORY SET NAME = ?, PARENT_CATEGORY_ID = ? WHERE ID = ?";
        jdbcTemplate.update(sql, category.getName(), category.getParentCategoryId(), category.getId());
    }

    public void delete(Integer id) {
        String sql = "DELETE FROM CATEGORY WHERE ID = ?";
        jdbcTemplate.update(sql, id);
    }

    public List<Category> findSubcategories(Integer parentCategoryId) {
        String sql = "SELECT * FROM CATEGORY WHERE PARENT_CATEGORY_ID = ?";
        return jdbcTemplate.query(sql, new Object[]{parentCategoryId}, categoryRowMapper);
    }
}
