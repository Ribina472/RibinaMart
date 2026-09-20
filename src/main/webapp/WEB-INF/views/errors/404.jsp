<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="pageTitle" value="404 Page Not Found - RibinaMart" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="container py-5 text-center">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <i class="bi bi-compass text-warning" style="font-size: 5rem;"></i>
            <h1 class="display-4 fw-bold mt-3">404 - Not Found</h1>
            <p class="lead text-muted">The page or item listing you are looking for might have been removed, had its name changed, or is temporarily unavailable.</p>
            <a href="${pageContext.request.contextPath}/products" class="btn btn-primary px-4 py-2 mt-3">
                <i class="bi bi-grid me-1"></i> Browse Catalog
            </a>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
