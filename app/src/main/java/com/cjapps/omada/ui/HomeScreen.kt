package com.cjapps.omada.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.cjapps.omada.ui.theme.OmadaTheme

@Composable
fun HomeScreen(
    modifier: Modifier
) {
    Greeting(
        name = "Android",
        modifier = modifier
    )
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    OmadaTheme {
        Greeting("Android")
    }
}