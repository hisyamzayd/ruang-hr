package com.zaydhisyam.ruanghr

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

class JobAdapter(
    private val jobListForFilter: ArrayList<JobModel>,
    private val jobAdapterInterface: JobAdapterInterface
): RecyclerView.Adapter<JobViewHolder>(), Filterable {

    private val jobList = arrayListOf<JobModel>()

    fun setJobList() {
        jobList.clear()
        jobList.addAll(jobListForFilter)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        return JobViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.row_item_main, parent, false)
        )
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        holder.setView(jobListForFilter[position])
        holder.setParentClickListener(
            jobAdapterInterface,
            jobListForFilter[position]
        )
        holder.setFotoPekerjaanIntoView(jobAdapterInterface, jobListForFilter[position])
    }

    override fun getItemCount(): Int = jobListForFilter.size

    override fun getFilter(): Filter {
        return object: Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                var filteredList = arrayListOf<JobModel>()

                if (p0.isNullOrEmpty()) {
                    filteredList = jobList
                } else {
                    val filterPattern = (p0 as String).toLowerCase(Locale.ROOT)
                    jobList.forEach { job ->
                        if (
                            job.nama_pekerjaan.toLowerCase(Locale.ROOT).contains(filterPattern) ||
                            job.nama_perusahaan.toLowerCase(Locale.ROOT).contains(filterPattern) ||
                            job.lokasi_pekerjaan.toLowerCase(Locale.ROOT).contains(filterPattern)) {
                            filteredList.add(job)
                        }
                    }
                }

                return FilterResults().apply {
                    this.values = filteredList
                }
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                jobListForFilter.clear()
                jobListForFilter.addAll(p1!!.values as ArrayList<JobModel>)
                jobAdapterInterface.onListSizeChanged(jobListForFilter.size)
                notifyDataSetChanged()
            }
        }
    }
}