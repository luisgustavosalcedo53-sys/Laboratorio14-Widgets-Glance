package pe.edu.lab14.dispensalud

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.action.*
import androidx.glance.appwidget.*
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition

val CompletedKey = booleanPreferencesKey("completed")

suspend fun refreshWidgets(context: Context) {
    val completed = DemoStore.completed(context)
    GlanceAppWidgetManager(context).getGlanceIds(DispensaludWidget::class.java).forEach { id ->
        updateAppWidgetState(context, id) { preferences -> preferences[CompletedKey] = completed }
    }
    DispensaludWidget().updateAll(context)
}

class DispensaludWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = DispensaludWidget()
}

class DispensaludWidget : GlanceAppWidget() {
    override val stateDefinition = PreferencesGlanceStateDefinition
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val initialCompleted = DemoStore.completed(context)
        provideContent {
            val completed = currentState<Preferences>()[CompletedKey] ?: initialCompleted
            GlanceTheme {
                Column(
                    GlanceModifier.fillMaxSize().background(Color(0xFFE1F1E7)).appWidgetBackground().cornerRadius(28.dp).padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("DISPENSALUD  ·  MI DÍA", style = TextStyle(color = ColorProvider(Color(0xFF146B52)), fontSize = 12.sp, fontWeight = FontWeight.Bold))
                    Spacer(GlanceModifier.height(8.dp))
                    Text(if (completed) "¡Rutina completada!" else "Tu próximo recordatorio", style = TextStyle(color = ColorProvider(Color(0xFF16392E)), fontSize = 22.sp, fontWeight = FontWeight.Bold))
                    Spacer(GlanceModifier.height(6.dp))
                    Text(if (completed) "1 de 1 realizado · Revisa tu historial" else "08:00 · Recordatorio de ejemplo", style = TextStyle(color = ColorProvider(Color(0xFF264C3D)), fontSize = 14.sp))
                    Spacer(GlanceModifier.height(12.dp))
                    Row(modifier = GlanceModifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Button(if (completed) "Ver registro" else "Realizado", onClick = if (completed) actionStartActivity<HistorialActivity>() else actionRunCallback<CompleteReminder>(), modifier = GlanceModifier.defaultWeight())
                        Spacer(GlanceModifier.width(8.dp))
                        Button("Mi rutina", actionStartActivity<MedicamentosActivity>(), modifier = GlanceModifier.defaultWeight())
                    }
                }
            }
        }
    }
}

class CompleteReminder : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        // Idempotente: varios toques mantienen un único registro de demostración.
        DemoStore.setCompleted(context, true)
        refreshWidgets(context)
    }
}
