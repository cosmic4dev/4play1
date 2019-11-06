package cosmic.com.mapprj.contract

import android.app.Application
import android.content.Context
import cosmic.com.mapprj.model.BlogList

interface SearchContract {

    interface view{
//        fun recieveDataList(dataList:List<BlogInfo>)
        fun dataToview(dataList:BlogList)
        fun showToast(msg:String)
        fun closeKeyboard()
    }

    interface presenter{
        fun requestBlogListData(text:String, context: Application)
        fun requestBlogList(text:String, context: Context)

    }
}