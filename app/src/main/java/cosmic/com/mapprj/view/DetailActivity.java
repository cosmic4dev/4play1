package cosmic.com.mapprj.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Optional;
import cosmic.com.mapprj.R;
import cosmic.com.mapprj.model.Office;

public class DetailActivity extends AppCompatActivity {

    final static String TAG = "DATACTivity";

    @BindView(R.id.bookingButton)
    Button bookingButton;
    @BindView(R.id.titleImage)
    ImageView titleImage;
    @BindView(R.id.audience_rate)
    TextView audience_rate;
    @BindView(R.id.detail_tv_name)
    TextView detail_tv_name;
    @BindView(R.id.detail_tv_url)
    TextView detail_tv_url;
    @BindView(R.id.reservation_rate)
    TextView reservation_rate;
    @BindView(R.id.audience)
    TextView audience;
    @BindView(R.id.detail_tv_address)
    TextView detail_tv_address;
    @BindView(R.id.detail_tv_call)
    TextView detail_tv_call;
    @BindView(R.id.ratingBar)
    RatingBar ratingBar;

    Office office;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_detail );
        getWindow().addFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN );
        ButterKnife.bind( this );

        requestDetailData();
        bookingButton = findViewById( R.id.bookingButton );
        bookingButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = office.call;
                Intent intent = new Intent( Intent.ACTION_DIAL, Uri.fromParts( "tel", phone, null ) );
                startActivity( intent );

            }
        } );
    }

    public void requestDetailData() {

        Intent intent = getIntent();
        final String getTitle = intent.getStringExtra( "spaceName" );
        final DatabaseReference mFiReference = FirebaseDatabase.getInstance().getReference();
        //주요했던 쿼리
        Query query = mFiReference.child( "coworkspaceInfo" ).orderByChild( "name" ).equalTo( getTitle );
        query.addValueEventListener( new ValueEventListener() {
            @Optional
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    office = postSnapshot.getValue( Office.class );
                    Log.d( TAG, "확인:" + office.name );
                    detail_tv_name.setText( office.name );
                    detail_tv_address.setText( office.address );
                    detail_tv_url.setText( office.url );
                    detail_tv_call.setText( office.call );

                    String uri = office.image;
                    //picasso
                    Picasso.get()
                            .load( uri )
                            .resize( 768, 432 )//사이즈 설정해주니 에러
                            .centerCrop()
                            .into( titleImage );
                    //glide
//                GlideDrawableImageViewTarget imageViewTarget =new GlideDrawableImageViewTarget(titleImage);
//                Glide.with(titleImage.getContext())
//                        .load( uri )
//                        .fitCenter()
//                        .override(768,432  )
//                        .into( imageViewTarget );
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        } );
    }
}