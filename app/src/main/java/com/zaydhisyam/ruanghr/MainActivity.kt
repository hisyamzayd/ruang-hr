package com.zaydhisyam.ruanghr

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() , JobAdapterInterface {

    private var data = ArrayList<JobModel>()
    private lateinit var jobListAdapter: JobAdapter
    private lateinit var storageRef: StorageReference

    companion object {
        var refreshData = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        jobListAdapter = JobAdapter(data, this@MainActivity)
        rv_job.layoutManager = LinearLayoutManager(this@MainActivity)
        rv_job.adapter = jobListAdapter
        jobListAdapter.setJobList()

        storageRef = Firebase.storage.getReference("jobs_photo")

        // data = Utils.getDummyJobList(10)
        progress_circular.visibility = View.VISIBLE
        getDataFromFirebase()

        //set filter box
        til_filter_job_list.editText!!.imeOptions = EditorInfo.IME_ACTION_DONE
        til_filter_job_list.editText!!.setOnEditorActionListener { _, _, _ ->
            val inputMethodManager = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(til_filter_job_list.editText!!.windowToken, 0)
            true
        }
        til_filter_job_list.editText!!.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //gangapangapain
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                jobListAdapter.filter.filter(p0)
            }
            override fun afterTextChanged(p0: Editable?) {
                //gangapangapain
            }
        })

        //set float button onclick
        btn_add.setOnClickListener {
            startActivity(Intent(this, JobAddEditActivity::class.java).apply {
                this.putExtra(JobAddEditActivity.IS_EDIT, false)
            })
        }
    }

    override fun onResume() {
        super.onResume()
        if (refreshData) {
            progress_circular.visibility = View.VISIBLE
            getDataFromFirebase()
            refreshData = false
        }
    }

    override fun onParentViewClicked(job: JobModel) {
        startActivity(Intent(this, JobDetailActivity::class.java).apply {
            this.putExtra(JobDetailActivity.INTENT_EXTRA, job)
        })
    }

    override fun onListSizeChanged(size: Int) {
        if (size == 0) {
            tv_empty_list_search.visibility = View.VISIBLE
        } else {
            tv_empty_list_search.visibility = View.GONE
        }
    }

    override fun setGlidePhoto(job: JobModel, imageView: ImageView) {
        GlideApp.with(this)
            .load(storageRef.child(job.foto_pekerjaan))
            .into(imageView)
    }

    private fun getDataFromFirebase() {
        Firebase.database.getReference("jobs")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    data.clear()
                    for (job: DataSnapshot in snapshot.children) {
                        data.add(job.getValue(JobModel::class.java)!!)
                    }
                    jobListAdapter.setJobList()
                    if (data.isEmpty()) {
                        tv_empty_list.visibility = View.VISIBLE
                    }
                    progress_circular.visibility = View.GONE
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MainActivity, "database cancelled! ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}