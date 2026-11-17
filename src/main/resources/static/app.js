let products = [];

let cart = JSON.parse(localStorage.getItem("chouchouCart")) || [];
let favorites = JSON.parse(localStorage.getItem("chouchouFavorites")) || [];
let orders = JSON.parse(localStorage.getItem("chouchouOrders")) || [];
let currentUser = JSON.parse(localStorage.getItem("chouchouUser")) || null;

let activeCategory = "Todos";
let activeUniverse = null;
let favoritesOnly = false;
let couponApplied = false;
let accountMode = "login";
let accountTab = "orders";

const $ = (id) => document.getElementById(id);

function money(value) {
    return value.toLocaleString("pt-BR", {
        style: "currency",
        currency: "BRL"
    });
}

async function carregarProdutos() {

    try {

        const response = await fetch("/api/produtos");

        if (!response.ok) {
            throw new Error("Erro ao buscar produtos.");
        }

        const dados = await response.json();

        products = dados.map(produto => {

            return {
                id: produto.id,
                name: produto.nome,
                category: produto.categoriaNome,
                price: Number(produto.preco),
                description: produto.descricao || "",
                stock: Number(produto.quantidadeEstoque ?? 0),
                active: produto.ativo === true,

                // Elementos visuais do frontend
                emoji: obterEmoji(produto.categoriaNome),
                bg: obterCor(produto.categoriaNome)
            };

        });

        renderCategoryFilters();
        renderProducts();
        renderCart();

    } catch (error) {

        console.error("Erro ao carregar produtos:", error);

        $("productGrid").innerHTML =
            "<p>Não foi possível carregar o cardápio.</p>";

    }
}

function obterEmoji(categoria) {

    const emojis = {
        "Cafés": "☕",
        "Bebidas Geladas": "🥤",
        "Doces": "🍰",
        "Salgados": "🥪",
        "Kits": "🎁"
    };

    return emojis[categoria] || "🍽️";
}


function obterCor(categoria) {

    const cores = {
        "Cafés": "#ead5d1",
        "Bebidas Geladas": "#dce8e8",
        "Doces": "#f2dce0",
        "Salgados": "#e5e6d7",
        "Combos": "#e9d5bc",
        "Kits": "#f2dce0"
    };

    return cores[categoria] || "#ead5d1";
}

function renderCategoryFilters() {

    const categories = [
        ...new Set(products.map(product => product.category))
    ];

    const filters = ["Todos", ...categories];

    $("categoryFilters").innerHTML = filters.map(category => `

        <button
            class="filter-button ${category === activeCategory ? "active" : ""}"
            data-filter="${category}">
            ${category}
        </button>

    `).join("");
}

function saveData() {
    localStorage.setItem("chouchouCart", JSON.stringify(cart));
    localStorage.setItem("chouchouFavorites", JSON.stringify(favorites));
    localStorage.setItem("chouchouOrders", JSON.stringify(orders));
}

function renderProducts() {
    const search = $("searchInput").value.toLowerCase().trim();

    const filtered = products.filter(product => {

        if (!product.active || product.stock <= 0) {
            return false;
        }

        const matchesSearch =
            product.name.toLowerCase().includes(search) ||
            product.description.toLowerCase().includes(search);

        const matchesCategory =
            activeCategory === "Todos" || product.category === activeCategory;

        const matchesUniverse =
            !activeUniverse || product.universe === activeUniverse;

        const matchesFavorites =
            !favoritesOnly || favorites.includes(product.id);

        return matchesSearch && matchesCategory &&
            matchesUniverse && matchesFavorites;
    });

    $("productGrid").innerHTML = filtered.map(product => `
        <article class="product-card">
            <button
                class="favorite-product ${favorites.includes(product.id) ? "is-favorite" : ""}"
                data-favorite="${product.id}"
                aria-label="Adicionar aos favoritos"
                title="Adicionar aos favoritos"
            >${favorites.includes(product.id) ? "♥" : "♡"}</button>

            <div class="product-art" style="--product-bg: ${product.bg}">
                ${product.emoji}
            </div>

            <div class="product-info">
                <span class="product-category">${product.category}</span>
                <h3>${product.name}</h3>
                <p class="product-description">${product.description}</p>
                <div class="product-bottom">
                    <span class="product-price">${money(product.price)}</span>
                    <button class="add-button" data-add="${product.id}" title="Adicionar ao carrinho">+</button>
                </div>
            </div>
        </article>
    `).join("");

    $("emptyMessage").classList.toggle("hidden", filtered.length > 0);
}

