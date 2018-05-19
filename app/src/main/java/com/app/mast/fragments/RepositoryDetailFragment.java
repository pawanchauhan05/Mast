package com.app.mast.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.mast.R;
import com.app.mast.activities.MainActivity;
import com.app.mast.models.Repository;
import com.app.mast.utils.Constants;
import com.squareup.picasso.Picasso;


public class RepositoryDetailFragment extends Fragment {
    private View view;
    private Repository repository;
    private ImageView imageViewAvatar;
    private TextView textViewProjectName, textViewProjectDescription;


    public RepositoryDetailFragment() {
        // Required empty public constructor
    }

    private void getBundleData() {
        Bundle bundle = getArguments();
        if(bundle != null && bundle.containsKey(Constants.BUNDLE_KEY)) {
            repository = bundle.getParcelable(Constants.BUNDLE_KEY);

            if(repository != null) {
                textViewProjectName.setText(repository.getName());
                textViewProjectDescription.setText(repository.getDescription());
                Picasso
                        .get()
                        .load(repository.getOwner().getAvatar_url())
                        .into(imageViewAvatar);
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_repository_detail, container, false);

        ((MainActivity)getActivity()).setToolBarTitle("Repository Details");
        initViews();
        getBundleData();
        return view;
    }

    private void initViews() {
        imageViewAvatar = view.findViewById(R.id.imageViewAvatar);
        textViewProjectName = view.findViewById(R.id.textViewProjectName);
        textViewProjectDescription = view.findViewById(R.id.textViewProjectDescription);
    }

}
