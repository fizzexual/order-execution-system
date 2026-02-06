// API Base URL
const API_BASE = '/api';

// Initialize app
document.addEventListener('DOMContentLoaded', () => {
    loadAccounts();
    loadOrders();
    loadExecutionLogs();
    setupOrderForm();
    
    // Auto-refresh every 10 seconds
    setInterval(() => {
        loadOrders();
        loadExecutionLogs();
    }, 10000);
});

// Load Accounts
async function loadAccounts() {
    try {
        const response = await fetch(`${API_BASE}/accounts`);
        const accounts = await response.json();
        
        displayAccounts(accounts);
        populateAccountDropdown(accounts);
    } catch (error) {
        console.error('Error loading accounts:', error);
        document.getElementById('accounts-list').innerHTML = 
            '<div class="error">Failed to load accounts</div>';
    }
}

function displayAccounts(accounts) {
    const container = document.getElementById('accounts-list');
    
    if (accounts.length === 0) {
        container.innerHTML = '<p>No accounts found</p>';
        return;
    }
    
    container.innerHTML = accounts.map(account => `
        <div class="account-item">
            <h3>${account.accountNumber}</h3>
            <p>Balance: <span class="balance">$${account.balance.toFixed(2)}</span></p>
            <p>Available: <span class="balance">$${account.availableBalance.toFixed(2)}</span></p>
            <p>Status: <span class="status-badge status-${account.status}">${account.status}</span></p>
        </div>
    `).join('');
}

function populateAccountDropdown(accounts) {
    const select = document.getElementById('accountId');
    select.innerHTML = '<option value="">Select Account</option>' +
        accounts.map(account => 
            `<option value="${account.id}">${account.accountNumber} - $${account.balance.toFixed(2)}</option>`
        ).join('');
}

// Load Orders
async function loadOrders() {
    try {
        const response = await fetch(`${API_BASE}/orders`);
        const orders = await response.json();
        
        displayOrders(orders);
    } catch (error) {
        console.error('Error loading orders:', error);
        document.getElementById('orders-body').innerHTML = 
            '<tr><td colspan="9" class="error">Failed to load orders</td></tr>';
    }
}

function displayOrders(orders) {
    const tbody = document.getElementById('orders-body');
    
    if (orders.length === 0) {
        tbody.innerHTML = '<tr><td colspan="9" style="text-align: center;">No orders yet</td></tr>';
        return;
    }
    
    tbody.innerHTML = orders.slice().reverse().slice(0, 20).map(order => `
        <tr>
            <td>${order.orderNumber}</td>
            <td>${order.accountId}</td>
            <td><strong>${order.symbol}</strong></td>
            <td>${order.type}</td>
            <td class="side-${order.side}">${order.side}</td>
            <td>${order.quantity}</td>
            <td>${order.executedPrice ? '$' + order.executedPrice.toFixed(2) : '-'}</td>
            <td class="status-${order.status}">${order.status}</td>
            <td>${formatDateTime(order.createdAt)}</td>
        </tr>
    `).join('');
}

// Load Execution Logs
async function loadExecutionLogs() {
    try {
        const response = await fetch(`${API_BASE}/execution-logs`);
        const logs = await response.json();
        
        displayExecutionLogs(logs);
    } catch (error) {
        console.error('Error loading execution logs:', error);
        document.getElementById('logs-body').innerHTML = 
            '<tr><td colspan="8" class="error">Failed to load logs</td></tr>';
    }
}

function displayExecutionLogs(logs) {
    const tbody = document.getElementById('logs-body');
    
    if (logs.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8" style="text-align: center;">No execution logs yet</td></tr>';
        return;
    }
    
    tbody.innerHTML = logs.slice().reverse().slice(0, 20).map(log => `
        <tr>
            <td>${log.id}</td>
            <td>${log.orderId}</td>
            <td>${log.quantity}</td>
            <td>$${log.price.toFixed(2)}</td>
            <td><strong>$${log.totalAmount.toFixed(2)}</strong></td>
            <td class="status-${log.status}">${log.status}</td>
            <td>${log.message}</td>
            <td>${formatDateTime(log.executedAt)}</td>
        </tr>
    `).join('');
}

// Setup Order Form
function setupOrderForm() {
    const form = document.getElementById('order-form');
    const typeSelect = document.getElementById('type');
    const limitPriceGroup = document.getElementById('limit-price-group');
    
    // Show/hide limit price based on order type
    typeSelect.addEventListener('change', (e) => {
        if (e.target.value === 'LIMIT') {
            limitPriceGroup.style.display = 'block';
            document.getElementById('limitPrice').required = true;
        } else {
            limitPriceGroup.style.display = 'none';
            document.getElementById('limitPrice').required = false;
        }
    });
    
    // Handle form submission
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        await createOrder();
    });
}

// Create Order
async function createOrder() {
    const messageDiv = document.getElementById('order-message');
    const submitBtn = document.querySelector('.btn-primary');
    
    // Get form values
    const orderData = {
        accountId: parseInt(document.getElementById('accountId').value),
        symbol: document.getElementById('symbol').value.toUpperCase(),
        type: document.getElementById('type').value,
        side: document.getElementById('side').value,
        quantity: parseInt(document.getElementById('quantity').value)
    };
    
    // Add limit price if LIMIT order
    if (orderData.type === 'LIMIT') {
        orderData.limitPrice = parseFloat(document.getElementById('limitPrice').value);
    }
    
    // Disable button
    submitBtn.disabled = true;
    submitBtn.textContent = 'Executing...';
    
    try {
        const response = await fetch(`${API_BASE}/orders`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(orderData)
        });
        
        if (response.ok) {
            const order = await response.json();
            showMessage('success', `Order ${order.orderNumber} executed successfully! Status: ${order.status}`);
            
            // Reset form
            document.getElementById('order-form').reset();
            
            // Reload data
            loadAccounts();
            loadOrders();
            loadExecutionLogs();
        } else {
            const error = await response.json();
            showMessage('error', error.message || 'Failed to create order');
        }
    } catch (error) {
        console.error('Error creating order:', error);
        showMessage('error', 'Network error. Please try again.');
    } finally {
        submitBtn.disabled = false;
        submitBtn.textContent = 'Execute Order';
    }
}

function showMessage(type, text) {
    const messageDiv = document.getElementById('order-message');
    messageDiv.className = `message ${type}`;
    messageDiv.textContent = text;
    
    // Auto-hide after 5 seconds
    setTimeout(() => {
        messageDiv.style.display = 'none';
    }, 5000);
}

// Utility function to format date/time
function formatDateTime(dateString) {
    const date = new Date(dateString);
    return date.toLocaleString('en-US', {
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
    });
}
