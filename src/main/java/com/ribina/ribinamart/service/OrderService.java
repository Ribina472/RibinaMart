package com.ribina.ribinamart.service;

import com.ribina.ribinamart.dao.CartDAO;
import com.ribina.ribinamart.dao.OrderDAO;
import com.ribina.ribinamart.dao.ProductDAO;
import com.ribina.ribinamart.dto.OrderDTO;
import com.ribina.ribinamart.exception.ResourceNotFoundException;
import com.ribina.ribinamart.exception.ValidationException;
import com.ribina.ribinamart.model.*;
import com.ribina.ribinamart.util.ValidationUtil;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderService {

    private final OrderDAO orderDAO;
    private final CartDAO cartDAO;
    private final ProductDAO productDAO;

    public OrderService(OrderDAO orderDAO, CartDAO cartDAO, ProductDAO productDAO) {
        this.orderDAO = orderDAO;
        this.cartDAO = cartDAO;
        this.productDAO = productDAO;
    }

    public OrderDTO checkout(Long buyerId, String shippingAddress, String paymentMethod) {
        if (buyerId == null || buyerId <= 0) {
            throw new ValidationException("Invalid buyer ID.");
        }
        if (!ValidationUtil.isNotEmpty(shippingAddress)) {
            throw new ValidationException("Shipping address is required.");
        }

        try {
            List<CartItem> cartItems = cartDAO.findByUserId(buyerId);
            if (cartItems.isEmpty()) {
                throw new ValidationException("Cannot place order: your cart is empty.");
            }

            BigDecimal totalAmount = BigDecimal.ZERO;
            List<OrderItem> orderItems = new ArrayList<>();

            for (CartItem cartItem : cartItems) {
                Product product = productDAO.findById(cartItem.getProductId())
                        .orElseThrow(() -> new ValidationException("Product with ID " + cartItem.getProductId() + " not found."));

                if (product.getStatus() != ProductStatus.ACTIVE) {
                    throw new ValidationException("Product '" + product.getName() + "' is no longer available.");
                }

                if (product.getStockQuantity() < cartItem.getQuantity()) {
                    throw new ValidationException("Insufficient stock for '" + product.getName() + "'. Available: " + product.getStockQuantity());
                }

                OrderItem oi = new OrderItem();
                oi.setProductId(product.getId());
                oi.setQuantity(cartItem.getQuantity());
                oi.setPriceAtPurchase(product.getPrice());
                orderItems.add(oi);

                BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
                totalAmount = totalAmount.add(itemTotal);
            }

            Order order = new Order();
            order.setBuyerId(buyerId);
            order.setTotalAmount(totalAmount);
            order.setStatus(OrderStatus.CONFIRMED);
            order.setShippingAddress(shippingAddress.trim());
            order.setPaymentMethod(ValidationUtil.isNotEmpty(paymentMethod) ? paymentMethod.trim() : "MOCK_CARD");

            Order created = orderDAO.createOrderWithItems(order, orderItems);
            return OrderDTO.fromEntity(created);
        } catch (SQLException e) {
            throw new RuntimeException("Database error executing order checkout", e);
        }
    }

    public List<OrderDTO> getOrdersByBuyer(Long buyerId) {
        if (buyerId == null || buyerId <= 0) {
            throw new ValidationException("Invalid buyer ID.");
        }
        try {
            List<Order> orders = orderDAO.findByBuyerId(buyerId);
            List<OrderDTO> dtos = new ArrayList<>();
            for (Order o : orders) {
                dtos.add(OrderDTO.fromEntity(o));
            }
            return dtos;
        } catch (SQLException e) {
            throw new RuntimeException("Database error retrieving buyer orders", e);
        }
    }

    public List<OrderDTO> getOrdersBySeller(Long sellerId) {
        if (sellerId == null || sellerId <= 0) {
            throw new ValidationException("Invalid seller ID.");
        }
        try {
            List<Order> orders = orderDAO.findBySellerId(sellerId);
            List<OrderDTO> dtos = new ArrayList<>();
            for (Order o : orders) {
                dtos.add(OrderDTO.fromEntity(o));
            }
            return dtos;
        } catch (SQLException e) {
            throw new RuntimeException("Database error retrieving seller orders", e);
        }
    }

    public List<OrderDTO> getAllOrders() {
        try {
            List<Order> orders = orderDAO.findAll();
            List<OrderDTO> dtos = new ArrayList<>();
            for (Order o : orders) {
                dtos.add(OrderDTO.fromEntity(o));
            }
            return dtos;
        } catch (SQLException e) {
            throw new RuntimeException("Database error retrieving all orders", e);
        }
    }

    public OrderDTO getOrderById(Long orderId) {
        if (orderId == null || orderId <= 0) {
            throw new ValidationException("Invalid order ID.");
        }
        try {
            Order order = orderDAO.findById(orderId)
                    .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
            return OrderDTO.fromEntity(order);
        } catch (SQLException e) {
            throw new RuntimeException("Database error retrieving order", e);
        }
    }

    public void updateOrderStatus(Long orderId, OrderStatus status) {
        if (orderId == null || orderId <= 0) {
            throw new ValidationException("Invalid order ID.");
        }
        if (status == null) {
            throw new ValidationException("Invalid order status.");
        }
        try {
            boolean updated = orderDAO.updateStatus(orderId, status);
            if (!updated) {
                throw new ResourceNotFoundException("Order not found with ID: " + orderId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error updating order status", e);
        }
    }

    public com.ribina.ribinamart.dto.SellerAnalyticsDTO getSellerAnalytics(Long sellerId) {
        if (sellerId == null || sellerId <= 0) {
            return new com.ribina.ribinamart.dto.SellerAnalyticsDTO();
        }
        try {
            List<Order> orders = orderDAO.findBySellerId(sellerId);
            BigDecimal totalRevenue = BigDecimal.ZERO;
            long unitsSold = 0;
            long validOrdersCount = 0;

            for (Order o : orders) {
                if (o.getStatus() != OrderStatus.CANCELLED) {
                    validOrdersCount++;
                    if (o.getItems() != null) {
                        for (OrderItem item : o.getItems()) {
                            BigDecimal itemTotal = item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity()));
                            totalRevenue = totalRevenue.add(itemTotal);
                            unitsSold += item.getQuantity();
                        }
                    }
                }
            }

            List<Product> products = productDAO.findBySellerId(sellerId);
            long activeListings = 0;
            long lowStock = 0;
            for (Product p : products) {
                if (p.getStatus() == ProductStatus.ACTIVE) {
                    activeListings++;
                }
                if (p.getStockQuantity() <= 5) {
                    lowStock++;
                }
            }

            return new com.ribina.ribinamart.dto.SellerAnalyticsDTO(totalRevenue, validOrdersCount, unitsSold, activeListings, lowStock);
        } catch (SQLException e) {
            throw new RuntimeException("Database error calculating seller analytics", e);
        }
    }
}
