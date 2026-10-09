<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="pageTitle" value="Seller Dashboard - RibinaMart" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h2 class="fw-bold mb-1"><i class="bi bi-speedometer2 me-2 text-warning"></i> Seller Dashboard</h2>
            <p class="text-muted mb-0">Manage your product catalog, prices, and inventory stock levels.</p>
        </div>
        <a href="${pageContext.request.contextPath}/seller/products/new" class="btn btn-warning fw-bold shadow-sm">
            <i class="bi bi-plus-circle me-1"></i> Add New Product
        </a>
    </div>

    <!-- Feedback messages -->
    <c:if test="${not empty param.created}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="bi bi-check-circle-fill me-2"></i> New product listing created successfully!
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    <c:if test="${not empty param.updated}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="bi bi-check-circle-fill me-2"></i> Product listing updated successfully!
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    <c:if test="${not empty param.deleted}">
        <div class="alert alert-info alert-dismissible fade show" role="alert">
            <i class="bi bi-info-circle-fill me-2"></i> Product listing removed from public catalog.
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <!-- Seller Sales Analytics KPI Cards (O3) -->
    <div class="row g-3 mb-4">
        <div class="col-sm-6 col-lg-3">
            <div class="card border-0 shadow-sm bg-white h-100">
                <div class="card-body d-flex align-items-center">
                    <div class="rounded-circle bg-success-subtle text-success p-3 me-3">
                        <i class="bi bi-currency-rupee fs-3"></i>
                    </div>
                    <div>
                        <div class="text-muted small fw-semibold text-uppercase">Total Revenue</div>
                        <div class="fs-4 fw-bold text-dark">₹<c:out value="${analytics.totalRevenue != null ? analytics.totalRevenue : '0.00'}"/></div>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-sm-6 col-lg-3">
            <div class="card border-0 shadow-sm bg-white h-100">
                <div class="card-body d-flex align-items-center">
                    <div class="rounded-circle bg-primary-subtle text-primary p-3 me-3">
                        <i class="bi bi-receipt fs-3"></i>
                    </div>
                    <div>
                        <div class="text-muted small fw-semibold text-uppercase">Orders Received</div>
                        <div class="fs-4 fw-bold text-dark"><c:out value="${analytics.totalOrdersCount != null ? analytics.totalOrdersCount : 0}"/></div>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-sm-6 col-lg-2">
            <div class="card border-0 shadow-sm bg-white h-100">
                <div class="card-body d-flex align-items-center">
                    <div class="rounded-circle bg-info-subtle text-info p-3 me-3">
                        <i class="bi bi-box-seam fs-3"></i>
                    </div>
                    <div>
                        <div class="text-muted small fw-semibold text-uppercase">Units Sold</div>
                        <div class="fs-4 fw-bold text-dark"><c:out value="${analytics.totalUnitsSold != null ? analytics.totalUnitsSold : 0}"/></div>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-sm-6 col-lg-2">
            <div class="card border-0 shadow-sm bg-white h-100">
                <div class="card-body d-flex align-items-center">
                    <div class="rounded-circle bg-secondary-subtle text-secondary p-3 me-3">
                        <i class="bi bi-tags fs-3"></i>
                    </div>
                    <div>
                        <div class="text-muted small fw-semibold text-uppercase">Active Items</div>
                        <div class="fs-4 fw-bold text-dark"><c:out value="${analytics.activeListingsCount != null ? analytics.activeListingsCount : 0}"/></div>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-sm-6 col-lg-2">
            <div class="card border-0 shadow-sm bg-white h-100">
                <div class="card-body d-flex align-items-center">
                    <div class="rounded-circle bg-warning-subtle text-warning p-3 me-3">
                        <i class="bi bi-exclamation-triangle fs-3"></i>
                    </div>
                    <div>
                        <div class="text-muted small fw-semibold text-uppercase">Low Stock</div>
                        <div class="fs-4 fw-bold text-warning"><c:out value="${analytics.lowStockCount != null ? analytics.lowStockCount : 0}"/></div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Product Listings Table -->
    <div class="card shadow-sm">
        <div class="card-header bg-white py-3 d-flex justify-content-between align-items-center">
            <h5 class="mb-0 fw-bold">My Product Inventory</h5>
            <span class="badge bg-secondary"><c:out value="${products.size()}"/> Products</span>
        </div>
        <c:choose>
            <c:when test="${empty products}">
                <div class="card-body text-center py-5">
                    <i class="bi bi-box-seam text-muted" style="font-size: 3rem;"></i>
                    <h5 class="mt-3">No Listings Created Yet</h5>
                    <p class="text-muted">Click "Add New Product" to start selling on RibinaMart.</p>
                    <a href="${pageContext.request.contextPath}/seller/products/new" class="btn btn-warning">Create Your First Listing</a>
                </div>
            </c:when>
            <c:otherwise>
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="table-light">
                            <tr>
                                <th scope="col">Product</th>
                                <th scope="col">Category</th>
                                <th scope="col">Price</th>
                                <th scope="col">Stock Level</th>
                                <th scope="col">Status</th>
                                <th scope="col" class="text-end">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="p" items="${products}">
                                <tr>
                                    <td>
                                        <div class="d-flex align-items-center">
                                            <img src="<c:out value='${p.imageUrl}'/>" class="rounded border me-3"
                                                 style="width: 50px; height: 50px; object-fit: cover;"
                                                 alt="<c:out value='${p.name}'/>"
                                                 onerror="this.onerror=null; this.src='https://via.placeholder.com/50?text=RibinaMart';">
                                            <div>
                                                <a href="${pageContext.request.contextPath}/products/detail?id=<c:out value='${p.id}'/>" class="fw-bold text-dark text-decoration-none">
                                                    <c:out value="${p.name}"/>
                                                </a>
                                                <div class="small text-muted text-truncate" style="max-width: 280px;">
                                                    <c:out value="${p.description}"/>
                                                </div>
                                            </div>
                                        </div>
                                    </td>
                                    <td><span class="badge bg-light text-dark border"><c:out value="${p.category}"/></span></td>
                                    <td class="fw-bold text-primary">₹<c:out value="${p.price}"/></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${p.stockQuantity > 5}">
                                                <span class="badge bg-success-subtle text-success"><c:out value="${p.stockQuantity}"/> units</span>
                                            </c:when>
                                            <c:when test="${p.stockQuantity > 0}">
                                                <span class="badge bg-warning-subtle text-warning"><c:out value="${p.stockQuantity}"/> left (Low)</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-danger-subtle text-danger">Out of Stock (0)</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <span class="badge ${p.status == 'ACTIVE' ? 'bg-success' : 'bg-secondary'}">
                                            <c:out value="${p.status}"/>
                                        </span>
                                    </td>
                                    <td class="text-end">
                                        <a href="${pageContext.request.contextPath}/seller/products/edit?id=<c:out value='${p.id}'/>"
                                           class="btn btn-sm btn-outline-secondary me-1" title="Edit Listing">
                                            <i class="bi bi-pencil"></i>
                                        </a>
                                        <form action="${pageContext.request.contextPath}/seller/products/delete" method="post"
                                              style="display:inline;" onsubmit="return confirm('Are you sure you want to remove this product listing?');">
                                            <input type="hidden" name="id" value="<c:out value='${p.id}'/>">
                                            <button type="submit" class="btn btn-sm btn-outline-danger" title="Delete Listing">
                                                <i class="bi bi-trash3"></i>
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
