package com.knotapi.demo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.knotapi.cardonfileswitcher.CardOnFileSwitcher
import com.knotapi.cardonfileswitcher.OnSessionEventListener
import com.knotapi.cardonfileswitcher.model.Customization

class MainActivity : AppCompatActivity(), OnSessionEventListener {
    var cardOnFileSwitcher: CardOnFileSwitcher? = null
    var button: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //
        button = findViewById(R.id.button)
        button?.setOnClickListener(View.OnClickListener { view: View? ->
            val customization = Customization()
            customization.textColor = "#fff000"
            customization.primaryColor = "#ff0000"
            customization.companyName = "Facebook"
            cardOnFileSwitcher = CardOnFileSwitcher.getInstance()
            cardOnFileSwitcher?.init(this, this, customization)
            cardOnFileSwitcher?.openCardOnFileSwitcher(
                "fc3fe2a1-0795-4e33-84c6-5f34ab96f61a",
                intArrayOf(1, 3),
                false
            )
        })
    }

    override fun onInvalidSession(sessionId: String?, errorMessage: String?) {
        Log.d("onInvalidSession", errorMessage!!)
    }

    override fun onSuccess(merchant: String?) {
        Log.d("onSuccess from main", merchant!!)
    }

    override fun onError(errorCode: String?, errorMessage: String?) {
        Log.d("onError", "$errorCode $errorMessage")
    }

    override fun onExit() {
        Log.d("onExit", "exit")
    }

    override fun onFinished() {
        Log.d("onFinished", "finished")
    }

    override fun onEvent(eventName: String?, merchantName: String?) {
        Log.d("onEvent", "$eventName $merchantName")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("onActivityResult", "$requestCode $resultCode")
    }
}