function addToCart(id) {
    const product = products.find(product => product.id === id);

    if (!product || !product.active) {
        alert("Este produto não está disponível.");
        return;
    }

    const item = cart.find(item => item.id === id);

    const quantityInCart = item ? item.quantity : 0;

    if (quantityInCart >= product.stock) {
        alert("Você atingiu o limite de estoque deste produto.");
        return;
    }

    if (item) {
        item.quantity++;
    } else {
        cart.push({
            id: product.id,
            quantity: 1
        });
    }

    saveData();
    renderCart();
}

function changeQuantity(id, change) {
    const item = cart.find(product => product.id === id);

    if (!item) return;

    const product = products.find(product => product.id === id);

    if (!product) return;

    const newQuantity = item.quantity + change;

    if (newQuantity <= 0) {
        cart = cart.filter(product => product.id !== id);
    } else if (newQuantity > product.stock) {
        alert("Quantidade maior que o estoque disponível.");
        return;
    } else {
        item.quantity = newQuantity;
    }

    saveData();
    renderCart();
}

function removeFromCart(id) {
    cart = cart.filter(product => product.id !== id);
    saveData();
    renderCart();
}

function getCartValues() {
    const subtotalCentavos = cart.reduce((total, item) => {
        const product = products.find(product => product.id === item.id);

        if (!product) return total;

        return total + Math.round(product.price * 100) * item.quantity;
    }, 0);

    const subtotal = subtotalCentavos / 100;

    const discount = couponApplied
        ? Math.round(subtotalCentavos * 0.10) / 100
        : 0;

    const delivery = 5.00;

    const total = subtotal + (subtotal > 0 ? delivery : 0) - discount;

    return { subtotal, discount, delivery, total };
}

function renderCart() {
    const count = cart.reduce((total, item) => total + item.quantity, 0);
    $("cartCount").textContent = count;

    if (cart.length === 0) {
        $("cartItems").innerHTML = `
            <div class="empty-message">
                <p>Seu carrinho está esperando por você ☕</p>
                <p>Que tal escolher alguma delícia?</p>
            </div>
        `;
    } else {
        $("cartItems").innerHTML = cart.map(item => {
            const product = products.find(product => product.id === item.id);
            if (!product) return "";

            return `
                <div class="cart-item">
                    <div class="cart-item-art">${product.emoji}</div>
                    <div>
                        <h4>${product.name}</h4>
                        <small>Unitário: ${money(product.price)}</small>
                        <small>Subtotal: ${money(product.price * item.quantity)}</small>
                        <div class="quantity-control">
                            <button data-quantity="${product.id}" data-change="-1">−</button>
                            <span>${item.quantity}</span>
                            <button data-quantity="${product.id}" data-change="1">+</button>
                        </div>
                    </div>
                    <button class="remove-item" data-remove="${product.id}" title="Remover">×</button>
                </div>
            `;
        }).join("");
    }

    const values = getCartValues();
    $("cartSubtotal").textContent = money(values.subtotal);
    $("deliveryFee").textContent = money(cart.length ? values.delivery : 0);
    $("cartDiscount").textContent = money(values.discount);
    $("cartTotal").textContent = money(cart.length ? values.total : 0);
}

