package ir.almasglass.sales.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ir.almasglass.sales.data.AppDatabase
import ir.almasglass.sales.data.AuthResult
import ir.almasglass.sales.data.CartItem
import ir.almasglass.sales.data.Customer
import ir.almasglass.sales.data.Order
import ir.almasglass.sales.data.OrderItem
import ir.almasglass.sales.data.PaymentType
import ir.almasglass.sales.data.PhoneChangeRequest
import ir.almasglass.sales.data.Product
import ir.almasglass.sales.data.RegisterResult
import ir.almasglass.sales.data.SalesRepository
import ir.almasglass.sales.util.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class UiMessage(val text: String, val isError: Boolean = false)

class SalesViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = SalesRepository(AppDatabase.get(application))
    private val session = SessionManager(application)

    // ----- session / auth -----
    private val _currentCustomer = MutableStateFlow<Customer?>(null)
    val currentCustomer: StateFlow<Customer?> = _currentCustomer

    private val _authMessage = MutableStateFlow<UiMessage?>(null)
    val authMessage: StateFlow<UiMessage?> = _authMessage

    private val _lastRegisteredNumber = MutableStateFlow<Int?>(null)
    val lastRegisteredNumber: StateFlow<Int?> = _lastRegisteredNumber

    init {
        viewModelScope.launch {
            session.loggedInCustomerId.collect { id ->
                if (id == null) {
                    _currentCustomer.value = null
                } else {
                    repo.observeCustomer(id).collect { _currentCustomer.value = it }
                }
            }
        }
    }

    fun login(phone: String, password: String, onResult: (AuthResult) -> Unit) {
        viewModelScope.launch {
            val result = repo.login(phone, password)
            if (result is AuthResult.Success) {
                session.setLoggedIn(result.customer.id)
                _currentCustomer.value = result.customer
            }
            onResult(result)
        }
    }

    fun register(
        fullName: String,
        storeName: String,
        phone: String,
        city: String,
        password: String,
        onResult: (RegisterResult) -> Unit
    ) {
        viewModelScope.launch {
            val result = repo.register(fullName, storeName, phone, city, password)
            if (result is RegisterResult.Success) {
                _lastRegisteredNumber.value = result.customer.customerNumber
            }
            onResult(result)
        }
    }

    fun logout() {
        viewModelScope.launch {
            session.clear()
            _currentCustomer.value = null
            selectedCarModel.value = null
        }
    }

    fun changePassword(current: String, new: String, onResult: (Boolean) -> Unit) {
        val customerId = _currentCustomer.value?.id ?: return
        viewModelScope.launch { onResult(repo.changePassword(customerId, current, new)) }
    }

    fun requestPhoneChange(newPhone: String, note: String, onDone: () -> Unit) {
        val customerId = _currentCustomer.value?.id ?: return
        viewModelScope.launch {
            repo.requestPhoneChange(customerId, newPhone, note)
            onDone()
        }
    }

    fun observePhoneRequests(): StateFlow<List<PhoneChangeRequest>> {
        val customerId = _currentCustomer.value?.id ?: return MutableStateFlow(emptyList())
        return repo.observePhoneRequests(customerId).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    // ----- catalog -----
    val selectedCarModel = MutableStateFlow<String?>(null)
    val productSearch = MutableStateFlow("")

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val products: StateFlow<List<Product>> = kotlinx.coroutines.flow.combine(
        selectedCarModel, productSearch
    ) { model, query -> model to query }
        .flatMapLatest { (model, query) ->
            if (model == null) MutableStateFlow(emptyList())
            else repo.observeProducts(model, query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectCarModel(model: String) {
        selectedCarModel.value = model
        productSearch.value = ""
    }

    suspend fun getProduct(id: Long): Product? = repo.getProduct(id)

    // ----- cart -----
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val cart: StateFlow<List<CartItem>> = _currentCustomer.flatMapLatest { customer ->
        if (customer == null) MutableStateFlow(emptyList()) else repo.observeCart(customer.id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartTotalQty: StateFlow<Int> = cart.let { f ->
        kotlinx.coroutines.flow.MutableStateFlow(0).also { out ->
            viewModelScope.launch { f.collect { out.value = it.sumOf { line -> line.quantity } } }
        }
    }
    val cartTotalAmount: StateFlow<Long> = cart.let { f ->
        kotlinx.coroutines.flow.MutableStateFlow(0L).also { out ->
            viewModelScope.launch { f.collect { out.value = it.sumOf { line -> line.unitPrice * line.quantity } } }
        }
    }

    fun addToCart(product: Product, quantity: Int) {
        val customer = _currentCustomer.value ?: return
        viewModelScope.launch { repo.addToCart(customer.id, product, quantity) }
    }

    fun updateCartLine(cartItemId: Long, newQuantity: Int, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repo.updateCartLineQuantity(cartItemId, newQuantity)
            onDone()
        }
    }

    fun removeCartLine(cartItemId: Long, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repo.removeCartLine(cartItemId)
            onDone()
        }
    }

    suspend fun getCartLine(cartItemId: Long): CartItem? = repo.getCartLine(cartItemId)

    // ----- orders -----
    private val _lastOrder = MutableStateFlow<Order?>(null)
    val lastOrder: StateFlow<Order?> = _lastOrder

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val orders: StateFlow<List<Order>> = _currentCustomer.flatMapLatest { customer ->
        if (customer == null) MutableStateFlow(emptyList()) else repo.observeOrders(customer.id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun placeOrder(paymentType: PaymentType, onDone: (Order) -> Unit) {
        val customer = _currentCustomer.value ?: return
        viewModelScope.launch {
            val order = repo.placeOrder(customer, paymentType)
            _lastOrder.value = order
            onDone(order)
        }
    }

    fun observeOrder(orderId: Long): StateFlow<Order?> =
        repo.observeOrder(orderId).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun observeOrderItems(orderId: Long): StateFlow<List<OrderItem>> =
        repo.observeOrderItems(orderId).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun cancelOrder(order: Order, onDone: () -> Unit) {
        viewModelScope.launch {
            repo.cancelOrder(order)
            onDone()
        }
    }

    fun clearAuthMessage() {
        _authMessage.value = null
    }
}
