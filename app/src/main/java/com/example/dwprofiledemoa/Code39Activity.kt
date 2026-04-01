package com.example.dwprofiledemoa

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

class Code39Activity: ComponentActivity() {

    val viewModel = Code39ViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.handleOnCreate(this)
        setContent {
            RootView(this)
        }
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

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            viewModel.handleGainFocus(this)
        } else {
            viewModel.handleLostFocus(this)
        }
    }

    @Composable
    fun RootView(context: Context) {
        Column(modifier = Modifier
            .padding(
                horizontal = 25.dp,
                vertical = 25.dp
            )
        ) {
            Text("Code39", color = Color.Blue)
        }
    }
}