function openOverlay(id) {
    $(id).classList.remove("hidden");
}

function closeOverlay(id) {
    $(id).classList.add("hidden");
}

function toggleFavorite(id) {
    if (favorites.includes(id)) {
        favorites = favorites.filter(item => item !== id);
    } else {
        favorites.push(id);
    }

    saveData();
    renderProducts();
}

function openAccount() {
    if (currentUser) {
        renderCustomerArea("orders");
        openOverlay("customerOverlay");
    } else {
        accountMode = "login";
        updateAccountForm();
        openOverlay("accountOverlay");
    }
}

function updateAccountForm() {
    const isRegister = accountMode === "register";

    $("accountTitle").textContent = isRegister ? "Criar minha conta" : "Entre na sua conta";
    $("nameField").classList.toggle("hidden", !isRegister);
    $("accountName").required = isRegister;
    $("accountSubmit").textContent = isRegister ? "Cadastrar" : "Entrar";
    $("switchText").textContent = isRegister ? "Já tem uma conta?" : "Ainda não tem conta?";
    $("switchAccountButton").textContent = isRegister ? "Entrar" : "Cadastre-se";
    $("accountMessage").textContent = "";
}

function renderCustomerArea(tab) {
    accountTab = tab;
    $("customerWelcome").textContent = `Olá, ${currentUser.name}!`;

    document.querySelectorAll("[data-account-tab]").forEach(button => {
        button.classList.toggle("active", button.dataset.accountTab === tab);
    });

    if (tab === "orders") {
        const userOrders = orders.filter(order => order.email === currentUser.email);

        $("customerContent").innerHTML = userOrders.length
            ? userOrders.map(order => `
                <div class="order-card">
                    <strong>Pedido #${order.id}</strong>
                    <span>${order.date} • ${order.status}</span>
                    <span>${order.items} item(ns) • ${money(order.total)}</span>
                </div>
            `).join("")
            : "<p class='empty-message'>Você ainda não tem pedidos.</p>";
    } else {
        const favoriteProducts = products.filter(product => favorites.includes(product.id));

        $("customerContent").innerHTML = favoriteProducts.length
            ? favoriteProducts.map(product => `
                <div class="favorite-row">
                    ${product.emoji} <strong>${product.name}</strong> — ${money(product.price)}
                    <button class="add-button" data-add="${product.id}">+</button>
                </div>
            `).join("")
            : "<p class='empty-message'>Você ainda não tem favoritos.</p>";
    }
}

function openCheckout() {
    if (cart.length === 0) {
        alert("Adicione algum produto ao carrinho primeiro.");
        return;
    }

    if (!currentUser) {
        closeOverlay("cartOverlay");
        openAccount();
        $("accountMessage").textContent = "Entre ou cadastre-se para continuar.";
        return;
    }

    const values = getCartValues();

    const itemsHTML = cart.map(item => {
        const product = products.find(product => product.id === item.id);
        return `
            <div class="checkout-line">
                <span>${item.quantity}x ${product.name}</span>
                <strong>${money(product.price * item.quantity)}</strong>
            </div>
        `;
    }).join("");

    $("checkoutDetails").innerHTML = `
        ${itemsHTML}
        <div class="checkout-line"><span>Subtotal</span><strong>${money(values.subtotal)}</strong></div>
        <div class="checkout-line"><span>Entrega estimada</span><strong>${money(values.delivery)}</strong></div>
        <div class="checkout-line"><span>Desconto</span><strong>${money(values.discount)}</strong></div>
        <div class="checkout-line checkout-total"><span>Total</span><strong>${money(values.total)}</strong></div>
    `;

    $("checkoutMessage").textContent = "";
    closeOverlay("cartOverlay");
    openOverlay("checkoutOverlay");
}

