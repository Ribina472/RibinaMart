<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="pageTitle" value="My Orders - RibinaMart" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="container py-4">
    <h2 class="fw-bold mb-4"><i class="bi bi-receipt me-2 text-primary"></i> My Order History</h2>

    <c:if test="${not empty param.placed}">
        <div class="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
            <h5 class="alert-heading fw-bold"><i class="bi bi-check-circle-fill me-2"></i> Order Placed Successfully!</h5>
            <p class="mb-0">Your order #<c:out value="${param.orderId}"/> has been confirmed via mock payment. The seller has been notified for dispatch.</p>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <c:choose>
        <c:when test="${empty orders}">
            <div class="card text-center py-5 shadow-sm">
                <div class="card-body">
                    <i class="bi bi-bag-x text-muted" style="font-size: 3.5rem;"></i>
                    <h4 class="mt-3">No Orders Placed Yet</h4>
                    <p class="text-muted">You haven't placed any orders on RibinaMart yet.</p>
                    <a href="${pageContext.request.contextPath}/products" class="btn btn-primary px-4">Browse Catalog</a>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <div class="d-flex flex-column gap-4">
                <c:forEach var="order" items="${orders}">
                    <div class="card shadow-sm border-0">
                        <div class="card-header bg-light d-flex flex-wrap justify-content-between align-items-center py-3">
                            <div class="d-flex flex-wrap gap-3 align-items-center">
                                <div>
                                    <span class="text-muted small">ORDER ID:</span>
                                    <span class="fw-bold">#<c:out value="${order.id}"/></span>
                                </div>
                                <div class="text-muted">|</div>
                                <div>
                                    <span class="text-muted small">DATE:</span>
                                    <span class="fw-semibold"><c:out value="${order.createdAt}"/></span>
                                </div>
                                <div class="text-muted">|</div>
                                <div>
                                    <span class="text-muted small">TOTAL:</span>
                                    <span class="fw-bold text-primary">₹<c:out value="${order.totalAmount}"/></span>
                                </div>
                            </div>
                            <div class="mt-2 mt-md-0">
                                <c:choose>
                                    <c:when test="${order.status == 'DELIVERED'}">
                                        <span class="badge bg-success px-3 py-2 fs-6"><i class="bi bi-check2-all me-1"></i> Delivered</span>
                                    </c:when>
                                    <c:when test="${order.status == 'SHIPPED'}">
                                        <span class="badge bg-primary px-3 py-2 fs-6"><i class="bi bi-truck me-1"></i> Shipped</span>
                                    </c:when>
                                    <c:when test="${order.status == 'CONFIRMED'}">
                                        <span class="badge bg-info text-dark px-3 py-2 fs-6"><i class="bi bi-check2-circle me-1"></i> Confirmed</span>
                                    </c:when>
                                    <c:when test="${order.status == 'PENDING'}">
                                        <span class="badge bg-warning text-dark px-3 py-2 fs-6"><i class="bi bi-hourglass-split me-1"></i> Pending</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-danger px-3 py-2 fs-6"><c:out value="${order.status}"/></span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                        <div class="card-body">
                            <div class="row">
                                <div class="col-md-8">
                                    <h6 class="fw-bold text-secondary mb-3 small text-uppercase">Items Ordered</h6>
                                    <div class="d-flex flex-column gap-3">
                                        <c:forEach var="item" items="${order.items}">
                                            <div class="d-flex align-items-center justify-content-between p-2 rounded bg-light">
                                                <div class="d-flex align-items-center">
                                                    <img src="<c:out value='${item.productImageUrl}'/>"
                                                         class="rounded border me-3"
                                                         style="width: 55px; height: 55px; object-fit: cover;"
                                                         alt="<c:out value='${item.productName}'/>"
                                                         onerror="this.onerror=null; this.src='https://via.placeholder.com/55?text=Item';">
                                                    <div>
                                                        <a href="${pageContext.request.contextPath}/products/detail?id=<c:out value='${item.productId}'/>" class="fw-bold text-dark text-decoration-none">
                                                            <c:out value="${item.productName}"/>
                                                        </a>
                                                        <div class="text-muted small">Qty: <c:out value="${item.quantity}"/> &times; ₹<c:out value="${item.priceAtPurchase}"/></div>
                                                    </div>
                                                </div>
                                                <div class="text-end">
                                                    <div class="fw-bold text-primary">₹<c:out value="${item.subtotal}"/></div>
                                                    <a href="${pageContext.request.contextPath}/products/detail?id=<c:out value='${item.productId}'/>" class="btn btn-sm btn-outline-primary mt-1">
                                                        <i class="bi bi-star me-1"></i> Review
                                                    </a>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </div>
                                <div class="col-md-4 mt-3 mt-md-0 border-start ps-md-4">
                                    <h6 class="fw-bold text-secondary mb-2 small text-uppercase">Delivery Address</h6>
                                    <p class="small text-muted mb-3"><c:out value="${order.shippingAddress}"/></p>
                                    <h6 class="fw-bold text-secondary mb-1 small text-uppercase">Payment Details</h6>
                                    <p class="small text-muted mb-0">
                                        Method: <span class="badge bg-secondary"><c:out value="${order.paymentMethod}"/></span>
                                    </p>
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
