<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<c:set var="pageTitle" value="Browse Products - RibinaMart" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="container py-4">
    <!-- Category & Filter Header -->
    <div class="row align-items-center mb-4">
        <div class="col-md-7">
            <h2 class="fw-bold mb-1">Explore Products</h2>
            <p class="text-muted mb-0">High-quality electronics, books, stationery, and student essentials.</p>
        </div>
        <div class="col-md-5 mt-3 mt-md-0">
            <form action="${pageContext.request.contextPath}/products" method="get" class="d-flex gap-2">
                <input type="hidden" name="category" value="<c:out value='${selectedCategory}'/>">
                <input type="text" name="keyword" class="form-control" placeholder="Search by name or keyword..." value="<c:out value='${keyword}'/>">
                <button type="submit" class="btn btn-primary"><i class="bi bi-search"></i></button>
                <c:if test="${not empty keyword or (not empty selectedCategory and selectedCategory != 'ALL')}">
                    <a href="${pageContext.request.contextPath}/products" class="btn btn-outline-secondary">Reset</a>
                </c:if>
            </form>
        </div>
    </div>

    <!-- Category Chips -->
    <div class="d-flex flex-wrap gap-2 mb-4">
        <a href="${pageContext.request.contextPath}/products?category=ALL&keyword=<c:out value='${keyword}'/>"
           class="btn btn-sm ${selectedCategory == 'ALL' ? 'btn-primary' : 'btn-outline-secondary'}">
            All Categories
        </a>
        <c:forEach var="cat" items="${categories}">
            <a href="${pageContext.request.contextPath}/products?category=<c:out value='${cat}'/>&keyword=<c:out value='${keyword}'/>"
               class="btn btn-sm ${selectedCategory == cat ? 'btn-primary' : 'btn-outline-secondary'}">
                <c:out value="${cat}"/>
            </a>
        </c:forEach>
    </div>

    <!-- Product Grid -->
    <c:choose>
        <c:when test="${empty products}">
            <div class="card text-center py-5 shadow-sm">
                <div class="card-body">
                    <i class="bi bi-box2 text-muted" style="font-size: 3rem;"></i>
                    <h4 class="mt-3">No Products Found</h4>
                    <p class="text-muted">Try adjusting your category filter or search keywords.</p>
                    <a href="${pageContext.request.contextPath}/products" class="btn btn-primary">View All Products</a>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <div class="row row-cols-1 row-cols-sm-2 row-cols-md-3 row-cols-lg-4 g-4">
                <c:forEach var="p" items="${products}">
                    <div class="col">
                        <div class="card h-100 shadow-sm hover-shadow">
                            <img src="<c:out value='${p.imageUrl}'/>" class="card-img-top" alt="<c:out value='${p.name}'/>"
                                 onerror="this.onerror=null; this.src='https://via.placeholder.com/300x200?text=RibinaMart';">
                            <div class="card-body d-flex flex-column">
                                <span class="badge bg-light text-dark border mb-2 align-self-start"><c:out value="${p.category}"/></span>
                                <h5 class="card-title text-truncate" title="<c:out value='${p.name}'/>">
                                    <c:out value="${p.name}"/>
                                </h5>
                                <p class="card-text text-muted small flex-grow-1" style="display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;">
                                    <c:out value="${p.description}"/>
                                </p>
                                <div class="d-flex align-items-center mb-2">
                                    <span class="star-rating me-1">
                                        <i class="bi bi-star-fill"></i>
                                    </span>
                                    <span class="fw-bold me-1"><c:out value="${p.averageRating}"/></span>
                                    <span class="text-muted small">(<c:out value="${p.reviewCount}"/>)</span>
                                </div>
                                <div class="d-flex justify-content-between align-items-center mt-auto pt-2 border-top">
                                    <span class="fs-5 fw-bold text-primary">₹<c:out value="${p.price}"/></span>
                                    <c:choose>
                                        <c:when test="${p.stockQuantity > 0}">
                                            <span class="badge bg-success-subtle text-success border border-success-subtle">
                                                In Stock (<c:out value="${p.stockQuantity}"/>)
                                            </span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-danger-subtle text-danger border border-danger-subtle">Out of Stock</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <div class="mt-3">
                                    <a href="${pageContext.request.contextPath}/products/detail?id=<c:out value='${p.id}'/>"
                                       class="btn btn-outline-primary w-100">
                                        View Details
                                    </a>
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
