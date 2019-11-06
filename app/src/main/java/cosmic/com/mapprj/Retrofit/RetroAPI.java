package cosmic.com.mapprj.Retrofit;

import java.util.List;

import cosmic.com.mapprj.model.NewsData;
import retrofit2.Call;
import retrofit2.http.GET;

public interface RetroAPI {

    @GET("posts")
    Call<List<NewsData>> getPosts();
}
