package ir.almasglass.sales.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddShoppingCart
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ir.almasglass.sales.components.*
import ir.almasglass.sales.data.Product
import ir.almasglass.sales.viewmodel.SalesViewModel

private val CAR_MODELS = listOf("سمند", "زانتیا", "پراید", "پژو")

@Composable
fun CategoriesScreen(
    vm: SalesViewModel,
    onOpenProducts: () -> Unit,
    onTab: (AppTab) -> Unit
) {
    val totalQty by vm.cartTotalQty.collectAsState()
    val totalAmount by vm.cartTotalAmount.collectAsState()

    Column(Modifier.fillMaxSize()) {
        HeaderPlain("سامانه فروش", "شرکت شیشه ایمنی الماس نگین بینالود")
        StatCardsRow("جمع تعداد سفارش", "تعداد کل شیشه‌ها: " + formatCount(totalQty), "مبلغ کل سفارش", formatToman(totalAmount))
        Text(
            "انتخاب خودرو",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
        )
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("سمند", "زانتیا").forEach { model ->
                Button(
                    onClick = { vm.selectCarModel(model); onOpenProducts() },
                    modifier = Modifier.width(90.dp).height(60.dp), shape = RoundedCornerShape(30.dp)
                ) { Text(model) }
            }
            Button(
                onClick = { vm.selectCarModel("پراید"); onOpenProducts() },
                modifier = Modifier.weight(1f).height(60.dp), shape = RoundedCornerShape(30.dp)
            ) { Text("پراید") }
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.End) {
            Button(
                onClick = { vm.selectCarModel("پژو"); onOpenProducts() },
                modifier = Modifier.width(90.dp).height(60.dp), shape = RoundedCornerShape(30.dp)
            ) { Text("پژو") }
        }
        Spacer(Modifier.weight(1f))
        BottomActionBar(current = AppTab.STORE, onSelect = onTab)
    }
}

@Composable
fun ProductsScreen(
    vm: SalesViewModel,
    onOpenCategories: () -> Unit,
    onOpenProduct: (Long) -> Unit,
    onSaveDraft: () -> Unit,
    onCheckout: () -> Unit,
    onTab: (AppTab) -> Unit
) {
    val products by vm.products.collectAsState()
    val totalQty by vm.cartTotalQty.collectAsState()
    val totalAmount by vm.cartTotalAmount.collectAsState()
    var query by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onOpenCategories, modifier = Modifier.width(92.dp).height(46.dp)) {
                Icon(Icons.Rounded.Category, contentDescription = null)
                Text("دسته‌ها")
            }
            ElevatedCard(
                modifier = Modifier.weight(1f).height(86.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("محصولات", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text("محصولات خودروی انتخاب‌شده", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        }
        StatCardsRow("جمع تعداد سفارش", "تعداد کل شیشه‌ها: " + formatCount(totalQty), "مبلغ کل سفارش", formatToman(totalAmount))
        OutlinedTextField(
            value = query,
            onValueChange = { query = it; vm.productSearch.value = it },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            placeholder = { Text("جستجوی محصول") },
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(28.dp)
        )
        Spacer(Modifier.height(8.dp))
        if (products.isEmpty()) {
            Box(
                Modifier.fillMaxWidth().weight(1f).padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("محصولی یافت نشد.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(products, key = { it.id }) { product ->
                    ProductRow(product) { onOpenProduct(product.id) }
                }
            }
        }
        Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilledTonalButton(onClick = onSaveDraft, modifier = Modifier.width(174.dp).height(48.dp), shape = RoundedCornerShape(24.dp)) {
                Icon(Icons.Rounded.Save, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("ذخیره موقت")
            }
            Button(onClick = onCheckout, modifier = Modifier.weight(1f).height(48.dp), shape = RoundedCornerShape(24.dp)) {
                Icon(Icons.Rounded.CheckCircle, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("اتمام سفارش")
            }
        }
        BottomActionBar(current = AppTab.STORE, onSelect = onTab)
    }
}

@Composable
private fun ProductRow(product: Product, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest)
    ) {
        Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(Modifier.weight(1f)) {
                Text(product.name, style = MaterialTheme.typography.titleSmall)
                Text(product.category, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(formatToman(product.unitPrice), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun ProductDetailScreen(
    vm: SalesViewModel,
    productId: Long,
    onBack: () -> Unit,
    onAddedGoCart: () -> Unit,
    onSelectAnother: () -> Unit,
    onCheckout: () -> Unit,
    onSaveDraft: () -> Unit
) {
    var product by remember { mutableStateOf<Product?>(null) }
    var quantity by remember { mutableStateOf(1) }
    var quantityText by remember { mutableStateOf("1") }

    LaunchedEffect(productId) { product = vm.getProduct(productId) }

    Column(Modifier.fillMaxSize().verticalScroll(androidx.compose.foundation.rememberScrollState())) {
        HeaderWithBack("محصول انتخاب‌شده", onBack)
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(212.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(product?.name ?: "", style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(4.dp))
                    Text(product?.description ?: "", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                }
            }
        }
        Text("تعداد", style = MaterialTheme.typography.titleMedium, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), textAlign = TextAlign.End)
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf(10, 5, 1).forEach { inc ->
                FilledTonalButton(onClick = {
                    quantity += inc
                    quantityText = quantity.toString()
                }, modifier = Modifier.height(48.dp)) { Text("+$inc") }
            }
            OutlinedTextField(
                value = quantityText,
                onValueChange = { txt ->
                    quantityText = txt
                    quantity = txt.toIntOrNull() ?: 0
                },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }
        val unitPrice = product?.unitPrice ?: 0L
        StatCardsRow("تعداد", "تعداد انتخاب‌شده: " + formatCount(quantity), "مبلغ کل", formatToman(unitPrice * quantity))
        Button(
            onClick = {
                val p = product
                if (p != null && quantity > 0) {
                    vm.addToCart(p, quantity)
                    onAddedGoCart()
                }
            },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(54.dp), shape = RoundedCornerShape(27.dp)
        ) {
            Icon(Icons.Rounded.AddShoppingCart, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("افزودن به سبد سفارش")
        }
        Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilledTonalButton(onClick = onSelectAnother, modifier = Modifier.width(180.dp).height(50.dp), shape = RoundedCornerShape(25.dp)) {
                Text("انتخاب محصول جدید")
            }
            Button(onClick = onCheckout, modifier = Modifier.weight(1f).height(50.dp), shape = RoundedCornerShape(25.dp)) {
                Icon(Icons.Rounded.CheckCircle, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("اتمام سفارش")
            }
        }
        FilledTonalButton(
            onClick = onSaveDraft,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(46.dp), shape = RoundedCornerShape(23.dp)
        ) { Text("ذخیره موقت سفارش") }
        Spacer(Modifier.height(16.dp))
    }
}
