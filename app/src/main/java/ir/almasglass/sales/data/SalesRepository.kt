package ir.almasglass.sales.data

import ir.almasglass.sales.util.nowMillis
import ir.almasglass.sales.util.sha256
import kotlinx.coroutines.flow.Flow

sealed class AuthResult {
    data class Success(val customer: Customer) : AuthResult()
    object InvalidCredentials : AuthResult()
    object PendingApproval : AuthResult()
}

sealed class RegisterResult {
    data class Success(val customer: Customer) : RegisterResult()
    object PhoneAlreadyUsed : RegisterResult()
}

class SalesRepository(private val db: AppDatabase) {

    // ---------- Auth ----------

    suspend fun register(
        fullName: String,
        storeName: String,
        phone: String,
        city: String,
        password: String
    ): RegisterResult {
        val existing = db.customerDao().findByPhone(phone)
        if (existing != null) return RegisterResult.PhoneAlreadyUsed
        val nextNumber = db.customerDao().maxCustomerNumber() + 1
        val customer = Customer(
            customerNumber = nextNumber,
            fullName = fullName,
            storeName = storeName,
            phone = phone,
            city = city,
            passwordHash = sha256(password),
            status = CustomerStatus.PENDING,
            createdAt = nowMillis()
        )
        val id = db.customerDao().insert(customer)
        return RegisterResult.Success(customer.copy(id = id))
    }

    suspend fun login(phone: String, password: String): AuthResult {
        val customer = db.customerDao().findByPhone(phone) ?: return AuthResult.InvalidCredentials
        if (customer.passwordHash != sha256(password)) return AuthResult.InvalidCredentials
        if (customer.status != CustomerStatus.APPROVED) return AuthResult.PendingApproval
        return AuthResult.Success(customer)
    }

    fun observeCustomer(id: Long): Flow<Customer?> = db.customerDao().observeById(id)

    suspend fun changePassword(customerId: Long, currentPassword: String, newPassword: String): Boolean {
        val customer = db.customerDao().getById(customerId) ?: return false
        if (customer.passwordHash != sha256(currentPassword)) return false
        db.customerDao().update(customer.copy(passwordHash = sha256(newPassword)))
        return true
    }

    suspend fun requestPhoneChange(customerId: Long, newPhone: String, note: String) {
        val customer = db.customerDao().getById(customerId) ?: return
        db.phoneChangeDao().insert(
            PhoneChangeRequest(
                customerId = customerId,
                oldPhone = customer.phone,
                newPhone = newPhone,
                note = note,
                createdAt = nowMillis()
            )
        )
    }

    fun observePhoneRequests(customerId: Long): Flow<List<PhoneChangeRequest>> =
        db.phoneChangeDao().observeForCustomer(customerId)

    // ---------- Catalog ----------

    fun observeProducts(carModel: String, query: String): Flow<List<Product>> =
        db.productDao().observeByCarModel(carModel, query)

    suspend fun getProduct(id: Long): Product? = db.productDao().getById(id)

    // ---------- Cart ----------

    fun observeCart(customerId: Long): Flow<List<CartItem>> = db.cartDao().observeCart(customerId)

    suspend fun addToCart(customerId: Long, product: Product, quantity: Int) {
        val existing = db.cartDao().findLine(customerId, product.id)
        if (existing != null) {
            db.cartDao().update(existing.copy(quantity = existing.quantity + quantity))
        } else {
            db.cartDao().insert(
                CartItem(
                    customerId = customerId,
                    productId = product.id,
                    productName = product.name,
                    unitPrice = product.unitPrice,
                    quantity = quantity
                )
            )
        }
    }

    suspend fun updateCartLineQuantity(cartItemId: Long, newQuantity: Int) {
        val line = db.cartDao().getById(cartItemId) ?: return
        db.cartDao().update(line.copy(quantity = newQuantity))
    }

    suspend fun removeCartLine(cartItemId: Long) {
        val line = db.cartDao().getById(cartItemId) ?: return
        db.cartDao().delete(line)
    }

    suspend fun getCartLine(cartItemId: Long): CartItem? = db.cartDao().getById(cartItemId)

    // ---------- Orders ----------

    /** Order code is ALM-{شماره مشتری}-{ردیف سفارش مشتری}, per v1.16. */
    suspend fun placeOrder(customer: Customer, paymentType: PaymentType): Order {
        val lines = db.cartDao().getCartOnce(customer.id)
        val totalQty = lines.sumOf { it.quantity }
        val totalAmount = lines.sumOf { it.unitPrice * it.quantity }
        val rowNumber = db.orderDao().countForCustomer(customer.id) + 1
        val order = Order(
            orderCode = "ALM-${customer.customerNumber}-$rowNumber",
            customerId = customer.id,
            totalQty = totalQty,
            totalAmount = totalAmount,
            paymentType = paymentType,
            status = OrderStatus.PENDING_REVIEW,
            createdAt = nowMillis()
        )
        val orderId = db.orderDao().insertOrder(order)
        db.orderDao().insertItems(
            lines.map {
                OrderItem(
                    orderId = orderId,
                    productId = it.productId,
                    productName = it.productName,
                    unitPrice = it.unitPrice,
                    quantity = it.quantity
                )
            }
        )
        db.cartDao().clearCart(customer.id)
        return order.copy(id = orderId)
    }

    fun observeOrders(customerId: Long): Flow<List<Order>> = db.orderDao().observeOrders(customerId)
    fun observeOrder(orderId: Long): Flow<Order?> = db.orderDao().observeOrder(orderId)
    fun observeOrderItems(orderId: Long): Flow<List<OrderItem>> = db.orderDao().observeOrderItems(orderId)

    suspend fun cancelOrder(order: Order) {
        db.orderDao().update(order.copy(status = OrderStatus.CANCELLED))
    }
}
