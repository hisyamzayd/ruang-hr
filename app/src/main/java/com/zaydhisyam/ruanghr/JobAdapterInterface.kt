package com.zaydhisyam.ruanghr

import android.widget.ImageView

interface JobAdapterInterface {

    fun onParentViewClicked(job: JobModel)

    fun onListSizeChanged(size: Int)

    fun setGlidePhoto(job: JobModel, imageView: ImageView)
}