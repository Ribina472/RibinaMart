<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="pageTitle" value="${isEdit ? 'Edit Product' : 'New Product Listing'} - RibinaMart" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="container py-4">
    <div class="row justify-content-center">
        <div class="col-lg-8">
            <div class="d-flex align-items-center justify-content-between mb-4">
                <div>
                    <h2 class="fw-bold mb-1">
                        <c:choose>
                            <c:when test="${isEdit}"><i class="bi bi-pencil-square me-2 text-warning"></i> Edit Product Listing</c:when>
                            <c:otherwise><i class="bi bi-plus-circle-fill me-2 text-warning"></i> Add New Product Listing</c:otherwise>
                        </c:choose>
                    </h2>
                    <p class="text-muted mb-0">Provide complete and accurate product details for prospective buyers.</p>
                </div>
                <a href="${pageContext.request.contextPath}/seller/dashboard" class="btn btn-outline-secondary">
                    <i class="bi bi-arrow-left me-1"></i> Back to Dashboard
                </a>
            </div>

            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="bi bi-exclamation-triangle-fill me-2"></i> <c:out value="${errorMessage}"/>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>

            <div class="card shadow-sm border-0">
                <div class="card-body p-4 p-md-5">
                    <form action="${pageContext.request.contextPath}/seller/products/${isEdit ? 'edit' : 'new'}" method="post">
                        <c:if test="${isEdit}">
                            <input type="hidden" name="id" value="<c:out value='${product.id}'/>">
                        </c:if>

                        <div class="mb-3">
                            <label class="form-label fw-semibold">Product Title / Name *</label>
                            <input type="text" name="name" class="form-control ${not empty fieldErrors['name'] ? 'is-invalid' : ''}"
                                   placeholder="e.g. UltraBook Pro 15.6-inch Laptop"
                                   value="<c:out value='${not empty name ? name : product.name}'/>" required autofocus>
                            <c:if test="${not empty fieldErrors['name']}">
                                <div class="invalid-feedback"><c:out value="${fieldErrors['name']}"/></div>
                            </c:if>
                        </div>

                        <div class="row g-3 mb-3">
                            <div class="col-md-6">
                                <label class="form-label fw-semibold">Category *</label>
                                <input type="text" name="category" list="categoryOptions" class="form-control ${not empty fieldErrors['category'] ? 'is-invalid' : ''}"
                                       placeholder="e.g. Electronics, Books, Stationery..."
                                       value="<c:out value='${not empty category ? category : product.category}'/>" required>
                                <datalist id="categoryOptions">
                                    <option value="Electronics">
                                    <option value="Books">
                                    <option value="Stationery">
                                    <option value="Hostel Essentials">
                                    <option value="Apparel">
                                    <option value="Sports">
                                </datalist>
                                <c:if test="${not empty fieldErrors['category']}">
                                    <div class="invalid-feedback"><c:out value="${fieldErrors['category']}"/></div>
                                </c:if>
                            </div>
                            <div class="col-md-3">
                                <label class="form-label fw-semibold">Price (INR ₹) *</label>
                                <div class="input-group">
                                    <span class="input-group-text">₹</span>
                                    <input type="number" step="0.01" min="1.00" name="price" class="form-control ${not empty fieldErrors['price'] ? 'is-invalid' : ''}"
                                           placeholder="0.00"
                                           value="<c:out value='${not empty price ? price : product.price}'/>" required>
                                </div>
                                <c:if test="${not empty fieldErrors['price']}">
                                    <div class="text-danger small mt-1"><c:out value="${fieldErrors['price']}"/></div>
                                </c:if>
                            </div>
                            <div class="col-md-3">
                                <label class="form-label fw-semibold">Stock Units *</label>
                                <input type="number" min="0" name="stockQuantity" class="form-control ${not empty fieldErrors['stockQuantity'] ? 'is-invalid' : ''}"
                                       placeholder="Units in stock"
                                       value="<c:out value='${not empty stockQuantity ? stockQuantity : (product != null ? product.stockQuantity : 1)}'/>" required>
                                <c:if test="${not empty fieldErrors['stockQuantity']}">
                                    <div class="invalid-feedback"><c:out value="${fieldErrors['stockQuantity']}"/></div>
                                </c:if>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-semibold">Image URL</label>
                            <input type="url" name="imageUrl" class="form-control"
                                   placeholder="https://example.com/images/product.jpg"
                                   value="<c:out value='${not empty imageUrl ? imageUrl : product.imageUrl}'/>">
                            <div class="form-text">Paste a web link to the product photo (e.g. Unsplash or direct image link).</div>
                        </div>

                        <div class="mb-4">
                            <label class="form-label fw-semibold">Product Description</label>
                            <textarea name="description" class="form-control" rows="4"
                                      placeholder="Detail features, technical specifications, edition, warranty, or condition..."><c:out value="${not empty description ? description : product.description}"/></textarea>
                        </div>

                        <div class="d-flex justify-content-end gap-2">
                            <a href="${pageContext.request.contextPath}/seller/dashboard" class="btn btn-outline-secondary px-4">Cancel</a>
                            <button type="submit" class="btn btn-warning px-4 fw-bold">
                                <c:choose>
                                    <c:when test="${isEdit}"><i class="bi bi-check2 me-1"></i> Save Changes</c:when>
                                    <c:otherwise><i class="bi bi-plus-lg me-1"></i> Create Listing</c:otherwise>
                                </c:choose>
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
