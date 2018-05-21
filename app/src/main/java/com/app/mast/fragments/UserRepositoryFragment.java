package com.app.mast.fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.mast.R;
import com.app.mast.activities.MainActivity;
import com.app.mast.app.AppController;
import com.app.mast.models.Repository;
import com.app.mast.models.User;
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
public class UserRepositoryFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;

    public UserRepositoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_repository, container, false);
        initViews();
        ((MainActivity) getActivity()).setToolBarTitle("User Repositories");
        initAdapter(AppController.getInstance().databaseHandler.getAllRepositories(MainActivity.USER));
        getPublicRepositories(MainActivity.USER, 1, null);
        return view;
    }


    private void initViews() {
        recyclerView = view.findViewById(R.id.recyclerView);
        progressDialog = new ProgressDialog(getContext());
    }

    private void init(final List<Repository> repositoryList, boolean isAlreadyInitialised, RecyclerView recyclerView) {
        if (!isAlreadyInitialised)
            initAdapter(repositoryList);
        else {
            RecyclerAdapterUtil recyclerAdapterUtil = (RecyclerAdapterUtil) recyclerView.getAdapter();
            recyclerAdapterUtil.updateItemList(repositoryList);
            recyclerAdapterUtil.notifyDataSetChanged();
        }
    }

    private void initAdapter(final List<Repository> repositoryList) {

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        new RecyclerAdapterUtil
                .Builder(getContext(), repositoryList, R.layout.repository_list_item_layout)
                .viewsList(R.id.textViewProjectName, R.id.textViewUrl, R.id.textViewSize, R.id.textViewWatcher, R.id.textViewOpenIssue)
                .bindView(new RecyclerAdapterUtil.BindDataHelper() {
                    @Override
                    public void bindView(int position, Map<Integer, View> viewMap) {
                        ((TextView) viewMap.get(R.id.textViewProjectName)).setText(repositoryList.get(position).getName());
                        ((TextView) viewMap.get(R.id.textViewUrl)).setText(repositoryList.get(position).getHtml_url());
                        ((TextView) viewMap.get(R.id.textViewSize)).setText(String.format("Size : %s", repositoryList.get(position).getSize()));
                        ((TextView) viewMap.get(R.id.textViewWatcher)).setText(String.format("Watchers : %s", repositoryList.get(position).getWatchers()));
                        ((TextView) viewMap.get(R.id.textViewOpenIssue)).setText(String.format("Open issue : %s", repositoryList.get(position).getOpen_issues_count()));
                    }
                })
                .addClickListener(new RecyclerAdapterUtil.ItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position, RecyclerAdapterUtil recyclerAdapterUtil) {
                        switch (view.getId()) {
                            default:
                                RepositoryDetailFragment repositoryDetailFragment = new RepositoryDetailFragment();
                                Bundle bundle = new Bundle();
                                bundle.putParcelable(Constants.BUNDLE_KEY, repositoryList.get(position));
                                repositoryDetailFragment.setArguments(bundle);
                                ((MainActivity) getActivity()).replaceFragment(R.id.frameLayout, repositoryDetailFragment, RepositoryDetailFragment.class.getName());
                                break;

                        }
                    }
                })
                .addOnScrollListener(new RecyclerAdapterUtil.RecyclerViewEndlessScrollListener(mLayoutManager) {
                    @Override
                    public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                        getPublicRepositories(MainActivity.USER, (page + 1), view);
                    }
                })
                .into(recyclerView);
    }

    private void getPublicRepositories(String userName, final int pageCount, final RecyclerView recyclerView) {
        if (pageCount == 1 && AppController.getInstance().databaseHandler.getAllRepositories(MainActivity.USER).isEmpty())
            Utility.getInstance().showProgressBar("Please wait", "Connecting to server...", progressDialog);
        Map<String, String> params = new HashMap<>();
        params.put("page", String.valueOf(pageCount));
        params.put("per_page", String.valueOf(20));
        ApiClient
                .getClient()
                .create(GitHubBasicApi.class)
                .getRepositories(userName, params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RetrofitObserver<List<Repository>>() {
                    @Override
                    protected void onSuccess(List<Repository> repositoryList) {

                        if (pageCount == 1) {
                            Utility.getInstance().hideProgressBar(progressDialog);
                            AppController.getInstance().databaseHandler.deleteRepository(MainActivity.USER);
                        }
                        init(repositoryList, pageCount == 1 ? false : true, recyclerView);

                        for (Repository repository:repositoryList) {
                            AppController.getInstance().databaseHandler.addRepository(repository, MainActivity.USER);
                        }


                    }

                    @Override
                    protected void onFailure(Throwable e) {
                        if (pageCount == 1)
                            Utility.getInstance().hideProgressBar(progressDialog);
                    }
                });
    }


}
