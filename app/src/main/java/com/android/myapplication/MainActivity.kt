package com.android.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log

import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.String.format
import kotlin.math.round

class MainActivity : AppCompatActivity() {
    @SuppressLint("DefaultLocale")
    private lateinit var myImage: Bitmap
lateinit var uri: Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val msg = findViewById<EditText>(R.id.edt_message)

        val btnUrl = findViewById<Button>(R.id.share_url_btn)



            val imageUrl =  "https://images.sftcdn.net/images/t_app-cover-l,f_auto/p/7db3cfba-057b-481e-b226-2dae45490e45/4031416712/youtools-seo-tools-screenshot.png"
        GlobalScope.launch(Dispatchers.Default) {
            Log.e("inside global Scope", Thread.currentThread().name.toString())
            Glide.with(this@MainActivity)
                .asBitmap()
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {

                        val imageFile = saveBitmapToFile(resource)
                        if (imageFile != null) {
                            uri = FileProvider.getUriForFile(
                                this@MainActivity,
                                "com.android.myapplication.fileprovider",
                                imageFile
                            )

                        }
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        // Failure callback
                        Log.e("Glide", "Failed to load image")
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        // Not needed in this case
                    }
                })
        }

              btnUrl.setOnClickListener(){
                  val message=msg.text.toString()
                  Log.e("outside global Scope", Thread.currentThread().name.toString())
                  val shareIntent = Intent(Intent.ACTION_SEND)
                  shareIntent.type = "image/*"
                  shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                  shareIntent.putExtra(Intent.EXTRA_TEXT,message)
                  shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                  startActivity(Intent.createChooser(shareIntent, "Share Image"))
              }

    }

    private fun saveBitmapToFile(bitmap: Bitmap): File? {
        val cachePath = File(cacheDir, "images")
        cachePath.mkdirs()
        try {
            val file = File(cachePath, "shared_image.jpg")
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.close()
            return file
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }
}
