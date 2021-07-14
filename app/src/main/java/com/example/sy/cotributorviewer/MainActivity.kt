package com.example.sy.cotributorviewer

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.widget.ArrayAdapter
import androidx.fragment.app.ListFragment
import androidx.fragment.app.commit
import com.example.sy.cotributorviewer.databinding.ActivityMainBinding
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity(), ContributorListFragment.OnContributorClickListener {
    companion object {
        const val TAG = "ContributorViewer"
        const val START_URL = "https://api.github.com/repos/googlesamples/android-architecture-components/contributors"
        const val RESP_HTTP_SUCCESS = 1
        const val RESP_HTTP_FAILURE = 2

        const val SAVE_KEY_LIST_CONTRIBUTOR = "list_contributor"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var listContributor: ArrayList<ContributorInfo>

    private val httpHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                RESP_HTTP_SUCCESS -> {
                    val jsonStr: String = msg.obj as String
                    val listType = object : TypeToken<List<ContributorInfo>>() {}.type
                    listContributor = Gson().fromJson<ArrayList<ContributorInfo>>(jsonStr, listType)
                    showListFragment()
                }
                RESP_HTTP_FAILURE -> {
                    showFailedDialog()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //onRestoreInstanceStateが呼ばれる前にlistContributorを復元したい
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState)
        }
        if (::listContributor.isInitialized) {
            Log.d(TAG, "onCreate: need not download info");
            showListFragment()
        } else {
            Log.d(TAG, "onCreate: need to download info");
            loadContributors()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(SAVE_KEY_LIST_CONTRIBUTOR, listContributor)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        if (!::listContributor.isInitialized) {
            val javaArray = savedInstanceState?.getParcelableArrayList<ContributorInfo>(SAVE_KEY_LIST_CONTRIBUTOR)
            listContributor = ArrayList<ContributorInfo>(javaArray)
        }
    }

    override fun onResume() {
        super.onResume()
    }

    /**
     * コントリビューターの情報をURLから取得
     */
    fun loadContributors() {
        START_URL.httpGet().response { request, response, result ->
            when (result) {
                is Result.Success -> {
                    if (response.statusCode == 200) {
                        val msg = httpHandler.obtainMessage(RESP_HTTP_SUCCESS)
                        msg.obj = String(response.data)
                        httpHandler.sendMessage(msg)
                    } else {
                        Log.i(TAG, "status code=" + response.statusCode)
                        httpHandler.sendEmptyMessage(RESP_HTTP_FAILURE)
                    }
                }
                is Result.Failure -> {
                    Log.e(TAG, "Error status code=" + response.statusCode)
                    httpHandler.sendEmptyMessage(RESP_HTTP_FAILURE)
                }
            }
        }
    }

    /**
     * リスト表示
     */
    fun showListFragment() {
        var fragment = supportFragmentManager.findFragmentByTag(ContributorListFragment::class.simpleName)
        if (fragment == null) {
            fragment = ContributorListFragment.create(listContributor)
            supportFragmentManager.commit {
                replace(R.id.fragment, fragment, ContributorListFragment::class.simpleName)
            }
        }
        if (fragment is ContributorListFragment)
            fragment.setOnContributorClickListener(this)
    }

    /**
     * 情報取得失敗時に表示
     */
    fun showFailedDialog() {
        val fragment = supportFragmentManager.findFragmentByTag(CommonDialogFragment::class.simpleName)
        if (fragment == null) {
            val dialog = CommonDialogFragment.create(
                    null,
                    getString(R.string.dialog_message_failed_get_info),
                    getString(R.string.dialog_button_ok),
                    null)
            dialog.onPositiveListener = DialogInterface.OnClickListener { dialog, which ->
                finish()
            }
            dialog.onNegativeListener = DialogInterface.OnClickListener { dialog, which ->
                finish()
            }
            dialog.show(supportFragmentManager, CommonDialogFragment::class.simpleName)
        }
    }

    override fun onClick(position: Int) {
        Log.d(TAG,"callback onClick position " + position)
        val info = listContributor[position]
        val fragment = ContributorDetailFragment.create(info)
        supportFragmentManager.commit {
            replace(R.id.fragment, fragment)
            setReorderingAllowed(true)
            addToBackStack(null)
        }
    }
}