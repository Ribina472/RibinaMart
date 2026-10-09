<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${pageTitle != null ? pageTitle : 'RibinaMart - Campus E-Commerce Platform'}"/></title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <!-- RibinaMart AI Chatbot Widget CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/chat-widget.css">
    <style>
        :root {
            --rm-primary: #0d6efd;
            --rm-dark: #212529;
            --rm-bg: #f8f9fa;
        }
        body {
            background-color: var(--rm-bg);
            min-height: 100vh;
            display: flex;
            flex-direction: column;
        }
        .main-content {
            flex: 1 0 auto;
        }
        .navbar-brand {
            font-weight: 800;
            letter-spacing: -0.5px;
        }
        .badge-role-admin { background-color: #dc3545; color: white; }
        .badge-role-seller { background-color: #fd7e14; color: white; }
        .badge-role-buyer { background-color: #198754; color: white; }
        .star-rating { color: #ffc107; }
        .card { border-radius: 0.75rem; border: 1px solid rgba(0,0,0,0.08); }
        .card-img-top { height: 200px; object-fit: cover; border-top-left-radius: 0.75rem; border-top-right-radius: 0.75rem; }
    </style>
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-dark bg-dark sticky-top shadow-sm">
    <div class="container">
        <a class="navbar-brand text-primary fs-3" href="${pageContext.request.contextPath}/products">
            <i class="bi bi-bag-check-fill me-2"></i>RibinaMart
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navContent">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navContent">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/products"><i class="bi bi-grid me-1"></i> Browse Catalog</a>
                </li>
                <c:if test="${sessionScope.currentUser != null && (sessionScope.currentUser.role == 'SELLER' || sessionScope.currentUser.role == 'ADMIN')}">
                    <li class="nav-item">
                        <a class="nav-link text-warning" href="${pageContext.request.contextPath}/seller/dashboard">
                            <i class="bi bi-speedometer2 me-1"></i> Seller Dashboard
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link text-warning" href="${pageContext.request.contextPath}/seller/orders">
                            <i class="bi bi-box-seam me-1"></i> Incoming Orders
                        </a>
                    </li>
                </c:if>
                <c:if test="${sessionScope.currentUser != null && sessionScope.currentUser.role == 'ADMIN'}">
                    <li class="nav-item">
                        <a class="nav-link text-danger" href="${pageContext.request.contextPath}/admin/dashboard">
                            <i class="bi bi-shield-lock me-1"></i> Admin Panel
                        </a>
                    </li>
                </c:if>
            </ul>

            <form class="d-flex me-3" action="${pageContext.request.contextPath}/products" method="get">
                <div class="input-group input-group-sm">
                    <input class="form-control" type="search" name="keyword" placeholder="Search products..." value="<c:out value='${param.keyword}'/>">
                    <button class="btn btn-outline-light" type="submit"><i class="bi bi-search"></i></button>
                </div>
            </form>

            <ul class="navbar-nav mb-2 mb-lg-0 align-items-center">
                <li class="nav-item me-2">
                    <a class="btn btn-outline-danger position-relative text-white border-secondary" href="${pageContext.request.contextPath}/wishlist" title="My Wishlist">
                        <i class="bi bi-heart"></i> Wishlist
                    </a>
                </li>
                <li class="nav-item me-2">
                    <a class="btn btn-outline-primary position-relative text-white border-secondary" href="${pageContext.request.contextPath}/cart">
                        <i class="bi bi-cart3"></i> Cart
                    </a>
                </li>
                <c:choose>
                    <c:when test="${sessionScope.currentUser != null}">
                        <li class="nav-item dropdown">
                            <a class="nav-link dropdown-toggle text-light" href="#" role="button" data-bs-toggle="dropdown">
                                <i class="bi bi-person-circle me-1"></i>
                                <c:out value="${sessionScope.currentUser.name}"/>
                                <span class="badge badge-role-${fn:toLowerCase(sessionScope.currentUser.role)} ms-1">
                                    <c:out value="${sessionScope.currentUser.role}"/>
                                </span>
                            </a>
                            <ul class="dropdown-menu dropdown-menu-end shadow">
                                <li><span class="dropdown-item-text small text-muted"><c:out value="${sessionScope.currentUser.email}"/></span></li>
                                <li><hr class="dropdown-divider"></li>
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/orders"><i class="bi bi-receipt me-2"></i>My Orders</a></li>
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/wishlist"><i class="bi bi-heart me-2 text-danger"></i>My Wishlist</a></li>
                                <li><hr class="dropdown-divider"></li>
                                <li><a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/auth/logout"><i class="bi bi-box-arrow-right me-2"></i>Logout</a></li>
                            </ul>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <li class="nav-item">
                            <a class="btn btn-sm btn-outline-light me-2" href="${pageContext.request.contextPath}/auth/login">Login</a>
                        </li>
                        <li class="nav-item">
                            <a class="btn btn-sm btn-primary" href="${pageContext.request.contextPath}/auth/register">Sign Up</a>
                        </li>
                    </c:otherwise>
                </c:choose>
            </ul>
        </div>
    </div>
</nav>
<div class="main-content">
