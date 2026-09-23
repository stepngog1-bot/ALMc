package ir.almasglass.sales.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.LockReset
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material.icons.rounded.SupportAgent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import ir.almasglass.sales.components.*
import ir.almasglass.sales.util.toPersianDigits
import ir.almasglass.sales.viewmodel.SalesViewModel

@Composable
fun AccountScreen(
    vm: SalesViewModel,
    onPhoneChangeRequest: () -> Unit,
    onChangePassword: () -> Unit,
    onLoggedOut: () -> Unit,
    onTab: (AppTab) -> Unit
) {
    val customer by vm.currentCustomer.collectAsState()
    val context = LocalContext.current

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        HeaderPlain("حساب کاربری")
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Card(
                modifier = Modifier.width(150.dp).height(84.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text("نام مشتری", style = MaterialTheme.typography.titleSmall)
                    Text(customer?.fullName ?: "", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            OutlinedCard(
                modifier = Modifier.weight(1f).height(84.dp),
                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text("شماره مشتری", style = MaterialTheme.typography.titleSmall)
                    Text(toPersianDigits((customer?.customerNumber ?: 0).toString()), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        OutlinedCard(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(92.dp),
            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("شماره همراه", style = MaterialTheme.typography.titleSmall)
                Text(customer?.phone ?: "", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("نام کاربری ورود", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Button(
            onClick = {
                try {
                    context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:")))
                } catch (e: Exception) {
                    Toast.makeText(context, "امکان تماس در این دستگاه وجود ندارد.", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(54.dp), shape = RoundedCornerShape(27.dp)
        ) {
            Icon(Icons.Rounded.SupportAgent, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("پشتیبانی")
        }
        FilledTonalButton(
            onClick = onPhoneChangeRequest,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(54.dp), shape = RoundedCornerShape(27.dp)
        ) {
            Icon(Icons.Rounded.PhoneAndroid, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("درخواست تغییر شماره همراه")
        }
        FilledTonalButton(
            onClick = onChangePassword,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(54.dp), shape = RoundedCornerShape(27.dp)
        ) {
            Icon(Icons.Rounded.LockReset, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("تغییر رمز عبور")
        }
        OutlinedButton(
            onClick = {
                try {
                    context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:")))
                } catch (e: Exception) {
                    Toast.makeText(context, "امکان تماس در این دستگاه وجود ندارد.", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(54.dp), shape = RoundedCornerShape(27.dp)
        ) {
            Icon(Icons.Rounded.Call, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("ارتباط با واحد فروش")
        }
        OutlinedButton(
            onClick = { vm.logout(); onLoggedOut() },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(54.dp), shape = RoundedCornerShape(27.dp)
        ) {
            Icon(Icons.Rounded.Logout, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("خروج از حساب")
        }
        Text(
            "برای امنیت حساب، تغییر شماره همراه پس از بررسی واحد فروش اعمال می‌شود.",
            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
        )
        BottomActionBar(current = AppTab.ACCOUNT, onSelect = onTab)
    }
}

@Composable
fun PhoneChangeRequestScreen(vm: SalesViewModel, onDone: () -> Unit) {
    val customer by vm.currentCustomer.collectAsState()
    var newPhone by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var sent by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        HeaderPlain("درخواست تغییر شماره همراه")
        OutlinedCard(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(88.dp),
            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("شماره فعلی", style = MaterialTheme.typography.titleSmall)
                Text(customer?.phone ?: "", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Text("شماره همراه جدید", style = MaterialTheme.typography.titleSmall, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp), textAlign = TextAlign.End)
        OutlinedTextField(value = newPhone, onValueChange = { newPhone = it }, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), singleLine = true)
        Text("توضیح اختیاری", style = MaterialTheme.typography.titleSmall, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp), textAlign = TextAlign.End)
        OutlinedTextField(value = note, onValueChange = { note = it }, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), minLines = 2)
        if (error != null) {
            Text(error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(horizontal = 16.dp))
        }
        if (sent) {
            Text("درخواست شما ثبت شد و پس از بررسی واحد فروش اعمال می‌شود.", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(horizontal = 16.dp))
        }
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = {
                if (newPhone.isBlank()) {
                    error = "شماره همراه جدید را وارد کنید."
                } else {
                    error = null
                    vm.requestPhoneChange(newPhone.trim(), note.trim()) { sent = true }
                }
            },
            modifier = Modifier.fillMaxWidth(0.92f).padding(horizontal = 16.dp).height(58.dp), shape = RoundedCornerShape(29.dp)
        ) {
            Icon(Icons.Rounded.Send, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("ثبت درخواست تغییر شماره")
        }
        Spacer(Modifier.height(12.dp))
        FilledTonalButton(
            onClick = onDone,
            modifier = Modifier.fillMaxWidth(0.92f).padding(horizontal = 16.dp).height(54.dp), shape = RoundedCornerShape(27.dp)
        ) {
            Icon(Icons.Rounded.ArrowBack, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("بازگشت")
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun ChangePasswordScreen(vm: SalesViewModel, onDone: () -> Unit) {
    var current by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var repeat by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var success by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        HeaderPlain("تغییر رمز عبور")
        Text("رمز عبور فعلی", style = MaterialTheme.typography.titleSmall, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp), textAlign = TextAlign.End)
        OutlinedTextField(
            value = current, onValueChange = { current = it }, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            singleLine = true, visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
        )
        Text("رمز عبور جدید", style = MaterialTheme.typography.titleSmall, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp), textAlign = TextAlign.End)
        OutlinedTextField(
            value = newPass, onValueChange = { newPass = it }, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            singleLine = true, visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
        )
        Text("تکرار رمز عبور جدید", style = MaterialTheme.typography.titleSmall, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp), textAlign = TextAlign.End)
        OutlinedTextField(
            value = repeat, onValueChange = { repeat = it }, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            singleLine = true, visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
        )
        if (error != null) {
            Text(error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(horizontal = 16.dp))
        }
        if (success) {
            Text("رمز عبور با موفقیت تغییر کرد.", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(horizontal = 16.dp))
        }
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = {
                when {
                    current.isBlank() || newPass.isBlank() || repeat.isBlank() -> error = "همه فیلدها الزامی است."
                    newPass != repeat -> error = "رمز عبور جدید و تکرار آن یکسان نیستند."
                    else -> {
                        vm.changePassword(current, newPass) { ok ->
                            if (ok) { error = null; success = true } else error = "رمز عبور فعلی صحیح نیست."
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(0.92f).padding(horizontal = 16.dp).height(58.dp), shape = RoundedCornerShape(29.dp)
        ) {
            Icon(Icons.Rounded.LockReset, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("تغییر رمز عبور")
        }
        Spacer(Modifier.height(12.dp))
        FilledTonalButton(
            onClick = onDone,
            modifier = Modifier.fillMaxWidth(0.92f).padding(horizontal = 16.dp).height(54.dp), shape = RoundedCornerShape(27.dp)
        ) {
            Icon(Icons.Rounded.ArrowBack, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("بازگشت")
        }
        Spacer(Modifier.height(24.dp))
    }
}
