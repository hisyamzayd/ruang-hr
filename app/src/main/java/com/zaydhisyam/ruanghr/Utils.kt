package com.zaydhisyam.ruanghr

import java.text.SimpleDateFormat
import java.util.Date

object Utils {

    fun getDummyJobList(n: Int): ArrayList<JobModel> {
        val list = arrayListOf<JobModel>()
        val dateFormatter = SimpleDateFormat("dd/MM/yyyy")
        for (i in 1..n) {
            val date = dateFormatter.format(Date())
            val job = JobModel(
                id = "123",
                nama_pekerjaan = "Pekerjaan $i",
                nama_perusahaan = "PT. Perusahaan $i",
                email_perusahaan = "email@perusahaan$i.com",
                lokasi_pekerjaan = "Kota $i",
                deskripsi_pekerjaan = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.",
                detail_pekerjaan = "Full-time",
                deskripsi_gaji = "Negotiable",
                jumlah_pelamar = i,
                jumlah_view = i,
                batas_waktu = "20/3/2021",
                timestamp = "4/3/2021"
            )
            list.add(job)
        }
        return list
    }

    fun countDays(date: String): Int {
        val dateFormatter = SimpleDateFormat("dd/MM/yyyy")
        val todaysDate = dateFormatter.format(Date())

        val diff = dateFormatter.parse(todaysDate).time - dateFormatter.parse(date).time
        return (diff / (1000*60*60*24)).toInt()
    }

    fun countDaysTimestamp(timestamp: String): String {
        return when(val days = countDays(timestamp)) {
            0 -> "today"
            1 -> "yesterday"
            else -> "$days days ago"
        }
    }

    fun countDaysBatasWaktu(batasWaktu: String): String {
        val days = countDays(batasWaktu)
        return when {
            days > 0 -> "application no longer available"
            days < 0 -> "application end in ${days*-1} days"
            else -> "application end today"
        }
    }

}