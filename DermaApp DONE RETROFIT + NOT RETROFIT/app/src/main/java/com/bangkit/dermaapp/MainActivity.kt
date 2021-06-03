package com.bangkit.dermaapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.dermaapp.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()


        binding.btnLogin.setOnClickListener {

            val email = binding.edtEmail.text.trim().toString()
            val password = binding.edtPassoword.text.trim().toString()
            val validEmail = Patterns.EMAIL_ADDRESS.matcher(email).matches()

            if (email.isEmpty() || !validEmail) {
                binding.edtEmail.error = "Email tidak valid"
                binding.edtEmail.requestFocus()
            }

            if (password.isEmpty() || password.length < 6) {
                binding.edtPassoword.error = "Password kurang dari 6"
                binding.edtPassoword.requestFocus()

            }

            if (validEmail && password.length > 6) {
                login(email, password)
            }


        }

        binding.tvCreateUser.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.tvForgetPassword.setOnClickListener {
            val intent = Intent(this, ForgetPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    private fun login(email: String, password: String) {
        loading(true)
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Intent(this, HomeActivity::class.java).also {
                        loading(false)

                        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(it)

                    }

                } else {
                    Toast.makeText(this, "Email atau Password salah", Toast.LENGTH_LONG).show()
                    loading(false)
                }
            }

    }

    override fun onStart() {
        super.onStart()
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            Intent(this, HomeActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
        }
    }

    private fun loading(load: Boolean) {
        if (load) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }


}

