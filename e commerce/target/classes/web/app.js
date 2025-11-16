const API_BASE = 'http://localhost:8080/api';

let products = [];
let cart = { items: [], subtotal: 0, discount: 0, total: 0 };

// Initialize the application
async function init() {
    await loadProducts();
    await loadCart();
    setupDiscountTypeChange();
    setInterval(loadCart, 2000); // Poll for updates every 2 seconds (Observer pattern simulation)
}

// Load products from API
async function loadProducts() {
    try {
        const response = await fetch(`${API_BASE}/products`);
        const data = await response.json();
        products = data.products;
        displayProducts();
        populateUpdateSelect();
    } catch (error) {
        console.error('Error loading products:', error);
    }
}

// Display products in the grid
function displayProducts() {
    const grid = document.getElementById('products-grid');
    grid.innerHTML = '';
    
    products.forEach(product => {
        const card = document.createElement('div');
        card.className = 'product-card';
        
        const configs = Object.entries(product.configurations || {})
            .map(([key, value]) => `${key}: ${value}`)
            .join(', ');
        
        card.innerHTML = `
            <h3>${product.name}</h3>
            <div class="product-price">$${product.basePrice.toFixed(2)}</div>
            <div class="product-stock">Stock: ${product.stock}</div>
            ${configs ? `<div class="product-config">${configs}</div>` : ''}
            <button class="add-to-cart-btn" 
                    onclick="addToCart('${product.id}')"
                    ${product.stock === 0 ? 'disabled' : ''}>
                ${product.stock === 0 ? 'Out of Stock' : 'Add to Cart'}
            </button>
        `;
        
        grid.appendChild(card);
    });
}

// Add product to cart
async function addToCart(productId) {
    try {
        const response = await fetch(`${API_BASE}/cart`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ productId, quantity: 1 })
        });
        
        if (response.ok) {
            showNotification('Product added to cart!');
            await loadCart();
            await loadProducts(); // Refresh to update stock
        }
    } catch (error) {
        console.error('Error adding to cart:', error);
        showNotification('Error adding product to cart', 'error');
    }
}

// Load cart from API
async function loadCart() {
    try {
        const response = await fetch(`${API_BASE}/cart`);
        const data = await response.json();
        cart = data;
        displayCart();
    } catch (error) {
        console.error('Error loading cart:', error);
    }
}

// Display cart items
function displayCart() {
    const cartItemsDiv = document.getElementById('cart-items');
    
    if (cart.items.length === 0) {
        cartItemsDiv.innerHTML = '<div class="empty-cart">Your cart is empty</div>';
    } else {
        cartItemsDiv.innerHTML = cart.items.map(item => `
            <div class="cart-item">
                <div class="cart-item-info">
                    <div class="cart-item-name">${item.productName}</div>
                    <div class="cart-item-details">
                        $${item.price.toFixed(2)} × ${item.quantity} = $${item.subtotal.toFixed(2)}
                    </div>
                </div>
                <div class="cart-item-controls">
                    <div class="quantity-control">
                        <button class="quantity-btn" onclick="updateQuantity('${item.productId}', ${item.quantity - 1})">-</button>
                        <input type="number" class="quantity-input" value="${item.quantity}" 
                               onchange="updateQuantity('${item.productId}', parseInt(this.value))" min="1">
                        <button class="quantity-btn" onclick="updateQuantity('${item.productId}', ${item.quantity + 1})">+</button>
                    </div>
                    <button class="remove-btn" onclick="removeFromCart('${item.productId}')">Remove</button>
                </div>
            </div>
        `).join('');
    }
    
    document.getElementById('subtotal').textContent = `$${cart.subtotal.toFixed(2)}`;
    document.getElementById('discount').textContent = `$${cart.discount.toFixed(2)}`;
    document.getElementById('total').textContent = `$${cart.total.toFixed(2)}`;
}

