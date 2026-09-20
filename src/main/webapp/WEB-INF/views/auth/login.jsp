<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="pageTitle" value="Sign In - RibinaMart" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-md-5">
            <div class="card shadow-sm border-0">
                <div class="card-body p-4 p-md-5">
                    <div class="text-center mb-4">
                        <i class="bi bi-person-lock text-primary" style="font-size: 2.5rem;"></i>
                        <h3 class="fw-bold mt-2">Sign In</h3>
                        <p class="text-muted small">Access your RibinaMart account</p>
                    </div>

                    <c:if test="${not empty param.registered}">
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            <i class="bi bi-check-circle-fill me-1"></i> Registration successful! Please sign in.
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>
                    <c:if test="${not empty param.loggedOut}">
                        <div class="alert alert-info alert-dismissible fade show" role="alert">
                            <i class="bi bi-info-circle-fill me-1"></i> You have been logged out securely.
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>
                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <i class="bi bi-exclamation-triangle-fill me-1"></i> <c:out value="${errorMessage}"/>
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>

                    <form action="${pageContext.request.contextPath}/auth/login" method="post">
                        <input type="hidden" name="redirect" value="<c:out value='${param.redirect}'/>">
                        <div class="mb-3">
                            <label class="form-label fw-semibold">Email Address</label>
                            <input type="email" name="email" id="loginEmail" class="form-control" placeholder="name@example.com"
                                   value="<c:out value='${email}'/>" required autofocus>
                        </div>
                        <div class="mb-4">
                            <label class="form-label fw-semibold">Password</label>
                            <input type="password" name="password" id="loginPassword" class="form-control" placeholder="••••••••" required>
                        </div>
                        <button type="submit" class="btn btn-primary w-100 py-2 fw-semibold">Sign In</button>
                    </form>

                    <div class="text-center mt-4">
                        <span class="text-muted small">Don't have an account? </span>
                        <a href="${pageContext.request.contextPath}/auth/register" class="small fw-semibold">Create account</a>
                    </div>
                </div>
            </div>

            <!-- Demo Seed Accounts Card for Evaluation -->
            <div class="card mt-4 bg-light border-0 shadow-sm">
                <div class="card-body p-3">
                    <h6 class="fw-bold text-secondary mb-2 small text-uppercase"><i class="bi bi-key-fill me-1"></i> Quick Demo Accounts</h6>
                    <div class="d-flex flex-wrap gap-1">
                        <button type="button" class="btn btn-sm btn-outline-danger" onclick="fillCredentials('admin@ribinamart.com', 'Admin@123')">Admin</button>
                        <button type="button" class="btn btn-sm btn-outline-warning" onclick="fillCredentials('seller1@ribinamart.com', 'Seller@123')">Seller 1</button>
                        <button type="button" class="btn btn-sm btn-outline-success" onclick="fillCredentials('buyer1@ribinamart.com', 'Buyer@123')">Buyer 1</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
function fillCredentials(email, password) {
    document.getElementById('loginEmail').value = email;
    document.getElementById('loginPassword').value = password;
}
</script>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
