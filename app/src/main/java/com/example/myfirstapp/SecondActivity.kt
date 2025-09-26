package com.example.myfirstapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myfirstapp.ui.theme.MyFirstAppTheme

class SecondActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val receivedText = intent.getStringExtra(Intent.EXTRA_TEXT) ?:  getString(R.string.title_screen_2)

        setContent {
            SecondScreen(receivedText = receivedText)
        }
    }
}

@Composable
fun SecondScreen(receivedText: String) {
    val context = LocalContext.current

    SecondScreen(
        receivedText = receivedText,
        toThird = { clickToThirdFromSecond(context, receivedText)},
        toFirst = { clickToFirstFromSecond(context, receivedText)}
    )
}

@Composable
fun SecondScreen(
    receivedText: String,
    toThird: () -> Unit,
    toFirst: () -> Unit
) {
    MyFirstAppTheme {

        Column(modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {

            Text(text = receivedText,
                style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = toThird,
                modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.open_third_second))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = toFirst,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.open_first_second))
            }

        }
    }
}

public fun clickToThirdFromSecond(context: android.content.Context, text: String){
    NavigationActivity.navigateActivity(context, text,ThirdActivity::class.java)
}

public fun clickToFirstFromSecond(context: android.content.Context, text: String){
    NavigationActivity.navigateActivity(context, text, MainActivity::class.java)
}


