package com.backpack.couteau_tarea6;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NewsFragment extends Fragment {
    private LinearLayout newsContainer;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        newsContainer = view.findViewById(R.id.news_container);
        fetchNews();
        return view;
    }

    private void fetchNews() {
        String url = "https://blog.ted.com/wp-json/wp/v2/posts";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject newsItem = response.getJSONObject(i);
                                String title = newsItem.getJSONObject("title").getString("rendered"); // Get the rendered title
                                String summary = newsItem.getJSONObject("excerpt").getString("rendered"); // Get the rendered excerpt
                                String link = newsItem.getString("link");

                                // Strip HTML tags from the title and summary
                                title = stripHtml(title);
                                summary = stripHtml(summary);

                                addNewsItem(title, summary, link);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        Volley.newRequestQueue(getContext()).add(jsonArrayRequest);
    }

    // Method to strip HTML tags from a string
    @SuppressLint("NewApi")
    private String stripHtml(String html) {
        return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString();
    }



    private void addNewsItem(String title, String summary, String link) {
        View newsItemView = LayoutInflater.from(getContext()).inflate(R.layout.news_item, newsContainer, false);
        TextView titleText = newsItemView.findViewById(R.id.title_text);
        TextView summaryText = newsItemView.findViewById(R.id.summary_text);
        TextView linkText = newsItemView.findViewById(R.id.link_text);

        titleText.setText(title);
        summaryText.setText(summary);
        linkText.setText("Leer más");

        linkText.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            startActivity(browserIntent);
        });

        newsContainer.addView(newsItemView);
    }
}
