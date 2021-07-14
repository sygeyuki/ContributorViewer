package com.example.sy.cotributorviewer

import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.sy.cotributorviewer.databinding.FragmentContributorDetailBinding
import com.example.sy.cotributorviewer.databinding.ListItemDetailBinding
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.google.gson.Gson

class ContributorDetailFragment : Fragment(){
    companion object {
        private const val TAG = MainActivity.TAG + ":Detail"
        const val KEY_INFO = "info"
        const val TWITTER_URL = "twitter://user?screen_name="

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
        firstInfo?.url?.httpGet()?.response { request, response, result ->
            when (result) {
                is Result.Success -> {
                    if (response.statusCode == 200) {
                        val additionalInfo = Gson().fromJson<AdditionalInfo>(String(response.data), AdditionalInfo::class.java)
                        val msg = updateViewHandler.obtainMessage()
                        msg.obj = additionalInfo
                        updateViewHandler.sendMessage(msg)
                    } else {
                        Log.i(TAG, "status code=" + response.statusCode)
                        //do nothing
                    }
                }
                is Result.Failure -> {
                    Log.e(TAG, "Error status code=" + response.statusCode)
                    //do nothing
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentContributorDetailBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val image = binding.detailAvator
        Glide.with(this).load(firstInfo?.avatar_url).into(image)
    }

    fun updateView(info: AdditionalInfo) {
        //名前
        if (info.name != null) {
            binding.detailName.text = info.name
            binding.detailLogin.text = String.format(getString(R.string.detail_info_login_format), firstInfo?.login)
        } else {
            //名前がない場合はloginを名前とする
            binding.detailName.text = firstInfo?.login
            binding.detailLogin.visibility = View.INVISIBLE
        }
        //Github起動ボタン
        if (!firstInfo?.html_url.isNullOrBlank()) {
            binding.toGithub.setOnClickListener {
                val intent = Intent().apply {
                    action = Intent.ACTION_VIEW
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    data = Uri.parse(firstInfo?.html_url)
                }
                activity?.startActivity(intent)
            }
            binding.toGithub.visibility = View.VISIBLE
        } else {
            binding.toGithub.visibility = View.GONE
        }
        //Twitter起動ボタン
        if (!info.twitter_username.isNullOrBlank()) {
            binding.toTwitter.setOnClickListener {
                val intent = Intent().apply {
                    action = Intent.ACTION_VIEW
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    data = Uri.parse(TWITTER_URL + info.twitter_username)
                }
                try {
                    activity?.startActivity(intent)
                } catch (e: Exception) {
                    Log.e(TAG, "No Activity for twitter")
                    showErrorDialog(R.string.dialog_message_failed_start_activity)
                }
            }
            binding.toTwitter.visibility = View.VISIBLE
        } else {
            binding.toTwitter.visibility = View.GONE
        }
        //Blog起動ボタン
        if (!info.blog.isNullOrBlank()) {
            binding.toBlog.setOnClickListener {
                var uri = Uri.parse(info.blog)
                if (uri.scheme.isNullOrBlank()) {
                    uri = Uri.parse("https://" + info.blog)
                }
                val intent = Intent().apply {
                    action = Intent.ACTION_VIEW
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    data = uri
                }
                activity?.startActivity(intent)
            }
            binding.toBlog.visibility = View.VISIBLE
        } else {
            if (binding.toTwitter.visibility == View.VISIBLE)
                binding.toBlog.visibility = View.GONE
            else
                binding.toBlog.visibility = View.INVISIBLE
        }
        //その他情報
        val adapter = DetailItemAdapter(requireContext(), createDisplayInfoList(info))
        binding.detailList.adapter = adapter
    }

    fun showErrorDialog(resId: Int) {
        val fragment = requireActivity().supportFragmentManager.findFragmentByTag(CommonDialogFragment::class.simpleName)
        if (fragment == null) {
            val dialog = CommonDialogFragment.create(
                    null,
                    getString(resId),
                    getString(R.string.dialog_button_ok),
                    null)
            dialog.show(requireActivity().supportFragmentManager, CommonDialogFragment::class.simpleName)
        }
    }

    fun createDisplayInfoList(info: AdditionalInfo) : List<DisplayInfo>{
        val list = ArrayList<DisplayInfo>();
        var label: String
        label = getString(R.string.detail_info_label_location)
        list.add(createDisplayInfo(label, info.location))
        label = getString(R.string.detail_info_label_company)
        list.add(createDisplayInfo(label, info.company))
        label = getString(R.string.detail_info_label_bio)
        list.add(createDisplayInfo(label, info.bio))
        label = getString(R.string.detail_info_label_repos)
        list.add(createDisplayInfo(label, info.public_repos.toString()))
        label = getString(R.string.detail_info_label_followers)
        list.add(createDisplayInfo(label, info.followers.toString()))
        label = getString(R.string.detail_info_label_contribute)
        list.add(createDisplayInfo(label, firstInfo?.contributions.toString()))

        //更新日時
        label = getString(R.string.detail_info_label_update_at)
        val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        val dt = df.parse(info.updated_at)
        val df2 = SimpleDateFormat("yyyy/M/d H:m:s")
        list.add(createDisplayInfo(label, df2.format(dt)))

        return list
    }

    fun createDisplayInfo(label: String, value: String?): DisplayInfo {
        var retValue = value
        if (value == null) {
            retValue = getString(R.string.detail_info_value_unknown)
        }
        return DisplayInfo(label, retValue!!)
    }

    private val updateViewHandler = object: Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            val info = msg.obj as AdditionalInfo
            updateView(info)
        }
    }

    data class AdditionalInfo(
            val login: String,
            val name: String,
            val company: String,
            val blog: String,
            val location: String,
            val email: String,
            val hireable: String,
            val bio: String,
            val twitter_username: String,
            val public_repos: Int,
            val public_gists: Int,
            val followers: Int,
            val following: Int,
            val created_at: String,
            val updated_at: String
    ) {
        override fun toString(): String {
            return name?: login
        }
    }

    data class DisplayInfo(
            val label: String,
            val value: String
    )

    class DetailItemAdapter(context: Context, val infoList: List<DisplayInfo>)
        : ArrayAdapter<DisplayInfo>(context, 0, infoList) {

        private val inflater: LayoutInflater
        init {
            inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val info = infoList[position]
            var view = convertView
            if (convertView == null) {
                view = ListItemDetailBinding.inflate(inflater, parent, false).root
            }
            val label = view?.findViewById<TextView>(R.id.detailItemLabel)
            label?.text = info.label
            val value = view?.findViewById<TextView>(R.id.detailItemValue)
            value?.text = info.value

            return view!!
        }

        override fun isEnabled(position: Int): Boolean {
            return false
        }
    }
}