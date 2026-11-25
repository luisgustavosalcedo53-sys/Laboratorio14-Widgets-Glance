package pe.edu.lab14.dispensalud

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.*
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.*
import androidx.glance.layout.*
import androidx.glance.text.Text

class SimpleWidget : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = SimpleWidgetContent()
}

class SimpleWidgetContent : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent { GlanceTheme { Content() } }
    }
    @Composable
    private fun Content() {
        Column(GlanceModifier.fillMaxSize().background(GlanceTheme.colors.background).padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalAlignment = Alignment.CenterVertically) {
            Text("¿A dónde quieres dirigirte?", modifier = GlanceModifier.padding(bottom = 12.dp))
            Row(horizontalAlignment = Alignment.CenterHorizontally) {
                Button("Página principal", actionStartActivity<MainActivity>())
                Spacer(GlanceModifier.width(8.dp))
                Button("Medicamentos", actionStartActivity<MedicamentosActivity>())
            }
        }
    }
}
