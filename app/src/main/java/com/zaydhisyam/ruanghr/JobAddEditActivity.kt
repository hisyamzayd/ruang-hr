package com.zaydhisyam.ruanghr

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.MimeTypeMap
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_job_add_edit.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

class JobAddEditActivity : AppCompatActivity() {

    companion object {
        const val IS_EDIT = "is_edit"
        const val INTENT_EXTRA = "intent_extra"
        private const val POSTER_REQ_CODE = 123
    }

    private var data: JobModel? = null
    private var photoURI: Uri? = null
    private var photoFileName: String? = null
    private var isPhotoChanged: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_add_edit)

        //set view
        if (intent.getBooleanExtra(IS_EDIT, false)) {
            activityTitle.text = "Edit Data"
            btn_save.text = "update"
            data = intent.getParcelableExtra(INTENT_EXTRA)
            setEditViewData()
        } else {
            activityTitle.text = "Tambah Data"
            btn_save.text = "tambah"
        }

        //set btn calendar onclicklistener
        iv_batas_waktu.setOnClickListener {
            openDatePicker()
        }

        //set upload foto onclicklistener
        btn_foto_pekerjaan.setOnClickListener {
            startActivityForResult(
                Intent().apply {
                    this.type = "image/*"
                    this.action = Intent.ACTION_GET_CONTENT
                },
                POSTER_REQ_CODE
            )
        }

        //set btn save onclicklistener
        btn_save.setOnClickListener {
            //upload photo
            if (isPhotoChanged) {
                photoFileName = getPhotoFileName()
                uploadPhotoToStorage(photoFileName!!)
            }
            //create or update database
            val databaseRef = Firebase.database.getReference("jobs")
            val message: String
            message = if (intent.getBooleanExtra(IS_EDIT, false)) {
                updateData()
                if (isPhotoChanged) data!!.foto_pekerjaan = photoFileName!!
                databaseRef.child(data!!.id).setValue(data)
                "data successfully updated!"
            } else {
                val newJob = createNewJob()
                if (isPhotoChanged) newJob.foto_pekerjaan = photoFileName!!
                databaseRef.child(newJob.id).setValue(newJob)
                "data successfully added into database!"
            }
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java).apply {
                this.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                MainActivity.refreshData = true
            })
        }
    }

    private fun setEditViewData() {
        til_nama_pekerjaan.editText!!.setText(data!!.nama_pekerjaan)
        til_nama_perusahaan.editText!!.setText(data!!.nama_perusahaan)
        til_lokasi_pekerjaan.editText!!.setText(data!!.lokasi_pekerjaan)
        til_email_perusahaan.editText!!.setText(data!!.email_perusahaan)
        til_deskripsi_pekerjaan.editText!!.setText(data!!.deskripsi_pekerjaan)
        til_detail_pekerjaan.editText!!.setText(data!!.detail_pekerjaan)
        til_deskripsi_gaji.editText!!.setText(data!!.deskripsi_gaji)
        tv_batas_waktu.text = data!!.batas_waktu
        //glide foto pekerjaan
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == POSTER_REQ_CODE &&
            resultCode == Activity.RESULT_OK &&
            data != null) {
            isPhotoChanged = true
            photoURI = data.data
            //set src foto pekerjaan
            Glide.with(this).load(photoURI).into(iv_foto_pekerjaan)
        }
    }

    private fun openDatePicker() {
        val datePicker = MaterialDatePicker.Builder
            .datePicker()
            .setTitleText("pilih tanggal batas lamaran")
            .build()

        datePicker.addOnPositiveButtonClickListener {
            val calendar = Calendar.getInstance(TimeZone.getDefault())
            calendar.time = Date(it)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH) + 1
            val year = calendar.get(Calendar.YEAR)
            //set date
            tv_batas_waktu.text = "$day/$month/$year"
        }

        datePicker.show(supportFragmentManager, "date-picker")
    }

    private fun createNewJob(): JobModel {
        val newJob = JobModel(
            id = System.currentTimeMillis().toString(),
            nama_pekerjaan = til_nama_pekerjaan.editText!!.text.toString(),
            nama_perusahaan = til_nama_perusahaan.editText!!.text.toString(),
            email_perusahaan = til_email_perusahaan.editText!!.text.toString(),
            lokasi_pekerjaan = til_lokasi_pekerjaan.editText!!.text.toString(),
            deskripsi_pekerjaan =  til_deskripsi_pekerjaan.editText!!.text.toString(),
            detail_pekerjaan = til_detail_pekerjaan.editText!!.text.toString(),
            deskripsi_gaji = til_deskripsi_gaji.editText!!.text.toString(),
            batas_waktu = tv_batas_waktu.text.toString(),
            timestamp = SimpleDateFormat("dd/MM/yyyy").format(Date())
        )
        return newJob
    }

    private fun updateData() {
        data!!.nama_pekerjaan = til_nama_pekerjaan.editText!!.text.toString()
        data!!.nama_perusahaan = til_nama_perusahaan.editText!!.text.toString()
        data!!.email_perusahaan = til_email_perusahaan.editText!!.text.toString()
        data!!.lokasi_pekerjaan = til_lokasi_pekerjaan.editText!!.text.toString()
        data!!.deskripsi_pekerjaan =  til_deskripsi_pekerjaan.editText!!.text.toString()
        data!!.detail_pekerjaan = til_detail_pekerjaan.editText!!.text.toString()
        data!!.deskripsi_gaji = til_deskripsi_gaji.editText!!.text.toString()
        data!!.batas_waktu = tv_batas_waktu.text.toString()
        data!!.timestamp = SimpleDateFormat("dd/MM/yyyy").format(Date())
        data!!.is_updated = true
    }

    private fun getPhotoFileName(): String {
        val fotoExtension =
            MimeTypeMap.getSingleton().getExtensionFromMimeType(this.contentResolver.getType(photoURI!!))
        return "${System.currentTimeMillis()}.$fotoExtension"
    }

    private fun uploadPhotoToStorage(filePhotoName: String) {
        val storageReference = Firebase.storage.getReference("jobs_photo")
        storageReference.child(filePhotoName).putFile(photoURI!!)
            .addOnSuccessListener {
                Toast.makeText(this, "photo successfully uploaded!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "photo failed to upload..: $it", Toast.LENGTH_SHORT).show()
            }
    }
}