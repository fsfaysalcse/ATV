package com.swiftie.androidtv.ui.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.swiftie.androidtv.data.model.AppInfo
import com.swiftie.androidtv.ui.theme.ProductSans

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AppItem(app: AppInfo, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(170.dp)
            .height(100.dp)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(5.dp)
            )
            .clickable { onClick() }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier, // Launch the app when clicked
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            app.icon?.let {
                Image(
                    bitmap = it.bitmap.asImageBitmap(),
                    contentDescription = app.label,
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }

            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = app.label,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black,
                fontFamily = ProductSans,
                fontWeight = FontWeight.Medium,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }
}