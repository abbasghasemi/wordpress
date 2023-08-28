/*
 * Copyright (C) 2019  All rights reserved for FaraSource (ABBAS GHASEMI)
 * https://farasource.com
 */
package ghasemi.abbas.wordpress.api;

import ghasemi.abbas.wordpress.BuildConfig;
import ghasemi.abbas.wordpress.BuildApp;

public class ApiSeivice {

    private final static String api;

    static {
        if (BuildConfig.API.endsWith("/")) {
            api = BuildConfig.API + "wp-json/wp/v2/";
        } else {
            api = BuildConfig.API + "/wp-json/wp/v2/";
        }
    }

    public static String getCategoryPosts(String id, String page) {
        return api + "posts?" + "categories=" + id + "&per_page=" + BuildApp.limitPage + "&page=" + page;
    }

    public static String getCategoryIndex() {
        return api + "categories?per_page=100";
    }

    public static String getPageIndex() {
        return api + "pages?per_page=100";
    }

    public static String getSearchResults(String search, String page) {
        return api + "search?" + "per_page=" + BuildApp.limitPage + "&search=" + search + "&page=" + page;
    }

    public static String getPost(String post) {
        return api + "posts/" + post;
    }

    public static String getPosts(String page) {
        return api + "posts?" + "per_page=" + BuildApp.limitPage + "&page=" + page;
    }

    public static String getComments(String id, String page) {
        return api + "comments?" + "post=" + id + "&per_page=" + /*BuildApp.LimitPage*/ 100 + "&page=" + page;
    }
}