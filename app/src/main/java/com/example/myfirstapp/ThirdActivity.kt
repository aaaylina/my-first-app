package com.example.myfirstapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myfirstapp.ui.theme.MyFirstAppTheme

class ThirdActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val receivedText = intent.getStringExtra(Intent.EXTRA_TEXT) ?: getString(R.string.title_screen_3)

        setContent {
            ThirdScreen(receivedText = receivedText)
        }
    }
}

@Composable
fun ThirdScreen(receivedText: String) {

    val context = LocalContext.current

    ThirdScreen(
        receivedText = receivedText,
        toFirst = { clickToFirstFromThird(context, receivedText)}
    )
}

@Composable
fun ThirdScreen(
    receivedText: String,
    toFirst: () -> Unit
) {
    MyFirstAppTheme {

        Column(modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {

            Text(text = receivedText, style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = toFirst,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.open_first_third))
            }

        }
    }
}

public fun clickToFirstFromThird(context: android.content.Context, text: String){

    val intent = Intent(context, MainActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT

    if (text.isNotEmpty()) {
        intent.putExtra(Intent.EXTRA_TEXT, text)
    }

    context.startActivity(intent)

}