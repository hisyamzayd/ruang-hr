package com.zaydhisyam.ruanghr

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class JobModel(
    val id: String = "",
    var nama_pekerjaan: String = "",
    var nama_perusahaan: String = "",
    var foto_pekerjaan: String = "",
    var email_perusahaan: String = "",
    var lokasi_pekerjaan: String = "",
    var deskripsi_pekerjaan: String = "",
    var detail_pekerjaan: String = "",
    var deskripsi_gaji: String = "",
    var jumlah_pelamar: Int = 0,
    var jumlah_view: Int = 0,
    var batas_waktu: String = "",
    var timestamp: String = "",
    var is_updated: Boolean = false
): Parcelable
