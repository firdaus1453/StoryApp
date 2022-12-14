package com.firdaus1453.storyapp.presentation.stackwidget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Binder
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.bumptech.glide.Glide
import com.firdaus1453.storyapp.R
import com.firdaus1453.storyapp.data.Result
import com.firdaus1453.storyapp.data.StoryRepository
import com.firdaus1453.storyapp.di.Injection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.IOException

internal class StackRemoteViewsFactory(private val mContext: Context) :
    RemoteViewsService.RemoteViewsFactory {

    private val mWidgetItems = ArrayList<Bitmap>()
    private lateinit var storyRepository: StoryRepository

    override fun onCreate() {
        storyRepository = Injection.provideRepository(mContext)
    }

    override fun onDataSetChanged() {
        val identityToken = Binder.clearCallingIdentity()
        fetchData()
        Binder.restoreCallingIdentity(identityToken)
    }

    private fun fetchData() {
        mWidgetItems.clear()
        CoroutineScope(Dispatchers.IO).launch {
            storyRepository.getStories().collectLatest { result ->
                when (result) {
                    is Result.Success -> {
                        val stories = result.data
                        stories?.forEach {
                            try {
                                val bitmap = try {
                                    Glide.with(mContext)
                                        .asBitmap()
                                        .load(it.photoUrl)
                                        .submit()
                                        .get()
                                } catch (e: Exception) {
                                    BitmapFactory.decodeResource(
                                        mContext.resources,
                                        R.drawable.ic_broken_image_black
                                    )
                                }
                                mWidgetItems.add(bitmap)
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }
                    }
                    else -> {
                        Log.d("Widget", "Error")
                    }
                }
            }
        }
    }

    override fun onDestroy() {

    }

    override fun getCount(): Int = mWidgetItems.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(mContext.packageName, R.layout.widget_item)
        rv.setImageViewBitmap(R.id.imageView, mWidgetItems[position])
        val fillInIntent = Intent()
        rv.setOnClickFillInIntent(R.id.imageView, fillInIntent)
        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(i: Int): Long = 0

    override fun hasStableIds(): Boolean = false

}