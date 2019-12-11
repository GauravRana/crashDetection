package com.example.biker112.ui.utils

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Base64
import androidx.fragment.app.Fragment
import com.esafirm.imagepicker.features.ImagePicker
import java.io.*
import kotlin.jvm.internal.Intrinsics

public class Utils{


    public fun selectImagePicker(activity : Activity){
        try {
            val imagePicker = ImagePicker.create(activity)
                .language("in") // Set image picker language
                .toolbarArrowColor(Color.RED) // set toolbar arrow up color
                .toolbarFolderTitle("Folder") // folder selection title
                .toolbarImageTitle("Tap to select") // image selection title
                .toolbarDoneButtonText("DONE")
                .single()
            imagePicker.start(1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    public fun selectImagePickerF(fragment : Fragment){
        try {
            val imagePicker = ImagePicker.create(fragment)
                .language("in") // Set image picker language
                .toolbarArrowColor(Color.RED) // set toolbar arrow up color
                .toolbarFolderTitle("Folder") // folder selection title
                .toolbarImageTitle("Tap to select") // image selection title
                .toolbarDoneButtonText("DONE")
                .single()
            imagePicker.start(1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    public  fun encodeImage(path: String): String {
        val imagefile = File(path)
        var fis: FileInputStream?= null

        try {
            fis = FileInputStream(imagefile)
        } catch (var7: FileNotFoundException) {
            var7.printStackTrace()
        }

        val bm = BitmapFactory.decodeStream(fis as InputStream)
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos as OutputStream)
        val b = baos.toByteArray()
        val var10000 = Base64.encodeToString(b, 0)
        Intrinsics.checkExpressionValueIsNotNull(
            var10000,
            "Base64.encodeToString(b, Base64.DEFAULT)"
        )
        return var10000
    }
}