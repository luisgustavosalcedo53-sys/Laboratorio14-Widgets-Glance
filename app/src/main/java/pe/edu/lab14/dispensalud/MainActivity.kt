package pe.edu.lab14.dispensalud

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.launch

object DemoStore {
    private fun prefs(c: Context) = c.getSharedPreferences("widget_demo", Context.MODE_PRIVATE)
    fun completed(c: Context) = prefs(c).getBoolean("completed", false)
    fun setCompleted(c: Context, value: Boolean) {
        prefs(c).edit().putBoolean("completed", value).apply()
    }
}

val Green = Color(0xFF146B52)
val Ink = Color(0xFF16392E)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppPage("Tu día, más simple", "DISPENSALUD") {
                Text("Todo lo importante,\na primera vista.", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = Ink)
                Text("Accede a tus recordatorios y consulta tu actividad desde la pantalla de inicio.", color = Ink)
                InfoCard("Laboratorio 14", "Widgets Android con Jetpack Glance")
                Button(onClick = { startActivity(Intent(this@MainActivity, MedicamentosActivity::class.java)) }, modifier = Modifier.fillMaxWidth()) { Text("Mis medicamentos") }
                OutlinedButton(onClick = { startActivity(Intent(this@MainActivity, HistorialActivity::class.java)) }, modifier = Modifier.fillMaxWidth()) { Text("Ver historial") }
                var message by remember { mutableStateOf("") }
                Text("EN TU PANTALLA DE INICIO", fontWeight = FontWeight.Bold, color = Green)
                OutlinedButton(onClick = { message = pin(SimpleWidget::class.java) }, modifier = Modifier.fillMaxWidth()) { Text("Añadir widget de accesos") }
                OutlinedButton(onClick = { message = pin(DispensaludWidgetReceiver::class.java) }, modifier = Modifier.fillMaxWidth()) { Text("Añadir widget Mi día") }
                if (message.isNotEmpty()) Text(message)
                Text("Datos de demostración para el laboratorio.", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
    private fun pin(receiver: Class<*>): String {
        val manager = AppWidgetManager.getInstance(this)
        return if (manager.isRequestPinAppWidgetSupported) {
            manager.requestPinAppWidget(ComponentName(this, receiver), null, null)
            "Confirma Añadir en el lanzador."
        } else "Mantén pulsada la pantalla de inicio y elige Widgets."
    }
}

class MedicamentosActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var completed by remember { mutableStateOf(DemoStore.completed(this)) }
            AppPage("Mis medicamentos", "TU RUTINA") {
                InfoCard("08:00 · Recordatorio de ejemplo", if (completed) "Registro completado" else "Pendiente de registrar")
                Text("Esta rutina usa datos ficticios para demostrar el widget.")
                Button(onClick = {
                    completed = true
                    DemoStore.setCompleted(this@MedicamentosActivity, true)
                    lifecycleScope.launch { DispensaludWidget().updateAll(this@MedicamentosActivity) }
                }, enabled = !completed, modifier = Modifier.fillMaxWidth()) { Text(if (completed) "Completado" else "Marcar como realizado") }
                OutlinedButton(onClick = { startActivity(Intent(this@MedicamentosActivity, HistorialActivity::class.java)) }, modifier = Modifier.fillMaxWidth()) { Text("Ver historial") }
                TextButton(onClick = { finish() }) { Text("Volver") }
            }
        }
    }
}

class HistorialActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var completed by remember { mutableStateOf(DemoStore.completed(this)) }
            AppPage("Historial", "TU ACTIVIDAD") {
                InfoCard(if (completed) "1 registro realizado" else "Sin registros todavía", if (completed) "Recordatorio de ejemplo · Completado" else "Tu actividad aparecerá aquí al completar el recordatorio.")
                Text("El estado se conserva al cerrar la aplicación y se comparte con el widget.")
                OutlinedButton(onClick = {
                    DemoStore.setCompleted(this@HistorialActivity, false)
                    completed = false
                    lifecycleScope.launch { DispensaludWidget().updateAll(this@HistorialActivity) }
                }, modifier = Modifier.fillMaxWidth()) { Text("Reiniciar demostración") }
                TextButton(onClick = { finish() }) { Text("Volver") }
            }
        }
    }
}

@Composable
fun AppPage(title: String, eyebrow: String, body: @Composable ColumnScope.() -> Unit) {
    MaterialTheme(colorScheme = lightColorScheme(primary = Green, background = Color(0xFFF6FAF7))) {
        Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).windowInsetsPadding(WindowInsets.systemBars).padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(eyebrow, color = Green, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Text(title, color = Ink, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            HorizontalDivider()
            body()
        }
    }
}

@Composable
fun InfoCard(title: String, subtitle: String) {
    Surface(color = Color(0xFFE1F1E7), shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, fontWeight = FontWeight.Bold, color = Ink, style = MaterialTheme.typography.titleLarge)
            Text(subtitle, color = Ink)
        }
    }
}
