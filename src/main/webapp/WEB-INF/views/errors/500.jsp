<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="pageTitle" value="500 Internal Server Error - RibinaMart" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="container py-5 text-center">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <i class="bi bi-exclamation-octagon text-danger" style="font-size: 5rem;"></i>
            <h1 class="display-4 fw-bold mt-3">500 - Server Error</h1>
            <p class="lead text-muted">An unexpected error occurred while processing your request. Please try again or contact support.</p>
            <a href="${pageContext.request.contextPath}/products" class="btn btn-primary px-4 py-2 mt-3">
                <i class="bi bi-house me-1"></i> Return to Homepage
            </a>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
