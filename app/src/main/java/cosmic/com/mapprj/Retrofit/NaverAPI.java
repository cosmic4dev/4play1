package cosmic.com.mapprj.Retrofit;

import cosmic.com.mapprj.model.BlogList;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface NaverAPI {

    @Headers( {
            "X-Naver-Client-Id: " + NaverConsts.CLIENT_ID,
            "X-Naver-Client-Secret: " + NaverConsts.CLIENT_SECRET
    } )

    @GET("v1/search/blog")
    Call<BlogList> getInfo(@Query( "query" ) String word);

}
