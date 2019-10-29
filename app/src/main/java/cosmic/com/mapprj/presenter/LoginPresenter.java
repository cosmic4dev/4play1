package cosmic.com.mapprj.presenter;

import cosmic.com.mapprj.contract.LoginContract;
import cosmic.com.mapprj.model.LoginCredentials;
import cosmic.com.mapprj.model.LoginInteractor;

public class LoginPresenter implements LoginContract.LoginListener {

    private LoginContract.LoginView loginView;

    private LoginInteractor loginInteractor;

    public LoginPresenter(LoginContract.LoginView loginView) {
        this.loginView = loginView;
        loginInteractor = new LoginInteractor(this);
    }

    public void start(LoginCredentials credentials){
//        loginView.showProgressbar();
        loginInteractor.login( credentials );
    }

    @Override
    public void onStart() {
        loginView.showProgressbar();
    }

    @Override
    public void onSuccess() {
//        loginView.hideProgressbar();
        loginView.onSuccess();

    }

    @Override
    public void onFailed(String message) {
//        loginView.hideProgressbar();
        loginView.onFailed( message );


    }
}
