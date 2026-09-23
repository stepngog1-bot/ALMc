package ir.almasglass.sales.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class CustomerStatus { PENDING, APPROVED }

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerNumber: Int,           // fixed شماره مشتری, assigned at registration
    val fullName: String,
    val storeName: String,
    val phone: String,                 // username
    val city: String,
    val passwordHash: String,
    val status: CustomerStatus = CustomerStatus.PENDING,
    val createdAt: Long
)

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val carModel: String,              // سمند / زانتیا / پراید / پژو
    val category: String,
    val unitPrice: Long,               // تومان
    val description: String = ""
)

enum class OrderStatus { PENDING_REVIEW, CONFIRMED, CANCELLED }
enum class PaymentType { CREDIT, CASH }

@Entity(
    tableName = "orders",
    foreignKeys = [ForeignKey(
        entity = Customer::class,
        parentColumns = ["id"],
        childColumns = ["customerId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("customerId")]
)
data class Order(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderCode: String,             // ALM-{customerNumber}-{row}
    val customerId: Long,
    val totalQty: Int,
    val totalAmount: Long,
    val paymentType: PaymentType,
    val status: OrderStatus,
    val createdAt: Long
)

@Entity(
    tableName = "order_items",
    foreignKeys = [ForeignKey(
        entity = Order::class,
        parentColumns = ["id"],
        childColumns = ["orderId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("orderId")]
)
data class OrderItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderId: Long,
    val productId: Long,
    val productName: String,
    val unitPrice: Long,
    val quantity: Int
)

/** Cart rows live per-customer so a saved (ذخیره موقت) cart survives restarts. */
@Entity(
    tableName = "cart_items",
    foreignKeys = [ForeignKey(
        entity = Customer::class,
        parentColumns = ["id"],
        childColumns = ["customerId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("customerId")]
)
data class CartItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerId: Long,
    val productId: Long,
    val productName: String,
    val unitPrice: Long,
    val quantity: Int
)

enum class RequestStatus { PENDING, DONE, REJECTED }

@Entity(
    tableName = "phone_change_requests",
    foreignKeys = [ForeignKey(
        entity = Customer::class,
        parentColumns = ["id"],
        childColumns = ["customerId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("customerId")]
)
data class PhoneChangeRequest(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerId: Long,
    val oldPhone: String,
    val newPhone: String,
    val note: String,
    val status: RequestStatus = RequestStatus.PENDING,
    val createdAt: Long
)
