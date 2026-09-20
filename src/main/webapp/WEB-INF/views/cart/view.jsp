<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="pageTitle" value="Shopping Cart - RibinaMart" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="container py-4">
    <h2 class="fw-bold mb-4"><i class="bi bi-cart3 me-2 text-primary"></i> Shopping Cart</h2>

    <c:if test="${not empty param.added}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="bi bi-check-circle-fill me-2"></i> Item added to your cart!
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    <c:if test="${not empty param.removed}">
        <div class="alert alert-info alert-dismissible fade show" role="alert">
            <i class="bi bi-info-circle-fill me-2"></i> Item removed from your cart.
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
        <c:when test="${empty cart.items}">
            <div class="card text-center py-5 shadow-sm">
                <div class="card-body">
                    <i class="bi bi-cart-x text-muted" style="font-size: 3.5rem;"></i>
                    <h4 class="mt-3">Your Cart is Empty</h4>
                    <p class="text-muted">Looks like you haven't added anything to your cart yet.</p>
                    <a href="${pageContext.request.contextPath}/products" class="btn btn-primary px-4">
                        <i class="bi bi-bag-plus me-1"></i> Start Shopping
                    </a>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <div class="row g-4">
                <div class="col-lg-8">
                    <div class="card shadow-sm">
                        <div class="table-responsive">
                            <table class="table table-hover align-middle mb-0">
                                <thead class="table-light">
                                    <tr>
                                        <th scope="col" style="min-width: 250px;">Product</th>
                                        <th scope="col">Price</th>
                                        <th scope="col" style="width: 140px;">Quantity</th>
                                        <th scope="col">Subtotal</th>
                                        <th scope="col" class="text-end">Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="item" items="${cart.items}">
                                        <tr>
                                            <td>
                                                <div class="d-flex align-items-center">
                                                    <img src="<c:out value='${item.productImageUrl}'/>"
                                                         class="rounded me-3 border"
                                                         style="width: 60px; height: 60px; object-fit: cover;"
                                                         alt="<c:out value='${item.productName}'/>"
                                                         onerror="this.onerror=null; this.src='https://via.placeholder.com/60?text=Item';">
                                                    <div>
                                                        <a href="${pageContext.request.contextPath}/products/detail?id=<c:out value='${item.productId}'/>" class="fw-bold text-dark text-decoration-none">
                                                            <c:out value="${item.productName}"/>
                                                        </a>
                                                        <div class="small text-muted">Stock available: <c:out value="${item.availableStock}"/></div>
                                                    </div>
                                                </div>
                                            </td>
                                            <td>₹<c:out value="${item.productPrice}"/></td>
                                            <td>
                                                <form action="${pageContext.request.contextPath}/cart/update" method="post" class="d-flex align-items-center">
                                                    <input type="hidden" name="cartItemId" value="<c:out value='${item.id}'/>">
                                                    <input type="number" name="quantity" class="form-control form-control-sm me-2 text-center"
                                                           value="<c:out value='${item.quantity}'/>" min="1" max="<c:out value='${item.availableStock}'/>" style="width: 65px;" required>
                                                    <button type="submit" class="btn btn-sm btn-outline-secondary" title="Update"><i class="bi bi-arrow-clockwise"></i></button>
                                                </form>
                                            </td>
                                            <td class="fw-bold text-primary">₹<c:out value="${item.subtotal}"/></td>
                                            <td class="text-end">
                                                <form action="${pageContext.request.contextPath}/cart/remove" method="post" style="display:inline;">
                                                    <input type="hidden" name="cartItemId" value="<c:out value='${item.id}'/>">
                                                    <button type="submit" class="btn btn-sm btn-outline-danger" title="Remove item">
                                                        <i class="bi bi-trash3"></i>
                                                    </button>
                                                </form>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                        <div class="card-footer bg-white d-flex justify-content-between py-3">
                            <a href="${pageContext.request.contextPath}/products" class="btn btn-outline-secondary">
                                <i class="bi bi-arrow-left me-1"></i> Continue Shopping
                            </a>
                            <form action="${pageContext.request.contextPath}/cart/clear" method="post" onsubmit="return confirm('Are you sure you want to clear your cart?');">
                                <button type="submit" class="btn btn-outline-danger">
                                    <i class="bi bi-trash me-1"></i> Clear Cart
                                </button>
                            </form>
                        </div>
                    </div>
                </div>

                <!-- Order Summary Card -->
                <div class="col-lg-4">
                    <div class="card shadow-sm">
                        <div class="card-header bg-white py-3">
                            <h5 class="mb-0 fw-bold">Order Summary</h5>
                        </div>
                        <div class="card-body">
                            <div class="d-flex justify-content-between mb-2">
                                <span class="text-muted">Total Items</span>
                                <span class="fw-semibold"><c:out value="${cart.totalItemsCount}"/></span>
                            </div>
                            <div class="d-flex justify-content-between mb-2">
                                <span class="text-muted">Subtotal</span>
                                <span>₹<c:out value="${cart.grandTotal}"/></span>
                            </div>
                            <div class="d-flex justify-content-between mb-3">
                                <span class="text-muted">Campus Delivery</span>
                                <span class="text-success fw-semibold">FREE</span>
                            </div>
                            <hr>
                            <div class="d-flex justify-content-between mb-4">
                                <span class="fs-5 fw-bold">Grand Total</span>
                                <span class="fs-5 fw-bold text-primary">₹<c:out value="${cart.grandTotal}"/></span>
                            </div>
                            <a href="${pageContext.request.contextPath}/checkout" class="btn btn-primary w-100 py-2 fw-semibold">
                                Proceed to Checkout <i class="bi bi-arrow-right ms-1"></i>
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
