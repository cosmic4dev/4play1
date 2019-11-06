package cosmic.com.mapprj.presenter

import cosmic.com.mapprj.contract.LoginContract
import cosmic.com.mapprj.model.LoginCredentials
import cosmic.com.mapprj.model.LoginInteractor

class LoginPresenter(private val loginView: LoginContract.LoginView) : LoginContract.LoginListener {

    private val loginInteractor: LoginInteractor

    init {
        loginInteractor = LoginInteractor(this)
    }

    fun start(credentials: LoginCredentials) {
        loginView.showProgressbar()
        loginInteractor.login(credentials)
    }

    override fun onStart() {
        loginView.showProgressbar()
    }

    override fun onSuccess() {
        loginView.hideProgressbar()
        loginView.onSuccess()

    }

    override fun onFailed(message: String) {
        loginView.hideProgressbar()
        loginView.onFailed(message)


    }
}
