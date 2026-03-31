package com.example.dwprofiledemoa

import android.app.Application
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.zebra.emdk_kotlin_wrapper.dw.DataWedgeHelper
import com.zebra.emdk_kotlin_wrapper.emdk.EMDKHelper

class MyApplication: Application() {

    companion object {
        const val TAG = "MyApplication"
        const val PROFILE_JSON_FILE = "profile_template.json"
        const val PROFILE_NAME_MAIN = "profile_main"

        const val WAIT_BEFORE_SWITCH_PARAMS: Long = 100

        var needWaitScannerIdleWhenAppResumeFromBackground = false
    }

    override fun onCreate() {
        super.onCreate()
        // EMDKの初期化を行います,MXAPIを呼び出す前に必ず行う事,DataWedgeだけを運用する時は不要
        // EMDKManager.getInstanceAsync()
        // https://techdocs.zebra.com/emdk-for-android/9-1/guide/xmlresponseguide/
        EMDKHelper.shared.prepare(this) { emdkSuccess ->
            if (emdkSuccess) {
                // DataWedgeの初期化を行います
                // com.symbol.datawedge.api.ENABLE_DATAWEDGE
                // https://techdocs.zebra.com/datawedge/latest/guide/api/enabledatawedge/
                DataWedgeHelper.prepare(this) { dwSuccess ->
                    if (dwSuccess) {
                        // アプリ全体唯一のProfileを作成します,ハードウェアトリガーを禁止します
                        // com.symbol.datawedge.api.SET_CONFIG
                        // https://techdocs.zebra.com/datawedge/15-0/guide/api/setconfig/
                        DataWedgeHelper.configWithJSON(
                            this,
                            PROFILE_JSON_FILE,
                            mapOf(
                                "PROFILE_NAME" to PROFILE_NAME_MAIN,
                                "beam_timer" to "6000",
                                "aim_type" to "0",             //0:trigger
                                "barcode_trigger_mode" to "0", //0:disabled 1:enabled
                                "aim_timer" to "6000",
                                "scanner_input_enabled" to "true",
                                "workflow_input_enabled" to "false"
                            )
                        ) { createProfileSuccess ->
                            if (createProfileSuccess) {
                                // 作成したProfileを永久にアプリと紐付け、この後SWITCH_TO_PROFILEの呼び出すは不要
                                // https://techdocs.zebra.com/datawedge/11-4/guide/api/setconfig/#parameters
                                // 上記ドキュメントのAPP_LISTの所です
                                DataWedgeHelper.bindProfileToApp(this, PROFILE_NAME_MAIN, this.packageName) { bindSuccess ->
                                    if (bindSuccess) {
                                        Log.d(TAG, "profile A bind success")
                                    } else {
                                        throw RuntimeException("profile A bind failed")
                                    }
                                }
                            } else {
                                throw RuntimeException("profile A create failed")
                            }
                        }
                    } else {
                        throw RuntimeException("DataWedgeHelper prepare failed")
                    }
                }
            } else {
                throw RuntimeException("EMDKHelper init failed")
            }
        }

        ProcessLifecycleOwner.get().lifecycle.addObserver(
            object : LifecycleEventObserver {
                override fun onStateChanged(
                    source: LifecycleOwner,
                    event: Lifecycle.Event
                ) {
                    if (event == Lifecycle.Event.ON_STOP) {
                        needWaitScannerIdleWhenAppResumeFromBackground = true
                    }
                }
            }
        )
    }
}