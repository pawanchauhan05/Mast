package com.app.mast.services;


import com.app.mast.models.Issue;
import com.app.mast.models.Repository;
import com.app.mast.models.User;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Created by pawansingh on 27/03/18.
 */

public interface GitHubBasicApi {

    @GET("users/{user}")
    Observable<User> getUser(@Path("user") String user);


    @GET("users/{user}/repos")
    Observable<List<Repository>> getRepositories(@Path("user") String user, @QueryMap Map<String, String> params);


    @GET("repos/{org}/{repo}/issues")
    Observable<List<Issue>> getDetails(@Path("org") String org, @Path("repo") String repo, @QueryMap Map<String, String> params);


}
