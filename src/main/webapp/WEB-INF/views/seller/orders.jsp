<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="pageTitle" value="Incoming Orders - RibinaMart Seller" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h2 class="fw-bold mb-1"><i class="bi bi-box-seam me-2 text-warning"></i> Incoming Customer Orders</h2>
            <p class="text-muted mb-0">Track order fulfillment, buyer shipping details, and update dispatch status.</p>
        </div>
        <a href="${pageContext.request.contextPath}/seller/dashboard" class="btn btn-outline-secondary">
            <i class="bi bi-arrow-left me-1"></i> Seller Dashboard
        </a>
    </div>

    <c:if test="${not empty param.statusUpdated}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="bi bi-check-circle-fill me-2"></i> Order fulfillment status has been updated successfully!
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    <c:if test="${not empty param.error}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="bi bi-exclamation-triangle-fill me-2"></i> <c:out value="${param.error}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <c:choose>
        <c:when test="${empty orders}">
            <div class="card text-center py-5 shadow-sm">
                <div class="card-body">
                    <i class="bi bi-inbox text-muted" style="font-size: 3.5rem;"></i>
                    <h5 class="mt-3">No Incoming Orders Yet</h5>
                    <p class="text-muted">When customers buy your products, their orders and delivery instructions will appear here.</p>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <div class="d-flex flex-column gap-4">
                <c:forEach var="order" items="${orders}">
                    <div class="card shadow-sm border-0">
                        <div class="card-header bg-light d-flex flex-wrap justify-content-between align-items-center py-3">
                            <div>
                                <span class="fw-bold fs-5">Order #<c:out value="${order.id}"/></span>
                                <span class="text-muted small ms-2"><c:out value="${order.createdAt}"/></span>
                            </div>
                            <!-- Order Status Workflow Update Form (O2 / Week 5) -->
                            <form action="${pageContext.request.contextPath}/seller/orders/status" method="post" class="d-flex align-items-center gap-2 mt-2 mt-md-0">
                                <input type="hidden" name="orderId" value="<c:out value='${order.id}'/>">
                                <label class="form-label mb-0 small fw-bold">Status:</label>
                                <select name="status" class="form-select form-select-sm" style="width: 140px;">
                                    <option value="PENDING" ${order.status == 'PENDING' ? 'selected' : ''}>Pending</option>
                                    <option value="CONFIRMED" ${order.status == 'CONFIRMED' ? 'selected' : ''}>Confirmed</option>
                                    <option value="SHIPPED" ${order.status == 'SHIPPED' ? 'selected' : ''}>Shipped</option>
                                    <option value="DELIVERED" ${order.status == 'DELIVERED' ? 'selected' : ''}>Delivered</option>
                                    <option value="CANCELLED" ${order.status == 'CANCELLED' ? 'selected' : ''}>Cancelled</option>
                                </select>
                                <button type="submit" class="btn btn-sm btn-primary">Update</button>
                            </form>
                        </div>
                        <div class="card-body">
                            <div class="row g-4">
                                <div class="col-md-7">
                                    <h6 class="fw-bold text-secondary mb-2 small text-uppercase">Your Items in this Order</h6>
                                    <div class="table-responsive">
                                        <table class="table table-sm align-middle mb-0">
                                            <thead>
                                                <tr>
                                                    <th>Item</th>
                                                    <th>Qty</th>
                                                    <th>Price</th>
                                                    <th>Subtotal</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="item" items="${order.items}">
                                                    <tr>
                                                        <td>
                                                            <span class="fw-semibold"><c:out value="${item.productName}"/></span>
                                                        </td>
                                                        <td><c:out value="${item.quantity}"/></td>
                                                        <td>₹<c:out value="${item.priceAtPurchase}"/></td>
                                                        <td class="fw-bold text-primary">₹<c:out value="${item.subtotal}"/></td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                                <div class="col-md-5 border-start ps-md-4">
                                    <h6 class="fw-bold text-secondary mb-2 small text-uppercase">Buyer & Delivery Info</h6>
                                    <p class="mb-1"><strong><c:out value="${order.buyerName}"/></strong> (<c:out value="${order.buyerEmail}"/>)</p>
                                    <p class="small text-muted mb-2"><c:out value="${order.shippingAddress}"/></p>
                                    <div class="small">
                                        Payment: <span class="badge bg-secondary"><c:out value="${order.paymentMethod}"/></span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
