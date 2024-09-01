package dev.notyouraverage.otpcourier.composables

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.notyouraverage.otpcourier.R
import dev.notyouraverage.otpcourier.services.foreground.MasterService

@Composable
fun MainScreen() {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.home), contentDescription = "Login Image",
            modifier = Modifier.size(250.dp)
        )

        Text(text = "Foreground Service", fontWeight = FontWeight.Bold, fontSize = 20.sp)

        Button(
            onClick = {
                Intent(context, MasterService::class.java).also {
                    it.setAction(MasterService.START_SELF)
                    context.startService(it)
                }
            },
            modifier = Modifier
                .width(100.dp)
                .height(45.dp)
                .clip(
                    RoundedCornerShape(10.dp)
                )
        ) {
            Text(text = "Start Foreground Service", fontSize = 15.sp)
        }

        Button(
            onClick = {
                Intent(context, MasterService::class.java).also {
                    it.setAction(MasterService.STOP_SELF)
                    context.startService(it)
                }
            },
            modifier = Modifier
                .width(100.dp)
                .height(45.dp)
                .clip(
                    RoundedCornerShape(10.dp)
                )
        ) {
            Text(text = "Stop Foreground Service", fontSize = 15.sp)
        }


    }
}
