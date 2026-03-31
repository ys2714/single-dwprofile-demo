package com.example.dwprofiledemoa

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.dwprofiledemoa.ui.components.RoundButton
import com.example.dwprofiledemoa.ui.theme.DWProfileDemoATheme

class MainActivity : ComponentActivity() {

    private val viewModel = MainViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DWProfileDemoATheme {
                RootView(this)
            }
        }
        viewModel.handleOnCreate(this)
    }

    override fun onResume() {
        super.onResume()
        viewModel.handleOnResume(this)
    }

    override fun onPause() {
        super.onPause()
        viewModel.handleOnPause(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.handleOnDestroy(this)
    }

    @Composable
    fun RootView(context: Context) {
        Column(modifier = Modifier
            .padding(
                horizontal = 25.dp,
                vertical = 25.dp
            )
        ) {
            Text("Demo A", color = Color.Blue)
            RoundButton("Scan Code39") {
                startActivity(Intent(context, Code39Activity::class.java))
            }
            RoundButton("Scan Codabar(NW7)") {
                startActivity(Intent(context, CodabarActivity::class.java))
            }
        }
    }
}
