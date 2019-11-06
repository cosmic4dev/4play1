package cosmic.com.mapprj.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import cosmic.com.mapprj.R;
import cosmic.com.mapprj.model.CalcuDistance;
import cosmic.com.mapprj.model.Office;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.DataViewHolder> {

    Context context;
    List<Office> officeArrayList ;
    final static String TAG = "아답터";
    Office office;

    private ClickListener clickListener;

    public interface ClickListener{
        void onItemClicked(int position);

    }
    public void setOnClickListener(ClickListener listener){
        clickListener =listener;
    }

    public DataAdapter(Context context, List<Office> items) {
        this.context = context;
        this.officeArrayList = items;

    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.item_xml, parent, false );

        return new DataViewHolder( view );
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, final int position) {
        office = officeArrayList.get( position );

        Uri imageUri = Uri.parse( office.image );

        holder.tv_title.setText( office.name );
        holder.tv_address.setText( office.address );
        holder.tv_call.setText( office.call );
        holder.tv_distance.setText((office.distance )+" KM");

//        ImageView target=holder.title_image;
//                Glide.with(context)
//                        .load( imageUri )
////                        .fitCenter()
//                        .centerCrop()
//                        .override(200,200  )
//                        .into( target );

        Picasso.get()
                .load( imageUri )
                .fit()
                .into( holder.title_image );

        holder.itemView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onItemClicked(position);
            }
        } );
    }

    @Override
    public int getItemCount() {
        if (officeArrayList == null) {
            return 0;
        }
        return officeArrayList.size();
    }

    public Office getItem(int position) {
        return officeArrayList.get( position );
    }



    public class DataViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title, tv_address, tv_call, tv_distance;
        ImageView title_image;

        public DataViewHolder(@NonNull View itemView) {
            super( itemView );
            title_image = itemView.findViewById( R.id.title_image );
            tv_title = itemView.findViewById( R.id.tv_title );
            tv_address = itemView.findViewById( R.id.tv_address );
            tv_call = itemView.findViewById( R.id.tv_call );
            tv_distance = itemView.findViewById( R.id.tv_distance );

        }


        public void setItem(CalcuDistance movieImageItem){

        }
    }
}
