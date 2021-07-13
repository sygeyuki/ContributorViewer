package com.example.sy.cotributorviewer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.sy.cotributorviewer.databinding.FragmentContributorDetailBinding

class ContributorDetailFragment : Fragment(){
    companion object {
        const val KEY_INFO = "info"

        fun create(info: ContributorInfo): ContributorDetailFragment {
            return ContributorDetailFragment().apply {
                val bundle = Bundle()
                bundle.putParcelable(KEY_INFO, info)
                arguments = bundle
            }
        }
    }

    private var firstInfo : ContributorInfo? = null
    private lateinit var binding : FragmentContributorDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firstInfo = arguments?.getParcelable(KEY_INFO)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentContributorDetailBinding.inflate(inflater)
        //return inflater.inflate(R.layout.fragment_contributor_detail, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val image = binding.detailAvator
        Glide.with(this).load(firstInfo?.avatar_url).into(image)
        binding.valueName.text = firstInfo?.login
        binding.toGithub.setOnClickListener {
            val intent = Intent().apply {
                action = Intent.ACTION_VIEW
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                data = Uri.parse(firstInfo?.html_url)
            }
            activity?.startActivity(intent)
        }
    }
}