package com.umg.proyecto.services;

import com.umg.proyecto.models.Brand;
import com.umg.proyecto.models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class SeasonProductService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Método para vincular un producto con una temporada (INSERT)
    public void addProductToSeason(Integer productId, Integer seasonId) {
        String sql = "INSERT INTO SEASON_PRODUCT (PRODUCT_ID, SEASON_ID) VALUES (?, ?)";
        jdbcTemplate.update(sql, productId, seasonId);
    }

    // Método para eliminar la vinculación de un producto con una temporada (DELETE)
    public void removeProductFromSeason(Integer productId, Integer seasonId) {
        String sql = "DELETE FROM SEASON_PRODUCT WHERE PRODUCT_ID = ? AND SEASON_ID = ?";
        jdbcTemplate.update(sql, productId, seasonId);
    }

    // Método para obtener todos los productos vinculados a una temporada
    public List<Product> getProductsBySeason(Integer seasonId) {
        String sql = "SELECT p.*, b.NAME AS BRAND_NAME " +
                "FROM PRODUCT p " +
                "INNER JOIN SEASON_PRODUCT sp ON p.ID = sp.PRODUCT_ID " +
                "LEFT JOIN BRAND b ON p.BRAND_ID = b.ID " +
                "WHERE sp.SEASON_ID = ?";
        return jdbcTemplate.query(sql, new Object[]{seasonId}, productRowMapper);
    }

    private final RowMapper<Product> productRowMapper = new RowMapper<Product>() {
        @Override
        public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
            Product product = new Product();
            product.setId(rs.getInt("ID"));
            product.setName(rs.getString("NAME"));
            product.setPrice(rs.getFloat("PRICE"));
            product.setDescription(rs.getString("DESCRIPTION"));
            product.setStock(rs.getInt("STOCK"));
            product.setImage(rs.getString("IMAGE"));

            // Instancia el objeto Brand y establece sus propiedades
            Brand brand = new Brand();
            brand.setId(rs.getInt("BRAND_ID"));
            brand.setName(rs.getString("BRAND_NAME")); // Asegúrate de seleccionar el nombre de la marca en la consulta SQL
            product.setBrand(brand);

            return product;
        }
    };
    // Método para obtener todas las temporadas a las que está vinculado un producto
    public List<Integer> getSeasonsByProduct(Integer productId) {
        String sql = "SELECT SEASON_ID FROM SEASON_PRODUCT WHERE PRODUCT_ID = ?";
        return jdbcTemplate.queryForList(sql, new Object[]{productId}, Integer.class);
    }
}
