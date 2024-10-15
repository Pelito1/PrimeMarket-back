package com.umg.proyecto.services;

import com.umg.proyecto.models.Customer;
import com.umg.proyecto.models.Order;
import com.umg.proyecto.models.OrderDetail;
import com.umg.proyecto.models.OrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CustomerService customerService;

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
    @Transactional
    public void processOrder(OrderRequest orderRequest) {
        System.out.println("Datos de la orden recibidos: " + orderRequest);

        // Si el cliente no ha iniciado sesión, se registra un nuevo cliente
        if (orderRequest.getCustomerId() == null) {
            Customer newCustomer = customerService.save(orderRequest.getCustomer());
            orderRequest.setCustomerId(newCustomer.getId());
            System.out.println("Nuevo cliente creado con ID: " + newCustomer.getId());
        }

        // Crear orden
        Integer orderId = createOrder(orderRequest);
        System.out.println("Orden creada con ID: " + orderId);

        // Crear detalles de orden y actualizar stock
        for (OrderDetail detail : orderRequest.getOrderDetails()) {
            System.out.println("Agregando detalle: " + detail);
            detail.setOrderId(orderId);
            saveOrderDetail(detail);
            updateStock(detail.getProductId(), detail.getQty());
        }
    }

    private Integer createOrder(OrderRequest orderRequest) {
        try {
            // Consulta SQL sin RETURNING INTO
            String sql = "INSERT INTO \"ORDER\" (ID, PURCHASE_DATE, CUSTOMER_ID, STATUS, TOTAL) " +
                    "VALUES (ORDER_SEQ.NEXTVAL, SYSDATE, ?, ?, ?)";

            KeyHolder keyHolder = new GeneratedKeyHolder();

            // Ejecutar la inserción y capturar el ID generado
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"ID"});
                ps.setInt(1, orderRequest.getCustomerId());
                ps.setString(2, orderRequest.getStatus());
                ps.setFloat(3, orderRequest.getTotal());
                return ps;
            }, keyHolder);

            // Capturar el ID generado desde el KeyHolder
            Integer orderId = keyHolder.getKey().intValue();
            System.out.println("Orden creada con ID: " + orderId);
            return orderId;

        } catch (Exception e) {
            System.err.println("Error al crear la orden: " + e.getMessage());
            throw new RuntimeException("Error al crear la orden", e);
        }
    }



    private void saveOrderDetail(OrderDetail detail) {
        try {
            String sql = "INSERT INTO ORDER_DETAIL (ORDER_ID, PRODUCT_ID, QTY) VALUES (?, ?, ?)";
            jdbcTemplate.update(sql, detail.getOrderId(), detail.getProductId(), detail.getQty());
            System.out.println("Detalle guardado: " + detail);
        } catch (Exception e) {
            System.err.println("Error al guardar detalle: " + e.getMessage());
            throw new RuntimeException("Error al guardar detalle", e);
        }
    }


    private void updateStock(Integer productId, Integer qty) {
        String sqlCheck = "SELECT STOCK FROM PRODUCT WHERE ID = ?";
        Integer currentStock = jdbcTemplate.queryForObject(sqlCheck, new Object[]{productId}, Integer.class);

        if (currentStock < qty) {
            throw new RuntimeException("Insufficient stock for product ID: " + productId);
        }

        String sqlUpdate = "UPDATE PRODUCT SET STOCK = STOCK - ? WHERE ID = ?";
        jdbcTemplate.update(sqlUpdate, qty, productId);
    }


}
