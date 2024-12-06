package com.wain.damkar.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.wain.damkar.BuildConfig
import com.wain.damkar.MainActivity
import com.wain.damkar.R
import com.wain.damkar.app.ApiConfig
import com.wain.damkar.halper.SharedPref
import com.wain.damkar.model.ResponModel
import com.wain.damkar.utils.GPSTracker
import kotlinx.android.synthetic.main.activity_laporan.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_update_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*


class LaporanActivity : AppCompatActivity(),AdapterView.OnItemSelectedListener{
    var gpsTracker: GPSTracker? = null
    var REQ_CAMERA = 100
    var fileSize = 0
    var imageFilePath: String? = null
    var encodedImage: String? = null
    var timeStamp: String? = null
    var imageName: String? = null
    var imageSize: String? = null
    var fileDirectoty: File? = null
    var imageFilename: File? = null
    var numberFormat: NumberFormat? = null
    lateinit var imageBytes: ByteArray

    lateinit var s: SharedPref

    var kategori = arrayOf("Kebakaran", "Hewan Liar", "Bantuan Pertolongan", "Bencana Alam")
    var spinner: Spinner?= null
    var textView_msg: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_laporan)

        s = SharedPref(this)

        //cek permission
        val verfiyPermission: Int = Build.VERSION.SDK_INT
        if (verfiyPermission > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (checkIfAlreadyhavePermission()) {
                requestForSpecificPermission()
            }
        }
        takeCameraImage()
        btn_laporan.setOnClickListener {
            laporan()
        }
        btn_camera.setOnClickListener {
            takeCameraImage()
        }
        setData()
        spinner()
        s.setStatusLogin(true)

    }

    private fun spinner(){
        textView_msg = this.edt_kategori
        spinner = this.spinner_kategori
        spinner!!.setOnItemSelectedListener(this)
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, kategori)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner!!.setAdapter(aa)
    }

    //ambil gambar dari kamera
    private fun takeCameraImage() {
        Dexter.withContext(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        if (report.areAllPermissionsGranted()) {
                            try {
                                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                        FileProvider.getUriForFile(this@LaporanActivity,
                                                BuildConfig.APPLICATION_ID.toString() + ".provider", createImageFile()))
                                startActivityForResult(intent, REQ_CAMERA)
                            } catch (ex: IOException) {
                                Toast.makeText(this@LaporanActivity, "Gagal membuka kamera!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {
                        token.continuePermissionRequest()
                    }
                }).check()
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        timeStamp = SimpleDateFormat("dd MMMM yyyy HH:mm").format(Date())
        imageName = "JPEG_"
        fileDirectoty = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "")
        imageFilename = File.createTempFile(imageName, ".jpg", fileDirectoty)
        imageFilePath = imageFilename?.getAbsolutePath()
        return imageFilename as File
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CAMERA && resultCode == Activity.RESULT_OK) {
            convertImage(imageFilePath)
        } else if (requestCode == REQUEST_PICK_PHOTO && resultCode == Activity.RESULT_OK) {
            val selectedImage: Uri? = data?.getData()
            val filePathColumn = arrayOf<String>(MediaStore.Images.Media.DATA)
            assert(selectedImage != null)

            val cursor: Cursor = contentResolver.query(selectedImage!!, filePathColumn,
                    null, null, null)!!
            cursor.moveToFirst()

            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            val mediaPath = cursor.getString(columnIndex)

            cursor.close()
            imageFilePath = mediaPath
            convertImage(mediaPath)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun convertImage(urlImg: String?) {
        val imgFile = File(urlImg)
        if (imgFile.exists()) {
            val options: BitmapFactory.Options = BitmapFactory.Options()
            val bitmap: Bitmap = BitmapFactory.decodeFile(imageFilePath, options)

            imagePreview.setImageBitmap(bitmap)
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

            imageBytes = baos.toByteArray()
            encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT)

            //menghitung size gambar
            numberFormat = DecimalFormat()
            (numberFormat as DecimalFormat).setMaximumFractionDigits(100)
            fileSize = (imgFile.length() / 1024).toString().toInt()
            imageSize = (numberFormat as DecimalFormat).format(fileSize.toLong())
            gpsTracker = GPSTracker(this@LaporanActivity)

            //menampilkan data gambar
            if (gpsTracker!!.isGPSTrackingEnabled) {
                val latitude: Double? = gpsTracker?.getLatitude()
                val longitude: Double? = gpsTracker?.getLongitude()
                val lokasiGambar: String? = gpsTracker?.getAddressLine(this)

                edt_laporan.text = lokasiGambar
                edt_lat.text = "" + latitude
                edt_long.text =  "" + longitude

            } else {
                Toast.makeText(this@LaporanActivity,
                        "Tidak mendapatkan lokasi. Silahkan periksa GPS atau koneksi internet anda!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun setData() {
        val user = s.getUser()
        if (user != null) {
            apply {
                edt_id.setText(""+user.id)
                edt_name.setText(user.name)
                edt_hp.setText(user.phone)
            }
        }
    }

    private fun laporan(){

        edt_status.setText("Menunggu")
        if (edt_kategori.text.isEmpty()) {
            edt_kategori.error = "Kolom Kategori tidak boleh kosong"
            edt_kategori.requestFocus()
            return
        }
        if (edt_ket.text.isEmpty()) {
            edt_ket.error = "Kolom Deskripsi tidak boleh kosong"
            edt_ket.requestFocus()
            return
        }
        val file = File(imageFilePath!!)
        val id = edt_id.text.toString()
        val edt_id = RequestBody.create(MediaType.parse("multipart/form-data"), id)
        val name = edt_name.text.toString()
        val edt_name = RequestBody.create(MediaType.parse("multipart/form-data"), name)
        val hp = edt_hp.text.toString()
        val edt_hp = RequestBody.create(MediaType.parse("multipart/form-data"), hp)
        val lokasi = edt_laporan.text.toString()
        val edt_laporan = RequestBody.create(MediaType.parse("multipart/form-data"), lokasi)
        val lat =edt_lat.text.toString()
        val status = edt_status.text.toString()
        val edt_status = RequestBody.create(MediaType.parse("multipart/form-data"), status)
        val edt_lat = RequestBody.create(MediaType.parse("multipart/form-data"), lat)
        val long = edt_long.text.toString()
        val edt_long = RequestBody.create(MediaType.parse("multipart/form-data"), long)
        val kategori = edt_kategori.text.toString()
        val edt_kategori = RequestBody.create(MediaType.parse("multipart/form-data"), kategori)
        val deskripsi = edt_ket.text.toString()
        val edt_description = RequestBody.create(MediaType.parse("multipart/form-data"), deskripsi)
        val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"),file)
        val fileImage = MultipartBody.Part.createFormData("image", file.getName(),requestBody)
        pb2.visibility = View.VISIBLE
        ApiConfig.instanceRetrofit.laporan(
                edt_id,
                edt_name,
                edt_hp,
                edt_laporan,
                edt_lat,
                edt_long,
                edt_kategori,
                edt_description,
                fileImage!!,
                edt_status
        ).enqueue(object : Callback<ResponModel> {
            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
                pb2.visibility = View.GONE
            }
            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                pb2.visibility = View.VISIBLE
                if(response.isSuccessful){
                    if(response.body()!!.success==1){
                        s.setStatusLogin(true)
                        val intent = Intent( this@LaporanActivity, MainActivity::class.java)
                        startActivity(intent)
                        Toast.makeText(this@LaporanActivity,"Berhasil Lapor", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this@LaporanActivity,"GAGAL"+response, Toast.LENGTH_SHORT).show()
                    }
                }
            }

        })
    }

    private fun checkIfAlreadyhavePermission(): Boolean {
        val result: Int = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), 101)
    }

    companion object {
        private const val REQUEST_PICK_PHOTO = 1
    }
    override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {
        textView_msg!!.text = kategori[position]
    }

    override fun onNothingSelected(arg0: AdapterView<*>) {

    }

}