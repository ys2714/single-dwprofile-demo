package com.example.dwprofiledemoa

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.ViewModel
import com.zebra.emdk_kotlin_wrapper.dw.DataWedgeHelper

class MainViewModel: ViewModel() {

    fun handleOnCreate(context: Context) {

    }

    fun handleOnResume(context: Context) {
        // 毎回Main画面が開く前にトリガーボタンを有効する
        // すべてのDecoderも無効する
        DataWedgeHelper.switchScannerParams(context, Bundle().apply {
            putString("barcode_trigger_mode", "0")
            putString("decoder_code39", "false")
            putString("decoder_ean13", "false")
            putString("decoder_codabar", "false")
        })
    }

    fun handleOnPause(context: Context) {

    }

    fun handleOnDestroy(context: Context) {

    }
}