package com.knotapi.demo

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.knotapi.cardonfileswitcher.CardOnFileSwitcher
import com.knotapi.cardonfileswitcher.SubscriptionCanceler
import com.knotapi.cardonfileswitcher.interfaces.OnSessionEventListener
import com.knotapi.cardonfileswitcher.models.Configuration
import com.knotapi.cardonfileswitcher.models.Environment
import com.knotapi.cardonfileswitcher.models.Options
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), OnSessionEventListener {

    private var progressDialog: ProgressDialog? = null

    var cardOnFileSwitcher: CardOnFileSwitcher? = null
    var subscriptionCanceler: SubscriptionCanceler? = null

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
        val switcherConfig = Configuration(
            Environment.SANDBOX,
            "3f4acb6b-a8c9-47bc-820c-b0eaf24ee771",
            sessionID
        )
        val options = initOptions()

        cardOnFileSwitcher = CardOnFileSwitcher.getInstance()
        cardOnFileSwitcher?.init(this, switcherConfig, options, this)
        cardOnFileSwitcher?.openCardOnFileSwitcher()
    }

    private fun openSubscriptionCanceller(sessionID: String?) {
        val cancelerConfig = Configuration(
            Environment.SANDBOX,
            "3f4acb6b-a8c9-47bc-820c-b0eaf24ee771",
            sessionID
        )
        val options = initOptions()

        subscriptionCanceler = SubscriptionCanceler.getInstance();
        subscriptionCanceler?.init(this, cancelerConfig, options, this);
        subscriptionCanceler?.openSubscriptionCanceller();
    }

    private fun initOptions(): Options {
        val options = Options()
        options.primaryColor = "#000000"
        options.textColor = "#ffffff"
        options.companyName = "Millions"
        options.useCategories = false
        return options
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