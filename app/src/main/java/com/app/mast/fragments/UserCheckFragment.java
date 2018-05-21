package com.app.mast.fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.mast.R;
import com.app.mast.activities.MainActivity;
import com.app.mast.app.AppController;
import com.app.mast.models.User;
import com.app.mast.retrofit.ApiClient;
import com.app.mast.retrofit.RetrofitObserver;
import com.app.mast.services.GitHubBasicApi;
import com.app.mast.utils.Constants;
import com.app.mast.utils.RecyclerAdapterUtil;
import com.app.mast.utils.Utility;

import java.util.List;
import java.util.Map;

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
    private RecyclerView recyclerView;
    private LinearLayout linearLayoutRecentSearch;

    public UserCheckFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_check, container, false);

        initViews();
        initListeners();
        validateUserName();


        initRecyclerView();
        return view;
    }

    private void initViews() {
        editTextUserName = view.findViewById(R.id.editTextUserName);
        buttonSubmit = view.findViewById(R.id.buttonSubmit);
        nameWrapper = view.findViewById(R.id.nameWrapper);
        recyclerView = view.findViewById(R.id.recyclerView);
        linearLayoutRecentSearch = view.findViewById(R.id.linearLayoutRecentSearch);

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
                    protected void onSuccess(final User user) {

                        if (AppController.getInstance().databaseHandler.getUser(user.getLogin()) == null) {
                            AppController.getInstance().databaseHandler.addUser(user);
                        } else {
                            AppController.getInstance().databaseHandler.updateUser(user);
                        }

                        Utility.getInstance().hideProgressBar(progressDialog);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(Constants.BUNDLE_KEY, user);
                        UserDetailsFragment userDetailsFragment = new UserDetailsFragment();
                        userDetailsFragment.setArguments(bundle);

                        ((MainActivity) getActivity()).replaceFragment(R.id.frameLayout, userDetailsFragment, UserDetailsFragment.class.getName());


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

    private void initRecyclerView() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        final List<User> userList = AppController.getInstance().databaseHandler.getAllUsers();
        linearLayoutRecentSearch.setVisibility(userList.isEmpty() ? View.GONE : View.VISIBLE);


        new RecyclerAdapterUtil
                .Builder(getContext(), userList, R.layout.recent_list_item_layout)
                .viewsList(R.id.textView)
                .bindView(new RecyclerAdapterUtil.BindDataHelper() {
                    @Override
                    public void bindView(int position, Map<Integer, View> viewMap) {
                        ((TextView) viewMap.get(R.id.textView)).setText(userList.get(position).getLogin());
                    }
                })
                .addClickListener(new RecyclerAdapterUtil.ItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position, RecyclerAdapterUtil recyclerAdapterUtil) {
                        switch (view.getId()) {
                            default:
                                MainActivity.USER = userList.get(position).getLogin();
                                Utility.getInstance().hideProgressBar(progressDialog);
                                Bundle bundle = new Bundle();
                                bundle.putParcelable(Constants.BUNDLE_KEY, userList.get(position));
                                UserDetailsFragment userDetailsFragment = new UserDetailsFragment();
                                userDetailsFragment.setArguments(bundle);

                                ((MainActivity) getActivity()).replaceFragment(R.id.frameLayout, userDetailsFragment, UserDetailsFragment.class.getName());
                                break;

                        }
                    }
                })
                .into(recyclerView);
    }
}
