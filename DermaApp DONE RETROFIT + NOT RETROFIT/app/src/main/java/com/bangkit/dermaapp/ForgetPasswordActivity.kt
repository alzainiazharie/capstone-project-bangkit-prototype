package com.bangkit.dermaapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.bangkit.dermaapp.databinding.ActivityForgetPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgetPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnForgetPassword.setOnClickListener {

            val email = binding.edtEmailForgetPassword.text.trim().toString()
            val validEmail = Patterns.EMAIL_ADDRESS.matcher(email).matches()

            if (email.isEmpty() || !validEmail) {
                binding.edtEmailForgetPassword.error = "Email tidak valid"
                binding.edtEmailForgetPassword.requestFocus()
            }

            FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener { task ->
                if (task.isSuccessful){
                    Toast.makeText(this,"Password dikirim ke email",Toast.LENGTH_SHORT).show()
                    Intent(this, MainActivity::class.java).also {
                        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(it)
                    }
                } else {
                    Toast.makeText(this,"Gagal",Toast.LENGTH_SHORT).show()
                }
            }

        }



    }
}