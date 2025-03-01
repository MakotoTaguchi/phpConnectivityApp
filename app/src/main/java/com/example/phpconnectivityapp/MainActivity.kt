package com.example.phpconnectivityapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.phpconnectivityapp.ui.theme.PhpConnectivityAppTheme
import okhttp3.*
import java.io.IOException

class MainActivity : ComponentActivity() {
    private val apiUrl = "http://10.0.2.2:8080" // エミュレーター用のURL
    private val client = OkHttpClient.Builder().build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PhpConnectivityAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ApiScreen(apiUrl, client, Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun ApiScreen(apiUrl: String, client: OkHttpClient, modifier: Modifier = Modifier) {
    var apiResponse by remember { mutableStateOf("Loading...") }

    LaunchedEffect(Unit) {
        fetchData(apiUrl, client) { response ->
            apiResponse = response
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "API Response:", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = apiResponse, style = MaterialTheme.typography.bodyLarge)
    }
}

fun fetchData(apiUrl: String, client: OkHttpClient, onResult: (String) -> Unit) {
    val request = Request.Builder().url(apiUrl).build()
    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            onResult("Error: ${e.message}")
        }

        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                val responseData = response.body?.string() ?: "No data"
                onResult(responseData)
            } else {
                onResult("Error: ${response.code}")
            }
        }
    })
}

@Preview(showBackground = true)
@Composable
fun ApiScreenPreview() {
    PhpConnectivityAppTheme {
        ApiScreen("http://10.0.2.2:8080", OkHttpClient())
    }
}