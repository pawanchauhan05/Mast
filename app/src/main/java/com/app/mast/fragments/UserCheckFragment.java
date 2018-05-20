package com.app.mast.fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.app.mast.R;
import com.app.mast.activities.MainActivity;
import com.app.mast.models.User;
import com.app.mast.retrofit.ApiClient;
import com.app.mast.retrofit.RetrofitObserver;
import com.app.mast.services.GitHubBasicApi;
import com.app.mast.utils.Constants;
import com.app.mast.utils.Utility;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.adapter.rxjava2.HttpException;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserCheckFragment extends Fragment implements View.OnClickListener {

    private EditText editTextUserName;
    private Button buttonSubmit;
    private View view;
    private TextInputLayout nameWrapper;
    private ProgressDialog progressDialog;

    public UserCheckFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user_check, container, false);
        initViews();
        initListeners();
        validateUserName();
        return view;
    }

    private void initViews() {
        editTextUserName = view.findViewById(R.id.editTextUserName);
        buttonSubmit = view.findViewById(R.id.buttonSubmit);
        nameWrapper = view.findViewById(R.id.nameWrapper);

        ((MainActivity) getActivity()).setToolBarTitle("Check User");
        progressDialog = new ProgressDialog(getContext());
    }

    private void validateUserName() {
        editTextUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(editTextUserName.getText().toString().trim())) {
                    nameWrapper.setErrorEnabled(true);
                    nameWrapper.setError("Enter User Name");
                } else {
                    nameWrapper.setErrorEnabled(false);
                }
            }
        });
    }

    private void initListeners() {
        buttonSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonSubmit:

                if (TextUtils.isEmpty(editTextUserName.getText().toString().trim())) {
                    nameWrapper.setErrorEnabled(true);
                    nameWrapper.setError("Please Enter Name");
                } else {
                    getUser(editTextUserName.getText().toString().trim());
                }
                break;
        }
    }

    private void getUser(final String userName) {
        Utility.getInstance().showProgressBar("Please wait", "Connecting to server...", progressDialog);
        MainActivity.USER = userName;
        ApiClient
                .getClient()
                .create(GitHubBasicApi.class)
                .getUser(userName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RetrofitObserver<User>() {
                    @Override
                    protected void onSuccess(User user) {
                        Utility.getInstance().hideProgressBar(progressDialog);
                        if (user != null && TextUtils.isEmpty(user.getMessage())) {
                            Bundle bundle = new Bundle();
                            bundle.putParcelable(Constants.BUNDLE_KEY, user);
                            UserDetailsFragment userDetailsFragment = new UserDetailsFragment();
                            userDetailsFragment.setArguments(bundle);

                            ((MainActivity) getActivity()).replaceFragment(R.id.frameLayout, userDetailsFragment, UserDetailsFragment.class.getName());

                        }
                    }

                    @Override
                    protected void onFailure(Throwable e) {
                        Utility.getInstance().hideProgressBar(progressDialog);
                        if (e instanceof HttpException) {
                            if (((HttpException) e).code() == 404) {
                                Utility.getInstance().showRedToast("User Not Found", getContext());
                            }
                        }
                    }
                });
    }
}
