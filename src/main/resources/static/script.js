const API_URL = 'http://localhost:8080/api';

window.addEventListener('load', () => {
    const user = sessionStorage.getItem('user');
    if (!user) {
        window.location.href = 'login.html';
        return;
    }
    
    const userData = JSON.parse(user);
    document.getElementById('userInfo').textContent = `👤 ${userData.fullName}`;
    loadDashboard();
});

function logout() {
    Swal.fire({
        title: 'Logout?',
        text: 'Anda yakin ingin keluar?',
        icon: 'question',
        showCancelButton: true,
        confirmButtonColor: '#ef4444',
        cancelButtonColor: '#6b7280',
        confirmButtonText: 'Ya, Logout',
        cancelButtonText: 'Batal'
    }).then((result) => {
        if (result.isConfirmed) {
            sessionStorage.removeItem('user');
            window.location.href = 'login.html';
        }
    });
}

// Load Users
// Load Users
async function loadUsers() {
    try {
        const response = await fetch(`${API_URL}/users`);
        
        // Handle non-200 responses
        if (!response.ok) {
            throw new Error(`HTTP Error: ${response.status}`);
        }
        
        const users = await response.json();
        
        // Ensure users is an array
        if (!Array.isArray(users)) {
            console.error('Response is not an array:', users);
            showError(document.getElementById('userList'), 'Format data tidak valid dari server');
            return;
        }
        
        const userList = document.getElementById('userList');
        
        if (users.length === 0) {
            userList.innerHTML = '<div class="alert alert-info">Belum ada user. Tambahkan user baru!</div>';
            return;
        }

        userList.innerHTML = `
            <div style="overflow-x: auto;">
                <table>
                    <thead>
                        <tr>
                            <th>Username</th>
                            <th>Nama Lengkap</th>
                            <th>Status</th>
                            <th>Dibuat</th>
                            <th>Aksi</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${users.map(u => `
                            <tr>
                                <td><strong>${u.username}</strong></td>
                                <td>${u.fullName}</td>
                                <td>
                                    <span class="badge ${u.active ? 'badge-ok' : 'badge-low'}">
                                        ${u.active ? '✅ Aktif' : '❌ Tidak Aktif'}
                                    </span>
                                </td>
                                <td>${new Date(u.createdAt).toLocaleDateString('id-ID')}</td>
                                <td class="actions">
                                    <button class="btn btn-warning btn-sm" onclick="editUser(${u.id})">✏️ Edit</button>
                                    <button class="btn btn-danger btn-sm" onclick="deleteUser(${u.id}, '${u.username}')">🗑️ Hapus</button>
                                </td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            </div>
        `;
    } catch (error) {
        console.error('Error loading users:', error);
        showError(document.getElementById('userList'), `Gagal memuat user: ${error.message}`);
    }
}

// Edit User
async function editUser(userId) {
    try {
        const response = await fetch(`${API_URL}/users/${userId}`);
        if (!response.ok) throw new Error('User tidak ditemukan');
        
        const user = await response.json();
        
        document.getElementById('userId').value = user.id;
        document.getElementById('isEditMode').value = 'true';
        document.getElementById('userUsername').value = user.username;
        document.getElementById('userUsername').readOnly = true;
        document.getElementById('userPassword').value = '';
        document.getElementById('userPassword').required = false;
        document.getElementById('userFullName').value = user.fullName;
        document.getElementById('userActive').value = user.active.toString();
        
        document.getElementById('userFormTitle').textContent = 'Edit User';
        document.getElementById('userSubmitBtn').textContent = '💾 Update User';
        document.getElementById('userCancelBtn').style.display = 'inline-block';
        document.getElementById('passwordLabel').textContent = '(opsional untuk edit)';
        
        window.scrollTo({ top: 0, behavior: 'smooth' });
    } catch (error) {
        Swal.fire({
            icon: 'error',
            title: 'Error',
            text: error.message,
            confirmButtonColor: '#ef4444'
        });
    }
}

// Cancel Edit
function cancelEditUser() {
    document.getElementById('userForm').reset();
    document.getElementById('userId').value = '';
    document.getElementById('isEditMode').value = '';
    document.getElementById('userUsername').readOnly = false;
    document.getElementById('userPassword').required = true;
    document.getElementById('userFormTitle').textContent = 'Tambah User Baru';
    document.getElementById('userSubmitBtn').textContent = '➕ Tambah User';
    document.getElementById('userCancelBtn').style.display = 'none';
    document.getElementById('passwordLabel').textContent = '(minimal 6 karakter)';
}

// Delete User
async function deleteUser(userId, username) {
    const result = await Swal.fire({
        title: 'Hapus User?',
        html: `Anda yakin ingin menghapus user <strong>${username}</strong>?<br><br>
               <span style="color: #ef4444;">⚠️ Aksi ini tidak dapat dibatalkan!</span>`,
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#ef4444',
        cancelButtonColor: '#6b7280',
        confirmButtonText: 'Ya, Hapus!',
        cancelButtonText: 'Batal'
    });

    if (!result.isConfirmed) return;

    Swal.fire({
        title: 'Menghapus User...',
        allowOutsideClick: false,
        didOpen: () => {
            Swal.showLoading();
        }
    });

    try {
        const response = await fetch(`${API_URL}/users/${userId}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            await Swal.fire({
                icon: 'success',
                title: 'User Berhasil Dihapus!',
                text: `${username} telah dihapus dari sistem`,
                confirmButtonColor: '#667eea',
                timer: 2000,
                showConfirmButton: false
            });
            
            loadUsers();
        } else {
            const error = await response.json();
            Swal.fire({
                icon: 'error',
                title: 'Gagal Menghapus User',
                text: error.error,
                confirmButtonColor: '#ef4444'
            });
        }
    } catch (error) {
        Swal.fire({
            icon: 'error',
            title: 'Terjadi Kesalahan',
            text: error.message,
            confirmButtonColor: '#ef4444'
        });
    }
}

