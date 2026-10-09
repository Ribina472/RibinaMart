<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="pageTitle" value="My Wishlist - RibinaMart" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="fw-bold mb-0">
            <i class="bi bi-heart-fill me-2 text-danger"></i> My Wishlist
        </h2>
        <a href="${pageContext.request.contextPath}/products" class="btn btn-outline-primary">
            <i class="bi bi-arrow-left me-1"></i> Continue Shopping
        </a>
    </div>

    <c:if test="${not empty param.success}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="bi bi-check-circle-fill me-2"></i> Item added to your wishlist!
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    <c:if test="${not empty param.removed}">
        <div class="alert alert-info alert-dismissible fade show" role="alert">
            <i class="bi bi-info-circle-fill me-2"></i> Item removed from your wishlist.
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
        <c:when test="${empty wishlistItems}">
            <div class="card text-center py-5 shadow-sm border-0 bg-light">
                <div class="card-body">
                    <i class="bi bi-heart text-muted" style="font-size: 3.5rem;"></i>
                    <h4 class="mt-3">Your Wishlist is Empty</h4>
                    <p class="text-muted">Save items that you like by clicking the heart button on product pages.</p>
                    <a href="${pageContext.request.contextPath}/products" class="btn btn-primary px-4 mt-2">
                        <i class="bi bi-bag-plus me-1"></i> Explore Products
                    </a>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <div class="card shadow-sm border-0">
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="table-light">
                            <tr>
                                <th scope="col" style="min-width: 280px;">Product</th>
                                <th scope="col">Price</th>
                                <th scope="col">Stock Status</th>
                                <th scope="col" class="text-end">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="item" items="${wishlistItems}">
                                <tr>
                                    <td>
                                        <div class="d-flex align-items-center">
                                            <img src="<c:out value='${item.product.imageUrl}'/>"
                                                 class="rounded me-3 border"
                                                 style="width: 70px; height: 70px; object-fit: cover;"
                                                 alt="<c:out value='${item.product.name}'/>"
                                                 onerror="this.onerror=null; this.src='https://via.placeholder.com/70?text=RibinaMart';">
                                            <div>
                                                <a href="${pageContext.request.contextPath}/products/detail?id=<c:out value='${item.product.id}'/>"
                                                   class="fw-bold text-dark text-decoration-none">
                                                    <c:out value="${item.product.name}"/>
                                                </a>
                                                <div class="small text-muted">Category: <c:out value="${item.product.category}"/></div>
                                            </div>
                                        </div>
                                    </td>
                                    <td>
                                        <span class="fs-6 fw-bold text-success">₹<c:out value="${item.product.price}"/></span>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${item.product.stockQuantity > 0}">
                                                <span class="badge bg-success-subtle text-success border border-success-subtle">
                                                    In Stock (${item.product.stockQuantity} units)
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-danger-subtle text-danger border border-danger-subtle">
                                                    Out of Stock
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="text-end">
                                        <div class="d-inline-flex gap-2">
                                            <c:if test="${item.product.stockQuantity > 0}">
                                                <form action="${pageContext.request.contextPath}/cart/add" method="post" class="d-inline">
                                                    <input type="hidden" name="productId" value="<c:out value='${item.product.id}'/>">
                                                    <input type="hidden" name="quantity" value="1">
                                                    <button type="submit" class="btn btn-sm btn-primary">
                                                        <i class="bi bi-cart-plus me-1"></i> Add to Cart
                                                    </button>
                                                </form>
                                            </c:if>
                                            <form action="${pageContext.request.contextPath}/wishlist/remove" method="post" class="d-inline">
                                                <input type="hidden" name="productId" value="<c:out value='${item.product.id}'/>">
                                                <button type="submit" class="btn btn-sm btn-outline-danger" title="Remove from Wishlist">
                                                    <i class="bi bi-trash"></i>
                                                </button>
                                            </form>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
