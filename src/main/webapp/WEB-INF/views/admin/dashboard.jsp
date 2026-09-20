<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="pageTitle" value="Admin Panel - RibinaMart" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h2 class="fw-bold mb-1 text-danger"><i class="bi bi-shield-lock-fill me-2"></i> Admin Panel & Oversight</h2>
            <p class="text-muted mb-0">Platform governance, user accounts audit, and listing moderation.</p>
        </div>
    </div>

    <c:if test="${not empty param.moderated}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="bi bi-check-circle-fill me-2"></i> Product listing moderation status has been updated!
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <!-- Platform KPI Stats -->
    <div class="row g-4 mb-5">
        <div class="col-md-4">
            <div class="card shadow-sm border-start border-4 border-primary">
                <div class="card-body p-4 d-flex align-items-center">
                    <div class="rounded-circle bg-primary-subtle p-3 me-3 text-primary fs-3">
                        <i class="bi bi-people-fill"></i>
                    </div>
                    <div>
                        <div class="text-muted small text-uppercase fw-bold">Total Users</div>
                        <h3 class="fw-bold mb-0"><c:out value="${users.size()}"/></h3>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card shadow-sm border-start border-4 border-warning">
                <div class="card-body p-4 d-flex align-items-center">
                    <div class="rounded-circle bg-warning-subtle p-3 me-3 text-warning fs-3">
                        <i class="bi bi-box-seam-fill"></i>
                    </div>
                    <div>
                        <div class="text-muted small text-uppercase fw-bold">Platform Listings</div>
                        <h3 class="fw-bold mb-0"><c:out value="${products.size()}"/></h3>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card shadow-sm border-start border-4 border-success">
                <div class="card-body p-4 d-flex align-items-center">
                    <div class="rounded-circle bg-success-subtle p-3 me-3 text-success fs-3">
                        <i class="bi bi-receipt-cutoff"></i>
                    </div>
                    <div>
                        <div class="text-muted small text-uppercase fw-bold">Total Orders</div>
                        <h3 class="fw-bold mb-0"><c:out value="${orders.size()}"/></h3>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Admin Navigation Tabs -->
    <ul class="nav nav-pills mb-4" id="adminTab" role="tablist">
        <li class="nav-item" role="presentation">
            <button class="nav-link active fw-bold" id="products-tab" data-bs-toggle="tab" data-bs-target="#products-pane" type="button">
                <i class="bi bi-shield-check me-1"></i> Product Moderation (<c:out value="${products.size()}"/>)
            </button>
        </li>
        <li class="nav-item" role="presentation">
            <button class="nav-link fw-bold" id="users-tab" data-bs-toggle="tab" data-bs-target="#users-pane" type="button">
                <i class="bi bi-people me-1"></i> User Accounts (<c:out value="${users.size()}"/>)
            </button>
        </li>
        <li class="nav-item" role="presentation">
            <button class="nav-link fw-bold" id="orders-tab" data-bs-toggle="tab" data-bs-target="#orders-pane" type="button">
                <i class="bi bi-receipt me-1"></i> Orders Audit (<c:out value="${orders.size()}"/>)
            </button>
        </li>
    </ul>

    <div class="tab-content" id="adminTabContent">
        <!-- 1. Product Moderation Pane -->
        <div class="tab-pane fade show active" id="products-pane" role="tabpanel">
            <div class="card shadow-sm">
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="table-light">
                            <tr>
                                <th>Product</th>
                                <th>Seller</th>
                                <th>Category</th>
                                <th>Price</th>
                                <th>Stock</th>
                                <th>Status</th>
                                <th class="text-end">Moderation Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="p" items="${products}">
                                <tr>
                                    <td>
                                        <div class="fw-bold"><c:out value="${p.name}"/></div>
                                        <span class="small text-muted">ID: #<c:out value="${p.id}"/></span>
                                    </td>
                                    <td><c:out value="${p.sellerName}"/></td>
                                    <td><span class="badge bg-light text-dark border"><c:out value="${p.category}"/></span></td>
                                    <td>₹<c:out value="${p.price}"/></td>
                                    <td><c:out value="${p.stockQuantity}"/></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${p.status == 'ACTIVE'}">
                                                <span class="badge bg-success">ACTIVE</span>
                                            </c:when>
                                            <c:when test="${p.status == 'MODERATED'}">
                                                <span class="badge bg-danger">MODERATED</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-secondary"><c:out value="${p.status}"/></span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="text-end">
                                        <form action="${pageContext.request.contextPath}/admin/products/moderate" method="post" class="d-inline">
                                            <input type="hidden" name="productId" value="<c:out value='${p.id}'/>">
                                            <c:choose>
                                                <c:when test="${p.status == 'ACTIVE'}">
                                                    <input type="hidden" name="status" value="MODERATED">
                                                    <button type="submit" class="btn btn-sm btn-outline-danger" title="Flag and hide listing">
                                                        <i class="bi bi-slash-circle me-1"></i> Moderate
                                                    </button>
                                                </c:when>
                                                <c:otherwise>
                                                    <input type="hidden" name="status" value="ACTIVE">
                                                    <button type="submit" class="btn btn-sm btn-outline-success" title="Restore listing">
                                                        <i class="bi bi-check-circle me-1"></i> Approve
                                                    </button>
                                                </c:otherwise>
                                            </c:choose>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <!-- 2. Users Accounts Pane -->
        <div class="tab-pane fade" id="users-pane" role="tabpanel">
            <div class="card shadow-sm">
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="table-light">
                            <tr>
                                <th>User ID</th>
                                <th>Name</th>
                                <th>Email</th>
                                <th>Role</th>
                                <th>Registered At</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="u" items="${users}">
                                <tr>
                                    <td>#<c:out value="${u.id}"/></td>
                                    <td class="fw-bold"><c:out value="${u.name}"/></td>
                                    <td><c:out value="${u.email}"/></td>
                                    <td>
                                        <span class="badge badge-role-${u.role.name().toLowerCase()}">
                                            <c:out value="${u.role}"/>
                                        </span>
                                    </td>
                                    <td class="text-muted small"><c:out value="${u.createdAt}"/></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <!-- 3. Orders Audit Pane -->
        <div class="tab-pane fade" id="orders-pane" role="tabpanel">
            <div class="card shadow-sm">
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="table-light">
                            <tr>
                                <th>Order ID</th>
                                <th>Buyer</th>
                                <th>Total Amount</th>
                                <th>Status</th>
                                <th>Payment</th>
                                <th>Date</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="o" items="${orders}">
                                <tr>
                                    <td class="fw-bold">#<c:out value="${o.id}"/></td>
                                    <td>
                                        <div><c:out value="${o.buyerName}"/></div>
                                        <div class="small text-muted"><c:out value="${o.buyerEmail}"/></div>
                                    </td>
                                    <td class="fw-bold text-primary">₹<c:out value="${o.totalAmount}"/></td>
                                    <td>
                                        <span class="badge ${o.status == 'DELIVERED' ? 'bg-success' : (o.status == 'CANCELLED' ? 'bg-danger' : 'bg-primary')}">
                                            <c:out value="${o.status}"/>
                                        </span>
                                    </td>
                                    <td><span class="badge bg-secondary"><c:out value="${o.paymentMethod}"/></span></td>
                                    <td class="small text-muted"><c:out value="${o.createdAt}"/></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