// User Form Submit
document.getElementById('userForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const isEdit = document.getElementById('isEditMode').value === 'true';
    const userId = document.getElementById('userId').value;
    const password = document.getElementById('userPassword').value;
    
    if (!isEdit && !password) {
        Swal.fire({
            icon: 'warning',
            title: 'Password Wajib Diisi',
            text: 'Silakan masukkan password untuk user baru',
            confirmButtonColor: '#667eea'
        });
        return;
    }
    
    const user = {
        username: document.getElementById('userUsername').value,
        fullName: document.getElementById('userFullName').value,
        active: document.getElementById('userActive').value === 'true'
    };
    
    if (password) {
        user.password = password;
    }

    Swal.fire({
        title: isEdit ? 'Memperbarui User...' : 'Menyimpan User...',
        allowOutsideClick: false,
        didOpen: () => {
            Swal.showLoading();
        }
    });

    try {
        const url = isEdit ? `${API_URL}/users/${userId}` : `${API_URL}/users`;
        const method = isEdit ? 'PUT' : 'POST';
        
        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(user)
        });

        if (response.ok) {
            await Swal.fire({
                icon: 'success',
                title: isEdit ? 'User Berhasil Diperbarui!' : 'User Berhasil Ditambahkan!',
                text: `${user.fullName} telah ${isEdit ? 'diperbarui' : 'ditambahkan ke'} database`,
                confirmButtonColor: '#667eea',
                timer: 2000,
                showConfirmButton: false
            });
            
            cancelEditUser();
            loadUsers();
        } else {
            const error = await response.json();
            Swal.fire({
                icon: 'error',
                title: isEdit ? 'Gagal Memperbarui User' : 'Gagal Menambahkan User',
                text: error.error,
                confirmButtonColor: '#ef4444'
            });
        }
    } catch (error) {
        Swal.fire({
            icon: 'error',
            title: 'Terjadi Kesalahan',
            text: error.message,
            confirmButtonColor: '#ef4444'
        });
    }
});

          // Tab Navigation
        function showTab(tabName) {
            document.querySelectorAll('.tab-content').forEach(content => {
                content.classList.remove('active');
            });
            document.querySelectorAll('.tab').forEach(tab => {
                tab.classList.remove('active');
            });
            document.getElementById(tabName).classList.add('active');
            event.target.classList.add('active');

            // Load data when tab is opened
            if (tabName === 'dashboard') loadDashboard();
            if (tabName === 'pos') loadPOS();
            if (tabName === 'products') loadProducts();
            if (tabName === 'suppliers') loadSuppliers();
            if (tabName === 'sales') loadSales();
            if (tabName === 'inventory') loadInventory();
            if (tabName === 'users') loadUsers();
        }

    // Format currency
    function formatRupiah(amount) {
        return new Intl.NumberFormat('id-ID', {
            style: 'currency',
            currency: 'IDR',
            minimumFractionDigits: 0
        }).format(amount);
    }

    // Show error message
    function showError(element, message) {
        element.innerHTML = `<div class="alert alert-error">❌ ${message}</div>`;
    }

    // Shopping Cart
    let cart = [];
    let allProductsForPOS = [];

    // Auto-generate Product Code
    async function generateProductCode() {
        const category = document.getElementById('productCategory').value;
        if (!category) {
            document.getElementById('productCode').value = '';
            return;
        }

        try {
            // Get all products in this category
            const response = await fetch(`${API_URL}/products/category/${category}`);
            const products = await response.json();

            // Find the highest number
            let maxNumber = 0;
            const prefix = getCategoryPrefix(category);
            
            products.forEach(p => {
                if (p.code.startsWith(prefix)) {
                    const num = parseInt(p.code.substring(prefix.length));
                    if (!isNaN(num) && num > maxNumber) {
                        maxNumber = num;
                    }
                }
            });

            // Generate new code
            const newNumber = (maxNumber + 1).toString().padStart(3, '0');
            document.getElementById('productCode').value = prefix + newNumber;
        } catch (error) {
            console.error('Error generating product code:', error);
        }
    }

    function getCategoryPrefix(category) {
        const prefixes = {
            'FOOD': 'FOOD',
            'ELECTRONIC': 'ELEC',
            'CLOTHING': 'CLOTH'
        };
        return prefixes[category] || category;
    }

    // Auto-generate Supplier Code
    async function generateSupplierCode() {
        try {
            const response = await fetch(`${API_URL}/suppliers`);
            const suppliers = await response.json();

            // Find the highest number
            let maxNumber = 0;
            suppliers.forEach(s => {
                if (s.code.startsWith('SUP')) {
                    const num = parseInt(s.code.substring(3));
                    if (!isNaN(num) && num > maxNumber) {
                        maxNumber = num;
                    }
                }
            });

            // Generate new code
            const newNumber = (maxNumber + 1).toString().padStart(3, '0');
            document.getElementById('supplierCode').value = 'SUP' + newNumber;
        } catch (error) {
            console.error('Error generating supplier code:', error);
        }
    }

    // Load POS
    async function loadPOS() {
        try {
            const products = await fetch(`${API_URL}/products`).then(r => r.json());
            allProductsForPOS = products.filter(p => p.stock > 0);
            displayProductsForPOS(allProductsForPOS);
            
            // Setup search
            document.getElementById('searchProduct').addEventListener('input', (e) => {
                const search = e.target.value.toLowerCase();
                const filtered = allProductsForPOS.filter(p => 
                    p.name.toLowerCase().includes(search) || 
                    p.code.toLowerCase().includes(search)
                );
                displayProductsForPOS(filtered);
            });
        } catch (error) {
            console.error('Error loading POS:', error);
            showError(document.getElementById('productListPOS'), 'Gagal memuat produk');
        }
    }

    function displayProductsForPOS(products) {
        const productListPOS = document.getElementById('productListPOS');
        
        if (products.length === 0) {
            productListPOS.innerHTML = '<div class="alert alert-info">Tidak ada produk tersedia.</div>';
            return;
        }

        productListPOS.innerHTML = products.map(p => `
            <div style="padding: 15px; border: 2px solid #e0e0e0; border-radius: 8px; margin-bottom: 10px; cursor: pointer; transition: all 0.3s;" 
                    onclick="addToCart(${p.id})"
                    onmouseover="this.style.borderColor='#667eea'; this.style.background='#f9fafb'"
                    onmouseout="this.style.borderColor='#e0e0e0'; this.style.background='white'">
                <div style="display: flex; justify-content: space-between; align-items: center;">
                    <div>
                        <strong style="font-size: 16px;">${p.name}</strong>
                        <div style="color: #666; font-size: 12px; margin-top: 5px;">
                            ${p.code} | <span class="badge badge-${p.category.toLowerCase()}">${p.category}</span>
                        </div>
                    </div>
                    <div style="text-align: right;">
                        <div style="font-size: 18px; font-weight: bold; color: #667eea;">${formatRupiah(p.price)}</div>
                        <div style="font-size: 12px; color: ${p.stock <= p.minimumStock ? '#ef4444' : '#10b981'}">
                            Stok: ${p.stock}
                        </div>
                    </div>
                </div>
            </div>
        `).join('');
    }

    function addToCart(productId) {
        const product = allProductsForPOS.find(p => p.id === productId);
        if (!product) return;

        const existingItem = cart.find(item => item.productId === productId);
        
        if (existingItem) {
            if (existingItem.quantity >= product.stock) {
                Swal.fire({
                    icon: 'error',
                    title: 'Stok Tidak Mencukupi',
                    text: `Stok ${product.name} hanya tersisa ${product.stock}`,
                    confirmButtonColor: '#667eea'
                });
                return;
            }
            existingItem.quantity++;
        } else {
            cart.push({
                productId: product.id,
                productName: product.name,
                productCategory: product.category,
                unitPrice: product.price,
                discountPercentage: product.discountPercentage,
                quantity: 1,
                maxStock: product.stock
            });
        }
        
        updateCartDisplay();
    }

    function updateCartDisplay() {
        const cartDiv = document.getElementById('cart');
        
        if (cart.length === 0) {
            cartDiv.innerHTML = '<div class="alert alert-info">Keranjang masih kosong. Pilih produk untuk memulai transaksi.</div>';
            document.getElementById('cartSubtotal').textContent = formatRupiah(0);
            document.getElementById('cartDiscount').textContent = formatRupiah(0);
            document.getElementById('cartTotal').textContent = formatRupiah(0);
            return;
        }

        let subtotal = 0;
        let totalDiscount = 0;

        cartDiv.innerHTML = `
            <table style="width: 100%; margin-bottom: 10px;">
                <thead>
                    <tr>
                        <th style="text-align: left; padding: 10px; background: #f9fafb;">Produk</th>
                        <th style="text-align: center; padding: 10px; background: #f9fafb;">Qty</th>
                        <th style="text-align: right; padding: 10px; background: #f9fafb;">Harga</th>
                        <th style="text-align: right; padding: 10px; background: #f9fafb;">Total</th>
                        <th style="text-align: center; padding: 10px; background: #f9fafb;"></th>
                    </tr>
                </thead>
                <tbody>
                    ${cart.map((item, index) => {
                        const itemSubtotal = item.unitPrice * item.quantity;
                        const itemDiscount = itemSubtotal * item.discountPercentage / 100;
                        const itemTotal = itemSubtotal - itemDiscount;
                        
                        subtotal += itemSubtotal;
                        totalDiscount += itemDiscount;
                        
                        return `
                            <tr>
                                <td style="padding: 10px;">
                                    <strong>${item.productName}</strong>
                                    ${item.discountPercentage > 0 ? `<br><small style="color: #ef4444;">Diskon ${item.discountPercentage}%</small>` : ''}
                                </td>
                                <td style="padding: 10px; text-align: center;">
                                    <div style="display: flex; align-items: center; justify-content: center; gap: 5px;">
                                        <button onclick="updateQuantity(${index}, -1)" class="btn btn-sm" style="padding: 5px 10px;">-</button>
                                        <input type="number" value="${item.quantity}" min="1" max="${item.maxStock}" 
                                                onchange="setQuantity(${index}, this.value)"
                                                style="width: 50px; text-align: center; padding: 5px;">
                                        <button onclick="updateQuantity(${index}, 1)" class="btn btn-sm" style="padding: 5px 10px;">+</button>
                                    </div>
                                </td>
                                <td style="padding: 10px; text-align: right;">${formatRupiah(item.unitPrice)}</td>
                                <td style="padding: 10px; text-align: right;"><strong>${formatRupiah(itemTotal)}</strong></td>
                                <td style="padding: 10px; text-align: center;">
                                    <button onclick="removeFromCart(${index})" class="btn btn-danger btn-sm">🗑️</button>
                                </td>
                            </tr>
                        `;
                    }).join('')}
                </tbody>
            </table>
        `;

        const total = subtotal - totalDiscount;
        document.getElementById('cartSubtotal').textContent = formatRupiah(subtotal);
        document.getElementById('cartDiscount').textContent = formatRupiah(totalDiscount);
        document.getElementById('cartTotal').textContent = formatRupiah(total);
    }

    function updateQuantity(index, change) {
        const item = cart[index];
        const newQty = item.quantity + change;
        
        if (newQty < 1) {
            removeFromCart(index);
            return;
        }
        
        if (newQty > item.maxStock) {
            alert('❌ Stok tidak mencukupi!');
            return;
        }
        
        item.quantity = newQty;
        updateCartDisplay();
    }

    function setQuantity(index, value) {
        const item = cart[index];
        const newQty = parseInt(value) || 1;
        
        if (newQty < 1) {
            removeFromCart(index);
            return;
        }
        
        if (newQty > item.maxStock) {
            Swal.fire({
                icon: 'warning',
                title: 'Stok Tidak Mencukupi',
                text: `Maksimal stok: ${item.maxStock}`,
                confirmButtonColor: '#667eea'
            });
            item.quantity = item.maxStock;
        } else {
            item.quantity = newQty;
        }
        
        updateCartDisplay();
    }

    function removeFromCart(index) {
        cart.splice(index, 1);
        updateCartDisplay();
    }

    function clearCart() {
        if (cart.length === 0) return;
        
        Swal.fire({
            title: 'Kosongkan Keranjang?',
            text: 'Semua item akan dihapus dari keranjang',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#ef4444',
            cancelButtonColor: '#6b7280',
            confirmButtonText: 'Ya, Kosongkan',
            cancelButtonText: 'Batal'
        }).then((result) => {
            if (result.isConfirmed) {
                cart = [];
                updateCartDisplay();
                Swal.fire({
                    icon: 'success',
                    title: 'Keranjang Dikosongkan',
                    showConfirmButton: false,
                    timer: 1500
                });
            }
        });
    }

    async function processPayment() {
        if (cart.length === 0) {
            Swal.fire({
                icon: 'warning',
                title: 'Keranjang Kosong',
                text: 'Silakan pilih produk terlebih dahulu',
                confirmButtonColor: '#667eea'
            });
            return;
        }

        const total = cart.reduce((sum, item) => {
            const itemSubtotal = item.unitPrice * item.quantity;
            const itemDiscount = itemSubtotal * item.discountPercentage / 100;
            return sum + (itemSubtotal - itemDiscount);
        }, 0);

        const result = await Swal.fire({
            title: 'Konfirmasi Pembayaran',
            html: `
                <div style="text-align: left; padding: 20px;">
                    <p><strong>Total Item:</strong> ${cart.length}</p>
                    <p><strong>Total Pembayaran:</strong></p>
                    <p style="font-size: 24px; color: #667eea; font-weight: bold;">${formatRupiah(total)}</p>
                </div>
            `,
            icon: 'question',
            showCancelButton: true,
            confirmButtonColor: '#10b981',
            cancelButtonColor: '#6b7280',
            confirmButtonText: '💳 Proses Pembayaran',
            cancelButtonText: 'Batal'
        });

        if (!result.isConfirmed) return;

        // Show loading
        Swal.fire({
            title: 'Memproses Transaksi...',
            allowOutsideClick: false,
            didOpen: () => {
                Swal.showLoading();
            }
        });

        try {
            const sale = {
                items: cart.map(item => ({
                    productId: item.productId,
                    quantity: item.quantity
                }))
            };

            const response = await fetch(`${API_URL}/sales`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(sale)
            });

            if (response.ok) {
                const result = await response.json();
                
                await Swal.fire({
                    icon: 'success',
                    title: 'Transaksi Berhasil!',
                    html: `
                        <div style="text-align: left; padding: 20px;">
                            <p><strong>Kode Transaksi:</strong></p>
                            <p style="font-size: 20px; color: #667eea; font-weight: bold;">${result.transactionCode}</p>
                            <hr style="margin: 15px 0;">
                            <p><strong>Total Pembayaran:</strong></p>
                            <p style="font-size: 24px; color: #10b981; font-weight: bold;">${formatRupiah(result.finalAmount)}</p>
                        </div>
                    `,
                    confirmButtonColor: '#667eea',
                    confirmButtonText: 'OK'
                });
                
                // Reset cart and reload
                cart = [];
                updateCartDisplay();
                loadPOS();
                loadDashboard();
            } else {
                const error = await response.json();
                Swal.fire({
                    icon: 'error',
                    title: 'Transaksi Gagal',
                    text: error.error,
                    confirmButtonColor: '#ef4444'
                });
            }
        } catch (error) {
            Swal.fire({
                icon: 'error',
                title: 'Terjadi Kesalahan',
                text: error.message,
                confirmButtonColor: '#ef4444'
            });
        }
    }

    // Load Dashboard
    async function loadDashboard() {
        try {
            const [products, suppliers, sales, inventory] = await Promise.all([
                fetch(`${API_URL}/products/count`).then(r => r.json()).catch(() => ({totalProducts: 0})),
                fetch(`${API_URL}/suppliers/count`).then(r => r.json()).catch(() => ({totalSuppliers: 0})),
                fetch(`${API_URL}/sales/total-amount`).then(r => r.json()).catch(() => ({totalAmount: 0})),
                fetch(`${API_URL}/inventory/total-value`).then(r => r.json()).catch(() => ({totalValue: 0}))
            ]);

            document.getElementById('totalProducts').textContent = products.totalProducts || 0;
            document.getElementById('totalSuppliers').textContent = suppliers.totalSuppliers || 0;
            document.getElementById('totalSales').textContent = formatRupiah(sales.totalAmount || 0);
            document.getElementById('totalInventory').textContent = formatRupiah(inventory.totalValue || 0);

            // Load low stock alerts
            const lowStock = await fetch(`${API_URL}/products/low-stock`).then(r => r.json());
            const alertDiv = document.getElementById('lowStockAlert');
            
            if (lowStock.length === 0) {
                alertDiv.innerHTML = '<div class="alert alert-success">✅ Semua produk memiliki stok yang cukup!</div>';
            } else {
                alertDiv.innerHTML = lowStock.map(p => `
                    <div class="alert alert-error">
                        ⚠️ <strong>${p.name}</strong> (${p.code}) - Stok: ${p.stock} (Min: ${p.minimumStock})
                    </div>
                `).join('');
            }
        } catch (error) {
            console.error('Error loading dashboard:', error);
            showError(document.getElementById('lowStockAlert'), 'Gagal memuat data. Pastikan backend sudah berjalan di port 8080');
        }
    }

    // Load Products
    async function loadProducts() {
        try {
            // Load suppliers for dropdown
            const suppliers = await fetch(`${API_URL}/suppliers`).then(r => r.json());
            const supplierSelect = document.getElementById('productSupplier');
            supplierSelect.innerHTML = '<option value="">Pilih Supplier</option>' + 
                suppliers.map(s => `<option value="${s.id}">${s.name}</option>`).join('');

            // Generate product code automatically
            generateProductCode();

            // Load products
            const products = await fetch(`${API_URL}/products`).then(r => r.json());
            const productList = document.getElementById('productList');
            
            if (products.length === 0) {
                productList.innerHTML = '<div class="alert alert-info">Belum ada produk. Tambahkan produk baru!</div>';
                return;
            }

            productList.innerHTML = `
                <div style="overflow-x: auto;">
                    <table>
                        <thead>
                            <tr>
                                <th>Kode</th>
                                <th>Nama</th>
                                <th>Kategori</th>
                                <th>Harga</th>
                                <th>Stok</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${products.map(p => `
                                <tr>
                                    <td>${p.code}</td>
                                    <td>${p.name}</td>
                                    <td><span class="badge badge-${p.category.toLowerCase()}">${p.category}</span></td>
                                    <td>${formatRupiah(p.price)}</td>
                                    <td>${p.stock}</td>
                                    <td>
                                        <span class="badge ${p.stock <= p.minimumStock ? 'badge-low' : 'badge-ok'}">
                                            ${p.stock <= p.minimumStock ? 'Stok Rendah' : 'OK'}
                                        </span>
                                    </td>
                                </tr>
                            `).join('')}
                        </tbody>
                    </table>
                </div>
            `;
        } catch (error) {
            console.error('Error loading products:', error);
            showError(document.getElementById('productList'), 'Gagal memuat produk');
        }
    }

    // Load Suppliers
    async function loadSuppliers() {
        try {
            // Generate supplier code automatically
            generateSupplierCode();

            const suppliers = await fetch(`${API_URL}/suppliers`).then(r => r.json());
            const supplierList = document.getElementById('supplierList');
            
            if (suppliers.length === 0) {
                supplierList.innerHTML = '<div class="alert alert-info">Belum ada supplier. Tambahkan supplier baru!</div>';
                return;
            }

            supplierList.innerHTML = `
                <div style="overflow-x: auto;">
                    <table>
                        <thead>
                            <tr>
                                <th>Kode</th>
                                <th>Nama</th>
                                <th>Contact</th>
                                <th>Telepon</th>
                                <th>Email</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${suppliers.map(s => `
                                <tr>
                                    <td>${s.code}</td>
                                    <td>${s.name}</td>
                                    <td>${s.contactPerson}</td>
                                    <td>${s.phone}</td>
                                    <td>${s.email}</td>
                                </tr>
                            `).join('')}
                        </tbody>
                    </table>
                </div>
            `;
        } catch (error) {
            console.error('Error loading suppliers:', error);
            showError(document.getElementById('supplierList'), 'Gagal memuat supplier');
        }
    }

    // Load Sales
    async function loadSales() {
        try {
            const sales = await fetch(`${API_URL}/sales`).then(r => r.json());
            const salesList = document.getElementById('salesList');
            
            if (sales.length === 0) {
                salesList.innerHTML = '<div class="alert alert-info">Belum ada transaksi penjualan.</div>';
                return;
            }

            salesList.innerHTML = `
                <div style="overflow-x: auto;">
                    <table>
                        <thead>
                            <tr>
                                <th>Kode Transaksi</th>
                                <th>Tanggal</th>
                                <th>Total Item</th>
                                <th>Total</th>
                                <th>Diskon</th>
                                <th>Final</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${sales.map(s => `
                                <tr>
                                    <td>${s.transactionCode}</td>
                                    <td>${new Date(s.saleDate).toLocaleString('id-ID')}</td>
                                    <td>${s.items.length} item</td>
                                    <td>${formatRupiah(s.totalAmount)}</td>
                                    <td>${formatRupiah(s.totalDiscount)}</td>
                                    <td><strong>${formatRupiah(s.finalAmount)}</strong></td>
                                </tr>
                            `).join('')}
                        </tbody>
                    </table>
                </div>
            `;
        } catch (error) {
            console.error('Error loading sales:', error);
            showError(document.getElementById('salesList'), 'Gagal memuat penjualan');
        }
    }

    // Load Inventory
    async function loadInventory() {
        try {
            const report = await fetch(`${API_URL}/inventory/report`).then(r => r.json());
            const inventoryReport = document.getElementById('inventoryReport');
            
            inventoryReport.innerHTML = `
                <div class="stats">
                    <div class="stat-card">
                        <h3>Total Produk</h3>
                        <div class="value">${report.totalProducts}</div>
                    </div>
                    <div class="stat-card">
                        <h3>Nilai Total</h3>
                        <div class="value">${formatRupiah(report.totalInventoryValue)}</div>
                    </div>
                    <div class="stat-card">
                        <h3>Produk Stok Rendah</h3>
                        <div class="value">${report.lowStockProducts.length}</div>
                    </div>
                </div>

                <h3 style="margin: 20px 0;">Top 10 Produk Bernilai Tinggi</h3>
                <div style="overflow-x: auto;">
                    <table>
                        <thead>
                            <tr>
                                <th>Nama</th>
                                <th>Kategori</th>
                                <th>Stok</th>
                                <th>Harga</th>
                                <th>Total Nilai</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${report.topValueProducts.map(p => `
                                <tr>
                                    <td>${p.name}</td>
                                    <td><span class="badge badge-${p.category.toLowerCase()}">${p.category}</span></td>
                                    <td>${p.stock}</td>
                                    <td>${formatRupiah(p.price)}</td>
                                    <td><strong>${formatRupiah(p.price * p.stock)}</strong></td>
                                </tr>
                            `).join('')}
                        </tbody>
                    </table>
                </div>
            `;
        } catch (error) {
            console.error('Error loading inventory:', error);
            showError(document.getElementById('inventoryReport'), 'Gagal memuat laporan inventori');
        }
    }

    // Add Product Form
    document.getElementById('productForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const product = {
            name: document.getElementById('productName').value,
            code: document.getElementById('productCode').value,
            category: document.getElementById('productCategory').value,
            price: parseFloat(document.getElementById('productPrice').value),
            stock: parseInt(document.getElementById('productStock').value),
            minimumStock: parseInt(document.getElementById('productMinStock').value),
            supplierId: parseInt(document.getElementById('productSupplier').value),
            description: document.getElementById('productDesc').value
        };

        // Show loading
        Swal.fire({
            title: 'Menyimpan Produk...',
            allowOutsideClick: false,
            didOpen: () => {
                Swal.showLoading();
            }
        });

        try {
            const response = await fetch(`${API_URL}/products`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(product)
            });

            if (response.ok) {
                await Swal.fire({
                    icon: 'success',
                    title: 'Produk Berhasil Ditambahkan!',
                    text: `${product.name} telah ditambahkan ke database`,
                    confirmButtonColor: '#667eea',
                    timer: 2000,
                    showConfirmButton: false
                });
                
                e.target.reset();
                loadProducts();
                loadDashboard();
                // Reset category dropdown to trigger new code generation
                document.getElementById('productCategory').value = '';
                document.getElementById('productCode').value = '';
            } else {
                const error = await response.json();
                Swal.fire({
                    icon: 'error',
                    title: 'Gagal Menambahkan Produk',
                    text: error.error,
                    confirmButtonColor: '#ef4444'
                });
            }
        } catch (error) {
            Swal.fire({
                icon: 'error',
                title: 'Terjadi Kesalahan',
                text: error.message,
                confirmButtonColor: '#ef4444'
            });
        }
    });

    // Add Supplier Form
    document.getElementById('supplierForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const supplier = {
            name: document.getElementById('supplierName').value,
            code: document.getElementById('supplierCode').value,
            contactPerson: document.getElementById('supplierContact').value,
            phone: document.getElementById('supplierPhone').value,
            email: document.getElementById('supplierEmail').value,
            address: document.getElementById('supplierAddress').value
        };

        // Show loading
        Swal.fire({
            title: 'Menyimpan Supplier...',
            allowOutsideClick: false,
            didOpen: () => {
                Swal.showLoading();
            }
        });

        try {
            const response = await fetch(`${API_URL}/suppliers`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(supplier)
            });

            if (response.ok) {
                await Swal.fire({
                    icon: 'success',
                    title: 'Supplier Berhasil Ditambahkan!',
                    text: `${supplier.name} telah ditambahkan ke database`,
                    confirmButtonColor: '#667eea',
                    timer: 2000,
                    showConfirmButton: false
                });
                
                e.target.reset();
                loadSuppliers();
                loadDashboard();
                // Generate new supplier code
                generateSupplierCode();
            } else {
                const error = await response.json();
                Swal.fire({
                    icon: 'error',
                    title: 'Gagal Menambahkan Supplier',
                    text: error.error,
                    confirmButtonColor: '#ef4444'
                });
            }
        } catch (error) {
            Swal.fire({
                icon: 'error',
                title: 'Terjadi Kesalahan',
                text: error.message,
                confirmButtonColor: '#ef4444'
            });
        }
    });

    // Load dashboard on page load
    window.addEventListener('load', () => {
        loadDashboard();
    });