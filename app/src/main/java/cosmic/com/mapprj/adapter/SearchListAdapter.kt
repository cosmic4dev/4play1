package cosmic.com.mapprj.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cosmic.com.mapprj.R
import cosmic.com.mapprj.model.BlogList

class SearchListAdapter(internal var context: Context, dataList: BlogList) : RecyclerView.Adapter<SearchListAdapter.ViewHolder>() {
    internal var blogList: BlogList? = null


    init {
        this.blogList = dataList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val info = blogList!!.items[position]

        holder.titleView.text = info.title.replace("<b>", "").replace("</b>", "")
        holder.descriptionView.text = info.description.replace("<b>", "").replace("</b>", "")
        holder.linkView.text = info.link
    }

    override fun getItemCount(): Int {
        return blogList!!.items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var titleView: TextView
        internal var descriptionView: TextView
        internal var linkView: TextView

        init {


            titleView = itemView.findViewById(R.id.titleView)
            descriptionView = itemView.findViewById(R.id.descriptionView)
            linkView = itemView.findViewById(R.id.linkView)

        }
    }
}
