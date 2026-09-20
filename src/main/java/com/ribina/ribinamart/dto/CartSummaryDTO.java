package com.ribina.ribinamart.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CartSummaryDTO {
    private List<CartItemDTO> items = new ArrayList<>();
    private int totalItemsCount;
    private BigDecimal grandTotal = BigDecimal.ZERO;

    public CartSummaryDTO() {
    }

    public CartSummaryDTO(List<CartItemDTO> items) {
        if (items != null) {
            this.items = items;
            int count = 0;
            BigDecimal total = BigDecimal.ZERO;
            for (CartItemDTO item : items) {
                count += item.getQuantity();
                if (item.getSubtotal() != null) {
                    total = total.add(item.getSubtotal());
                }
            }
            this.totalItemsCount = count;
            this.grandTotal = total;
        }
    }

    public List<CartItemDTO> getItems() {
        return items;
    }

    public void setItems(List<CartItemDTO> items) {
        this.items = items;
    }

    public int getTotalItemsCount() {
        return totalItemsCount;
    }

    public void setTotalItemsCount(int totalItemsCount) {
        this.totalItemsCount = totalItemsCount;
    }

    public BigDecimal getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(BigDecimal grandTotal) {
        this.grandTotal = grandTotal;
    }
}
