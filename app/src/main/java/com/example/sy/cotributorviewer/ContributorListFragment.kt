package com.example.sy.cotributorviewer

import android.os.Bundle
import android.view.View
import android.widget.ListView
import androidx.fragment.app.ListFragment

open class ContributorListFragment : ListFragment() {
    companion object {
        const val TAG = MainActivity.TAG + ":ListFragment"
        private const val KEY_LIST_INFO = "list_info"

        fun create(listInfo: ArrayList<ContributorInfo>): ContributorListFragment {
            return ContributorListFragment().apply {
                val bundle = Bundle()
                bundle.putParcelableArrayList(KEY_LIST_INFO, listInfo)
                arguments = bundle
            }
        }
    }

    private var callback: OnContributorClickListener? = null

    interface OnContributorClickListener {
        fun onClick(position: Int)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val listInfo = arguments?.getParcelableArrayList<ContributorInfo>(KEY_LIST_INFO) as ArrayList<ContributorInfo>
        val adapter = ContributorAdapter(requireActivity(), listInfo)
        listAdapter = adapter
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        super.onListItemClick(l, v, position, id)
        callback?.onClick(position)
    }

    fun setOnContributorClickListener(listener: OnContributorClickListener) {
        callback = listener
    }
}