async function confirmOrder() {
    if (!currentUser || cart.length === 0) return;

    const rua =
        $("customerStreet").value.trim();

    const numero =
        $("customerNumber").value.trim();

    const complemento =
        $("customerComplement").value.trim();

    const bairro =
        $("customerNeighborhood").value.trim();

    const cidade =
        $("customerCity").value.trim();

    const cep =
        $("customerZipCode").value.trim();


    if (
        !rua ||
        !numero ||
        !bairro ||
        !cidade ||
        !cep
    ) {
        $("checkoutMessage").textContent =
            "Preencha todos os campos obrigatórios.";

        return;
    }

    try {

        const response = await fetch(
            "/api/enderecos",
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    clienteId: currentUser.id,
                    rua,
                    numero,
                    complemento,
                    bairro,
                    cidade,
                    cep,
                    definirComoPadrao: true
                })
            }
        );

        if (!response.ok) {

            $("checkoutMessage").textContent =
                "Erro ao salvar endereço.";

            return;
        }

    } catch (error) {

        $("checkoutMessage").textContent =
            "Erro ao conectar com o servidor.";

        return;
    }

    const values = getCartValues();
    const itemCount = cart.reduce((total, item) => total + item.quantity, 0);

    const order = {
        id: Date.now(),
        email: currentUser.email,
        date: new Date().toLocaleDateString("pt-BR"),
        status: "Recebido (demonstração)",
        items: itemCount,
        total: values.total,
        address:
            `${rua}, ${numero} - ${bairro}, ${cidade} - ${cep}`,
        payment: $("paymentMethod").value
    };

    orders.push(order);
    cart = [];
    couponApplied = false;

    saveData();
    renderCart();

    $("couponInput").value = "";
    $("couponMessage").textContent = "";
    $("checkoutMessage").textContent = "Pedido demonstrativo registrado!";

    setTimeout(() => {
        closeOverlay("checkoutOverlay");
        renderCustomerArea("orders");
        openOverlay("customerOverlay");
    }, 900);
}

/* Eventos dos produtos */
$("productGrid").addEventListener("click", event => {
    const addButton = event.target.closest("[data-add]");
    const favoriteButton = event.target.closest("[data-favorite]");

    if (addButton) addToCart(Number(addButton.dataset.add));
    if (favoriteButton) toggleFavorite(Number(favoriteButton.dataset.favorite));
});

/* Eventos do carrinho */
$("cartItems").addEventListener("click", event => {
    const quantityButton = event.target.closest("[data-quantity]");
    const removeButton = event.target.closest("[data-remove]");

    if (quantityButton) {
        changeQuantity(
            Number(quantityButton.dataset.quantity),
            Number(quantityButton.dataset.change)
        );
    }

    if (removeButton) {
        removeFromCart(Number(removeButton.dataset.remove));
    }
});

/* Filtros */
$("categoryFilters").addEventListener("click", event => {
    const button = event.target.closest("[data-filter]");
    if (!button) return;

    activeCategory = button.dataset.filter;
    activeUniverse = null;
    favoritesOnly = false;

    document.querySelectorAll("#categoryFilters .filter-button").forEach(item => {
        item.classList.toggle("active", item === button);
    });

    renderProducts();
});

document.querySelectorAll(".universe-card").forEach(button => {
    button.addEventListener("click", () => {

        activeUniverse = null;
        activeCategory = "Todos";
        favoritesOnly = false;

        document.querySelectorAll(
            "#categoryFilters .filter-button"
        ).forEach(item => {

            item.classList.toggle(
                "active",
                item.dataset.filter === "Todos"
            );

        });

        $("cardapio").scrollIntoView({
            behavior: "smooth"
        });

        renderProducts();

    });
});

$("searchInput").addEventListener("input", renderProducts);

/* Botões do cabeçalho */
$("cartButton").addEventListener("click", () => {
    renderCart();
    openOverlay("cartOverlay");
});

$("loginButton").addEventListener("click", openAccount);

