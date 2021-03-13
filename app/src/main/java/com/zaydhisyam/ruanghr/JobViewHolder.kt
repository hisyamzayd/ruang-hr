package com.zaydhisyam.ruanghr

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

class JobViewHolder(
    itemView: View
): RecyclerView.ViewHolder(itemView) {

    private val parent = itemView.findViewById<ConstraintLayout>(R.id.parent)
    private val iv_foto_pekerjaan = itemView.findViewById<ImageView>(R.id.iv_foto_pekerjaan)
    private val tv_nama_pekerjaan = itemView.findViewById<TextView>(R.id.tv_nama_pekerjaan)
    private val tv_nama_perusahaan = itemView.findViewById<TextView>(R.id.tv_nama_perusahaan)
    private val tv_lokasi_pekerjaan = itemView.findViewById<TextView>(R.id.tv_lokasi_pekerjaan)
    private val tv_timestamp_dan_update = itemView.findViewById<TextView>(R.id.tv_timestamp_dan_update)
    private val tv_jumlah_pelamar = itemView.findViewById<TextView>(R.id.tv_jumlah_pelamar)

    fun setView(job: JobModel) {
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
    }

    fun setParentClickListener(
        jobAdapterInterface: JobAdapterInterface,
        job: JobModel
    ) {
        parent.setOnClickListener {
            jobAdapterInterface.onParentViewClicked(job)
        }
    }

    fun setFotoPekerjaanIntoView(
        jobAdapterInterface: JobAdapterInterface,
        job: JobModel
    ) {
        jobAdapterInterface.setGlidePhoto(job, iv_foto_pekerjaan)
    }

}