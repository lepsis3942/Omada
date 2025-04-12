package com.cjapps.omada.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.cjapps.omada.ui.theme.OmadaTheme

@Composable
fun HomeScreen(
    modifier: Modifier
) {
    val viewModel = hiltViewModel<HomeScreenViewModel>()
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