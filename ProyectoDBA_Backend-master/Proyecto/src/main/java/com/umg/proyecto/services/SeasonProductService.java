package com.umg.proyecto.services;

import com.umg.proyecto.models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeasonProductService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Obtener productos asociados a una temporada
    public List<Product> getProductsBySeason(Integer seasonId) {
        String sql = "SELECT p.* FROM PRODUCT p " +
                "JOIN SEASON_PRODUCT sp ON p.ID = sp.PRODUCT_ID " +
                "WHERE sp.SEASON_ID = ?";
        return jdbcTemplate.query(sql, new Object[]{seasonId}, (rs, rowNum) -> {
            Product product = new Product();
            product.setId(rs.getInt("ID"));
            product.setName(rs.getString("NAME"));
            product.setPrice(rs.getFloat("PRICE"));
            product.setDescription(rs.getString("DESCRIPTION"));
            product.setImage(rs.getString("IMAGE"));
            product.setStock(rs.getInt("STOCK"));
            return product;
        });
    }

    public void addProductToSeason(Integer productId, Integer seasonId) {
        String sql = "INSERT INTO SEASON_PRODUCT (PRODUCT_ID, SEASON_ID) VALUES (?, ?)";
        jdbcTemplate.update(sql, productId, seasonId);
    }
    public void removeProductFromSeason(Integer productId, Integer seasonId) {
        String sql = "DELETE FROM SEASON_PRODUCT WHERE PRODUCT_ID = ? AND SEASON_ID = ?";
        jdbcTemplate.update(sql, productId, seasonId);
    }

}
