package com.youtu.sleep.youtubbackground.data.source.remote.listvideo;

import android.os.AsyncTask;

import com.youtu.sleep.youtubbackground.data.model.popularvideo.Video;
import com.youtu.sleep.youtubbackground.data.source.YoutubeVideoDataSource;
import com.youtu.sleep.youtubbackground.utils.exception.ParseJSONException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.youtu.sleep.youtubbackground.BuildConfig.API_KEY;
import static com.youtu.sleep.youtubbackground.utils.Contants.BASE_URL;
import static com.youtu.sleep.youtubbackground.utils.Contants.SEARCH_VIDEO_URL;

/**
 * Created by thuy on 10/08/2018.
 */
public class SearchVideoAsyncTask extends AsyncTask<String, Void, List<Video>> {
    private YoutubeVideoDataSource.CallBack<List<Video>> mCallBack;

    private Exception mException = null;

    public SearchVideoAsyncTask(YoutubeVideoDataSource.CallBack callBack) {
        this.mCallBack = callBack;
    }

    @Override
    protected List<Video> doInBackground(String... strings) {
        try {
            String s = getFullUrl(strings[0]);
            URL url = new URL(s);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(ParameterSearchVideo.REQUEST_METHOD);
            InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
            String result = convertInputStreamToString(inputStream);
            return parseData(result);
        } catch (IOException | ParseJSONException e) {
            mException = e;
            return null;
        }
    }

    /**
     * Parse from json string to Video object list
     */

    @Override
    protected void onPostExecute(List videos) {
        super.onPostExecute(videos);
        if (mException == null) {
            if (videos != null) {
                mCallBack.onGetDataSuccess(videos);
            } else mCallBack.onFail(ParameterSearchVideo.NULL_LIST);
        } else mCallBack.onFail(mException.getMessage());
    }

    private List<Video> parseData(String s) throws ParseJSONException {
        List<Video> videos = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray jsonArray = jsonObject.getJSONArray(ParameterSearchVideo.ITEMS);
            for (int i = 0; i < jsonArray.length(); i++) {
                String id = jsonArray.getJSONObject(i).getJSONObject(ParameterSearchVideo.ID)
                        .getString(ParameterSearchVideo.VIDEO_ID);
                JSONObject snippet = jsonArray.getJSONObject(i).getJSONObject(ParameterSearchVideo.SNIPPET);
                String title = snippet.getString(ParameterSearchVideo.TITLE);
                String description = snippet.getString(ParameterSearchVideo.DES);
                String channelTitle = snippet.getString(ParameterSearchVideo.CHANNEL_TITLE);
                JSONObject high = snippet.getJSONObject(ParameterSearchVideo.THUMBNAILS)
                        .getJSONObject(ParameterSearchVideo.HIGH_THUMB);
                String url = high.getString(ParameterSearchVideo.URL_THUMB);
                videos.add(new Video(id, title, channelTitle, description, url));
            }
        } catch (JSONException e) {
            throw new ParseJSONException(e);
        }
        return videos;
    }

    /**
     * Get url to get videos
     */

    private String getFullUrl(String query) {
        StringBuilder str = new StringBuilder(BASE_URL + SEARCH_VIDEO_URL);
        Map<String, String> params = new HashMap<>();

        params.put(ParameterSearchVideo.PART_KEY, ParameterSearchVideo.PART_VALUE);
        params.put(ParameterSearchVideo.ORDER_KEY, ParameterSearchVideo.ORDER_VALUE);
        params.put(ParameterSearchVideo.Q_KEY, query);
        params.put(ParameterSearchVideo.TYPE_KEY, ParameterSearchVideo.TYPE_VALUE);
        params.put(ParameterSearchVideo.VIDEO_DEFINITION_KEY, ParameterSearchVideo.VIDEO_DEFINITION_VALUE);
        params.put(ParameterSearchVideo.KEY, API_KEY);
        for (String s : params.keySet()) {
            str.append(s).append(params.get(s));
        }
        return str.toString();
    }

    /**
     * convert from json to string
     */

    private String convertInputStreamToString(InputStream inputStream) {
        String str;
        StringBuilder sb = new StringBuilder();
        InputStreamReader isr = new InputStreamReader(inputStream);
        BufferedReader br = new BufferedReader(isr);
        try {
            while ((str = br.readLine()) != null) {
                sb.append(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    @interface ParameterSearchVideo {
        String PART_KEY = "part=";
        String PART_VALUE = "snippet";
        String ORDER_KEY = "&order=";
        String ORDER_VALUE = "viewCount";
        String Q_KEY = "&q=";
        String TYPE_KEY = "&type=";
        String TYPE_VALUE = "video";
        String VIDEO_DEFINITION_KEY = "&videoDefinition=";
        String VIDEO_DEFINITION_VALUE = "high";
        String KEY = "&key=";

        String ID = "id";
        String VIDEO_ID = "videoId";
        String ITEMS = "items";
        String SNIPPET = "snippet";
        String TITLE = "title";
        String DES = "description";
        String CHANNEL_TITLE = "channelTitle";
        String THUMBNAILS = "thumbnails";
        String HIGH_THUMB = "high";
        String URL_THUMB = "url";

        String REQUEST_METHOD = "GET";

        String NULL_LIST = "No search result!";
    }
}
