package cosmic.com.mapprj.model;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Patterns;

import cosmic.com.mapprj.view.LoginActivity;
import cosmic.com.mapprj.contract.LoginContract;

public class LoginInteractor {
    private LoginContract.LoginListener loginListener;
    private LoginActivity loginActivity;

    public LoginInteractor(LoginContract.LoginListener loginListener) {
        this.loginListener = loginListener;
    }

    public void login(final LoginCredentials loginCredentials){


        new Handler(  ).postDelayed( new Runnable() {
            @Override
            public void run() {
                if(hasError( loginCredentials )){
                    return;
                }

                loginListener.onSuccess();

            }
        },2000 );
    }

    private boolean hasError(LoginCredentials loginCredentials){
        String username = loginCredentials.getUsername();
        String password = loginCredentials.getPassword();

        if (TextUtils.isEmpty(username )){
            loginListener.onFailed( "이메일을 입력해주세요" );
            return true;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher( username ).matches()){
            loginListener.onFailed( "정확한 이메일을 입력해주세요" );
            return true;
        }
        if(TextUtils.isEmpty( password )){
            loginListener.onFailed( "패스워드를 입력해주세요" );
            return true;
        }
        if (password.length() < 6){
            loginListener.onFailed("패스워드는 6자 이상입니다.");
            return true;
        }

        return false;
    }
}
