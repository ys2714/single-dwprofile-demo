package com.example.dwprofiledemoa

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zebra.emdk_kotlin_wrapper.dw.DataWedgeHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Code39ViewModel: ViewModel() {

    fun handleOnCreate(context: Context) {

    }

    fun handleOnResume(context: Context) {
        registerDataListener(context)
        // 毎回画面が開く前にトリガーボタンを有効する
        // EAN13のDecoderだけを有効する
        // 100ms待たないと他のアプリから復帰する場合は、Decoderが効かない
        viewModelScope.launch {
            delay(MyApplication.WAIT_BEFORE_SWITCH_PARAMS)
            DataWedgeHelper.switchScannerParams(context, Bundle().apply {
                putString("barcode_trigger_mode", "1")
                putString("decoder_code39", "true")
                putString("decoder_ean13", "false")
                putString("decoder_codabar", "false")
            })
        }
    }

    fun handleOnPause(context: Context) {
        // 毎回画面が閉じる前にトリガーボタンを無効する
        // すべてのDecoderを無効する
        DataWedgeHelper.switchScannerParams(context, Bundle().apply {
            putString("barcode_trigger_mode", "0")
            putString("decoder_code39", "false")
            putString("decoder_ean13", "false")
            putString("decoder_codabar", "false")
        })
        unregisterDataListener()
    }

    fun handleOnDestroy(context: Context) {

    }

    private var scanDataListener: DataWedgeHelper.ScanDataListener? = null

    private fun registerDataListener(context: Context) {
        if (scanDataListener == null) {
            scanDataListener = object : DataWedgeHelper.ScanDataListener {
                override fun onData(type: String, value: String, timestamp: String) {
                    Toast.makeText(context, "$type:$value", Toast.LENGTH_LONG).show()
                }

                override fun getID(): String {
                    return "Code39ViewModel"
                }

                override fun onDisposal() {
                    scanDataListener = null
                }
            }

            DataWedgeHelper.addScanDataListener(scanDataListener!!)
        }
    }

    private fun unregisterDataListener() {
        if (scanDataListener != null) {
            DataWedgeHelper.removeScanDataListener(scanDataListener!!)
        }
    }
}