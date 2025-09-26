package com.example.myfirstapp

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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myfirstapp.ui.theme.MyFirstAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen() {
    var text by remember { mutableStateOf("") }
    val context = LocalContext.current

    MainScreen(
        text = text,
        onTextChange = { newText -> text = newText },
        toSecond = { clickToSecondFromFirst(context, text) },
        toThird = { clickToThirdFromFirst(context, text) }
    )
}

@Composable
fun MainScreen(text: String,
    onTextChange: (String) -> Unit,
    toSecond: () -> Unit,
    toThird: () -> Unit
) {
    MyFirstAppTheme {

        Column(modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {

            TextField(value = text, onValueChange = onTextChange,
                label = { Text(stringResource(R.string.enter_text)) },
                modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = toSecond, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.open_second_first))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = toThird, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.open_third_first))
            }

        }

    }
}


private fun clickToSecondFromFirst(context: android.content.Context, text: String){
    NavigationActivity.navigateActivity(context, text, SecondActivity::class.java)
}

private fun clickToThirdFromFirst(context: android.content.Context, text: String){
    NavigationActivity.navigateActivity(context, text, ThirdActivity::class.java)
}

