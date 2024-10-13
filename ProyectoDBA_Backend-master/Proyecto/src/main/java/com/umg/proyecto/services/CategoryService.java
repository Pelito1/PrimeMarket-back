package com.umg.proyecto.services;

import com.umg.proyecto.models.Category;
import com.umg.proyecto.models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataAccessException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CategoryProductService categoryProductService;

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

   // public void delete(Integer id) {
     //   String sql = "DELETE FROM CATEGORY WHERE ID = ?";
       // jdbcTemplate.update(sql, id);
    //}

    public List<Category> findSubcategories(Integer parentCategoryId) {
        String sql = "SELECT * FROM CATEGORY WHERE PARENT_CATEGORY_ID = ?";
        return jdbcTemplate.query(sql, new Object[]{parentCategoryId}, categoryRowMapper);
    }

    // Método para obtener todas las categorías padre
    public List<Category> findParentCategories() {
        String sql = "SELECT * FROM CATEGORY WHERE PARENT_CATEGORY_ID IS NULL";
        return jdbcTemplate.query(sql, categoryRowMapper);
    }


    /**
     * Eliminar una categoría, garantizando la consistencia de datos.
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Integer id) {
        Category categoryToDelete = findById(id);
        List<Product> products = categoryProductService.getProductsByCategory(id);
        List<Category> subcategories = findSubcategories(id);
        boolean isPrincipal = categoryToDelete.getParentCategoryId() == null;

        if (isPrincipal) {
            handlePrincipalCategoryDeletion(products, subcategories);
        } else {
            handleSecondaryCategoryDeletion(categoryToDelete, products, subcategories);
        }

        // Finalmente, intentamos eliminar la categoría.
        deleteCategoryById(id);
    }

    /**
     * Manejo de eliminación para categorías principales.
     */
    private void handlePrincipalCategoryDeletion(List<Product> products, List<Category> subcategories) {
        if (!products.isEmpty()) {
            throw new RuntimeException("No se puede eliminar una categoría principal con productos asociados.");
        }

        if (!subcategories.isEmpty()) {
            subcategories.forEach(sub -> updateParentCategoryToNull(sub.getId()));
        }
    }

    /**
     * Manejo de eliminación para categorías secundarias.
     */
    private void handleSecondaryCategoryDeletion(Category category, List<Product> products, List<Category> subcategories) {
        Integer parentCategoryId = category.getParentCategoryId();

        if (!products.isEmpty()) {
            // Reasignamos los productos a la categoría padre.
            for (Product product : products) {
                categoryProductService.addProductToCategory(product.getId(), parentCategoryId);
                categoryProductService.removeProductFromCategory(product.getId(), category.getId());
            }
        }

        if (!subcategories.isEmpty()) {
            // Reasignamos las subcategorías a la categoría padre.
            subcategories.forEach(sub -> updateParentCategory(sub.getId(), parentCategoryId));
        }
    }

    /**
     * Actualiza la categoría padre de una subcategoría a null.
     */
    private void updateParentCategoryToNull(Integer categoryId) {
        String sql = "UPDATE CATEGORY SET PARENT_CATEGORY_ID = NULL WHERE ID = ?";
        jdbcTemplate.update(sql, categoryId);
    }

    /**
     * Actualiza la categoría padre de una subcategoría.
     */
    private void updateParentCategory(Integer categoryId, Integer newParentCategoryId) {
        String sql = "UPDATE CATEGORY SET PARENT_CATEGORY_ID = ? WHERE ID = ?";
        jdbcTemplate.update(sql, newParentCategoryId, categoryId);
    }

    /**
     * Elimina una categoría por su ID.
     */
    private void deleteCategoryById(Integer id) {
        String sql = "DELETE FROM CATEGORY WHERE ID = ?";
        jdbcTemplate.update(sql, id);
    }
}
