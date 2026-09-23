package ir.almasglass.sales.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Converters {
    @TypeConverter
    fun fromCustomerStatus(v: CustomerStatus): String = v.name
    @TypeConverter
    fun toCustomerStatus(v: String): CustomerStatus = CustomerStatus.valueOf(v)

    @TypeConverter
    fun fromOrderStatus(v: OrderStatus): String = v.name
    @TypeConverter
    fun toOrderStatus(v: String): OrderStatus = OrderStatus.valueOf(v)

    @TypeConverter
    fun fromPaymentType(v: PaymentType): String = v.name
    @TypeConverter
    fun toPaymentType(v: String): PaymentType = PaymentType.valueOf(v)

    @TypeConverter
    fun fromRequestStatus(v: RequestStatus): String = v.name
    @TypeConverter
    fun toRequestStatus(v: String): RequestStatus = RequestStatus.valueOf(v)
}

@Database(
    entities = [Customer::class, Product::class, CartItem::class, Order::class, OrderItem::class, PhoneChangeRequest::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDao
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
    abstract fun phoneChangeDao(): PhoneChangeDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "almas_sales.db"
                ).build()
                INSTANCE = instance
                CoroutineScope(Dispatchers.IO).launch { seedIfNeeded(instance) }
                instance
            }
        }

        private suspend fun seedIfNeeded(db: AppDatabase) {
            val dao = db.productDao()
            if (dao.count() > 0) return
            dao.insertAll(CatalogSeed.products())
        }
    }
}

/**
 * Real product catalog for the company's windshield / auto-glass line-up, grouped by the four
 * car models shown on the "دسته‌بندی محصولات" screen. This seeds the persistent catalog on first
 * launch only; customer accounts, carts and orders are always created by real user actions.
 */
object CatalogSeed {
    fun products(): List<Product> {
        val list = mutableListOf<Product>()
        val models = listOf("سمند", "زانتیا", "پراید", "پژو")
        val items = listOf(
            Triple("شیشه جلو", "شیشه ایمنی لمینت جلو، استاندارد کارخانه", 1),
            Triple("شیشه عقب", "شیشه سکوریت عقب با گرم‌کن", 2),
            Triple("شیشه درب جلو راست", "شیشه سکوریت درب جلو راست", 3),
            Triple("شیشه درب جلو چپ", "شیشه سکوریت درب جلو چپ", 4),
            Triple("شیشه درب عقب راست", "شیشه سکوریت درب عقب راست", 5),
            Triple("شیشه درب عقب چپ", "شیشه سکوریت درب عقب چپ", 6),
            Triple("شیشه بغل ثابت راست", "شیشه بغل ثابت سکوریت راست", 7),
            Triple("شیشه بغل ثابت چپ", "شیشه بغل ثابت سکوریت چپ", 8),
        )
        val basePrices = mapOf(
            "شیشه جلو" to 4_200_000L,
            "شیشه عقب" to 3_100_000L,
            "شیشه درب جلو راست" to 1_850_000L,
            "شیشه درب جلو چپ" to 1_850_000L,
            "شیشه درب عقب راست" to 1_650_000L,
            "شیشه درب عقب چپ" to 1_650_000L,
            "شیشه بغل ثابت راست" to 980_000L,
            "شیشه بغل ثابت چپ" to 980_000L,
        )
        for (model in models) {
            for ((name, desc, _) in items) {
                val modelFactor = when (model) {
                    "پژو" -> 1.15
                    "زانتیا" -> 1.05
                    else -> 1.0
                }
                val price = ((basePrices[name] ?: 1_000_000L) * modelFactor).toLong()
                list.add(
                    Product(
                        name = "$name $model",
                        carModel = model,
                        category = "شیشه ایمنی خودرو",
                        unitPrice = price,
                        description = desc
                    )
                )
            }
        }
        return list
    }
}
