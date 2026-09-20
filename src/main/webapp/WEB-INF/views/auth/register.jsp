<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="pageTitle" value="Create Account - RibinaMart" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-md-6 col-lg-5">
            <div class="card shadow-sm border-0">
                <div class="card-body p-4 p-md-5">
                    <div class="text-center mb-4">
                        <i class="bi bi-person-plus-fill text-primary" style="font-size: 2.5rem;"></i>
                        <h3 class="fw-bold mt-2">Create Account</h3>
                        <p class="text-muted small">Join the RibinaMart campus marketplace</p>
                    </div>

                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <i class="bi bi-exclamation-triangle-fill me-1"></i> <c:out value="${errorMessage}"/>
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>

                    <form action="${pageContext.request.contextPath}/auth/register" method="post">
                        <div class="mb-3">
                            <label class="form-label fw-semibold">Full Name</label>
                            <input type="text" name="name" class="form-control ${not empty fieldErrors['name'] ? 'is-invalid' : ''}"
                                   placeholder="e.g. Aditya Sharma" value="<c:out value='${name}'/>" required autofocus>
                            <c:if test="${not empty fieldErrors['name']}">
                                <div class="invalid-feedback"><c:out value="${fieldErrors['name']}"/></div>
                            </c:if>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-semibold">Email Address</label>
                            <input type="email" name="email" class="form-control ${not empty fieldErrors['email'] ? 'is-invalid' : ''}"
                                   placeholder="name@student.annauniv.edu" value="<c:out value='${email}'/>" required>
                            <c:if test="${not empty fieldErrors['email']}">
                                <div class="invalid-feedback"><c:out value="${fieldErrors['email']}"/></div>
                            </c:if>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-semibold">Password</label>
                            <input type="password" name="password" class="form-control ${not empty fieldErrors['password'] ? 'is-invalid' : ''}"
                                   placeholder="Minimum 6 characters" required>
                            <c:if test="${not empty fieldErrors['password']}">
                                <div class="invalid-feedback"><c:out value="${fieldErrors['password']}"/></div>
                            </c:if>
                        </div>

                        <div class="mb-4">
                            <label class="form-label fw-semibold">I want to register as:</label>
                            <div class="row g-2">
                                <div class="col-6">
                                    <input type="radio" class="btn-check" name="role" id="roleBuyer" value="BUYER" ${role != 'SELLER' ? 'checked' : ''}>
                                    <label class="btn btn-outline-success w-100 py-2" for="roleBuyer">
                                        <i class="bi bi-cart me-1"></i> Buyer
                                    </label>
                                </div>
                                <div class="col-6">
                                    <input type="radio" class="btn-check" name="role" id="roleSeller" value="SELLER" ${role == 'SELLER' ? 'checked' : ''}>
                                    <label class="btn btn-outline-warning w-100 py-2" for="roleSeller">
                                        <i class="bi bi-shop me-1"></i> Seller
                                    </label>
                                </div>
                            </div>
                            <c:if test="${not empty fieldErrors['role']}">
                                <div class="text-danger small mt-1"><c:out value="${fieldErrors['role']}"/></div>
                            </c:if>
                        </div>

                        <button type="submit" class="btn btn-primary w-100 py-2 fw-semibold">Register</button>
                    </form>

                    <div class="text-center mt-4">
                        <span class="text-muted small">Already have an account? </span>
                        <a href="${pageContext.request.contextPath}/auth/login" class="small fw-semibold">Sign in</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
