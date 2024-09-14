package com.swiftie.androidtv

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.swiftie.androidtv.data.model.AppInfo
import com.swiftie.androidtv.ui.theme.ProductSans
import com.swiftie.androidtv.ui.theme.SwiftieTVTheme
import com.swiftie.androidtv.ui.widgets.AppItem
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    private lateinit var appUpdateReceiver: BroadcastReceiver

    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SwiftieTVTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    HomeScreen()
                }
            }
        }

        // Register BroadcastReceiver to listen for package changes (app installs/uninstalls)
        val intentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addDataScheme("package")
        }

        appUpdateReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                // Trigger UI update when an app is added or removed
                intent?.let {
                    if (it.action == Intent.ACTION_PACKAGE_ADDED || it.action == Intent.ACTION_PACKAGE_REMOVED) {
                        // Re-fetch the installed apps
                        setContent {
                            SwiftieTVTheme {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(MaterialTheme.colorScheme.background)
                                ) {
                                    HomeScreen() // Update the HomeScreen with the new app list
                                }
                            }
                        }
                    }
                }
            }
        }

        registerReceiver(appUpdateReceiver, intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(appUpdateReceiver) // Unregister the receiver when the activity is destroyed
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val packageManager = context.packageManager
    var appList by remember { mutableStateOf(listOf<AppInfo>()) }


    appList.forEach {
        Log.d("dsffdgsgfds", "HomeScreen: ${it.label}")
    }

    var currentTime by remember { mutableStateOf(LocalDateTime.now()) }

    // Update the current time every second
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = LocalDateTime.now()
            delay(1000L)
        }
    }

    // Fetch installed apps when the screen is first created
    LaunchedEffect(Unit) {
        appList = getInstalledApps(packageManager)
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 32.dp,
                    vertical = 5.dp
                ),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier
                    .padding(top = 50.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 32.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.app_name),
                    color = Color.White,
                    fontSize = 52.sp,
                    fontFamily = ProductSans,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                )

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(
                            color = Color.White,
                            shape = CircleShape
                        )
                        .clickable {
                            val intent = Intent(Settings.ACTION_SETTINGS)
                            context.startActivity(intent)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_settings),
                        contentDescription = "Settings",
                        tint = Color.Black
                    )
                }
            }

            val timeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss a")
            val dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")

            val dateTimeTextBuilder = buildAnnotatedString {
                withStyle(style = MaterialTheme.typography.titleLarge.toSpanStyle()) {
                    append(timeFormatter.format(currentTime))
                }
                append("\n")
                withStyle(style = MaterialTheme.typography.bodyLarge.toSpanStyle()) {
                    append(dateFormatter.format(currentTime))
                }
            }

            Text(
                text = dateTimeTextBuilder,
                color = Color.White,
                fontFamily = ProductSans,
                fontWeight = FontWeight.Normal,
                fontSize = 32.sp,
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 5.dp)
            )

            Spacer(modifier = Modifier.size(50.dp))

            Text(
                text = "Installed Apps",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontFamily = ProductSans,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
            ) {
                items(
                    items = appList,
                    key = { app -> app.packageName }
                ) { app ->
                    AppItem(app) { launchApp(context, app.packageName) }
                }
            }
        }
    }
}

// Function to retrieve the list of installed apps
fun getInstalledApps(packageManager: PackageManager): List<AppInfo> {
    val intent = Intent(Intent.ACTION_MAIN, null).apply {
        addCategory(Intent.CATEGORY_LAUNCHER)
    }
    val installedApps = packageManager.queryIntentActivities(intent, 0)
    return installedApps.map {
        val appLabel = it.loadLabel(packageManager).toString()
        val appPackage = it.activityInfo.packageName
        val appIcon = it.loadIcon(packageManager)

        // Convert to Bitmap if necessary
        val appIconBitmap = drawableToBitmap(appIcon)

        AppInfo(appLabel, appPackage, BitmapDrawable(appIconBitmap))
    }
}

fun drawableToBitmap(drawable: Drawable): Bitmap {
    return when (drawable) {
        is BitmapDrawable -> drawable.bitmap
        is AdaptiveIconDrawable -> {
            // Adaptive icons need to be handled separately
            val bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }

        else -> {
            // For any other type of drawable, fallback
            val bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }
}


// Function to launch an app
fun launchApp(context: Context, packageName: String) {
    val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
    if (launchIntent != null) {
        context.startActivity(launchIntent)
    }
}