// Update quantity
async function updateQuantity(productId, quantity) {
    if (quantity < 1) {
        removeFromCart(productId);
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/cart`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ productId, quantity })
        });
        
        if (response.ok) {
            await loadCart();
        }
    } catch (error) {
        console.error('Error updating quantity:', error);
    }
}

// Remove from cart
async function removeFromCart(productId) {
    try {
        const response = await fetch(`${API_BASE}/cart?productId=${productId}`, {
            method: 'DELETE'
        });
        
        if (response.ok) {
            showNotification('Product removed from cart');
            await loadCart();
        }
    } catch (error) {
        console.error('Error removing from cart:', error);
    }
}

// Apply discount (Strategy pattern)
async function applyDiscount() {
    const type = document.getElementById('discount-type').value;
    const value = parseFloat(document.getElementById('discount-value').value);
    const threshold = parseInt(document.getElementById('discount-threshold').value) || 0;
    
    if (!value || (type === 'bulk' && !threshold)) {
        showNotification('Please enter valid discount values', 'error');
        return;
    }
    
    try {
        const requestBody = { type, value };
        if (type === 'bulk') {
            requestBody.threshold = threshold;
        }
        
        const response = await fetch(`${API_BASE}/discount`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(requestBody)
        });
        
        const data = await response.json();
        if (response.ok) {
            showNotification(`Discount applied: ${data.description}`);
            await loadCart();
        } else {
            showNotification(data.error || 'Error applying discount', 'error');
        }
    } catch (error) {
        console.error('Error applying discount:', error);
        showNotification('Error applying discount', 'error');
    }
}

// Setup discount type change handler
function setupDiscountTypeChange() {
    document.getElementById('discount-type').addEventListener('change', function() {
        const thresholdInput = document.getElementById('discount-threshold');
        if (this.value === 'bulk') {
            thresholdInput.style.display = 'block';
        } else {
            thresholdInput.style.display = 'none';
        }
    });
}

// Update product (Observer pattern - triggers real-time updates)
async function updateProduct() {
    const productId = document.getElementById('update-product').value;
    const type = document.getElementById('update-type').value;
    const value = parseFloat(document.getElementById('update-value').value);
    
    if (!productId || !value) {
        showNotification('Please select product and enter value', 'error');
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/update`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ productId, type, value })
        });
        
        if (response.ok) {
            showNotification(`Product ${type} updated! (Observer pattern will notify cart)`);
            await loadProducts();
            await loadCart(); // Cart will be updated by Observer pattern
        }
    } catch (error) {
        console.error('Error updating product:', error);
        showNotification('Error updating product', 'error');
    }
}

// Populate update product select
function populateUpdateSelect() {
    const select = document.getElementById('update-product');
    select.innerHTML = '<option value="">Select Product</option>';
    products.forEach(product => {
        const option = document.createElement('option');
        option.value = product.id;
        option.textContent = `${product.name} ($${product.basePrice.toFixed(2)})`;
        select.appendChild(option);
    });
}

// Checkout function
async function checkout() {
    const email = document.getElementById('customer-email').value;
    const paymentMethod = document.getElementById('checkout-payment-method').value;
    const paymentDetails = document.getElementById('payment-details').value;
    
    if (!email || !email.includes('@')) {
        showNotification('Please enter a valid email address', 'error');
        return;
    }
    
    if (cart.items.length === 0) {
        showNotification('Your cart is empty', 'error');
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/checkout`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                email: email,
                paymentMethod: paymentMethod,
                paymentDetails: paymentDetails
            })
        });
        
        const data = await response.json();
        const resultDiv = document.getElementById('checkout-result');
        
        if (response.ok && data.success) {
            resultDiv.innerHTML = `
                <div class="checkout-success">
                    <h4>Order Confirmed!</h4>
                    <p><strong>Order ID:</strong> ${data.orderId}</p>
                    <p>${data.message}</p>
                    <p style="color: #666; font-size: 0.9em;">A confirmation email has been sent to ${email}</p>
                </div>
            `;
            showNotification('Order confirmed! Check your email.');
            await loadCart(); // Cart should be empty now
        } else {
            resultDiv.innerHTML = `
                <div class="checkout-error">
                    <p>${data.error || 'Checkout failed. Please try again.'}</p>
                </div>
            `;
            showNotification(data.error || 'Checkout failed', 'error');
        }
    } catch (error) {
        console.error('Error during checkout:', error);
        showNotification('Error during checkout', 'error');
    }
}

// Show notification
function showNotification(message, type = 'success') {
    const notification = document.createElement('div');
    notification.className = 'notification';
    notification.style.background = type === 'error' ? '#f44336' : '#4caf50';
    notification.textContent = message;
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.style.animation = 'slideIn 0.3s ease reverse';
        setTimeout(() => notification.remove(), 300);
    }, 3000);
}

// Initialize when page loads
window.addEventListener('DOMContentLoaded', init);

