package com.example.dwprofiledemoa

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.dwprofiledemoa.ui.components.RoundButton

class CodabarActivity: ComponentActivity() {

    companion object {
        val TAG = "CodabarActivity"
    }

    val viewModel = CodabarViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.handleOnCreate(this)
        setContent {
            RootView(this)
        }
        Log.d(TAG, "onCreate")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
    }

    override fun onResume() {
        super.onResume()
        viewModel.handleOnResume(this)
        Log.d(TAG, "onResume")
    }

    override fun onPause() {
        super.onPause()
        viewModel.handleOnPause(this)
        Log.d(TAG, "onPause")
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.handleOnDestroy(this)
        Log.d(TAG, "onDestroy")
    }

    @Composable
    fun RootView(context: Context) {
        Column(modifier = Modifier
            .padding(
                horizontal = 25.dp,
                vertical = 25.dp
            )
        ) {
            Text("Codabar(NW7)", color = Color.Blue)
            RoundButton("Scan Code39") {
                startActivity(Intent(context, Code39Activity::class.java))
            }
        }
    }
}