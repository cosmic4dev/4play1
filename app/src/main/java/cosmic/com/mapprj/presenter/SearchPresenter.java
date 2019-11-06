package cosmic.com.mapprj.presenter;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cosmic.com.mapprj.Retrofit.NaverAPI;
import cosmic.com.mapprj.Retrofit.NaverClient;
import cosmic.com.mapprj.Retrofit.NaverConsts;
import cosmic.com.mapprj.contract.SearchContract;
import cosmic.com.mapprj.model.BlogInfo;
import cosmic.com.mapprj.model.BlogList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class SearchPresenter implements SearchContract.presenter {

    SearchContract.view view;

    public static BlogList blogList;
    String titleValue,descriptionValue,linkValue;

    NaverAPI naverAPI;
    Retrofit retrofit;

    public SearchPresenter(SearchContract.view view) {
        this.view = view;
    }

    //retrofit
    @Override
    public void requestBlogListData(@NotNull String text, @NotNull Application context) {
        //retrofit init
        retrofit= NaverClient.getInstance();
        naverAPI=retrofit.create( NaverAPI.class );
        Call<BlogList> call=naverAPI.getInfo ( text );
        call.enqueue( new Callback<BlogList>() {
            @Override
            public void onResponse(Call<BlogList> call, retrofit2.Response<BlogList> response) {
                if(response.isSuccessful()){
                    BlogList blogList=response.body();

                    try{
                        //유효하지않는검색어처리
                        if(blogList.getItems().isEmpty()){
                            Log.d( "ch","유효하지않음" );
                            view.showToast( "유효하지 않는 검색입니다." );
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        view.showToast( "검색결과가 없습니다." );
                    }

                    view.dataToview( blogList );
                }
            }

            @Override
            public void onFailure(Call<BlogList> call, Throwable t) {
                t.printStackTrace();
            }
        } );
    }


    //volley
    @Override
    public void requestBlogList(@NotNull String text, @NotNull Context context) {

        String apiURL= "https://openapi.naver.com/v1/search/blog?query="+text+"&display=10&start=1&sort=sim";

        RequestQueue queue= Volley.newRequestQueue(context);

        StringRequest request=new StringRequest(
                Request.Method.GET,
                apiURL,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        processResponse(response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("error", "error " + error.getMessage());
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String>params=new HashMap<>();
                params.put("X-Naver-Client-Id", NaverConsts.CLIENT_ID );
                params.put("X-Naver-Client-Secret", NaverConsts.CLIENT_SECRET);
                return params;
            }
        };
        request.setShouldCache( false );
        queue.add( request );
    }

    private void processResponse(String response) {
        Gson gson = new Gson();

        try{
            blogList = gson.fromJson( response, BlogList.class );

            //유효하지않는검색어처리
            if(blogList.getItems().isEmpty()){
                view.showToast( "유효하지 않는 검색입니다." );
            }
        }catch (Exception e){
            e.printStackTrace();
            view.showToast( "검색결과가 없습니다." );
        }

        List<BlogInfo>dataList = new ArrayList<>();

        if(blogList !=null){
            for(int i = 0; i<blogList.getItems().size(); i++){
                BlogInfo blogInfo=blogList.getItems().get( i );
                titleValue=blogInfo.getTitle().replace( "<b>", "" ).replace( "</b>", "" );
                descriptionValue=blogInfo.getDescription().replace( "<b>", "" ).replace( "</b>", "" );;
                linkValue=blogInfo.getLink();

                dataList.add( new BlogInfo( titleValue,linkValue,descriptionValue ) );
            }
        }else{
            view.showToast( "결과없음" );
        }
//        view.recieveDataList( dataList );
//        view.closeKeyboard();
    }


}
