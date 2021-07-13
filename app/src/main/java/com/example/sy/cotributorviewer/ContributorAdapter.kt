package com.example.sy.cotributorviewer

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.sy.cotributorviewer.databinding.ListItemContributorBinding

open class ContributorAdapter(context: Context, val contributorList: List<ContributorInfo>)
    : ArrayAdapter<ContributorInfo>(context, 0, contributorList) {
    companion object {
        const val TAG = MainActivity.TAG + ":Adapter"
    }

    private val inflater: LayoutInflater
    init {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val info = contributorList[position]

        // レイアウトの設定
        var view = convertView
        if (convertView == null) {
            view = ListItemContributorBinding.inflate(inflater, parent, false).root
        }
        val login = view?.findViewById<TextView>(R.id.login)
        login?.text = info.login
        val image = view?.findViewById<ImageView>(R.id.avator)
        Glide.with(context).load(info.avatar_url).into(image!!)

        return view!!
    }
}