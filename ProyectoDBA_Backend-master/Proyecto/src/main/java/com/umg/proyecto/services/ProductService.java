package com.umg.proyecto.services;

import com.umg.proyecto.models.Brand;
import com.umg.proyecto.models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
@Service
public class ProductService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CategoryProductService categoryProductService;

    // Mapeador de filas para convertir las filas de la base de datos en objetos Product con Brand
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

    // Método para obtener todos los productos
    public List<Product> findAll() {
        String sql = "SELECT p.*, b.NAME AS BRAND_NAME FROM PRODUCT p LEFT JOIN BRAND b ON p.BRAND_ID = b.ID where rownum <=20";
        return jdbcTemplate.query(sql, productRowMapper);
    }

    // Método para obtener un producto por ID
    public Product findById(Integer id) {
        String sql = "SELECT p.*, b.NAME AS BRAND_NAME FROM PRODUCT p LEFT JOIN BRAND b ON p.BRAND_ID = b.ID WHERE p.ID = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, productRowMapper);
    }

    // **Nuevo método para buscar productos por palabra clave**
    public List<Product> searchByKeyword(String keyword) {
        String sql = "SELECT p.*, b.NAME AS BRAND_NAME FROM PRODUCT p LEFT JOIN BRAND b ON p.BRAND_ID = b.ID " +
                "WHERE p.NAME LIKE ? OR p.DESCRIPTION LIKE ?";
        String likePattern = "%" + keyword + "%";
        return jdbcTemplate.query(sql, new Object[]{likePattern, likePattern}, productRowMapper);
    }

    // Método para guardar un nuevo producto
    public void save(Product product) {
        String sql = "INSERT INTO PRODUCT (ID, NAME, PRICE, DESCRIPTION, STOCK, IMAGE, BRAND_ID) " +
                "VALUES (PRODUCT_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, product.getName(), product.getPrice(), product.getDescription(),
                product.getStock(), product.getImage(), product.getBrand().getId());
    }

    // Método para actualizar un producto existente
    public void update(Product product) {
        String sql = "UPDATE PRODUCT SET NAME = ?, PRICE = ?, DESCRIPTION = ?, STOCK = ?, IMAGE = ?, BRAND_ID = ? WHERE ID = ?";
        jdbcTemplate.update(sql, product.getName(), product.getPrice(), product.getDescription(),
                product.getStock(), product.getImage(), product.getBrand().getId(), product.getId());
    }

    // Método para eliminar un producto por ID
    public void delete(Integer id) {
        String sql = "DELETE FROM PRODUCT WHERE ID = ?";
        jdbcTemplate.update(sql, id);
    }

    public void checkAndReduceStock(Integer productId, Integer qty) {
        String sqlCheck = "SELECT STOCK FROM PRODUCT WHERE ID = ?";
        Integer currentStock = jdbcTemplate.queryForObject(sqlCheck, new Object[]{productId}, Integer.class);

        if (currentStock < qty) {
            throw new RuntimeException("Insufficient stock for product ID: " + productId);
        }

        String sqlUpdate = "UPDATE PRODUCT SET STOCK = STOCK - ? WHERE ID = ?";
        jdbcTemplate.update(sqlUpdate, qty, productId);
    }

    public List<Product> getProductsByPriceRange(int minPrice, int maxPrice, int page, int size) {
        String sql = "SELECT * FROM ( " +
                "SELECT p.*, b.NAME AS BRAND_NAME, " +
                "ROW_NUMBER() OVER (ORDER BY p.ID) AS row_num " +
                "FROM PRODUCT p " +
                "LEFT JOIN BRAND b ON p.BRAND_ID = b.ID " +
                "WHERE p.PRICE BETWEEN ? AND ? " +
                ") WHERE row_num BETWEEN ? AND ?";

        int startRow = (page - 1) * size + 1;
        int endRow = page * size;

        return jdbcTemplate.query(sql, new Object[]{minPrice, maxPrice, startRow, endRow}, productRowMapper);
    }

    public int countProductsByPriceRange(int minPrice, int maxPrice) {
        String sql = "SELECT COUNT(*) FROM PRODUCT WHERE PRICE BETWEEN ? AND ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{minPrice, maxPrice}, Integer.class);
    }


    public List<Product> getPaginatedProducts(int page, int size) {
        String sql = "SELECT * FROM ( " +
                "SELECT p.*, b.NAME AS BRAND_NAME, " +
                "ROW_NUMBER() OVER (ORDER BY p.ID) AS row_num " +
                "FROM PRODUCT p " +
                "LEFT JOIN BRAND b ON p.BRAND_ID = b.ID " +
                ") WHERE row_num BETWEEN ? AND ?";

        int startRow = (page - 1) * size + 1;  // Primer producto de la página
        int endRow = page * size;              // Último producto de la página

        return jdbcTemplate.query(sql, new Object[]{startRow, endRow}, productRowMapper);
    }

    public int countAllProducts() {
        String sql = "SELECT COUNT(*) FROM PRODUCT";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    @Transactional
    public void updateProductWithCategory(Product product, Integer categoryId) {
        // Actualizar producto en la tabla PRODUCT
        String updateProductSql = "UPDATE PRODUCT SET NAME = ?, PRICE = ?, DESCRIPTION = ?, STOCK = ?, IMAGE = ?, BRAND_ID = ? WHERE ID = ?";
        jdbcTemplate.update(updateProductSql,
                product.getName(), product.getPrice(), product.getDescription(),
                product.getStock(), product.getImage(),
                product.getBrand().getId(), product.getId()
        );

        // Eliminar todas las relaciones del producto en CATEGORY_PRODUCT
        categoryProductService.removeAllCategoriesFromProduct(product.getId());

        // Insertar la nueva relación en CATEGORY_PRODUCT
        categoryProductService.addProductToCategory(product.getId(), categoryId);
    }

}
