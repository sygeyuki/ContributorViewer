package com.example.sy.cotributorviewer

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ListView
import androidx.fragment.app.ListFragment

open class ContributorListFragment : ListFragment() {
    companion object {
        const val TAG = MainActivity.TAG + ":ListFragment"
    }
    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        super.onListItemClick(l, v, position, id)
        Log.d(TAG, "onClick " + position)
        val info = listAdapter?.getItem(position) as ContributorInfo
        val intent = Intent().apply {
            action = Intent.ACTION_VIEW
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            data = Uri.parse(info.html_url)
        }
        activity?.startActivity(intent)
    }
}