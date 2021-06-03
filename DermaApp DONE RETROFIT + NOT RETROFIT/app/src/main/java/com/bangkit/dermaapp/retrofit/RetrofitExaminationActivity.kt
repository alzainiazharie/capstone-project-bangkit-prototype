package com.bangkit.dermaapp.retrofit

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bangkit.dermaapp.R
import com.bangkit.dermaapp.databinding.ActivityRetrofitExaminationBinding
import com.bangkit.dermaapp.retrofit.ApiConfig.getRetrofit
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream


class RetrofitExaminationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRetrofitExaminationBinding
    private lateinit var bitmap: Bitmap

    private lateinit var loadingView: AlertDialog

    companion object {
        private const val CAMERA_PERMISSION_CODE = 600
        private const val CAMERA_REQUEST_CODE = 666
        private const val GALLERY_PERMISSION_CODE = 900
        private const val GALLERY_REQUEST_CODE = 999
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRetrofitExaminationBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setView(R.layout.loading_dialog)
        loadingView = builder.create()

        binding.btnGallery.setOnClickListener {
            gallery()
        }
        binding.btnCamera.setOnClickListener {
            camera()
        }

        binding.btnPrediction.setOnClickListener {
            uploadImage()
        }

    }

    private fun camera() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, RetrofitExaminationActivity.CAMERA_REQUEST_CODE)
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                RetrofitExaminationActivity.CAMERA_PERMISSION_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RetrofitExaminationActivity.CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, RetrofitExaminationActivity.CAMERA_REQUEST_CODE)
            } else {
                Toast.makeText(this, "BLM ADA PERMISSION KK", Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun gallery() {
        Log.d("TAG", "gallery")
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, RetrofitExaminationActivity.GALLERY_REQUEST_CODE)
    }

    private fun showSnackbarMessage(message: String) {
        Snackbar.make(binding?.root as View, message, Snackbar.LENGTH_SHORT).show()
    }

    private val image: String
        get() {
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val dataByte = baos.toByteArray()
            return Base64.encodeToString(dataByte, Base64.DEFAULT)
        }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RetrofitExaminationActivity.CAMERA_REQUEST_CODE) {
            bitmap = data!!.extras!!.get("data") as Bitmap
            binding.imgIndication.setImageBitmap(bitmap)
        }

        if (requestCode == RetrofitExaminationActivity.GALLERY_REQUEST_CODE) {
            Log.d("TAG", "gallery")
            binding.imgIndication.setImageURI(data?.data)
            Log.d("TAG", "masukkan ke img 1")
            val uri: Uri? = data?.data
            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
            Log.d("TAG", "coba masuk ke function image upload")
        }
    }

    private fun uploadImage() {
        val title = "IMG_${System.currentTimeMillis()}"

        val api = getRetrofit()!!.create(ApiService::class.java)
        val call = api.uploadImageImgur(title, image)


        call.enqueue(object : Callback<ImageResponse> {
            override fun onResponse(
                call: Call<ImageResponse>,
                response: Response<ImageResponse>
            ) {
                try {
                    val dataSemua = response.body()
                    Log.d("Response", response.isSuccessful.toString())
                    val dataLink = response.body()?.data?.link.toString()
                    val dataStatus = response.body()?.status.toString()
                    Log.d("RESPONSE", dataSemua.toString())
                    Log.d("RESPONSE", dataLink)
                    Log.d("RESPONSE", dataStatus)
                    Log.d("RESPONSE", dataSemua?.data?.name.toString())
                    Log.d("RESPONSE", dataSemua?.data?.title.toString())

                    val link = dataSemua?.data?.link.toString()

                    binding.tvDiseaseName.text = link



                    Glide.with(this@RetrofitExaminationActivity)
                        .load(link)
                        .apply(RequestOptions().override(200, 200))
                        .error(R.drawable.ic_launcher_background)
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(binding.imgSegmentation)

                } catch (e: Exception) {
                    Log.d("error", e.message.toString())
                }


            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            override fun onFailure(
                call: Call<ImageResponse>,
                t: Throwable
            ) {
                Log.d(
                    "Error Response", "GAGAL"
                )
            }

        }
        )
    }

}