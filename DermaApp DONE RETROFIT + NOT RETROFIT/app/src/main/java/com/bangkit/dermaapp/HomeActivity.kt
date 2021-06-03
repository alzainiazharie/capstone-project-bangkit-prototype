package com.bangkit.dermaapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bangkit.dermaapp.databinding.ActivityHomeBinding
import com.bangkit.dermaapp.retrofit.RetrofitExaminationActivity
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {
    private lateinit var loadingView: AlertDialog
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnLogout.setOnClickListener {
            logout()
        }
        binding.btnScan.setOnClickListener {
            //test retrofit
            val intent = Intent(this, RetrofitExaminationActivity::class.java)
            //val intent = Intent(this, ExaminationActivity::class.java)
            startActivity(intent)
        }

        showUserDoctor()

        binding.btnProfile.setOnClickListener {
            val mOptionDialogFragment = ProfileFragment()

            val mFragmentManager = supportFragmentManager
            mOptionDialogFragment.show(mFragmentManager,ProfileFragment::class.java.simpleName)
/*
*  val mFragManager = supportFragmentManager
        val mHomeFragment = HomeFragment()
        val fragment = mFragManager.findFragmentByTag(HomeFragment::class.java.simpleName)

        if(fragment !is HomeFragment){
            Log.d("My Fleksible Fragment","Name Fragment: "+ HomeFragment::class.java.simpleName)

            mFragManager
                .beginTransaction()
                .add(R.id.frame_container,mHomeFragment,HomeFragment::class.java.simpleName)
                .commit()
        }
* */

            //mOptionDialogFragment.dialog?.show()
        }


    }

    private fun logout(){
        firebaseAuth.signOut()
        Intent(this, MainActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(it)
        }
    }

    private fun showUserDoctor(){
        val doctor = firebaseAuth.currentUser?.email == "dokter@gmail.com"

        if (doctor){
            Toast.makeText(this, "Hai Dokter", Toast.LENGTH_LONG).show()
        }
    }
}