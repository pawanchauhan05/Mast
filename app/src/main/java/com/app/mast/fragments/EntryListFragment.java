package com.app.mast.fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.mast.R;
import com.app.mast.activities.MainActivity;
import com.app.mast.app.AppController;
import com.app.mast.models.Issue;
import com.app.mast.models.Repository;
import com.app.mast.retrofit.ApiClient;
import com.app.mast.retrofit.RetrofitObserver;
import com.app.mast.services.GitHubBasicApi;
import com.app.mast.utils.Constants;
import com.app.mast.utils.RecyclerAdapterUtil;
import com.app.mast.utils.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class EntryListFragment extends Fragment {


    public EntryListFragment() {
        // Required empty public constructor
    }

    private RecyclerView recyclerView;
    private List<Issue> issueList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getBundleData();
        return inflater.inflate(R.layout.fragment_entry_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView);
        initAdapter(issueList);
        ((MainActivity) getActivity()).setToolBarTitle("Issue List");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initAdapter(final List<Issue> issueList) {

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        new RecyclerAdapterUtil
                .Builder(getContext(), issueList, R.layout.issue_detail_layout)
                .viewsList(R.id.textViewUser, R.id.textViewUrl, R.id.textViewTitle, R.id.textViewPRNumber)
                .bindView(new RecyclerAdapterUtil.BindDataHelper() {
                    @Override
                    public void bindView(int position, Map<Integer, View> viewMap) {
                        if(issueList.get(position).getUser() != null && !TextUtils.isEmpty(issueList.get(position).getUser().getLogin())) {
                            ((TextView) viewMap.get(R.id.textViewUser)).setText(issueList.get(position).getUser().getLogin());
                        } else {
                            ((TextView) viewMap.get(R.id.textViewUser)).setText("NA");
                        }

                        if(issueList.get(position).getPull_request() != null && !TextUtils.isEmpty(issueList.get(position).getPull_request().getPatch_url())) {
                            ((TextView) viewMap.get(R.id.textViewUrl)).setText(issueList.get(position).getPull_request().getPatch_url());
                        } else {
                            ((TextView) viewMap.get(R.id.textViewUrl)).setText("NA");
                        }

                        if( !TextUtils.isEmpty(issueList.get(position).getTitle())) {
                            ((TextView) viewMap.get(R.id.textViewTitle)).setText(issueList.get(position).getTitle());
                        } else {
                            ((TextView) viewMap.get(R.id.textViewTitle)).setText("NA");
                        }
                        ((TextView) viewMap.get(R.id.textViewPRNumber)).setText(String.format("PR Number : %s, State : %s", issueList.get(position).getNumber(), issueList.get(position).getState()));
                    }
                })
                .into(recyclerView);
    }

    private void getBundleData() {
        Bundle bundle = getArguments();
        if(bundle != null && bundle.containsKey(Constants.BUNDLE_KEY)) {
            issueList.clear();
            issueList.addAll(bundle.<Issue>getParcelableArrayList(Constants.BUNDLE_KEY));
        }
    }

}
