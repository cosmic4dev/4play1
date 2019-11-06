package cosmic.com.mapprj.contract

interface LoginContract {

    interface LoginView {
        fun showProgressbar()
        fun hideProgressbar()
        fun onSuccess()
        fun onFailed(message: String)

    }

    interface LoginListener {
        fun onStart()
        fun onSuccess()
        fun onFailed(message: String)
    }
}
