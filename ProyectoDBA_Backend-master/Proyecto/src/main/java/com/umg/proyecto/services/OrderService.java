package com.umg.proyecto.services;

import com.umg.proyecto.models.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Mapeador de filas para convertir las filas de la base de datos en objetos Order
    private final RowMapper<Order> orderRowMapper = new RowMapper<Order>() {
        @Override
        public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
            Order order = new Order();
            order.setId(rs.getInt("ID"));
            order.setPurchaseDate(rs.getDate("PURCHASE_DATE"));
            order.setCustomerId(rs.getInt("CUSTOMER_ID"));
            order.setStatus(rs.getString("STATUS"));
            order.setTotal(rs.getFloat("TOTAL"));
            return order;
        }
    };

    // Método para obtener todas las órdenes
    public List<Order> findAll() {
        String sql = "SELECT * FROM \"ORDER\"";
        return jdbcTemplate.query(sql, orderRowMapper);
    }

    // Método para obtener una orden por ID
    public Order findById(Integer id) {
        String sql = "SELECT * FROM \"ORDER\" WHERE ID = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, orderRowMapper);
    }

    // Método para obtener todas las órdenes de un cliente
    public List<Order> findByCustomerId(Integer customerId) {
        String sql = "SELECT * FROM \"ORDER\" WHERE CUSTOMER_ID = ?";
        return jdbcTemplate.query(sql, new Object[]{customerId}, orderRowMapper);
    }

    // Método para guardar una nueva orden
    public void save(Order order) {
        String sql = "INSERT INTO \"ORDER\" (ID, PURCHASE_DATE, CUSTOMER_ID, TOTAL) " +
                "VALUES (ORDER_SEQ.NEXTVAL, ?, ?, ?)";
        jdbcTemplate.update(sql, order.getPurchaseDate(), order.getCustomerId(), order.getTotal());
    }

    // Método para actualizar una orden existente
    public void update(Order order) {
        String sql = "UPDATE \"ORDER\" SET PURCHASE_DATE = ?, CUSTOMER_ID = ?, TOTAL = ? WHERE ID = ?";
        jdbcTemplate.update(sql, order.getPurchaseDate(), order.getCustomerId(), order.getTotal(), order.getId());
    }

    // Método para eliminar una orden por ID
    public void delete(Integer id) {
        String sql = "DELETE FROM \"ORDER\" WHERE ID = ?";
        jdbcTemplate.update(sql, id);
    }

    public void updateOrderTotal(Integer orderId) {
        String sql = "SELECT SUM(od.QTY * p.PRICE) AS TOTAL " +
                "FROM ORDER_DETAIL od " +
                "JOIN PRODUCT p ON od.PRODUCT_ID = p.ID " +
                "WHERE od.ORDER_ID = ?";
        Float total = jdbcTemplate.queryForObject(sql, new Object[]{orderId}, Float.class);

        String updateSql = "UPDATE \"ORDER\" SET TOTAL = ? WHERE ID = ?";
        jdbcTemplate.update(updateSql, total, orderId);
    }

    @Transactional
    public void updateOrderStatus(Integer orderId, String purchaseStatus) {

        String sql = "UPDATE \"ORDER\" SET STATUS = ? WHERE ID = ?";
        jdbcTemplate.update(sql, purchaseStatus, orderId);
    }
}
