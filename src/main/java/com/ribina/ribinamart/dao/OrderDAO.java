package com.ribina.ribinamart.dao;

import com.ribina.ribinamart.model.Order;
import com.ribina.ribinamart.model.OrderItem;
import com.ribina.ribinamart.model.OrderStatus;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface OrderDAO {
    Order createOrderWithItems(Order order, List<OrderItem> items) throws SQLException;
    Optional<Order> findById(Long id) throws SQLException;
    List<Order> findByBuyerId(Long buyerId) throws SQLException;
    List<Order> findBySellerId(Long sellerId) throws SQLException;
    List<Order> findAll() throws SQLException;
    boolean updateStatus(Long orderId, OrderStatus status) throws SQLException;
}
