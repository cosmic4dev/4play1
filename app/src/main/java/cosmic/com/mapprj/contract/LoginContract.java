package cosmic.com.mapprj.contract;

public interface LoginContract {

    interface LoginView{
        void showProgressbar();
        void hideProgressbar();
        void onSuccess();
        void onFailed(String message);

    }

    interface LoginListener{
        void onStart();

        void onSuccess();

        void onFailed(String message);
    }
}
