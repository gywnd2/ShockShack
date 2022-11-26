package com.udangtangtang.shockshack

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.udangtangtang.shockshack.databinding.ActivitySignUpBinding
import retrofit2.Retrofit

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySignUpBinding

    // Retrofit
    private lateinit var retrofit: Retrofit
    private lateinit var retrofitService: RetrofitService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonSignupSubmit.setOnClickListener {
            Toast.makeText(this, "회원가입 POST 요청 보내기", Toast.LENGTH_SHORT).show()
        }
    }
}