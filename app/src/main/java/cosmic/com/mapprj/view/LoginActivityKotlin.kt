package cosmic.com.mapprj.view

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cosmic.com.mapprj.R
import cosmic.com.mapprj.contract.LoginContract
import cosmic.com.mapprj.model.LoginCredentials
import cosmic.com.mapprj.presenter.LoginPresenter

class LoginActivityKotlin : AppCompatActivity() ,LoginContract.LoginView {


    //변수는 사용하기전에 반드시 초기화
     var presenter: LoginPresenter? = null
     var dialog: Dialog?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        presenter = LoginPresenter(this)

        val emailView = findViewById<EditText>(R.id.email_input)
        val passwordView = findViewById<EditText>(R.id.password_input)
        val loginButton = findViewById<Button>(R.id.loginBtn)

        loginButton.setOnClickListener {
            val email = emailView.text.toString()
            val password = passwordView.text.toString()

            val credentials =LoginCredentials(email,password)
            presenter!!.start(credentials)
        }

        var pass = findViewById<Button>(R.id.passBtn)
        pass.setOnClickListener {
          showProgressbar()
          onSuccess()
        }
    }


    override fun showProgressbar() {
        dialog = Dialog(this)
        dialog!!.setContentView(R.layout.dialog)
        dialog!!.setCancelable(true)

        val msg= dialog!!.findViewById<TextView>(R.id.dialogText)
        msg.setText("진행중..")
        dialog!!.show()

    }

    override fun hideProgressbar() {
        dialog!!.dismiss()  //!!은 강제로 not null임을 선언.
    }

    override fun onSuccess() {
        hideProgressbar();
        val intent=Intent(this,MainActivity::class.java)
        startActivity(intent)
        finish()

    }

    override fun onFailed(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
        dialog!!.dismiss()
    }
}
