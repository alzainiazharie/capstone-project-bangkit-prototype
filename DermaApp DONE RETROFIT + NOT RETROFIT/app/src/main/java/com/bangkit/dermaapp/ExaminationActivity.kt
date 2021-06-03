package com.bangkit.dermaapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bangkit.dermaapp.databinding.ActivityExaminationBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.bumptech.glide.request.target.Target
import com.google.android.material.snackbar.Snackbar
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import java.io.ByteArrayOutputStream
import java.io.OutputStreamWriter
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class ExaminationActivity : AppCompatActivity() {



    private lateinit var binding: ActivityExaminationBinding
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
        binding = ActivityExaminationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGallery.setOnClickListener {
            gallery()
        }
        binding.btnCamera.setOnClickListener {
            camera()
        }
        binding.btnPrediction.setOnClickListener {

            Log.d("TAG UPLOAD", imgurUrl)



            Glide.with(this@ExaminationActivity)
                .asBitmap()
                .load(imgurUrl)
                .apply(RequestOptions().override(240, 300))
                .placeholder(R.drawable.ic_launcher_background)
                .into(object : BitmapImageViewTarget(binding.imgSegmentation) {

                    override fun setResource(resource: Bitmap?) {
                        binding.imgSegmentation.setImageBitmap(resource)
                        super.setResource(resource)

                    }

                })

            showSnackbarMessage("Ini hasilnya")


        }

        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setView(R.layout.loading_dialog)
        loadingView = builder.create()


    }

    private fun showSnackbarMessage(message: String) {
        Snackbar.make(binding?.root as View, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun gallery() {
        Log.d("TAG", "gallery")
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun camera() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, CAMERA_REQUEST_CODE)
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, CAMERA_REQUEST_CODE)
            } else {
                Toast.makeText(this, "BLM ADA PERMISSION KK", Toast.LENGTH_LONG).show()
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE) {
            bitmap = data!!.extras!!.get("data") as Bitmap
            binding.imgIndication.setImageBitmap(bitmap)
           imageUpload(bitmap)
        }

        if (requestCode == GALLERY_REQUEST_CODE) {
            Log.d("TAG", "gallery")
            binding.imgIndication.setImageURI(data?.data)
            Log.d("TAG", "masukkan ke img 1")
            val uri: Uri? = data?.data
            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
            Log.d("TAG", "coba masuk ke function image upload")
            imageUpload(bitmap)
        }





    }

    private var imgurUrl: String = ""

    private fun imageUpload(image: Bitmap) {
        loadingView.show()
        Log.d("TAG", "didalam fun image Upload")
        convert(image, complete = { base64Image ->
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val url = URL("https://api.imgur.com/3/image")
                    val boundary = "Boundary-${System.currentTimeMillis()}"

                    val httpsURLConnection =
                        withContext(Dispatchers.IO) { url.openConnection() as HttpsURLConnection }
                    httpsURLConnection.setRequestProperty(
                        "Authorization",
                        "Client-ID 81a44c34bb65c2c"
                    )
                    httpsURLConnection.setRequestProperty(
                        "Content-Type",
                        "multipart/form-data; boundary=$boundary"
                    )

                    httpsURLConnection.requestMethod = "POST"
                    httpsURLConnection.doInput = true
                    httpsURLConnection.doOutput = true

                    var body = ""
                    body += "--$boundary\r\n"
                    body += "Content-Disposition:form-data; name=\"image\""
                    body += "\r\n\r\n$base64Image\r\n"
                    body += "--$boundary--\r\n"

                    Log.d("BODY", body)


                    val outputStreamWriter = OutputStreamWriter(httpsURLConnection.outputStream)
                    withContext(Dispatchers.IO) {
                        outputStreamWriter.write(body)
                        outputStreamWriter.flush()

                        Log.d("BODY ENCODE", body)
                    }
                    val response = httpsURLConnection.inputStream.bufferedReader()
                        .use { it.readText() }  // defaults to UTF-8
                    val jsonObject = JSONTokener(response).nextValue() as JSONObject
                    val data = jsonObject.getJSONObject("data")


                    Log.d("TAG", "Link is : ${data.getString("link")}")
                    imgurUrl = data.getString("link")
                    val type = data.getString("type")

                    val size = data.getInt("size")
                    val width = data.getInt("width")
                    val height = data.getInt("height")





                    Log.d("response: ", response.toString())


                    Log.d("TAG SIZE : ", type)
                    Log.d("TAG SIZE : ", size.toString())
                    Log.d("TAG SIZE : ", height.toString())
                    Log.d("TAG SIZE : ", width.toString())


                    Log.d("TAG", "proses masukan image ke imgUpload")





                    Log.d("TAG", "compleated")



                    Log.d("TAG1", imgurUrl)

                    Log.d("BODY FINISH", body)


                    loadingView.dismiss()

                    Toast.makeText(this@ExaminationActivity, "Berhasil", Toast.LENGTH_SHORT).show()




                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d("TAG message", e.message.toString())
                    Log.d("TAG", "GAGAL CATCH")


                }
            }
            Log.d("TAG2", imgurUrl)
        }

        )

        Log.d("TAG3", imgurUrl)

/*
         GlideApp.with(this)
             .load(imgurUrl)
             .apply(RequestOptions().override(240,320))
             .placeholder(R.drawable.ic_launcher_background)
             .into(binding.imgUpload)
         Log.d("TAG PREDIC1", imgurUrl)*/





        Log.d("TAG3  popopo", "selesai")


    }

    private fun convert(image: Bitmap, complete: (String) -> Unit) {
        GlobalScope.launch {
            val outputStream = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            val b = outputStream.toByteArray()
            complete(Base64.encodeToString(b, Base64.DEFAULT))

        }




    }

}