package ir.almasglass.sales.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Login
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ir.almasglass.sales.data.AuthResult
import ir.almasglass.sales.data.RegisterResult
import ir.almasglass.sales.viewmodel.SalesViewModel

@Composable
fun SplashScreen(vm: SalesViewModel, onLogin: () -> Unit, onRegister: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(136.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(136.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        androidx.compose.material.icons.Icons.Rounded.Home,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(64.dp)
                    )
                }
            }
        }
        Spacer(Modifier.height(20.dp))
        Text(
            "شرکت شیشه ایمنی الماس نگین بینالود",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(6.dp))
        Text("نرم افزار فروش", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))
        Divider(color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp))
        Spacer(Modifier.height(16.dp))
        ElevatedCard(
            modifier = Modifier.fillMaxWidth().height(126.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("خوش آمدید", style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(4.dp))
                Text(
                    "برای ورود به حساب کاربری یا ثبت درخواست عضویت، یکی از گزینه‌های زیر را انتخاب کنید.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = onLogin, modifier = Modifier.fillMaxWidth().height(58.dp), shape = RoundedCornerShape(29.dp)) {
            Icon(Icons.Rounded.Login, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("ورود")
        }
        Spacer(Modifier.height(12.dp))
        FilledTonalButton(onClick = onRegister, modifier = Modifier.fillMaxWidth().height(58.dp), shape = RoundedCornerShape(29.dp)) {
            Icon(Icons.Rounded.PersonAdd, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("ثبت نام")
        }
        Spacer(Modifier.height(12.dp))
        Text(
            "ثبت نام مشتریان پس از بررسی و تأیید واحد فروش فعال می‌شود.",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun LoginScreen(vm: SalesViewModel, onBack: () -> Unit, onLoggedIn: () -> Unit, onGoRegister: () -> Unit) {
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(onClick = onBack, modifier = Modifier.width(96.dp).height(46.dp)) {
                Icon(Icons.Rounded.ArrowBack, contentDescription = null)
                Text("بازگشت")
            }
            ElevatedCard(
                modifier = Modifier.weight(1f).height(86.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Box(Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.CenterStart) {
                    Text("ورود به نرم افزار فروش", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        }
        ElevatedCard(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(430.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(Modifier.fillMaxSize().padding(20.dp)) {
                Text("حساب کاربری", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Text("شماره موبایل همان نام کاربری شماست.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Spacer(Modifier.height(16.dp))
                Text("شماره موبایل", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                OutlinedTextField(
                    value = phone, onValueChange = { phone = it }, label = { Text("مثلاً 0912xxxxxxx") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true
                )
                Spacer(Modifier.height(12.dp))
                Text("رمز عبور", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                OutlinedTextField(
                    value = password, onValueChange = { password = it }, label = { Text("رمز عبور") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true,
                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
                )
                if (error != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (phone.isBlank() || password.isBlank()) {
                            error = "شماره موبایل و رمز عبور را وارد کنید."
                        } else {
                            vm.login(phone.trim(), password) { result ->
                                when (result) {
                                    is AuthResult.Success -> onLoggedIn()
                                    AuthResult.InvalidCredentials -> error = "شماره موبایل یا رمز عبور اشتباه است."
                                    AuthResult.PendingApproval -> error = "حساب شما هنوز توسط واحد فروش تأیید نشده است."
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(58.dp), shape = RoundedCornerShape(29.dp)
                ) {
                    Icon(Icons.Rounded.Login, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("ورود")
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        FilledTonalButton(
            onClick = onGoRegister,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(54.dp),
            shape = RoundedCornerShape(27.dp)
        ) {
            Icon(Icons.Rounded.PersonAdd, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("حساب ندارید؟ ثبت نام")
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun RegisterScreen(vm: SalesViewModel, onBack: () -> Unit, onRegistered: () -> Unit) {
    var fullName by remember { mutableStateOf("") }
    var storeName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(onClick = onBack, modifier = Modifier.width(96.dp).height(46.dp)) {
                Icon(Icons.Rounded.ArrowBack, contentDescription = null)
                Text("بازگشت")
            }
            ElevatedCard(
                modifier = Modifier.weight(1f).height(86.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Box(Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.CenterStart) {
                    Text("ثبت نام مشتری جدید", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        }
        ElevatedCard(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            Column(Modifier.fillMaxWidth().padding(20.dp)) {
                Text("اطلاعات مشتری", style = MaterialTheme.typography.titleMedium)
                Text("اطلاعات زیر را تکمیل کنید. شماره موبایل، نام کاربری ورود شما خواهد بود.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(12.dp))
                Text("نام و نام خانوادگی", style = MaterialTheme.typography.titleSmall)
                OutlinedTextField(value = fullName, onValueChange = { fullName = it }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(Modifier.height(10.dp))
                Text("نام فروشگاه / شرکت", style = MaterialTheme.typography.titleSmall)
                OutlinedTextField(value = storeName, onValueChange = { storeName = it }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(Modifier.height(10.dp))
                Text("شماره موبایل (نام کاربری)", style = MaterialTheme.typography.titleSmall)
                OutlinedTextField(value = phone, onValueChange = { phone = it }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("رمز عبور", style = MaterialTheme.typography.titleSmall)
                    Text("شهر", style = MaterialTheme.typography.titleSmall)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = city, onValueChange = { city = it }, modifier = Modifier.width(140.dp), singleLine = true
                    )
                    OutlinedTextField(
                        value = password, onValueChange = { password = it }, modifier = Modifier.weight(1f), singleLine = true,
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
                    )
                }
                if (error != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (fullName.isBlank() || storeName.isBlank() || phone.isBlank() || password.isBlank() || city.isBlank()) {
                            error = "همه فیلدها الزامی است."
                        } else {
                            vm.register(fullName.trim(), storeName.trim(), phone.trim(), city.trim(), password) { result ->
                                when (result) {
                                    is RegisterResult.Success -> onRegistered()
                                    RegisterResult.PhoneAlreadyUsed -> error = "این شماره موبایل قبلاً ثبت شده است."
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(28.dp)
                ) {
                    Icon(Icons.Rounded.PersonAdd, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("ثبت درخواست عضویت")
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    "شماره موبایل شما همان نام کاربری ورود به سامانه است؛ نام کاربری جداگانه نداریم.",
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun RegisterSuccessScreen(vm: SalesViewModel, onHome: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.size(136.dp)) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(Icons.Rounded.Home, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(64.dp))
            }
        }
        Spacer(Modifier.height(16.dp))
        Text("شرکت شیشه ایمنی الماس نگین بینالود", style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
        Spacer(Modifier.height(16.dp))
        ElevatedCard(
            modifier = Modifier.fillMaxWidth().height(260.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                Modifier.fillMaxSize().padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("درخواست ثبت نام ارسال شد", style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Spacer(Modifier.height(6.dp))
                Text(
                    "درخواست شما ثبت شد. پس از تأیید واحد فروش، ورود با شماره موبایل و رمز انتخابی فعال می‌شود.",
                    style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = onHome, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(28.dp)) {
            Icon(Icons.Rounded.Home, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("بازگشت به صفحه اصلی")
        }
    }
}
