package com.project.postnav.ui.scan

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.database.*
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.project.postnav.PackageActivity
import com.project.postnav.R
import kotlinx.android.synthetic.main.fragment_scan.*
import kotlinx.android.synthetic.main.fragment_scan.view.*
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScanFragment : Fragment() {

    private lateinit var captureButton: Button
    private lateinit var frontScreen: ImageView
    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private var ref:DatabaseReference = FirebaseDatabase.getInstance().reference.child("Packages")
    private var scrren = true

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val root = inflater.inflate(R.layout.fragment_scan, container, false)

        captureButton = root.findViewById(R.id.camera_capture_button)
        frontScreen = root.findViewById(R.id.frontScreen)
        root.camera_capture_button.setOnClickListener {
            if (allPermissionsGranted()) {
                if(scrren){
                    hideFrontScreen()
                    captureButton.text = getString(R.string.text_scan)
                    startCamera()
                }
                else
                    takePhoto()
            } else {
                requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
            }
        }
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
        return root
    }

    private fun hideFrontScreen(){
        scrren = false
        appTitle.visibility = View.INVISIBLE
        scanDesc.visibility = View.INVISIBLE
        scanInfo.visibility = View.INVISIBLE
        arrow.visibility = View.INVISIBLE
        frontScreen.visibility = View.INVISIBLE
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
                captureButton.text = getString(R.string.text_scan)
                hideFrontScreen()
            } else {
                Toast.makeText(requireContext(), "Camera permission needed for scanning QR codes.",
                        Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = File(outputDirectory, "Code.bmp")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        captureButton.visibility = View.INVISIBLE
        progressBar.visibility = View.VISIBLE
        imageCapture.takePicture(
                outputOptions, ContextCompat.getMainExecutor(requireContext()),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onError(exc: ImageCaptureException) {
                        Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                    }

                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                        val savedUri = Uri.fromFile(photoFile)
                        val msg = "Photo capture succeeded: $savedUri"
                        Log.d(TAG, msg)
                        val bmp: Bitmap = getResizedBitmap(BitmapFactory.decodeFile(photoFile.path), 500)
                        val image = InputImage.fromBitmap(bmp, 0)
                        scanBarcode(image)
                    }
                })
    }

    private fun startCamera() {
        val t:Toast = Toast.makeText(requireContext(),"Starting Camera...",Toast.LENGTH_SHORT)
        t.setGravity(Gravity.CENTER,0,0)
        t.show()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(viewFinder.createSurfaceProvider())
            }

            imageCapture = ImageCapture.Builder().build()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
                requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    @Suppress("DEPRECATION")
    private fun getOutputDirectory(): File {
        val mediaDir = requireContext().externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() } }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else requireContext().filesDir
    }

    private fun scanBarcode(image: InputImage) {

        BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_AZTEC).build()

        val scanner = BarcodeScanning.getClient()
        var flag = false
        val photoFile = File(outputDirectory, "Code.bmp")
        scanner.process(image).addOnSuccessListener { BARCODE ->
            for (barcode in BARCODE) {
                flag=true
                when (barcode.valueType) {
                    Barcode.TYPE_TEXT -> {
                        showResult(barcode.displayValue)
                        photoFile.delete()
                    }
                }
            }
            if(!flag) {
                photoFile.delete()
                captureButton.performClick()
            }
        }
                .addOnFailureListener {
                    progressBar.visibility = View.INVISIBLE
                    Toast.makeText(requireContext(), "An ERROR occurred while scanning.", Toast.LENGTH_SHORT).show()
                }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap {
        var width = image.width
        var height = image.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    private fun showResult(str: String?) {
        if (str != null) {
            progressBar.visibility = View.INVISIBLE

            val builder = AlertDialog.Builder(requireContext())
            var dialog:AlertDialog
            var valid = false

            if (isInFormat(str)) {
                val state = getState(str.substring(0, 2))
                if(state != "Invalid State"){
                    val district = getDistrict(state, str.substring(2, 4))
                    if(district != "Invalid District"){
                        valid = true
                        builder.setTitle(str)
                                .setMessage("State : " + state +
                                        "\nDistrict : " + district +
                                        "\nDistrict code : " + str.substring(2, 4) +
                                        "\nBatch code : " + str.substring(4, 8) +
                                        "\nUnique ID : " + str.substring(8, 13) + "\n")
                                .setCancelable(false)
                                .setNegativeButton("More info") { _, _ ->
                                    ref.child(state).child(district).child(str)
                                            .addValueEventListener(object : ValueEventListener {
                                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        val i = Intent(requireContext(), PackageActivity::class.java)
                                                        i.putExtra("PrimaryKey", str)
                                                        i.putExtra("State", state)
                                                        i.putExtra("District", district)
                                                        startActivity(i)
                                                    }
                                                    else{
                                                        Toast.makeText(requireContext(),"Package doesn't exist in database.",Toast.LENGTH_SHORT).show()
                                                    }
                                                    captureButton.visibility = View.VISIBLE
                                                }
                                                override fun onCancelled(databaseError: DatabaseError) {
                                                    Toast.makeText(requireContext(),"Error in retrieving data.",Toast.LENGTH_SHORT).show()
                                                }
                                            })
                                }
                                .setPositiveButton("Okay") { _, _ ->
                                    captureButton.visibility = View.VISIBLE
                                }
                        dialog = builder.create()
                        dialog.show()
                    }
                }

            }

            if(!valid){
                builder.setTitle(R.string.app_name)
                        .setMessage("Invalid package ID : $str")
                        .setCancelable(false)
                        .setNegativeButton("Okay") { _, _ ->
                            captureButton.visibility = View.VISIBLE
                        }
                dialog = builder.create()
                dialog.show()
            }
        }
    }

    private fun isInFormat(str: String): Boolean{
        if(str.length==13){
            try {
                str.substring(2, 13).toDouble()
            } catch (nfe: NumberFormatException) {
                return false
            }
            return true
        }
        return false
    }


    private fun getState(str: String): String {
        val stateCodes = resources.getStringArray(R.array.StateCodes)
        val states = resources.getStringArray(R.array.State_dropdown)
        for(i in stateCodes.indices){
            if (stateCodes[i]==str)
                return states[i+1]
        }
        return "Invalid State"
    }

    private fun getDistrict(state: String, str: String): String {
        val districts: Array<String>
        val index = Integer.parseInt(str)

        when(state) {
            "Andhra Pradesh" -> districts = resources.getStringArray(R.array.State_Andhra_Pradesh)
            "Arunachal Pradesh" -> districts = resources.getStringArray(R.array.State_Arunachal_Pradesh)
            "Assam" -> districts = resources.getStringArray(R.array.State_Assam)
            "Bihar" -> districts = resources.getStringArray(R.array.State_Bihar)
            "Chhattisgarh" -> districts = resources.getStringArray(R.array.State_Chhattisgarh)
            "Goa" -> districts = resources.getStringArray(R.array.State_Goa)
            "Gujarat" -> districts = resources.getStringArray(R.array.State_Gujarat)
            "Haryana" -> districts = resources.getStringArray(R.array.State_Haryana)
            "Himachal Pradesh" -> districts = resources.getStringArray(R.array.State_Himachal_Pradesh)
            "Jharkhand" -> districts = resources.getStringArray(R.array.State_Jharkhand)
            "Karnataka" -> districts = resources.getStringArray(R.array.State_Karnataka)
            "Kerala" -> districts = resources.getStringArray(R.array.State_Kerala)
            "Madhya Pradesh" -> districts = resources.getStringArray(R.array.State_Madhya_Pradesh)
            "Maharashtra" -> districts = resources.getStringArray(R.array.State_Maharashtra)
            "Manipur" -> districts = resources.getStringArray(R.array.State_Manipur)
            "Meghalaya" -> districts = resources.getStringArray(R.array.State_Meghalaya)
            "Mizoram" -> districts = resources.getStringArray(R.array.State_Mizoram)
            "Nagaland" -> districts = resources.getStringArray(R.array.State_Nagaland)
            "Odisha" -> districts = resources.getStringArray(R.array.State_Odisha)
            "Punjab" -> districts = resources.getStringArray(R.array.State_Punjab)
            "Rajasthan" -> districts = resources.getStringArray(R.array.State_Rajasthan)
            "Sikkim" -> districts = resources.getStringArray(R.array.State_Sikkim)
            "Tamil Nadu" -> districts = resources.getStringArray(R.array.State_Tamil_Nadu)
            "Telangana" -> districts = resources.getStringArray(R.array.State_Telangana)
            "Tripura" -> districts = resources.getStringArray(R.array.State_Tripura)
            "Uttar Pradesh" -> districts = resources.getStringArray(R.array.State_Uttar_Pradesh)
            "Uttarakhand" -> districts = resources.getStringArray(R.array.State_Uttarakhand)
            "West Bengal" -> districts = resources.getStringArray(R.array.State_West_Bengal)
            "Andaman and Nicobar Islands" -> districts = resources.getStringArray(R.array.UT_Andaman_and_Nicobar_Islands)
            "Chandigarh" -> districts = resources.getStringArray(R.array.UT_Chandigarh)
            "Dadra and Nagar Haveli" -> districts = resources.getStringArray(R.array.UT_Dadra_and_Nagar_Haveli)
            "Daman and Diu" -> districts = resources.getStringArray(R.array.UT_Daman_and_Diu)
            "Delhi" -> districts = resources.getStringArray(R.array.UT_Delhi)
            "Jammu and Kashmir" -> districts = resources.getStringArray(R.array.UT_Jammu_and_Kashmir)
            "Ladakh" -> districts = resources.getStringArray(R.array.UT_Ladakh)
            "Lakshadweep" -> districts = resources.getStringArray(R.array.UT_Lakshadweep)
            "Puducherry" -> districts = resources.getStringArray(R.array.UT_Puducherry)

            else -> return "Invalid District"
        }

        return if(index>0 && index<=districts.size)
            districts[index]
        else
            "Invalid District"
    }

    companion object {
        private const val TAG = "CameraXBasic"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}
