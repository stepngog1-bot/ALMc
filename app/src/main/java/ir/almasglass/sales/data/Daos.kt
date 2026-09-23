package ir.almasglass.sales.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(customer: Customer): Long

    @Update
    suspend fun update(customer: Customer)

    @Query("SELECT * FROM customers WHERE phone = :phone LIMIT 1")
    suspend fun findByPhone(phone: String): Customer?

    @Query("SELECT * FROM customers WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<Customer?>

    @Query("SELECT * FROM customers WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): Customer?

    @Query("SELECT COALESCE(MAX(customerNumber), 1000) FROM customers")
    suspend fun maxCustomerNumber(): Int
}

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(products: List<Product>)

    @Query("SELECT COUNT(*) FROM products")
    suspend fun count(): Int

    @Query("SELECT * FROM products WHERE carModel = :carModel AND name LIKE '%' || :query || '%' ORDER BY name")
    fun observeByCarModel(carModel: String, query: String): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): Product?
}

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items WHERE customerId = :customerId ORDER BY id")
    fun observeCart(customerId: Long): Flow<List<CartItem>>

    @Query("SELECT * FROM cart_items WHERE customerId = :customerId ORDER BY id")
    suspend fun getCartOnce(customerId: Long): List<CartItem>

    @Query("SELECT * FROM cart_items WHERE customerId = :customerId AND productId = :productId LIMIT 1")
    suspend fun findLine(customerId: Long, productId: Long): CartItem?

    @Insert
    suspend fun insert(item: CartItem): Long

    @Update
    suspend fun update(item: CartItem)

    @Query("SELECT * FROM cart_items WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): CartItem?

    @Delete
    suspend fun delete(item: CartItem)

    @Query("DELETE FROM cart_items WHERE customerId = :customerId")
    suspend fun clearCart(customerId: Long)
}

@Dao
interface OrderDao {
    @Insert
    suspend fun insertOrder(order: Order): Long

    @Insert
    suspend fun insertItems(items: List<OrderItem>)

    @Query("SELECT * FROM orders WHERE customerId = :customerId ORDER BY createdAt DESC")
    fun observeOrders(customerId: Long): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE id = :id LIMIT 1")
    fun observeOrder(id: Long): Flow<Order?>

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    fun observeOrderItems(orderId: Long): Flow<List<OrderItem>>

    @Update
    suspend fun update(order: Order)

    @Query("SELECT COUNT(*) FROM orders WHERE customerId = :customerId")
    suspend fun countForCustomer(customerId: Long): Int
}

@Dao
interface PhoneChangeDao {
    @Insert
    suspend fun insert(request: PhoneChangeRequest): Long

    @Query("SELECT * FROM phone_change_requests WHERE customerId = :customerId ORDER BY createdAt DESC")
    fun observeForCustomer(customerId: Long): Flow<List<PhoneChangeRequest>>
}
