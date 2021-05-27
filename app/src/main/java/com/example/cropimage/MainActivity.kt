package com.example.cropimage

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.opengl.Visibility
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.cropimage.databinding.ActivityMainBinding
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    var url: Uri? = null
    private lateinit var bitmap: Bitmap
    private lateinit var binding: ActivityMainBinding

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setOnClick()
        colorMania()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun colorMania() {
        binding.imageView.isDrawingCacheEnabled = true
        binding.imageView.buildDrawingCache()

        // on touch listener on image view
        binding.imageView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
                bitmap = Bitmap.createBitmap(
                    v.drawingCache
                )
                val pixel = bitmap.getPixel(event.x.toInt(), event.y.toInt())

                // get RGB values from touched pixel
                val r = Color.red(pixel)
                val g = Color.green(pixel)
                val b = Color.blue(pixel)

                // get HEX  value from touched pixel
                val hex = Integer.toHexString(pixel)

                //set back ground color to view
                binding.colorView.setBackgroundColor(Color.rgb(r, g, b))

                // set Hex and RGB value to text view
                binding.resultColor.text = hex
            }
            true
        }
//        binding.imageView.background=null

    }

    private fun setOnClick() {
        binding.btBrowse.setOnClickListener {
            CropImage.startPickImageActivity(this)
//            binding.imageView.background = null

        }
        binding.btReset.setOnClickListener {
            binding.imageView.setImageBitmap(null)
            binding.resultColor.visibility = View.INVISIBLE
            binding.colorView.visibility = View.INVISIBLE
            binding.idDefault.visibility = View.INVISIBLE

        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE &&
            resultCode == Activity.RESULT_OK
        ) {
            val uriImage = CropImage.getPickImageResultUri(this, data)

            if (CropImage.isReadExternalStoragePermissionsRequired(this, uriImage)) {
                url = uriImage
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    0
                )
            } else {
                startCrop(uriImage)
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            val result: CropImage.ActivityResult = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                binding.imageView.setImageURI(result.uri)
                Toast.makeText(this, "image update !!!", Toast.LENGTH_SHORT).show()
                binding.resultColor.visibility = View.VISIBLE
                binding.colorView.visibility = View.VISIBLE
                binding.idDefault.visibility = View.VISIBLE

            }
        }
    }

    private fun startCrop(uriImage: Uri) {
        CropImage.activity(uriImage)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setMultiTouchEnabled(true)
            .setBackgroundColor(resources.getColor(android.R.color.darker_gray))
            .start(this)
    }

}