package com.ribina.ribinamart.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Data Transfer Object encapsulating seller performance KPIs and sales analytics (O3).
 */
public class SellerAnalyticsDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private BigDecimal totalRevenue = BigDecimal.ZERO;
    private long totalOrdersCount;
    private long totalUnitsSold;
    private long activeListingsCount;
    private long lowStockCount;

    public SellerAnalyticsDTO() {
    }

    public SellerAnalyticsDTO(BigDecimal totalRevenue, long totalOrdersCount, long totalUnitsSold, long activeListingsCount, long lowStockCount) {
        this.totalRevenue = (totalRevenue != null) ? totalRevenue : BigDecimal.ZERO;
        this.totalOrdersCount = totalOrdersCount;
        this.totalUnitsSold = totalUnitsSold;
        this.activeListingsCount = activeListingsCount;
        this.lowStockCount = lowStockCount;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = (totalRevenue != null) ? totalRevenue : BigDecimal.ZERO;
    }

    public long getTotalOrdersCount() {
        return totalOrdersCount;
    }

    public void setTotalOrdersCount(long totalOrdersCount) {
        this.totalOrdersCount = totalOrdersCount;
    }

    public long getTotalUnitsSold() {
        return totalUnitsSold;
    }

    public void setTotalUnitsSold(long totalUnitsSold) {
        this.totalUnitsSold = totalUnitsSold;
    }

    public long getActiveListingsCount() {
        return activeListingsCount;
    }

    public void setActiveListingsCount(long activeListingsCount) {
        this.activeListingsCount = activeListingsCount;
    }

    public long getLowStockCount() {
        return lowStockCount;
    }

    public void setLowStockCount(long lowStockCount) {
        this.lowStockCount = lowStockCount;
    }
}
