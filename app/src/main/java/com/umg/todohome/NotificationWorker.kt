package com.umg.todohome

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import com.umg.todohome.activityAddFamily.Companion.idFamily
import com.umg.todohome.activityDataUser.Companion.userName
import java.math.BigInteger
import java.security.MessageDigest
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date

class NotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        checkForNewNotifications(applicationContext, "Message")
        checkForNewNotifications(applicationContext, "Expense")
        checkForNewNotifications(applicationContext, "Task")

        // Return success to indicate the work finished successfully
        return Result.success()
    }

    private fun checkForNewNotifications(context: Context, type: String) {
        val dbNotifications = FirebaseFirestore.getInstance()
        val sharedPreferences = context.getSharedPreferences("notification_prefs", Context.MODE_PRIVATE)

        dbNotifications.collection("notifications/$idFamily/$idFamily").whereEqualTo("id", type)
            .get()
            .addOnSuccessListener { documents ->
                for (notification in documents) {
                    val user = notification.getString("user") ?: continue

                    if (notification.exists() && user != userName) {
                        val notificationId = notification.id
                        val nameParts = user.split("\\s+".toRegex())
                        val name = if (nameParts.size > 1) {
                            "${nameParts[0]} ${nameParts[1]}"
                        } else {
                            user
                        }

                        val title = notification.getString("title").orEmpty()
                        val text = if (type == "Expense") {
                            val amount = notification.get("cant").toString().toFloat()
                            val formattedAmount = DecimalFormat("Q###,###.00").format(amount)

                            "$name: ${notification.getString("text").orEmpty()} $formattedAmount"
                        } else {
                            "$name: ${notification.getString("text").orEmpty()}"
                        }

                        val contentHash = hashContent(title + text)
                        val hashKey = "hash_$notificationId"
                        val storedHash = sharedPreferences.getString(hashKey, "")

                        if (storedHash != contentHash) {
                            sendNotification(
                                context, title, text,
                                SimpleDateFormat("yyMMddmmss").format(Date()).toBigInteger(), R.mipmap.ic_launcher
                            )
                            sharedPreferences.edit().putString(hashKey, contentHash).apply()
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting documents: ", exception)
            }
    }


    fun hashContent(content: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(content.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
    private fun sendNotification(context: Context, title: String, text: String,
                                 notificationId: BigInteger, icon: Int){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            // Creación del canal de mensajes
            val channelId = "ToDoHome"
            val channelName = "ToDoHome"
            // Definir la prioridad del mensaje
            val importance = NotificationManager.IMPORTANCE_HIGH
            // Crear el canal para los envíos
            val channel = NotificationChannel(channelId, channelName, importance)

            // Crear el administrador de notificaciones
            val manager = context.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)

            // Crear la notificación
            val notification =
                NotificationCompat.Builder(context, channelId).also{ noti->
                    noti.setContentTitle(title)
                    noti.setContentText(text)
                    noti.setSmallIcon(icon)
                }.build()

            // Crear manejador de envíos y lanzar la notificación
            val notificationManager = NotificationManagerCompat.from(context)
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            notificationManager.notify(notificationId.toInt(), notification)
        }
    }
}



