package com.example.chatapp

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.example.chatapp.databinding.ActivityMainBinding
import com.example.chatapp.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var getResult: ActivityResultLauncher<Intent>
    private val STORAGE_REQUEST_CODE = 23423
    private lateinit var uri: Uri
    private lateinit var storageRef: StorageReference
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val userRef: CollectionReference = db.collection("users_collection")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        storageRef = FirebaseStorage.getInstance().reference

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

        binding.profileImage.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this@MainActivity,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermission()
            } else {
                getImage()
            }
        }

        getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                binding.profileImage.setImageURI(it.data?.data)
                uri = it.data?.data!!
            }
        }
    }


    private fun signIn(
        email: String = binding.singInInputEmail.editText?.text.toString().trim(),
        password: String = binding.singInInputPassword.editText?.text.toString().trim(),
    ) {

        showProgressBar1()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(
                this,
                "You should provide an email and a password",
                Toast.LENGTH_LONG
            ).show()
            hideProgressBar1()
            return
        }

        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "User signed in", Toast.LENGTH_LONG).show()
                    hideProgressBar1()
                    sendToActivity()
                } else {
                    Toast.makeText(
                        this,
                        "Couldn't sign in/nSomething went wrong !",
                        Toast.LENGTH_LONG
                    ).show()
                    hideProgressBar1()
                }
            }
    }

    private fun createAccount() {

        showProgressBar2()

        val email = binding.singUpInputEmail.text.toString().trim()
        val password = binding.singUpInputPassword.text.toString().trim()
        val confirmPassword = binding.singUpInputConfirmPassword.text.toString().trim()
        val userName = binding.singUpInputUsername.text.toString().trim()

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "You should provide an email and a password", Toast.LENGTH_LONG)
                .show()
            hideProgressBar2()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords don't match", Toast.LENGTH_LONG).show()
            hideProgressBar2()
            return
        }

        if (userName.isEmpty()) {
            Toast.makeText(this, "You should provide an username", Toast.LENGTH_LONG).show()
            hideProgressBar2()
            return
        }

        if (password.length <= 6) {
            Toast.makeText(this, "Password should have six characters", Toast.LENGTH_LONG).show()
            hideProgressBar2()
            return
        }

        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Account created", Toast.LENGTH_LONG).show()

                    if (task.isComplete) {

                        if (this::uri.isInitialized) {
                            val filePath = storageRef.child("profile_images")
                                .child(uri.lastPathSegment!!)
                            filePath.putFile(uri)
                                .addOnSuccessListener(this) { task ->
                                    val result: Task<Uri> = task.metadata?.reference?.downloadUrl!!
                                    result.addOnSuccessListener {
                                        uri = it
                                    }

                                    val user = User(
                                        userName,
                                        uri.toString(),
                                        FirebaseAuth.getInstance().currentUser?.uid!!
                                    )
                                    userRef.document()
                                        .set(user)
                                        .addOnSuccessListener {
                                            Toast.makeText(
                                                this,
                                                "Account Created",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            hideProgressBar2()
                                            sendToActivity()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(
                                                this,
                                                "Account wasn't created",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            hideProgressBar2()
                                        }
                                }
                        } else {
//                            signIn(email = email, password = password)
                            val user =
                                User(userName, "", FirebaseAuth.getInstance().currentUser?.uid!!)
                            userRef.document()
                                .set(user)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Account Created", Toast.LENGTH_SHORT)
                                        .show()
                                    hideProgressBar2()
                                    sendToActivity()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        this,
                                        "Account wasn't created",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    hideProgressBar2()
                                }
                        }

                    } else {
                        Toast.makeText(
                            this,
                            "Account wasn't created",
                            Toast.LENGTH_LONG
                        ).show()
                    }
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

    private fun getImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        getResult.launch(intent)
    }

    private fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this@MainActivity,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            AlertDialog.Builder(this@MainActivity)
                .setPositiveButton(R.string.dialog_button_yes) { _, _ ->
                    ActivityCompat.requestPermissions(
                        this@MainActivity,
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        STORAGE_REQUEST_CODE
                    )
                }.setNegativeButton(R.string.dialog_button_no) { dialog, _ ->
                    dialog.cancel()
                }.setTitle("Permission needed")
                .setMessage("This permission is needed for accessing the internal storage")
                .show()
        } else {
            ActivityCompat.requestPermissions(
                this@MainActivity, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_REQUEST_CODE && grantResults.isNotEmpty()
        /*&& grantResults[0] == PackageManager.PERMISSION_GRANTED*/
        ) {
            getImage()
        } else {
            Toast.makeText(this@MainActivity, "Permission not granted", Toast.LENGTH_LONG).show()
        }
    }

    private fun sendToActivity() {
        startActivity(Intent(this@MainActivity, ChatActivity::class.java))
        finish()
    }

    private fun showProgressBar1() {
        binding.progressBar1.visibility = View.VISIBLE
    }

    private fun hideProgressBar1() {
        binding.progressBar1.visibility = View.GONE
    }

    private fun showProgressBar2() {
        binding.progressBar2.visibility = View.VISIBLE
    }

    private fun hideProgressBar2() {
        binding.progressBar2.visibility = View.GONE
    }
}
