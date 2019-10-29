package cosmic.com.mapprj.presenter;

import android.widget.TextView;

import butterknife.BindView;
import cosmic.com.mapprj.R;
import cosmic.com.mapprj.contract.NewsContract;

public class NewsPresenter implements NewsContract.presenter {

    @BindView( R.id.tv_news_title )
    TextView tv_news_title;
    @BindView( R.id.tv_description )
    TextView tv_description;

    public static final String URL =
            "http://www.startuptoday.kr/rss/allArticle.xml"; //retrofit Json??

    @Override
    public void CallNews() {

    }


    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
