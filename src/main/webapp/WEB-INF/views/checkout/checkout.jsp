<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="pageTitle" value="Checkout - RibinaMart" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="container py-4">
    <h2 class="fw-bold mb-4"><i class="bi bi-credit-card-2-front me-2 text-primary"></i> Checkout & Mock Payment</h2>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="bi bi-exclamation-triangle-fill me-2"></i> <c:out value="${errorMessage}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <div class="row g-4">
        <!-- Checkout Form -->
        <div class="col-lg-7">
            <form action="${pageContext.request.contextPath}/checkout" method="post">
                <!-- Shipping Address Card -->
                <div class="card shadow-sm mb-4">
                    <div class="card-header bg-white py-3">
                        <h5 class="mb-0 fw-bold"><i class="bi bi-geo-alt-fill me-2 text-primary"></i> Shipping / Delivery Address</h5>
                    </div>
                    <div class="card-body">
                        <div class="mb-3">
                            <label class="form-label fw-semibold">Delivery Location (Hostel / Campus Dept / Residence)</label>
                            <textarea name="shippingAddress" class="form-control" rows="3"
                                      placeholder="e.g. Room 304, Emerald Hostel, Anna University CEG Campus, Guindy, Chennai - 600025" required><c:out value="${shippingAddress}"/></textarea>
                            <div class="form-text">Provide exact room, hostel, or department details for campus courier delivery.</div>
                        </div>
                    </div>
                </div>

                <!-- Mock Payment Method Card (F5) -->
                <div class="card shadow-sm mb-4">
                    <div class="card-header bg-white py-3">
                        <h5 class="mb-0 fw-bold"><i class="bi bi-wallet2 me-2 text-primary"></i> Mock Payment Gateway</h5>
                    </div>
                    <div class="card-body">
                        <div class="alert alert-warning py-2 small mb-3">
                            <i class="bi bi-shield-check me-1"></i> <strong>Simulation Mode:</strong> No real payment gateway or credit card will be charged.
                        </div>

                        <div class="form-check mb-3 p-3 border rounded">
                            <input class="form-check-input" type="radio" name="paymentMethod" id="payMockCard" value="MOCK_CARD" checked>
                            <label class="form-check-label ms-2 d-flex align-items-center" for="payMockCard">
                                <i class="bi bi-credit-card text-primary fs-4 me-2"></i>
                                <div>
                                    <div class="fw-bold">Mock Credit / Debit Card</div>
                                    <div class="text-muted small">Simulated instant card transaction (approved automatically)</div>
                                </div>
                            </label>
                        </div>

                        <div class="form-check mb-3 p-3 border rounded">
                            <input class="form-check-input" type="radio" name="paymentMethod" id="payMockUPI" value="MOCK_UPI">
                            <label class="form-check-label ms-2 d-flex align-items-center" for="payMockUPI">
                                <i class="bi bi-phone text-success fs-4 me-2"></i>
                                <div>
                                    <div class="fw-bold">Mock UPI / QR</div>
                                    <div class="text-muted small">Simulated UPI transfer (Instant verification)</div>
                                </div>
                            </label>
                        </div>

                        <div class="form-check p-3 border rounded">
                            <input class="form-check-input" type="radio" name="paymentMethod" id="payCOD" value="CASH_ON_DELIVERY">
                            <label class="form-check-label ms-2 d-flex align-items-center" for="payCOD">
                                <i class="bi bi-cash-stack text-warning fs-4 me-2"></i>
                                <div>
                                    <div class="fw-bold">Pay on Delivery</div>
                                    <div class="text-muted small">Pay in cash or campus card upon physical item receipt</div>
                                </div>
                            </label>
                        </div>
                    </div>
                </div>

                <button type="submit" class="btn btn-primary w-100 py-3 fw-bold fs-5 shadow-sm">
                    <i class="bi bi-lock-fill me-1"></i> Place Order & Complete Mock Payment (₹<c:out value="${cart.grandTotal}"/>)
                </button>
            </form>
        </div>

        <!-- Order Items Sidebar -->
        <div class="col-lg-5">
            <div class="card shadow-sm sticky-top" style="top: 80px;">
                <div class="card-header bg-white py-3">
                    <h5 class="mb-0 fw-bold">Items in Order (<c:out value="${cart.totalItemsCount}"/>)</h5>
                </div>
                <div class="card-body p-0">
                    <ul class="list-group list-group-flush">
                        <c:forEach var="item" items="${cart.items}">
                            <li class="list-group-item d-flex justify-content-between align-items-center py-3">
                                <div class="d-flex align-items-center">
                                    <img src="<c:out value='${item.productImageUrl}'/>" class="rounded border me-2"
                                         style="width: 45px; height: 45px; object-fit: cover;"
                                         onerror="this.onerror=null; this.src='https://via.placeholder.com/45?text=Item';">
                                    <div>
                                        <div class="fw-bold small text-truncate" style="max-width: 180px;"><c:out value="${item.productName}"/></div>
                                        <div class="text-muted small">Qty: <c:out value="${item.quantity}"/> &times; ₹<c:out value="${item.productPrice}"/></div>
                                    </div>
                                </div>
                                <span class="fw-semibold">₹<c:out value="${item.subtotal}"/></span>
                            </li>
                        </c:forEach>
                    </ul>
                </div>
                <div class="card-footer bg-white py-3">
                    <div class="d-flex justify-content-between mb-2">
                        <span class="text-muted">Subtotal</span>
                        <span>₹<c:out value="${cart.grandTotal}"/></span>
                    </div>
                    <div class="d-flex justify-content-between mb-2">
                        <span class="text-muted">Campus Delivery</span>
                        <span class="text-success fw-semibold">FREE</span>
                    </div>
                    <hr>
                    <div class="d-flex justify-content-between">
                        <span class="fs-5 fw-bold">Total Due</span>
                        <span class="fs-5 fw-bold text-primary">₹<c:out value="${cart.grandTotal}"/></span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
