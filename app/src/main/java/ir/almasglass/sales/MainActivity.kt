package ir.almasglass.sales

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import ir.almasglass.sales.navigation.AlmasNavGraph
import ir.almasglass.sales.ui.theme.AlmasSalesTheme
import ir.almasglass.sales.viewmodel.SalesViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: SalesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                AlmasSalesTheme {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        AlmasNavGraph(vm = viewModel)
                    }
                }
            }
        }
    }
}
