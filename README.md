# Laboratorio 14: Widgets con Jetpack Glance

Proyecto Android independiente de demostración para Dispensalud. No modifica el proyecto DispensaludCRUD existente. Todos los recordatorios son ficticios.

## Ejecutar

1. Abrir esta carpeta en Android Studio y sincronizar Gradle.
2. Usar JDK 25 y Android SDK 37 (la configuración del entorno instalado).
3. Ejecutar `./gradlew :app:assembleDebug` o `gradlew.bat :app:assembleDebug` en Windows.
4. Instalar el APK y abrir **Dispensalud · Lab 14**.
5. Pulsar **Añadir widget de accesos** o **Añadir widget Mi día** y confirmar en el lanzador. También pueden añadirse desde el selector de widgets de Android.

## Parte guiada

- Dependencias `glance-appwidget:1.1.0` y `glance-material3:1.1.0`.
- Compose habilitado con el complemento de Kotlin, compatible con la herramienta instalada. No se mezcla con el compilador antiguo 1.5 indicado en la guía.
- `SimpleWidget` registra su proveedor en el manifiesto y muestra dos botones: Página principal y Medicamentos.
- Cada botón abre una actividad diferente (`MainActivity` y `MedicamentosActivity`).
- Los metadatos residen en `res/xml`; el diseño inicial pertenece a `res/layout`. Se corrige así la referencia inconsistente a `@xml/simple_widget_layout` de la guía.

## Ejercicio final

**Dispensalud · Mi día** muestra un recordatorio ficticio y dos acciones. **Realizado** guarda el estado sin abrir la aplicación, actualiza todas las instancias del widget y cambia a **Ver registro**. Este botón abre el historial. **Mi rutina** abre directamente Medicamentos. El historial permite reiniciar la demostración. La actualización sincroniza SharedPreferences con PreferencesGlanceStateDefinition mediante updateAppWidgetState y updateAll; currentState observa el cambio dentro de la composición de Glance.

Se revisó la sección Widgets de Communication en el Android UI Kit abierto en Figma:
https://www.figma.com/design/dgJZNf2hgc5xveupnudoLc/Android-UI-Kit--Community-?node-id=337-26209

Criterios aplicados: contenido conciso, título identificable, jerarquía tipográfica, espaciado, esquinas redondeadas, acciones visibles y estados pendiente/completado/sin registros. El proveedor permite redimensionar el widget a partir de su tamaño mínimo.

La demostración usa un recordatorio único y persistencia local en SharedPreferences. No se conecta a una cuenta, no dispensa medicamentos ni implementa un motor de horarios reales.

## Reflexión

Los widgets permiten consultar información y ejecutar acciones frecuentes desde la pantalla de inicio. En Dispensalud reducen los pasos para revisar una rutina y registrar su cumplimiento. Mostrar un estado claro ayuda a distinguir lo pendiente de lo realizado y el acceso directo al historial mantiene el contexto de la acción.

## Observación técnica

La parte más delicada fue coordinar el estado entre el widget y las actividades: Glance genera RemoteViews y no comparte directamente el estado `remember` de Compose. La primera prueba detectó que guardar SharedPreferences y llamar solo a `updateAll` no actualizaba una composición activa. Se corrigió sincronizando también el estado de Glance con `updateAppWidgetState` y observándolo mediante `currentState`. Se repitió la prueba y el widget cambió inmediatamente al estado completado. La acción es idempotente: varios toques no crean registros duplicados.

También se corrigieron las rutas de recursos de la guía y se adaptó la configuración del compilador a Kotlin moderno. El historial representa un registro de demostración, no una bitácora médica real.

## Uso de IA

La IA se utilizó para interpretar la guía, revisar la referencia visual de Figma, escribir el proyecto Kotlin y los recursos XML, compilar, diagnosticar problemas y preparar las evidencias. Esta explicación describe el trabajo asistido realizado, sin atribuir al alumno experiencias personales que no se hayan indicado.

## Fuentes técnicas

- https://developer.android.com/develop/ui/compose/glance/create-app-widget
- https://developer.android.com/develop/ui/views/appwidgets/overview
- Guía GLAB-S14-BPAREJA-2026-01 proporcionada por el usuario.

## Repositorio

Esta carpeta incluye un repositorio Git local con commits por avance. No se ha publicado un repositorio remoto.

## Verificación realizada

- `assembleDebug` y `lintDebug`: correctos; lint reporta 0 errores y 23 advertencias.
- Widgets añadidos al lanzador del Pixel 7.
- Página principal y Medicamentos abren sus actividades desde el widget guiado.
- Realizado actualiza el widget sin abrir la app; Ver registro muestra el historial.
- Reiniciar demostración restaura el estado vacío y el widget pendiente.
- Tras detener y volver a abrir el proceso, se conserva el registro; Mi rutina muestra el estado completado.
- Videos reales sin audio: `01-widget-accesos.mp4` y `02-widget-dispensalud.mp4`, entregados junto al proyecto. Se recortaron esperas y se aceleró el video de accesos para mantenerlo breve.
