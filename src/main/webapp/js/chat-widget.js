/**
 * RibinaMart AI Customer Support Assistant Widget
 * Asynchronously interacts with /api/chat endpoint with local session persistence.
 */
document.addEventListener('DOMContentLoaded', function () {
    const contextPath = window.RIBINAMART_CONTEXT_PATH || '';
    const launcher = document.getElementById('rmChatLauncher');
    const container = document.getElementById('rmChatContainer');
    const closeBtn = document.getElementById('rmChatCloseBtn');
    const messagesEl = document.getElementById('rmChatMessages');
    const inputEl = document.getElementById('rmChatInput');
    const sendBtn = document.getElementById('rmChatSendBtn');
    const quickChips = document.querySelectorAll('.rm-chip');

    if (!launcher || !container) return;

    // Toggle Chat Window
    launcher.addEventListener('click', function () {
        const isHidden = container.classList.contains('d-none');
        if (isHidden) {
            container.classList.remove('d-none');
            inputEl.focus();
            scrollToBottom();
            if (messagesEl.children.length === 0) {
                loadInitialHistory();
            }
        } else {
            container.classList.add('d-none');
        }
    });

    closeBtn.addEventListener('click', function () {
        container.classList.add('d-none');
    });

    // Quick chip buttons
    quickChips.forEach(chip => {
        chip.addEventListener('click', function () {
            const query = this.getAttribute('data-query');
            if (query) {
                sendMessage(query);
            }
        });
    });

    // Send on button click
    sendBtn.addEventListener('click', function () {
        const text = inputEl.value.trim();
        if (text) {
            sendMessage(text);
        }
    });

    // Send on Enter key
    inputEl.addEventListener('keypress', function (e) {
        if (e.key === 'Enter') {
            e.preventDefault();
            const text = inputEl.value.trim();
            if (text) {
                sendMessage(text);
            }
        }
    });

    function loadInitialHistory() {
        const saved = sessionStorage.getItem('rm_chat_history');
        if (saved) {
            try {
                const history = JSON.parse(saved);
                history.forEach(item => appendMessage(item.sender, item.text, item.provider, false));
                scrollToBottom();
                return;
            } catch (e) {
                console.error('Failed to parse chat history', e);
            }
        }
        // Default initial greeting
        appendMessage('bot', 'Hello! Welcome to RibinaMart. How can I help you today? Ask about orders, delivery, returns, or selling!', 'Assistant', true);
    }

    function sendMessage(text) {
        appendMessage('user', text, null, true);
        inputEl.value = '';
        inputEl.disabled = true;
        sendBtn.disabled = true;

        showTypingIndicator();

        fetch(contextPath + '/api/chat', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-Requested-With': 'XMLHttpRequest'
            },
            body: JSON.stringify({ message: text })
        })
        .then(response => response.json())
        .then(data => {
            hideTypingIndicator();
            inputEl.disabled = false;
            sendBtn.disabled = false;
            inputEl.focus();

            if (data && data.success && data.data && data.data.reply) {
                appendMessage('bot', data.data.reply, data.data.provider, true);
            } else if (data && data.error) {
                appendMessage('bot', data.error, 'Error', true);
            } else {
                appendMessage('bot', "I'm having trouble connecting right now. Please try again shortly.", 'Offline', true);
            }
        })
        .catch(err => {
            console.error('Chat error:', err);
            hideTypingIndicator();
            inputEl.disabled = false;
            sendBtn.disabled = false;
            appendMessage('bot', "We apologize, our assistant is temporarily offline. Please contact support@ribinamart.com for assistance.", 'System', true);
        });
    }

    function appendMessage(sender, text, provider, save) {
        const msgDiv = document.createElement('div');
        msgDiv.className = 'rm-message ' + sender;
        msgDiv.textContent = text;

        if (sender === 'bot' && provider) {
            const tag = document.createElement('span');
            tag.className = 'provider-tag';
            tag.textContent = 'via ' + provider;
            msgDiv.appendChild(tag);
        }

        messagesEl.appendChild(msgDiv);
        scrollToBottom();

        if (save) {
            saveMessageToStorage(sender, text, provider);
        }
    }

    function saveMessageToStorage(sender, text, provider) {
        let history = [];
        try {
            const raw = sessionStorage.getItem('rm_chat_history');
            if (raw) history = JSON.parse(raw);
        } catch (e) {}

        history.push({ sender, text, provider });
        if (history.length > 30) history = history.slice(history.length - 30);
        sessionStorage.setItem('rm_chat_history', JSON.stringify(history));
    }

    function showTypingIndicator() {
        let indicator = document.getElementById('rmTypingIndicator');
        if (!indicator) {
            indicator = document.createElement('div');
            indicator.id = 'rmTypingIndicator';
            indicator.className = 'rm-typing-indicator';
            indicator.innerHTML = '<i class="bi bi-three-dots me-1"></i> Assistant is typing...';
            messagesEl.appendChild(indicator);
            scrollToBottom();
        }
    }

    function hideTypingIndicator() {
        const indicator = document.getElementById('rmTypingIndicator');
        if (indicator) {
            indicator.remove();
        }
    }

    function scrollToBottom() {
        messagesEl.scrollTop = messagesEl.scrollHeight;
    }
});
