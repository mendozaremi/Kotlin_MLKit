package com.remi.mlkitproject3.ui

import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.remi.mlkitproject3.R
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var cb: Button
    private lateinit var im: ImageView
    private lateinit var imageBitmap: Bitmap
    val REQUEST_IMAGE_CAPTURE = 1

    /*
     onCreateView() - Instantiate the view hierarchy
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        var myroot = inflater.inflate(R.layout.main_fragment, container, false)
        cb = myroot!!.findViewById(R.id.takePicture_button)
        im = myroot!!.findViewById(R.id.camera_imageview)
        return myroot
    }

    /*
     onActivityCreated() - Called when Activity is created and the Views are created.
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        // when we click on camera button will call dispatch function return intent
        cb.setOnClickListener {
            Log.i("Frank", "Inside onClick()")
            dispatchTakePictureIntent()
        }
    }

    /*
    dispatchTakePictureIntent() - my routine that creates an Intent to get an image from a camera app
     */
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            Log.i("Frank", "*****ERROR.  Cannot start Camera app")
        }
    }

    /*
     onActivityResult() - Called when the Intent returns.  If a valid image is returned, then set that image in the ImageView (im)
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            imageBitmap = data!!.extras!!.get("data") as Bitmap
            Log.i("Remi", "Setting Image...")
            im.setImageBitmap(imageBitmap)
            imageLabeling(imageBitmap)
        }
    }

    /*
     imageLabeling() - my routine that calls ML Kit ImageLabeling to get recognized objects (labels) with their associated accuracy.

     Accuracy is how confident ML Kit feels about a recognized object in the image.
     */
    private fun imageLabeling(mybitmap: Bitmap) {
        val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
        val image = InputImage.fromBitmap(mybitmap, 0)
        var outputText = ""

        labeler.process(image).addOnSuccessListener { labels ->
            for (label in labels) {
                val text = label.text                   // retrieve the label text
                val confidence = label.confidence       // retrieve the confidence
                outputText += "$text : $confidence\n"   // build a string for output
                Log.i("Remi", "LOOP>     [$text]:$confidence")
            }
            Log.i("Remi", "***** Labels\n $outputText")

        }.addOnFailureListener { e ->
            Log.e("Remi", "**Failure when processing image")
        }
    }

}