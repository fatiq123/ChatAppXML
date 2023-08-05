package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.example.chatapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var getResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)


        binding.signInButton.setOnClickListener {
            signIn()
        }
        binding.signUpButton.setOnClickListener {
            createAccount()
        }

        binding.textViewRegister.setOnClickListener {
            startNextAnimation()
        }
        binding.textViewSignIn.setOnClickListener {
           startPreviousAnimation()
        }

        binding.textViewGoToProfile.setOnClickListener {
            startNextAnimation()
        }
        binding.textViewSignUp.setOnClickListener {
            startPreviousAnimation()
        }

        // uploading the profile image
        binding.profileImage.setOnClickListener {
            uploadImage()
        }

        getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                binding.profileImage.setImageURI(it.data?.data)
            }
        }
    }


    private fun signIn() {

        val email = binding.singInInputEmail.editText?.text.toString().trim()
        val password = binding.singInInputPassword.editText?.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(
                this,
                "You should provide an email and a password",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "User signed in",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        "Couldn't sign in/nSomething went wrong !",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun createAccount() {

        val email = binding.singUpInputEmail.text.toString().trim()
        val password = binding.singUpInputPassword.text.toString().trim()
        val confirmPassword = binding.singUpInputConfirmPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(
                this,
                "You should provide an email and a password",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        if (password != confirmPassword) {
            Toast.makeText(
                this,
                "Passwords don't match",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Account created",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        "Account wasn't created",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun startNextAnimation() {
        binding.flipper.setInAnimation(this, android.R.anim.slide_in_left)
        binding.flipper.setOutAnimation(this, android.R.anim.slide_out_right)
        binding.flipper.showNext()
    }

    private fun startPreviousAnimation() {
        binding.flipper.setInAnimation(this, R.anim.slide_in_right)
        binding.flipper.setOutAnimation(this, R.anim.slide_out_left)
        binding.flipper.showPrevious()
    }

    private fun uploadImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        getResult.launch(intent)
    }
}