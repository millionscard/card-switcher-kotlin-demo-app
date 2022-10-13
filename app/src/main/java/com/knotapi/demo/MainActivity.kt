package com.knotapi.demo

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.knotapi.cardonfileswitcher.CardOnFileSwitcher
import com.knotapi.cardonfileswitcher.Environment
import com.knotapi.cardonfileswitcher.OnSessionEventListener
import com.knotapi.cardonfileswitcher.SubscriptionCanceler
import com.knotapi.cardonfileswitcher.model.Customization
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), OnSessionEventListener {

    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressDialog = ProgressDialog(this)
        progressDialog?.setMessage("Please wait...")
        progressDialog?.setCancelable(false)

        btnOpenCardSwitcher.setOnClickListener {
            callCreateUserAPI(false)
        }

        btnOpenSubscriptionCanceller.setOnClickListener {
            callCreateUserAPI(true)
        }
    }

    private fun callCreateUserAPI(openSubCanceller: Boolean) {
        val createUserRequest = CreateUserRequest()
        createUserRequest.first_name = "Nikunj"
        createUserRequest.last_name = "Patel"
        createUserRequest.email = "production@knotapi.com"
        createUserRequest.phone_number = "+18024687679"
        createUserRequest.password = "password"
        createUserRequest.address1 = "348 W 57th St"
        createUserRequest.address2 = "#367"
        createUserRequest.state = "NY"
        createUserRequest.city = "New York"
        createUserRequest.postal_code = "10019"

        progressDialog?.show()
        val call: Call<CreateUserResponse>? =
            RetrofitClient.instance?.myApi?.createUserAPI(createUserRequest)
        call?.enqueue(object : Callback<CreateUserResponse?> {
            override fun onResponse(
                call: Call<CreateUserResponse?>,
                response: Response<CreateUserResponse?>
            ) {
                val createUserResponse = response.body()
                callCreateSessionAPI(createUserResponse?.token, openSubCanceller)
            }

            override fun onFailure(call: Call<CreateUserResponse?>, t: Throwable) {
                progressDialog?.hide()
                Toast.makeText(applicationContext, "An error has occurred", Toast.LENGTH_LONG)
                    .show()
            }
        })
    }

    private fun callCreateSessionAPI(token: String?, openSubCanceller: Boolean) {
        val call: Call<CreateSessionResponse>? =
            RetrofitClient.instance?.myApi?.createSessionAPI("Bearer $token")
        call?.enqueue(object : Callback<CreateSessionResponse?> {
            override fun onResponse(
                call: Call<CreateSessionResponse?>,
                response: Response<CreateSessionResponse?>
            ) {
                val createSessionResponse = response.body()
                progressDialog?.hide()
                if (openSubCanceller) {
                    openSubscriptionCanceller(createSessionResponse?.session)
                } else {
                    openCardSwitcher(createSessionResponse?.session)
                }
            }

            override fun onFailure(call: Call<CreateSessionResponse?>, t: Throwable) {
                progressDialog?.hide()
                Toast.makeText(applicationContext, "An error has occurred", Toast.LENGTH_LONG)
                    .show()
            }
        })
    }

    private fun openCardSwitcher(sessionID: String?) {
        val customization = Customization()
        customization.textColor = "#ffffff"
        customization.primaryColor = "#000000"
        customization.companyName = "Millions"

        val cardOnFileSwitcher = CardOnFileSwitcher.getInstance()
        cardOnFileSwitcher.init(this, "1c0a49cd-a28a-4c96-9ade-854eee575613","ab86955e-22f4-49c3-97d7-369973f4cb9e", Environment.SANDBOX)
        cardOnFileSwitcher.setCustomization(customization)
        cardOnFileSwitcher.onSessionEventListener = this
        cardOnFileSwitcher.openCardOnFileSwitcher(intArrayOf())
    }

    private fun openSubscriptionCanceller(sessionID: String?) {
        val customization = Customization()
        customization.textColor = "#fff000"
        customization.primaryColor = "#ff0000"
        customization.companyName = "Millions"

        val subscriptionCanceler = SubscriptionCanceler.getInstance()
        subscriptionCanceler.setCustomization(customization)
        subscriptionCanceler.setOnSessionEventListener(this)
        subscriptionCanceler.init(this, "1c0a49cd-a28a-4c96-9ade-854eee575613","ab86955e-22f4-49c3-97d7-369973f4cb9e", Environment.SANDBOX)
        subscriptionCanceler.openSubscriptionCanceller(true)
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