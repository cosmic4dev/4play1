package cosmic.com.mapprj.model

import android.os.Handler
import android.text.TextUtils
import android.util.Patterns
import cosmic.com.mapprj.contract.LoginContract

class LoginInteractor(private val loginListener: LoginContract.LoginListener) {

    fun login(loginCredentials: LoginCredentials) {


        Handler().postDelayed(Runnable {
            if (hasError(loginCredentials)) {
                return@Runnable
            }

            loginListener.onSuccess()
        }, 2000)
    }

    private fun hasError(loginCredentials: LoginCredentials): Boolean {
        val username = loginCredentials.username
        val password = loginCredentials.password

        if (TextUtils.isEmpty(username)) {
            loginListener.onFailed("이메일을 입력해주세요")
            return true
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
            loginListener.onFailed("정확한 이메일을 입력해주세요")
            return true
        }
        if (TextUtils.isEmpty(password)) {
            loginListener.onFailed("패스워드를 입력해주세요")
            return true
        }
        if (password!!.length < 6) {
            loginListener.onFailed("패스워드는 6자 이상입니다.")
            return true
        }

        return false
    }
}