$("favoritesButton").addEventListener("click", () => {
    favoritesOnly = !favoritesOnly;
    activeCategory = "Todos";
    activeUniverse = null;

    document.querySelectorAll("#categoryFilters .filter-button").forEach(item => {
        item.classList.toggle("active", item.dataset.filter === "Todos");
    });

    $("cardapio").scrollIntoView({ behavior: "smooth" });
    renderProducts();
});

/* Fechar painéis e janelas */
document.querySelectorAll("[data-close]").forEach(button => {
    button.addEventListener("click", () => closeOverlay(button.dataset.close));
});

document.querySelectorAll(".overlay").forEach(overlay => {
    overlay.addEventListener("click", event => {
        if (event.target === overlay) closeOverlay(overlay.id);
    });
});

/* Cupom de teste */
$("applyCouponButton").addEventListener("click", () => {
    const coupon = $("couponInput").value.trim().toUpperCase();

    if (coupon === "CHOUCHOU10" && cart.length > 0) {
        couponApplied = true;
        $("couponMessage").textContent = "Cupom aplicado! Você ganhou 10% de desconto.";
    } else {
        couponApplied = false;
        $("couponMessage").textContent = "Cupom inválido ou carrinho vazio.";
    }

    renderCart();
});

/* Cadastro e login demonstrativos */
$("switchAccountButton").addEventListener("click", () => {
    accountMode = accountMode === "login" ? "register" : "login";
    updateAccountForm();
});

$("accountForm").addEventListener("submit", async event => {

    event.preventDefault();

    const email =
        $("accountEmail").value.trim().toLowerCase();

    const password =
        $("accountPassword").value;

    const name =
        $("accountName").value.trim();

    $("accountMessage").textContent = "";

    // =====================================
    // CADASTRO
    // =====================================

    if (accountMode === "register") {

        try {

            const response = await fetch(
                "/api/clientes",
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({
                        nome: name,
                        email: email,
                        senha: password,
                        celular: ""
                    })
                }
            );

            if (!response.ok) {

                const erro = await response.json();

                $("accountMessage").textContent =
                    erro.mensagem || "Erro ao cadastrar.";

                return;
            }

            const cliente =
                await response.json();

            currentUser = {
                id: cliente.id,
                name: cliente.nome,
                email: cliente.email
            };

            localStorage.setItem(
                "chouchouUser",
                JSON.stringify(currentUser)
            );

            closeOverlay("accountOverlay");

            renderCustomerArea("orders");

            openOverlay("customerOverlay");

        } catch (error) {

            $("accountMessage").textContent =
                "Erro ao conectar com o servidor.";
        }

        return;
    }

    // =====================================
    // LOGIN
    // =====================================

    try {

        const response = await fetch(
            "/api/clientes/login",
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    email,
                    senha: password
                })
            }
        );

        if (!response.ok) {

            const erro = await response.json();

            $("accountMessage").textContent =
                erro.mensagem || "Login inválido.";

            return;
        }

        const cliente =
            await response.json();

        currentUser = {
            id: cliente.id,
            name: cliente.nome,
            email: cliente.email
        };

        localStorage.setItem(
            "chouchouUser",
            JSON.stringify(currentUser)
        );

        closeOverlay("accountOverlay");

        renderCustomerArea("orders");

        openOverlay("customerOverlay");

    } catch (error) {

        $("accountMessage").textContent =
            "Erro ao conectar com o servidor.";
    }
});

/* Área do cliente */
$("customerOverlay").addEventListener("click", event => {
    const button = event.target.closest("[data-account-tab]");
    if (button) renderCustomerArea(button.dataset.accountTab);
});

$("logoutButton").addEventListener("click", () => {
    currentUser = null;
    localStorage.removeItem("chouchouUser");
    closeOverlay("customerOverlay");
});

$("checkoutButton").addEventListener("click", openCheckout);
$("confirmOrderButton").addEventListener("click", confirmOrder);

// Inicialização
carregarProdutos();
renderCart();
