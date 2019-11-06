package cosmic.com.mapprj.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cosmic.com.mapprj.R;
import cosmic.com.mapprj.model.NewsData;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    final static String TAG = "뉴스아답터";

    Context context;
    List<NewsData>newsList;
    NewsData newsData;

    public NewsAdapter(Context context, List<NewsData> newsList) {
        this.context = context;
        this.newsList = newsList;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from( parent.getContext() ).inflate( R.layout.post_layout,parent,false );

        return new NewsViewHolder( view );
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        newsData=newsList.get( position );

        holder.txt_author.setText( String.valueOf( newsList.get( position ).getUserId() ));
        holder.txt_title.setText( String.valueOf( newsList.get( position ).getTitle() ) );
        holder.txt_content.setText( new StringBuilder( newsList.get( position ).getBody().substring( 0,20 ))
        .append( "...." ).toString());



    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder{

        TextView txt_title,txt_content,txt_author;

        public NewsViewHolder(@NonNull View itemView) {
            super( itemView );

            txt_author= itemView.findViewById( R.id.txt_author );
            txt_title= itemView.findViewById( R.id.txt_title );
            txt_content = itemView.findViewById( R.id.txt_content );


        }
    }
}
