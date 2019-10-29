package cosmic.com.mapprj.Retrofit;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit outInstance;

    private RetrofitClient(){

    }

    public static Retrofit getInstance(){
        if(outInstance ==null)
            outInstance=new Retrofit.Builder()
                    .baseUrl("https://jsonplaceholder.typicode.com/"  )
                    .addConverterFactory( GsonConverterFactory.create() )
                    .addCallAdapterFactory( RxJava2CallAdapterFactory.create() )//팩토리쓰면 observable이나 single로 뱉는다.
                    .build();
        return outInstance;
    }
}
