package cosmic.com.mapprj.view

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cosmic.com.mapprj.R
import cosmic.com.mapprj.adapter.SearchListAdapter
import cosmic.com.mapprj.contract.SearchContract
import cosmic.com.mapprj.model.BlogList
import cosmic.com.mapprj.presenter.SearchPresenter
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity(), SearchContract.view {


    internal lateinit var presenter: SearchPresenter
    internal lateinit var searchListAdapter: SearchListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        inputText?.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchBlogAction()
                true
            } else {
                false
            }
        }

        presenter = SearchPresenter(this)
        recyclerView_search!!.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        recyclerView_search!!.layoutManager = layoutManager

    }

    private fun searchBlogAction() {
        if (inputText == null) {
            showToast("검색어를 입력해주세요.")
        }
        val searchWord = inputText.text.toString()
        inputText.text = null
        presenter.requestBlogListData(searchWord, application)
        closeKeyboard()
    }

    override fun showToast(msg: String) {
        Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
    }

    override fun closeKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(inputText.windowToken, 0)
    }

    override fun dataToview(dataList: BlogList) {
        val layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        recyclerView_search!!.layoutManager = layoutManager
        recyclerView_search!!.itemAnimator = DefaultItemAnimator()
        searchListAdapter = SearchListAdapter(applicationContext, dataList)
        recyclerView_search!!.adapter = searchListAdapter
        searchListAdapter.notifyDataSetChanged()
    }
}