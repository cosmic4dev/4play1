package cosmic.com.mapprj.view;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import cosmic.com.mapprj.R;
import cosmic.com.mapprj.contract.LoginContract;
import cosmic.com.mapprj.model.LoginCredentials;
import cosmic.com.mapprj.presenter.LoginPresenter;

public class LoginActivity extends AppCompatActivity  implements LoginContract.LoginView {


    private ProgressBar progressBar;
    private LoginPresenter presenter;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );

        presenter = new LoginPresenter( this );
        final EditText emailView = findViewById( R.id.email_input );
        final EditText passwordView = findViewById( R.id.password_input );
        Button loginButton = findViewById( R.id.loginBtn );

        loginButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailView.getText().toString();
                String password = passwordView.getText().toString();

                LoginCredentials credentials = new LoginCredentials( email, password );
                presenter.start( credentials );

                showProgressbar();
            }
        } );

        Button pass = findViewById( R.id.passBtn );
        pass.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSuccess();
            }
        } );

    }
        @Override
        public void showProgressbar() {
            dialog=new Dialog( this );
            dialog.setContentView( R.layout.dialog );
            dialog.setCancelable( true );
            TextView msg = dialog.findViewById( R.id.dialogText );
            msg.setText( "진행중.." );
            dialog.show();
        }

        @Override
        public void hideProgressbar() {
            dialog.dismiss();
        }

        @Override
        public void onSuccess() {
            Intent loginSuccessIntent = new Intent(this,MainActivity.class);
            startActivity(loginSuccessIntent);
            finish();
        }

        @Override
        public void onFailed(String message) {
            Toast.makeText( this,message,Toast.LENGTH_SHORT ).show();
            dialog.dismiss();
        }
}
