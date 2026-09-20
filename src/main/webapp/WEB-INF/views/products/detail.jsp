<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<c:set var="pageTitle" value="${product.name} - RibinaMart" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="container py-4">
    <!-- Breadcrumb -->
    <nav aria-label="breadcrumb" class="mb-3">
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/products">Catalog</a></li>
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/products?category=<c:out value='${product.category}'/>"><c:out value="${product.category}"/></a></li>
            <li class="breadcrumb-item active" aria-current="page"><c:out value="${product.name}"/></li>
        </ol>
    </nav>

    <!-- Alerts -->
    <c:if test="${not empty param.reviewed}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="bi bi-check-circle-fill me-2"></i> Thank you! Your verified review has been published.
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    <c:if test="${not empty param.error}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="bi bi-exclamation-triangle-fill me-2"></i> <c:out value="${param.error}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <!-- Product Card Details -->
    <div class="card shadow-sm mb-5">
        <div class="card-body p-4">
            <div class="row g-4">
                <div class="col-md-5">
                    <img src="<c:out value='${product.imageUrl}'/>" class="img-fluid rounded border w-100" alt="<c:out value='${product.name}'/>"
                         style="max-height: 420px; object-fit: cover;"
                         onerror="this.onerror=null; this.src='https://via.placeholder.com/500x400?text=RibinaMart';">
                </div>
                <div class="col-md-7 d-flex flex-column">
                    <div class="mb-2">
                        <span class="badge bg-secondary"><c:out value="${product.category}"/></span>
                        <span class="text-muted ms-2 small">Sold by: <strong><c:out value="${product.sellerName}"/></strong></span>
                    </div>
                    <h2 class="fw-bold"><c:out value="${product.name}"/></h2>

                    <!-- Star Rating & Review Count -->
                    <div class="d-flex align-items-center mb-3">
                        <div class="star-rating me-2 fs-5">
                            <c:forEach begin="1" end="5" var="i">
                                <i class="bi ${i <= product.averageRating ? 'bi-star-fill' : 'bi-star'}"></i>
                            </c:forEach>
                        </div>
                        <span class="fw-bold fs-5 me-1"><c:out value="${product.averageRating}"/></span>
                        <span class="text-muted">(<c:out value="${product.reviewCount}"/> reviews)</span>
                    </div>

                    <div class="mb-3">
                        <span class="fs-2 fw-bold text-primary">₹<c:out value="${product.price}"/></span>
                        <span class="text-muted small ms-2">Inclusive of all taxes</span>
                    </div>

                    <div class="mb-4">
                        <c:choose>
                            <c:when test="${product.stockQuantity > 0}">
                                <span class="badge bg-success-subtle text-success border border-success-subtle px-3 py-2 fs-6">
                                    <i class="bi bi-check2-circle me-1"></i> In Stock (<c:out value="${product.stockQuantity}"/> units available)
                                </span>
                            </c:when>
                            <c:otherwise>
                                <span class="badge bg-danger-subtle text-danger border border-danger-subtle px-3 py-2 fs-6">
                                    <i class="bi bi-x-circle me-1"></i> Currently Out of Stock
                                </span>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <div class="mb-4">
                        <h6 class="fw-bold text-uppercase text-secondary small">Description</h6>
                        <p class="text-secondary" style="white-space: pre-line;"><c:out value="${product.description}"/></p>
                    </div>

                    <!-- Add To Cart Form -->
                    <div class="mt-auto pt-3 border-top">
                        <c:choose>
                            <c:when test="${product.stockQuantity > 0}">
                                <form action="${pageContext.request.contextPath}/cart/add" method="post" class="row g-2 align-items-center">
                                    <input type="hidden" name="productId" value="<c:out value='${product.id}'/>">
                                    <div class="col-auto">
                                        <label for="quantity" class="col-form-label fw-bold">Quantity:</label>
                                    </div>
                                    <div class="col-auto" style="width: 100px;">
                                        <input type="number" id="quantity" name="quantity" class="form-control" value="1" min="1" max="<c:out value='${product.stockQuantity}'/>" required>
                                    </div>
                                    <div class="col-auto">
                                        <button type="submit" class="btn btn-primary px-4">
                                            <i class="bi bi-cart-plus-fill me-1"></i> Add to Cart
                                        </button>
                                    </div>
                                </form>
                            </c:when>
                            <c:otherwise>
                                <button class="btn btn-secondary px-4" disabled>Out of Stock</button>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Customer Reviews Section (F8) -->
    <div class="row">
        <div class="col-lg-8">
            <div class="card shadow-sm mb-4">
                <div class="card-header bg-white py-3">
                    <h5 class="mb-0 fw-bold"><i class="bi bi-chat-left-quote-fill me-2 text-primary"></i> Customer Reviews</h5>
                </div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${empty reviews}">
                            <p class="text-muted text-center py-4 mb-0">No reviews yet for this product. Be the first to review!</p>
                        </c:when>
                        <c:otherwise>
                            <div class="list-group list-group-flush">
                                <c:forEach var="rev" items="${reviews}">
                                    <div class="list-group-item px-0 py-3">
                                        <div class="d-flex justify-content-between align-items-center mb-1">
                                            <span class="fw-bold"><c:out value="${rev.buyerName}"/></span>
                                            <span class="text-muted small"><c:out value="${rev.createdAt}"/></span>
                                        </div>
                                        <div class="star-rating mb-2">
                                            <c:forEach begin="1" end="5" var="s">
                                                <i class="bi ${s <= rev.rating ? 'bi-star-fill' : 'bi-star'}"></i>
                                            </c:forEach>
                                            <span class="badge bg-success-subtle text-success ms-2 small">Verified Purchase</span>
                                        </div>
                                        <p class="mb-0 text-secondary"><c:out value="${rev.comment}"/></p>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <!-- Add Review Column -->
        <div class="col-lg-4">
            <div class="card shadow-sm">
                <div class="card-header bg-white py-3">
                    <h5 class="mb-0 fw-bold"><i class="bi bi-pencil-square me-2 text-primary"></i> Write a Review</h5>
                </div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${sessionScope.currentUser == null}">
                            <p class="text-muted small mb-3">Please log in as a buyer to leave a verified review.</p>
                            <a href="${pageContext.request.contextPath}/auth/login?redirect=/products/detail?id=<c:out value='${product.id}'/>" class="btn btn-outline-primary btn-sm w-100">
                                Log in to Review
                            </a>
                        </c:when>
                        <c:when test="${!canReview}">
                            <div class="alert alert-info small mb-0">
                                <i class="bi bi-info-circle me-1"></i> Reviews are restricted to customers who have ordered and received this product.
                            </div>
                        </c:when>
                        <c:otherwise>
                            <form action="${pageContext.request.contextPath}/reviews/add" method="post">
                                <input type="hidden" name="productId" value="<c:out value='${product.id}'/>">
                                <div class="mb-3">
                                    <label class="form-label fw-bold small">Rating (1 to 5 Stars):</label>
                                    <select name="rating" class="form-select" required>
                                        <option value="5">⭐⭐⭐⭐⭐ (5 - Excellent)</option>
                                        <option value="4">⭐⭐⭐⭐ (4 - Very Good)</option>
                                        <option value="3">⭐⭐⭐ (3 - Average)</option>
                                        <option value="2">⭐⭐ (2 - Below Average)</option>
                                        <option value="1">⭐ (1 - Poor)</option>
                                    </select>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label fw-bold small">Your Feedback:</label>
                                    <textarea name="comment" class="form-control" rows="3" placeholder="Share details of your experience with this item..." required></textarea>
                                </div>
                                <button type="submit" class="btn btn-primary w-100">Submit Review</button>
                            </form>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
