package com.umg.proyecto.services;

import com.umg.proyecto.models.CreditCard;
import com.umg.proyecto.models.OrderDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class CreditCardService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private ProductService productService;

    // Mapeador de filas para convertir las filas de la base de datos en objetos CreditCard
    private final RowMapper<CreditCard> creditCardRowMapper = new RowMapper<CreditCard>() {
        @Override
        public CreditCard mapRow(ResultSet rs, int rowNum) throws SQLException {
            CreditCard creditCard = new CreditCard();
            creditCard.setId(rs.getInt("ID"));
            creditCard.setType(rs.getString("TYPE"));
            creditCard.setCcNumber(rs.getString("CC_NUMBER"));
            creditCard.setCcDueDate(rs.getDate("CC_DUE_DATE"));
            creditCard.setCcName(rs.getString("CC_NAME"));
            creditCard.setStatus(rs.getString("STATUS").charAt(0));
            creditCard.setCustomerId(rs.getInt("CUSTOMER_ID"));
            return creditCard;
        }
    };

    // Método para obtener todas las tarjetas de crédito
    public List<CreditCard> findAll() {
        String sql = "SELECT * FROM CREDIT_CARD";
        return jdbcTemplate.query(sql, creditCardRowMapper);
    }

    public CreditCard findById(Integer id) {
        String sql = "SELECT * FROM CREDIT_CARD WHERE ID = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, creditCardRowMapper);
    }

    public List<CreditCard> findByCustomerId(Integer customerId) {
        String sql = "SELECT * FROM CREDIT_CARD WHERE CUSTOMER_ID = ?";
        return jdbcTemplate.query(sql, new Object[]{customerId}, creditCardRowMapper);
    }

    public void save(CreditCard creditCard) {
        String sql = "INSERT INTO CREDIT_CARD (ID, TYPE, CC_NUMBER, CC_DUE_DATE, CC_NAME, STATUS, CUSTOMER_ID) " +
                "VALUES (CREDIT_CARD_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, creditCard.getType(), creditCard.getCcNumber(), creditCard.getCcDueDate(),
                creditCard.getCcName(), String.valueOf(creditCard.getStatus()), creditCard.getCustomerId());
    }

    public void update(CreditCard creditCard) {
        String sql = "UPDATE CREDIT_CARD SET TYPE = ?, CC_NUMBER = ?, CC_DUE_DATE = ?, CC_NAME = ?, STATUS = ?, CUSTOMER_ID = ? WHERE ID = ?";
        jdbcTemplate.update(sql, creditCard.getType(), creditCard.getCcNumber(), creditCard.getCcDueDate(),
                creditCard.getCcName(), String.valueOf(creditCard.getStatus()), creditCard.getCustomerId(), creditCard.getId());
    }

    // Método para eliminar una tarjeta de crédito por ID
    public void delete(Integer id) {
        String sql = "DELETE FROM CREDIT_CARD WHERE ID = ?";
        jdbcTemplate.update(sql, id);
    }

    @Transactional
    public void processPurchase(Integer creditCardId, Integer orderId) {
        CreditCard creditCard = findById(creditCardId);

        if (creditCard.getStatus() != 'A') {
            throw new RuntimeException("Tarjeta de crédito inactiva");
        }
        checkAndReduceInventory(orderId);
        orderService.updateOrderStatus(orderId, "COMPLETED");
    }

    private void checkAndReduceInventory(Integer orderId) {
        List<OrderDetail> orderDetails = orderDetailService.findByOrderId(orderId);

        for (OrderDetail detail : orderDetails) {
            productService.checkAndReduceStock(detail.getProductId(), detail.getQty());
        }
    }
}
