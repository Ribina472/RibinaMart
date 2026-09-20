<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="pageTitle" value="403 Forbidden - RibinaMart" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="container py-5 text-center">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <i class="bi bi-shield-x text-danger" style="font-size: 5rem;"></i>
            <h1 class="display-4 fw-bold mt-3">403 Forbidden</h1>
            <p class="lead text-muted">You do not have administrative or appropriate role permissions to access this page.</p>
            <a href="${pageContext.request.contextPath}/products" class="btn btn-primary px-4 py-2 mt-3">
                <i class="bi bi-house me-1"></i> Return to Homepage
            </a>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
