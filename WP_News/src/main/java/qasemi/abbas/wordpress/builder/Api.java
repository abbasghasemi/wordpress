/*
 * Copyright (C) 2019  All rights reserved for Abbas Qasemi
 *
 * For The Android Open Source Project
 *
 */
package qasemi.abbas.wordpress.builder;

import qasemi.abbas.wordpress.BuildConfig;

public class Api {

    public static String getCategoryPosts(String id, String page) {
        return BuildConfig.API + "/?json=get_category_posts&date_format=" + Builder.formatDate + "&id=" + id + "&count=" + Builder.LimitPage + "&page=" + page;
    }

    public static String getCategoryIndex() {
        return BuildConfig.API + "/?json=get_category_index";
    }

    public static String getDateIndex() {
        return BuildConfig.API + "/?json=get_date_index";
    }

    public static String getSearchResults(String search, String page) {
        return BuildConfig.API + "/?json=get_search_results&date_format=" + Builder.formatDate + "&count=" + Builder.LimitPage + "&search=" + search + "&page=" + page;
    }

    public static String getPageIndex() {
        return BuildConfig.API + "/?json=get_page_index&date_format=" + Builder.formatDate;
    }

    public static String getPosts(String page) {
        return BuildConfig.API + "/?json=get_posts&date_format=" + Builder.formatDate + "&count=" + Builder.LimitPage + "&page=" + page;
    }

    public static String getDatePosts(String date, String page) {
        return BuildConfig.API + "/?json=get_date_posts&date_format=" + Builder.formatDate + "&date=" + date + "&count=" + Builder.LimitPage + "&page=" + page;
    }
//    private final static String api = BuildConfig.API + "/wp-json/wp/v2/";
//
//    public static String getCategoryPosts(String id, String page) {
//        return api + "posts?" + "categories=" + id + "&per_page=" + Builder.LimitPage + "&page=" + page;
//    }
//
//    public static String getCategoryIndex() {
//        return api + "categories?per_page=100";
//    }
//
//    public static String getPageIndex() {
//        return api + "pages?per_page=100";
//    }
//
//    // [] , date not available , rest_invalid_param
//    public static String getSearchResults(String search, String page) {
//        return api + "search?" + "per_page=" + Builder.LimitPage + "&search=" + search + "&page=" + page;
//    }
//
//    // rest_post_invalid_page_number
//    public static String getPosts(String page) {
//        return api + "posts?" + "per_page=" + Builder.LimitPage + "&page=" + page;
//    }
//
//    public static String getComments(String id, String page) {
//        return api + "comments?" + "post=" + id + "&per_page=" + Builder.LimitPage + "&page=" + page;
//    }
//

}

