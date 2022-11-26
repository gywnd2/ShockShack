package com.udangtangtang.shockshack

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.udangtangtang.shockshack.databinding.ActivitySignUpBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import standardMemberModel
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySignUpBinding

    // Retrofit
    private lateinit var retrofit: Retrofit
    private lateinit var service: RetrofitService

    private var TAG="SignUpActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Init Retrofit
        retrofit=Retrofit.Builder().baseUrl(getString(R.string.server_addr))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        service=retrofit.create(RetrofitService::class.java)

        binding.buttonSignupSubmit.setOnClickListener {
            // Check if email / password are null or not
            var isEmailValid=false
            var isPasswordValid=false
            val emailExpr="^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
            if(binding.inputTextSignupEmail.toString().equals("")||binding.inputTextSignupEmail.toString()==null){
                Toast.makeText(this, getString(R.string.hint_signup_email_null), Toast.LENGTH_SHORT).show()
            }else{
                // Check email pattern is valid
//                if(Pattern.matches(binding.inputTextSignupEmail.toString(), emailExpr)){ isEmailValid=true }
//                else{Toast.makeText(this, getString(R.string.hint_signup_email_pattern_invalid), Toast.LENGTH_SHORT).show()}

            }
            if(binding.inputTextSignupPassword.toString().equals("")||binding.inputTextSignupPassword.toString()==null){
                Toast.makeText(this, getString(R.string.hint_signup_password_null), Toast.LENGTH_SHORT).show()
            }else{ isPasswordValid=true }

            // If email/password are valid
            if(isEmailValid && isPasswordValid)
            {
                // Create standard member model object to post
                val member=standardMemberModel(binding.inputTextSignupEmail.toString(), binding.inputTextSignupPassword.toString(), "STANDARD")

                // Log member info
                Log.d("Retrofit", "Post new member to server \nemail : "+member.email+" password :"+member.password+"member type :"+member.type)

                service.postSignUpNewUser(member).enqueue(object: Callback<Void>{
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        Log.d("Retrofit", "New member posted with status "+response.code().toString())
                        finish()
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.d("Retrofit", "New member post failed : " + t.message.toString())
                        Toast.makeText(applicationContext, getString(R.string.hint_signup_post_newmember_failed), Toast.LENGTH_SHORT).show()
                    }
                })
            }

        }
    }
}