package com.app.mast.fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
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
import com.app.mast.app.AppController;
import com.app.mast.models.Issue;
import com.app.mast.models.IssueLocal;
import com.app.mast.models.LocalDBObject;
import com.app.mast.models.Repository;
import com.app.mast.models.User;
import com.app.mast.retrofit.ApiClient;
import com.app.mast.retrofit.RetrofitObserver;
import com.app.mast.services.GitHubBasicApi;
import com.app.mast.utils.Constants;
import com.app.mast.utils.FileDBUtils;
import com.app.mast.utils.Utility;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.adapter.rxjava2.HttpException;

/**
 * A simple {@link Fragment} subclass.
 */
public class EntryFragment extends Fragment implements View.OnClickListener {


    public EntryFragment() {
        // Required empty public constructor
    }

    private EditText editTextOrg, editTextRepo;
    private Button buttonSubmit;
    private TextInputLayout orgWrapper, repoWrapper;
    private ProgressDialog progressDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_entry, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buttonSubmit = view.findViewById(R.id.buttonSubmit);
        editTextOrg = view.findViewById(R.id.editTextOrg);
        editTextRepo = view.findViewById(R.id.editTextRepo);
        orgWrapper = view.findViewById(R.id.orgWrapper);
        repoWrapper = view.findViewById(R.id.repoWrapper);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((MainActivity) getActivity()).setToolBarTitle("Check User");
        initListeners();
        validateOrganisationName();
        validateRepository();

        progressDialog = new ProgressDialog(getContext());
    }

    private void validateOrganisationName() {
        editTextOrg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(editTextOrg.getText().toString().trim())) {
                    orgWrapper.setErrorEnabled(true);
                    orgWrapper.setError("Enter User Name");
                } else {
                    orgWrapper.setErrorEnabled(false);
                }
            }
        });
    }

    private void validateRepository() {
        editTextRepo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(editTextRepo.getText().toString().trim())) {
                    repoWrapper.setErrorEnabled(true);
                    repoWrapper.setError("Enter User Name");
                } else {
                    repoWrapper.setErrorEnabled(false);
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
                if (TextUtils.isEmpty(editTextOrg.getText().toString().trim())) {
                    orgWrapper.setErrorEnabled(true);
                    orgWrapper.setError("Please Organisation Name");
                } else if (TextUtils.isEmpty(editTextRepo.getText().toString().trim())) {
                    repoWrapper.setErrorEnabled(true);
                    repoWrapper.setError("Please Repository Name");
                } else {

                    FileDBUtils<LocalDBObject> fileDBUtils = new FileDBUtils<>(getActivity(), "issues", LocalDBObject.class, "/db/custom");
                    LocalDBObject localDBObject = fileDBUtils.readObject();
                    String key = String.format("%s,%s", editTextOrg.getText().toString().trim(), editTextRepo.getText().toString().trim());
                    if(localDBObject != null && localDBObject.getMap().containsKey(key)) {

                        IssueLocal issueLocal =  localDBObject.getMap().get(key);

                        Calendar calendarActual = Calendar.getInstance();
                        calendarActual.setTimeInMillis(issueLocal.getTimestamp());
                        calendarActual.add(Calendar.MINUTE, 30);

                        Calendar calendar = Calendar.getInstance();

                        if(calendarActual.getTimeInMillis() < calendar.getTimeInMillis()) {
                            getOrganisationDetails(editTextOrg.getText().toString().trim(), editTextRepo.getText().toString().trim());
                        } else {
                            Bundle bundle = new Bundle();
                            bundle.putParcelableArrayList(Constants.BUNDLE_KEY, (ArrayList<? extends Parcelable>) issueLocal.getIssueList());
                            EntryListFragment entryListFragment = new EntryListFragment();
                            entryListFragment.setArguments(bundle);
                            ((MainActivity) getActivity()).replaceFragment(R.id.frameLayout, entryListFragment, EntryListFragment.class.getName());
                        }
                    } else {
                        getOrganisationDetails(editTextOrg.getText().toString().trim(), editTextRepo.getText().toString().trim());
                    }


                }
                break;
        }
    }

    private void getOrganisationDetails(final String orgName, final String repoName) {
        Utility.getInstance().showProgressBar("Please wait", "Connecting to server...", progressDialog);
        Map<String, String> map = new HashMap<>();
        map.put("state", "all");
        ApiClient
                .getClient()
                .create(GitHubBasicApi.class)
                .getDetails(orgName, repoName, map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RetrofitObserver<List<Issue>>() {
                    @Override
                    protected void onSuccess(List<Issue> object) {
                        editTextOrg.setText("");
                        editTextRepo.setText("");

                        if (object != null && !object.isEmpty()) {

                            //LocalDBObject localDBObject = new LocalDBObject();
                            String key = String.format("%s,%s", orgName, repoName);

                            FileDBUtils<LocalDBObject> fileDBUtils = new FileDBUtils<>(getActivity(), "issues", LocalDBObject.class, "/db/custom");
                            LocalDBObject localDBObject = fileDBUtils.readObject();

                            IssueLocal issueLocal = new IssueLocal();
                            issueLocal.setTimestamp(Calendar.getInstance().getTimeInMillis());
                            issueLocal.setIssueList(object);

                            if(localDBObject != null) {
                                localDBObject.getMap().put(key, issueLocal);
                            } else {
                                localDBObject = new LocalDBObject();
                                Map<String, LocalDBObject> objectMap = new HashMap<>();
                                objectMap.put(key, localDBObject);
                            }


                            fileDBUtils.saveObject(localDBObject);



                            Bundle bundle = new Bundle();
                            bundle.putParcelableArrayList(Constants.BUNDLE_KEY, (ArrayList<? extends Parcelable>) object);
                            EntryListFragment entryListFragment = new EntryListFragment();
                            entryListFragment.setArguments(bundle);
                            ((MainActivity) getActivity()).replaceFragment(R.id.frameLayout, entryListFragment, EntryListFragment.class.getName());
                        }

                        Utility.getInstance().hideProgressBar(progressDialog);
                    }

                    @Override
                    protected void onFailure(Throwable e) {
                        Utility.getInstance().hideProgressBar(progressDialog);
                        if (e instanceof HttpException) {
                            if (((HttpException) e).code() == 404) {
                                Utility.getInstance().showRedToast("Something went wrong", getContext());
                            }
                        }
                    }
                });
    }
}
