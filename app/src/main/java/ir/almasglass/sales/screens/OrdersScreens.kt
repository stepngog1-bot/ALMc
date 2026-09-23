package ir.almasglass.sales.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ir.almasglass.sales.components.*
import ir.almasglass.sales.data.Order
import ir.almasglass.sales.data.OrderStatus
import ir.almasglass.sales.data.PaymentType
import ir.almasglass.sales.util.formatJalaliDateTime
import ir.almasglass.sales.viewmodel.SalesViewModel

private fun statusLabel(status: OrderStatus): String = when (status) {
    OrderStatus.PENDING_REVIEW -> "در انتظار بررسی واحد فروش"
    OrderStatus.CONFIRMED -> "تأیید شده"
    OrderStatus.CANCELLED -> "لغو شده"
}

private fun paymentLabel(type: PaymentType): String = when (type) {
    PaymentType.CREDIT -> "اعتباری"
    PaymentType.CASH -> "نقدی"
}

@Composable
fun MyOrdersScreen(vm: SalesViewModel, onOpenOrder: (Long) -> Unit, onTab: (AppTab) -> Unit) {
    val orders by vm.orders.collectAsState()

    Column(Modifier.fillMaxSize()) {
        HeaderPlain("سفارش‌های من")
        if (orders.isEmpty()) {
            Box(Modifier.fillMaxWidth().weight(1f).padding(horizontal = 16.dp), contentAlignment = Alignment.Center) {
                Text("هنوز سفارشی ثبت نکرده‌اید.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 16.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                LazyColumn(Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(orders, key = { it.id }) { order -> OrderRow(order, onClick = { onOpenOrder(order.id) }) }
                }
            }
        }
        BottomActionBar(current = AppTab.ORDERS, onSelect = onTab)
    }
}

@Composable
private fun OrderRow(order: Order, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.fillMaxWidth().padding(14.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(order.orderCode, style = MaterialTheme.typography.titleSmall)
                Text(formatToman(order.totalAmount), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.height(4.dp))
            Text(formatJalaliDateTime(order.createdAt), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(statusLabel(order.status) + " · " + paymentLabel(order.paymentType), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun OrderDetailScreen(
    vm: SalesViewModel,
    orderId: Long,
    onBack: () -> Unit,
    onBackToStore: () -> Unit
) {
    val order by vm.observeOrder(orderId).collectAsState()
    val items by vm.observeOrderItems(orderId).collectAsState()

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        HeaderWithBack("جزئیات سفارش", onBack)
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).heightIn(min = 220.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
        ) {
            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                order?.let { o ->
                    Text(o.orderCode, style = MaterialTheme.typography.titleMedium)
                    Text(formatJalaliDateTime(o.createdAt), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(statusLabel(o.status), style = MaterialTheme.typography.bodyMedium)
                    Text("نوع پرداخت: " + paymentLabel(o.paymentType), style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(8.dp))
                    Divider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(Modifier.height(8.dp))
                }
                items.forEach { item ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(item.productName + " × " + formatCount(item.quantity), style = MaterialTheme.typography.bodyMedium)
                        Text(formatToman(item.unitPrice * item.quantity), style = MaterialTheme.typography.bodyMedium)
                    }
                }
                Spacer(Modifier.height(8.dp))
                Divider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(Modifier.height(8.dp))
                order?.let { o ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("مبلغ کل سفارش", style = MaterialTheme.typography.titleSmall)
                        Text(formatToman(o.totalAmount), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        var showCancelConfirm by remember { mutableStateOf(false) }
        OutlinedButton(
            onClick = { showCancelConfirm = true },
            enabled = order?.status == OrderStatus.PENDING_REVIEW,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(54.dp), shape = RoundedCornerShape(27.dp)
        ) {
            Icon(Icons.Rounded.Delete, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("لغو سفارش از طریق واحد فروش")
        }
        if (showCancelConfirm) {
            AlertDialog(
                onDismissRequest = { showCancelConfirm = false },
                title = { Text("لغو سفارش") },
                text = { Text("آیا از لغو این سفارش مطمئن هستید؟ درخواست لغو برای واحد فروش ارسال می‌شود.") },
                confirmButton = {
                    TextButton(onClick = {
                        order?.let { vm.cancelOrder(it) {} }
                        showCancelConfirm = false
                    }) { Text("بله، لغو شود") }
                },
                dismissButton = { TextButton(onClick = { showCancelConfirm = false }) { Text("انصراف") } }
            )
        }
        Button(
            onClick = onBackToStore,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(52.dp), shape = RoundedCornerShape(26.dp)
        ) {
            Icon(Icons.Rounded.Storefront, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("بازگشت به فروشگاه")
        }
        Spacer(Modifier.height(24.dp))
    }
}
