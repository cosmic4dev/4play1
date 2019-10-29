package cosmic.com.mapprj.presenter;

import cosmic.com.mapprj.contract.MainContract;

public class MainPresenter implements MainContract.presenter {

    MainContract.view view;


    public MainPresenter(MainContract.view view) {
        this.view = view;
    }



}
