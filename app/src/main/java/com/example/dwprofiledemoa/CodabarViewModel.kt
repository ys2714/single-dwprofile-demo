package com.example.dwprofiledemoa

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.ViewModel
import com.zebra.emdk_kotlin_wrapper.dw.DWAPI
import com.zebra.emdk_kotlin_wrapper.dw.DataWedgeHelper

class CodabarViewModel: ViewModel() {

    private var appLifecycleListener: LifecycleEventObserver? = null

    private var scanDataListener: DataWedgeHelper.ScanDataListener? = null
    private var statusListener: DataWedgeHelper.SessionStatusListener? = null

    private var needWaitScannerIdle = false

    fun handleOnCreate(context: Context) {
        registerAppLifecycleListener()
    }

    fun handleOnResume(context: Context) {
        DataWedgeHelper.getScannerStatus(context, 0) { status ->
            if (status == DWAPI.ScannerStatus.IDLE || status == DWAPI.ScannerStatus.WAITING) {
                // 通常の場合はPROFILEの切り替えがないため、そのままSWITCH_SCANER_PARAMSでスキャナーを設定
                // 画面が開く前にトリガーボタンを有効する
                // Codabar(NW7)のDecoderだけを有効する
                DataWedgeHelper.switchScannerParams(context, Bundle().apply {
                    putString("barcode_trigger_mode", "1")
                    putString("decoder_code39", "false")
                    putString("decoder_codabar", "true")
                })
            } else {
                // 何らかの原因で
                needWaitScannerIdle = true
            }
        }
        registerDataListener(context)
        registerStatusListener(context) { type, value ->
            if (DWAPI.NotificationType.SCANNER_STATUS == type &&
                (DWAPI.ScannerStatus.IDLE.value == value || DWAPI.ScannerStatus.WAITING.value == value)) {
                if (needWaitScannerIdle) {
                    needWaitScannerIdle = false
                    // アプリはバックグラウンドから復帰する時は、自動でSWITCH_TO_PROFILEが呼ばれ
                    // スキャナーのステータスをIDLEかWAITINGになるまでSWITCH_SCANER_PARAMSを行わない
                    DataWedgeHelper.switchScannerParams(context, Bundle().apply {
                        putString("barcode_trigger_mode", "1")
                        putString("decoder_code39", "false")
                        putString("decoder_codabar", "true")
                    })
                }
            }
        }
    }

    fun handleOnPause(context: Context) {
        unregisterDataListener()
        unregisterStatusListener(context)
        // 毎回画面が閉じる前にトリガーボタンを無効する
        // すべてのDecoderを無効する
        DataWedgeHelper.switchScannerParams(context, Bundle().apply {
            putString("barcode_trigger_mode", "0")
            putString("decoder_code39", "false")
            putString("decoder_codabar", "false")
        })
    }

    fun handleOnDestroy(context: Context) {
        unregisterAppLifecycleListener()
    }

    fun handleGainFocus(context: Context) {

    }

    fun handleLostFocus(context: Context) {
        // ■ボタンを押して、他のアプリに切り替えずに、元のアプリに戻る際
        // DataWedge側自動でProfileを無効と有効をするから
        // スキャナーのステータスをIDLEかWAITINGになるまで待つ必要がある
        needWaitScannerIdle = true
    }

    // ステータス監視系

    private fun registerAppLifecycleListener() {
        if (appLifecycleListener == null) {
            appLifecycleListener = object : LifecycleEventObserver {
                override fun onStateChanged(
                    source: LifecycleOwner,
                    event: Lifecycle.Event
                ) {
                    if (event == Lifecycle.Event.ON_STOP) {
                        // アプリがバックグラウンドに入ると、まだ復帰する時はDataWedge側自動でProfileを切り替えるから
                        // スキャナーのステータスをIDLEかWAITINGになるまで待つ必要がある
                        needWaitScannerIdle = true
                    }
                }
            }
            ProcessLifecycleOwner.get().lifecycle.addObserver(appLifecycleListener!!)
        }
    }

    private fun unregisterAppLifecycleListener() {
        if (appLifecycleListener != null) {
            ProcessLifecycleOwner.get().lifecycle.removeObserver(appLifecycleListener!!)
        }
    }

    private fun registerDataListener(context: Context) {
        if (scanDataListener == null) {
            scanDataListener = object : DataWedgeHelper.ScanDataListener {
                override fun onData(type: String, value: String, timestamp: String) {
                    Toast.makeText(context, "$type:$value", Toast.LENGTH_LONG).show()
                }

                override fun getID(): String {
                    return "CodabarViewModel"
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

    private fun registerStatusListener(context: Context, onValue: (DWAPI.NotificationType, String) -> Unit) {
        if (statusListener != null) {
            return
        }
        statusListener = object : DataWedgeHelper.SessionStatusListener {
            override fun onStatus(
                type: DWAPI.NotificationType,
                status: String,
                profileName: String
            ) {
                onValue(type, status)
            }

            override fun getID(): String {
                return hashCode().toString()
            }

            override fun onDisposal() {
                statusListener = null
            }
        }.also {
            DataWedgeHelper.addSessionStatusListener(it)
            DataWedgeHelper.enableScannerStatusNotification(context)
        }
    }

    private fun unregisterStatusListener(context: Context) {
        if (statusListener != null) {
            DataWedgeHelper.removeSessionStatusListener(statusListener!!)
            DataWedgeHelper.disableScannerStatusNotification(context)
            statusListener = null
        }
    }
}