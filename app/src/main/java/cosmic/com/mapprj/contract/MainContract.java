package cosmic.com.mapprj.contract;

public interface MainContract {

    interface view{
        void  onBackPressedListener();
        void showToast(String msg);
        void closeFragment();

    }
         interface presenter{

    }
}
