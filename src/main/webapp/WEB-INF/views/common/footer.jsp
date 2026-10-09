<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
</div> <!-- end main-content -->
<footer class="bg-dark text-white py-4 mt-5 border-top border-secondary">
    <div class="container text-center">
        <div class="row align-items-center">
            <div class="col-md-6 text-md-start mb-2 mb-md-0">
                <span class="fw-bold text-primary fs-5">RibinaMart</span>
                <span class="text-secondary ms-2 small">| Anna University R2025 Semester 3 Capstone</span>
            </div>
            <div class="col-md-6 text-md-end text-secondary small">
                <span>Java Servlets &middot; JDBC &middot; Apache Tomcat 9.0 &middot; H2 Database</span>
            </div>
        </div>
    </div>
</footer>

<!-- Context Path for Client-side AJAX -->
<script>
    window.RIBINAMART_CONTEXT_PATH = '${pageContext.request.contextPath}';
</script>

<!-- Floating Chatbot Widget Launcher (O4) -->
<div id="rmChatLauncher" class="rm-chat-launcher" title="Chat with RibinaMart AI Assistant">
    <i class="bi bi-robot"></i>
    <span class="badge-pulse"></span>
</div>

<!-- Floating Chatbot Widget Container (O4) -->
<div id="rmChatContainer" class="rm-chat-container d-none">
    <div class="rm-chat-header">
        <div class="title">
            <span class="status-dot"></span>
            <i class="bi bi-robot"></i> RibinaMart Assistant
        </div>
        <button type="button" id="rmChatCloseBtn" class="btn btn-sm btn-link text-white p-0 fs-5" aria-label="Close">
            <i class="bi bi-x-lg"></i>
        </button>
    </div>
    <div id="rmChatMessages" class="rm-chat-messages">
        <!-- Messages rendered dynamically -->
    </div>
    <!-- Quick Topic Suggestions -->
    <div class="rm-quick-chips">
        <button type="button" class="rm-chip" data-query="How long does delivery take?">🚚 Shipping</button>
        <button type="button" class="rm-chip" data-query="What is your return and refund policy?">🔄 Returns</button>
        <button type="button" class="rm-chip" data-query="How can I track my order?">📦 Track Order</button>
        <button type="button" class="rm-chip" data-query="What payment methods are supported?">💳 Payments</button>
        <button type="button" class="rm-chip" data-query="How can I sell products on RibinaMart?">🏪 Sell Here</button>
        <button type="button" class="rm-chip" data-query="How does the wishlist work?">❤️ Wishlist</button>
    </div>
    <div class="rm-chat-input-area">
        <input type="text" id="rmChatInput" placeholder="Ask anything about RibinaMart..." maxlength="500">
        <button type="button" id="rmChatSendBtn" class="btn btn-primary btn-sm rounded-circle p-2" title="Send">
            <i class="bi bi-send-fill"></i>
        </button>
    </div>
</div>

<!-- Bootstrap 5 Bundle with Popper -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<!-- RibinaMart AI Chatbot Client Script -->
<script src="${pageContext.request.contextPath}/js/chat-widget.js"></script>
</body>
</html>
