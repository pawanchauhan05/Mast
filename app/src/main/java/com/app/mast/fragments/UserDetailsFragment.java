package com.app.mast.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.mast.R;
import com.app.mast.activities.MainActivity;
import com.app.mast.models.User;
import com.app.mast.utils.Constants;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserDetailsFragment extends Fragment implements View.OnClickListener {

    private View view;
    private TextView textViewName, textViewPublicRepo, textViewPublicGist, textViewFollowers, textViewFollowing;
    private Button buttonRepository;
    private ImageView imageViewAvatar;
    private User user;


    public UserDetailsFragment() {
        // Required empty public constructor
    }

    private void getBundleData() {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(Constants.BUNDLE_KEY)) {
            user = bundle.getParcelable(Constants.BUNDLE_KEY);
            textViewName.setText(String.format("Name : %s", TextUtils.isEmpty(user.getName()) ? "Not Available" : user.getName()));
            textViewPublicRepo.setText(String.format("Public Repository : %s", user.getPublic_repos()));
            textViewPublicGist.setText(String.format("Public Gist : %s", user.getPublic_gists()));
            textViewFollowers.setText(String.format("Followers : %s", user.getFollowers()));
            textViewFollowing.setText(String.format("Following : %s", user.getFollowing()));

            Picasso
                    .get()
                    .load(user.getAvatar_url())
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(imageViewAvatar, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(user.getAvatar_url()).into(imageViewAvatar);
                        }
                    });


        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user_details, container, false);
        initViews();
        initListeners();
        ((MainActivity) getActivity()).setToolBarTitle("User Details");
        getBundleData();
        return view;
    }

    private void initViews() {
        textViewName = view.findViewById(R.id.textViewName);
        textViewPublicRepo = view.findViewById(R.id.textViewPublicRepo);
        textViewPublicGist = view.findViewById(R.id.textViewPublicGist);
        textViewFollowers = view.findViewById(R.id.textViewFollowers);
        textViewFollowing = view.findViewById(R.id.textViewFollowing);
        buttonRepository = view.findViewById(R.id.buttonRepository);
        imageViewAvatar = view.findViewById(R.id.imageViewAvatar);
    }

    private void initListeners() {
        buttonRepository.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonRepository:
                ((MainActivity) getActivity()).replaceFragment(R.id.frameLayout, new UserRepositoryFragment(), UserRepositoryFragment.class.getName());
                break;
        }
    }
}
