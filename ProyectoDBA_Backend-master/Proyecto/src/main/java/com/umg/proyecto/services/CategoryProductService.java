package com.umg.proyecto.services;

import com.umg.proyecto.models.Category;
import com.umg.proyecto.models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class CategoryProductService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Método para agregar un producto a una categoría
    public void addProductToCategory(Integer productId, Integer categoryId) {
        String sql = "INSERT INTO CATEGORY_PRODUCT (CATEGORY_ID, PRODUCT_ID) VALUES (?, ?)";
        jdbcTemplate.update(sql, categoryId, productId);
    }

    // Método para eliminar un producto de una categoría
    public void removeProductFromCategory(Integer productId, Integer categoryId) {
        String sql = "DELETE FROM CATEGORY_PRODUCT WHERE CATEGORY_ID = ? AND PRODUCT_ID = ?";
        jdbcTemplate.update(sql, categoryId, productId);
    }

    // Método para obtener los objetos Product por categoría
    public List<Product> getProductsByCategory(Integer categoryId) {
        String sql = "SELECT p.* FROM PRODUCT p INNER JOIN CATEGORY_PRODUCT cp ON p.ID = cp.PRODUCT_ID WHERE cp.CATEGORY_ID = ?";
        return jdbcTemplate.query(sql, new Object[]{categoryId}, new ProductRowMapper());
    }

    // Método para obtener los objetos Category por producto
    public List<Category> getCategoriesByProduct(Integer productId) {
        String sql = "SELECT c.* FROM CATEGORY c INNER JOIN CATEGORY_PRODUCT cp ON c.ID = cp.CATEGORY_ID WHERE cp.PRODUCT_ID = ?";
        return jdbcTemplate.query(sql, new Object[]{productId}, new CategoryRowMapper());
    }

    // RowMapper para Product
    private static class ProductRowMapper implements RowMapper<Product> {
        @Override
        public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
            Product product = new Product();
            product.setId(rs.getInt("ID"));
            product.setName(rs.getString("NAME"));
            product.setPrice(rs.getFloat("PRICE"));
            product.setDescription(rs.getString("DESCRIPTION"));
            product.setStock(rs.getInt("STOCK"));
            product.setImage(rs.getString("IMAGE"));
            // Asumimos que el Brand se obtiene por otro método o lo puedes agregar si es necesario
            return product;
        }
    }

    // RowMapper para Category
    private static class CategoryRowMapper implements RowMapper<Category> {
        @Override
        public Category mapRow(ResultSet rs, int rowNum) throws SQLException {
            Category category = new Category();
            category.setId(rs.getInt("ID"));
            category.setName(rs.getString("NAME"));
            category.setParentCategoryId(rs.getObject("PARENT_CATEGORY_ID") != null ? rs.getInt("PARENT_CATEGORY_ID") : null);
            return category;
        }
    }
}
