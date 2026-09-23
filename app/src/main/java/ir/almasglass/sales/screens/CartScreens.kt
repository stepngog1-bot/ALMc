package ir.almasglass.sales.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ir.almasglass.sales.components.*
import ir.almasglass.sales.data.CartItem
import ir.almasglass.sales.data.PaymentType
import ir.almasglass.sales.viewmodel.SalesViewModel

@Composable
fun CartScreen(
    vm: SalesViewModel,
    onEditLine: (Long) -> Unit,
    onSelectAnother: () -> Unit,
    onCheckout: () -> Unit,
    onSaveDraft: () -> Unit,
    onTab: (AppTab) -> Unit
) {
    val cart by vm.cart.collectAsState()
    val totalQty by vm.cartTotalQty.collectAsState()
    val totalAmount by vm.cartTotalAmount.collectAsState()

    Column(Modifier.fillMaxSize()) {
        HeaderPlain("سبد سفارش")
        if (cart.isEmpty()) {
            Box(
                Modifier.fillMaxWidth().weight(1f).padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) { Text("سبد سفارش شما خالی است.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 16.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                LazyColumn(Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(cart, key = { it.id }) { line -> CartLineRow(line, onClick = { onEditLine(line.id) }) }
                }
            }
        }
        StatCardsRow("جمع تعداد", "تعداد کل اقلام: " + formatCount(totalQty), "مبلغ کل سفارش", formatToman(totalAmount))
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilledTonalButton(onClick = onSelectAnother, modifier = Modifier.width(172.dp).height(52.dp), shape = RoundedCornerShape(26.dp)) {
                Icon(Icons.Rounded.Add, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("انتخاب محصول جدید")
            }
            Button(
                onClick = onCheckout,
                enabled = cart.isNotEmpty(),
                modifier = Modifier.weight(1f).height(52.dp), shape = RoundedCornerShape(26.dp)
            ) {
                Icon(Icons.Rounded.CheckCircle, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("اتمام سفارش")
            }
        }
        FilledTonalButton(
            onClick = onSaveDraft,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(50.dp), shape = RoundedCornerShape(25.dp)
        ) {
            Icon(Icons.Rounded.Save, contentDescription = null)
            Spacer(Modifier.width(6.dp))
            Text("ذخیره موقت سفارش")
        }
        BottomActionBar(current = AppTab.CART, onSelect = onTab)
    }
}

@Composable
private fun CartLineRow(line: CartItem, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(Modifier.weight(1f)) {
                Text(line.productName, style = MaterialTheme.typography.titleSmall)
                Text("تعداد: " + formatCount(line.quantity), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(formatToman(line.unitPrice * line.quantity), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun EditCartItemScreen(
    vm: SalesViewModel,
    cartItemId: Long,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    onSelectAnother: () -> Unit,
    onCheckout: () -> Unit
) {
    var line by remember { mutableStateOf<CartItem?>(null) }
    var quantityText by remember { mutableStateOf("") }

    LaunchedEffect(cartItemId) {
        val l = vm.getCartLine(cartItemId)
        line = l
        quantityText = l?.quantity?.toString() ?: ""
    }

    Column(Modifier.fillMaxSize().verticalScroll(androidx.compose.foundation.rememberScrollState())) {
        HeaderWithBack("ویرایش ردیف", onBack)
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(196.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(line?.productName ?: "", style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
            }
        }
        Text("تعداد جدید", style = MaterialTheme.typography.titleMedium, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), textAlign = TextAlign.End)
        OutlinedTextField(
            value = quantityText,
            onValueChange = { quantityText = it },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            singleLine = true
        )
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf(1, 5, 10).forEach { n ->
                FilledTonalButton(
                    onClick = { quantityText = ((quantityText.toIntOrNull() ?: 0) + n).toString() },
                    modifier = Modifier.height(40.dp)
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Text("$n عدد")
                }
            }
        }
        val newQty = quantityText.toIntOrNull() ?: 0
        val unitPrice = line?.unitPrice ?: 0L
        StatCardsRow("تعداد جدید", "تعداد واردشده: " + formatCount(newQty), "مبلغ کل جدید", formatToman(unitPrice * newQty))
        Button(
            onClick = {
                if (newQty > 0) vm.updateCartLine(cartItemId, newQty) { onSaved() }
            },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(50.dp), shape = RoundedCornerShape(25.dp)
        ) {
            Icon(Icons.Rounded.Save, contentDescription = null)
            Spacer(Modifier.width(6.dp))
            Text("ذخیره تغییر تعداد")
        }
        OutlinedButton(
            onClick = { vm.removeCartLine(cartItemId) { onSaved() } },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(48.dp), shape = RoundedCornerShape(24.dp)
        ) {
            Icon(Icons.Rounded.Delete, contentDescription = null)
            Spacer(Modifier.width(6.dp))
            Text("حذف این ردیف از سبد")
        }
        Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilledTonalButton(onClick = onSelectAnother, modifier = Modifier.width(180.dp).height(48.dp), shape = RoundedCornerShape(24.dp)) {
                Icon(Icons.Rounded.Add, contentDescription = null)
                Text("انتخاب محصول جدید")
            }
            Button(onClick = onCheckout, modifier = Modifier.weight(1f).height(48.dp), shape = RoundedCornerShape(24.dp)) {
                Icon(Icons.Rounded.CheckCircle, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("اتمام سفارش")
            }
        }
    }
}

@Composable
fun CheckoutConfirmScreen(
    vm: SalesViewModel,
    onBack: () -> Unit,
    onCreditPlaced: () -> Unit,
    onCashPlaced: () -> Unit
) {
    val totalQty by vm.cartTotalQty.collectAsState()
    val totalAmount by vm.cartTotalAmount.collectAsState()

    Column(Modifier.fillMaxSize().verticalScroll(androidx.compose.foundation.rememberScrollState())) {
        HeaderWithBack("اتمام سفارش", onBack)
        StatCardsRow("جمع تعداد", "تعداد کل اقلام: " + formatCount(totalQty), "مبلغ کل سفارش", "تومان: " + formatToman(totalAmount))
        OutlinedCard(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(142.dp),
            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            Column(Modifier.padding(20.dp)) {
                Text("پرداخت اعتباری", style = MaterialTheme.typography.titleMedium)
                Text("سفارش همین حالا ثبت می‌شود و واحد فروش برای ادامه هماهنگی تماس خواهد گرفت.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Button(
            onClick = { vm.placeOrder(PaymentType.CREDIT) { onCreditPlaced() } },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(56.dp), shape = RoundedCornerShape(28.dp)
        ) {
            Icon(Icons.Rounded.AccountBalanceWallet, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("تأیید و ثبت سفارش اعتباری")
        }
        Spacer(Modifier.height(8.dp))
        OutlinedCard(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(144.dp),
            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            Column(Modifier.padding(20.dp)) {
                Text("پرداخت نقدی", style = MaterialTheme.typography.titleMedium)
                Text("پرداخت نقدی فعلاً غیرفعال است و در مرحله بعدی پس از اتصال درگاه فعال می‌شود.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        FilledTonalButton(
            onClick = { vm.placeOrder(PaymentType.CASH) { onCashPlaced() } },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(56.dp), shape = RoundedCornerShape(28.dp)
        ) {
            Icon(Icons.Rounded.Block, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("پرداخت نقدی — غیرفعال")
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun OrderSuccessScreen(onViewOrders: () -> Unit, onBackToStore: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth().height(220.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(Modifier.fillMaxSize().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text("سفارش شما با موفقیت ثبت شد ✓", style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Spacer(Modifier.height(6.dp))
                Text("سفارش در پنل فروش ثبت شد و از بخش «سفارش‌های من» قابل پیگیری است.", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = onViewOrders, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(28.dp)) {
            Icon(Icons.Rounded.ReceiptLong, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("مشاهده سفارش‌های من")
        }
        Spacer(Modifier.height(12.dp))
        FilledTonalButton(onClick = onBackToStore, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(28.dp)) {
            Icon(Icons.Rounded.Storefront, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("بازگشت به فروشگاه")
        }
    }
}

@Composable
fun CashPaymentDemoScreen(onGateway: () -> Unit, onViewOrders: () -> Unit, onBackToStore: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth().height(220.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(Modifier.fillMaxSize().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text("سفارش نقدی با موفقیت ثبت شد ✓", style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Spacer(Modifier.height(6.dp))
                Text("منتظر تماس از طرف واحد فروش باشید.", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = onGateway, modifier = Modifier.fillMaxWidth(0.92f).height(52.dp), shape = RoundedCornerShape(26.dp)) {
            Icon(Icons.Rounded.Payments, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("رفتن به درگاه پرداخت")
        }
        Spacer(Modifier.height(12.dp))
        Button(onClick = onViewOrders, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(28.dp)) {
            Icon(Icons.Rounded.ReceiptLong, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("مشاهده سفارش‌های من")
        }
        Spacer(Modifier.height(12.dp))
        FilledTonalButton(onClick = onBackToStore, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(28.dp)) {
            Icon(Icons.Rounded.Storefront, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("بازگشت به فروشگاه")
        }
    }
}
