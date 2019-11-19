package cosmic.com.mapprj.view

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cosmic.com.mapprj.R
import cosmic.com.mapprj.contract.LoginContract
import cosmic.com.mapprj.model.LoginCredentials
import cosmic.com.mapprj.presenter.LoginPresenter
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivityKotlin : AppCompatActivity() ,LoginContract.LoginView {

     var presenter: LoginPresenter? = null
     var dialog: Dialog?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        presenter = LoginPresenter(this)

        loginBtn.setOnClickListener {
            val email=email_input.text.toString()
            val password = password_input.text.toString()
            val credentials=LoginCredentials(email,password)
            presenter!!.start(credentials)
        }

        passBtn.setOnClickListener {
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
        dialog!!.dismiss()
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
