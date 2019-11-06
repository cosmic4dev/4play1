package cosmic.com.mapprj.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import cosmic.com.mapprj.R;
import cosmic.com.mapprj.model.BlogInfo;
import cosmic.com.mapprj.model.BlogList;

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.ViewHolder> {

    Context context;
    BlogList blogList=null;


    public SearchListAdapter(Context context, BlogList dataList) {
        this.context=context;
        this.blogList=dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.item_search,parent,false );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BlogInfo info=blogList.items.get( position );

        holder.titleView.setText(info.getTitle().replace( "<b>", "" ).replace( "</b>", "" ));
        holder.descriptionView.setText( info.getDescription().replace( "<b>", "" ).replace( "</b>", ""  ));
        holder.linkView.setText( info.getLink() );
    }

    @Override
    public int getItemCount() {
        return blogList.items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView titleView,descriptionView,linkView;

        public ViewHolder(@NonNull View itemView) {
            super( itemView );


            titleView=itemView.findViewById( R.id.titleView );
            descriptionView=itemView.findViewById( R.id.descriptionView );
            linkView=itemView.findViewById( R.id.linkView );

        }
    }
}
