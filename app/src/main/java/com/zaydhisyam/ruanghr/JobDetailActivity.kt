package com.zaydhisyam.ruanghr

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_job_detail.*
import java.util.Locale

class JobDetailActivity : AppCompatActivity() {

    companion object {
        const val INTENT_EXTRA = "intentExtra"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_detail)

        val job = intent.getParcelableExtra<JobModel>(INTENT_EXTRA)!!

        //set view
        //glide for foto pekerjaan
        tv_nama_pekerjaan.text = job.nama_pekerjaan
        tv_nama_perusahaan.text = job.nama_perusahaan
        tv_lokasi_pekerjaan.text = job.lokasi_pekerjaan
        val timestamp = Utils.countDaysTimestamp(job.timestamp)
        tv_timestamp_dan_update.text =
            if (job.is_updated) {
                "$timestamp (updated)"
            } else {
                timestamp
            }
        tv_jumlah_pelamar.text = "- ${job.jumlah_pelamar} applicants"
        tv_jumlah_view.text = "- ${job.jumlah_view} views"
        val batasWaktuInt = Utils.countDays(job.batas_waktu)
        if (batasWaktuInt > 0) btn_apply.isEnabled = false
        val batasWaktu = Utils.countDaysBatasWaktu(job.batas_waktu)
        tv_batas_waktu.text = batasWaktu.toUpperCase(Locale.ROOT)
        tv_deskripsi_pekerjaan.text = job.deskripsi_pekerjaan
        tv_detail_pekerjaan.text = job.detail_pekerjaan
        tv_deskripsi_gaji.text = job.deskripsi_gaji

        //set email intent
        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            this.data = Uri.parse("mailto:")
            this.type = "text/plain"
            this.putExtra(Intent.EXTRA_EMAIL, arrayOf(job.email_perusahaan))
            this.putExtra(Intent.EXTRA_SUBJECT, "Application for ${job.nama_pekerjaan} - ${job.nama_perusahaan}")
        }
        btn_apply.setOnClickListener {
            startActivity(emailIntent)
        }

        //set btn edit onclicklistener
        btn_edit.setOnClickListener {
            startActivity(Intent(this, JobAddEditActivity::class.java).apply {
                this.putExtra(JobAddEditActivity.IS_EDIT, true)
                this.putExtra(JobAddEditActivity.INTENT_EXTRA, job)
            })
        }
        btn_delete.setOnClickListener {
            deleteDataFromFirebase(job)
            MainActivity.refreshData = true
            finish()
        }
    }

    private fun deleteDataFromFirebase(job: JobModel) {
        Firebase.database.getReference("jobs").child(job.id)
            .removeValue().addOnSuccessListener {
                Toast.makeText(this,  "database successfully deleted!", Toast.LENGTH_SHORT).show()
            }
        if (job.foto_pekerjaan.isNotEmpty()) {
            Firebase.storage.getReference("jobs_photo").child(job.foto_pekerjaan)
                .delete().addOnSuccessListener {
                    Toast.makeText(this, "storage successfully deleted!", Toast.LENGTH_SHORT).show()
                }
        }
    }
}