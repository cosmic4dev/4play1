package cosmic.com.mapprj.Retrofit;

import java.util.List;

import cosmic.com.mapprj.model.NewsData;
import retrofit2.http.GET;

public interface RetroAPI {

    @GET("posts")
    io.reactivex.Observable<List<NewsData>> getPosts();
}
