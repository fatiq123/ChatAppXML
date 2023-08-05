package com.example.chatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.chatapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)


        binding.signInButton.setOnClickListener {

        }
        binding.signUpButton.setOnClickListener {

        }

        binding.textViewRegister.setOnClickListener {
            binding.flipper.setInAnimation(this, android.R.anim.slide_in_left)
            binding.flipper.setOutAnimation(this, android.R.anim.slide_out_right)
            binding.flipper.showNext()
        }
        binding.textViewSignIn.setOnClickListener {
            binding.flipper.setInAnimation(this, R.anim.slide_in_right)
            binding.flipper.setOutAnimation(this, R.anim.slide_out_left)
            binding.flipper.showPrevious()
        }
    }
}