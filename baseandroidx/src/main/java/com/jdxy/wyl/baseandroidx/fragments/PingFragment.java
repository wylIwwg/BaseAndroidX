package com.jdxy.wyl.baseandroidx.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ThreadUtils;
import com.jdxy.wyl.baseandroidx.R;
import com.jdxy.wyl.baseandroidx.R2;
import com.jdxy.wyl.baseandroidx.base.NetworkFeedBean;
import com.jdxy.wyl.baseandroidx.ping.PingView;
import com.jdxy.wyl.baseandroidx.tools.IConfigs;
import com.jdxy.wyl.baseandroidx.tools.ToolLog;
import com.jdxy.wyl.baseandroidx.tools.ToolSP;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;

import static com.jdxy.wyl.baseandroidx.base.CommonSettingActivity.JSON_INDENT;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class PingFragment extends Fragment {


    private static final String TAG = " PingFragment ";

    EditText mEtHttpIP;
    Button mBtnPingIp;
    EditText mEtHttpPort;
    EditText mEtApi;
    Button mBtnPingApi;
    EditText mEtSocketPort;
    Button mBtnPingSocket;
    PingView mPingResult;
    TextView mTvContent;
    TextView mTvUrlContent;
    TextView mTvRequestHeaders;
    TextView mTvResponseHeaders;
    TextView mTvBody;
    NestedScrollView mApiResult;

    public PingFragment() {
        // Required empty public constructor
    }

    public static PingFragment newInstance(String api) {

        PingFragment fragment = new PingFragment();
        Bundle args = new Bundle();
        args.putString(IConfigs.SP_API, api);
        fragment.setArguments(args);
        return fragment;
    }

    String mApi;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mInflate = inflater.inflate(R.layout.fragment_ping, container, false);
        findViewId(mInflate);
        return mInflate;
    }

    private void findViewId(View inflate) {
        mEtHttpIP = inflate.findViewById(R.id.etHttpIP);
        mEtApi = inflate.findViewById(R.id.etApi);
        mEtHttpPort = inflate.findViewById(R.id.etHttpPort);
        mEtSocketPort = inflate.findViewById(R.id.etSocketPort);

        mBtnPingApi = inflate.findViewById(R.id.btnPingApi);
        mBtnPingIp = inflate.findViewById(R.id.btnPingIp);
        mBtnPingSocket = inflate.findViewById(R.id.btnPingSocket);
        mTvBody = inflate.findViewById(R.id.tv_body);
        mApiResult = inflate.findViewById(R.id.apiResult);
        mPingResult = inflate.findViewById(R.id.pingResult);

        mTvUrlContent = inflate.findViewById(R.id.tv_url_content);


        mTvRequestHeaders = inflate.findViewById(R.id.tv_request_headers);
        mTvResponseHeaders = inflate.findViewById(R.id.tv_response_headers);


        mEtSocketPort.setText(ToolSP.getDIYString(IConfigs.SP_PORT_SOCKET));
        mEtHttpPort.setText(ToolSP.getDIYString(IConfigs.SP_PORT_HTTP));
        mEtHttpIP.setText(ToolSP.getDIYString(IConfigs.SP_IP));

        mBtnPingApi.setOnClickListener((v) -> {
            BtnClick(v);
        });
        mBtnPingSocket.setOnClickListener((v) -> {
            BtnClick(v);
        });
        mBtnPingIp.setOnClickListener((v) -> {
            BtnClick(v);
        });

        if (getArguments() != null) {
            mApi = getArguments().getString(IConfigs.SP_API);
            if (mEtApi != null) {
                if (mApi != null && !mApi.contains("http")) {
                    mApi = "http://" + mEtHttpIP.getText().toString() + ":" + mEtHttpPort
                            .getText().toString() + mApi;
                }
                mEtApi.setText(mApi);
            }
        }
    }

    public void BtnClick(View view) {
        int mId = view.getId();
        if (mId == R.id.btnPingSocket) {
            if (!TextUtils.isEmpty(mEtHttpIP.getText().toString()) && !TextUtils.isEmpty(mEtSocketPort.getText().toString())) {
                mApiResult.setVisibility(View.GONE);
                mPingResult.setVisibility(View.VISIBLE);
                mPingResult.pingHost(mEtHttpIP.getText().toString());
                pingSocket(mEtHttpIP.getText().toString(), mEtSocketPort.getText().toString());

            }
        } else if (mId == R.id.btnPingIp) {
            if (!TextUtils.isEmpty(mEtHttpIP.getText().toString())) {
                mApiResult.setVisibility(View.GONE);
                mPingResult.setVisibility(View.VISIBLE);
                mPingResult.pingHost(mEtHttpIP.getText().toString());
            }
        } else if (mId == R.id.btnPingApi) {
            if (!TextUtils.isEmpty(mEtApi.getText().toString())) {
                mApiResult.setVisibility(View.VISIBLE);
                mPingResult.setVisibility(View.GONE);
                showApiResult();
            }
        }
    }

    private void pingSocket(String str1, String str2) {
        mPingResult.pingHost(str1 + ":" + str2);
    }


    private NetworkFeedBean mNetworkFeedModel;

    void showApiResult() {

        OkGo.<String>get(mEtApi.getText().toString())
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        mNetworkFeedModel = new NetworkFeedBean();
                        mNetworkFeedModel.setBody(response.body());
                        mNetworkFeedModel.setCreateTime(response.getRawResponse().sentRequestAtMillis());
                        mNetworkFeedModel.setCostTime(response.getRawResponse().receivedResponseAtMillis() - response.getRawResponse().sentRequestAtMillis());

                        mNetworkFeedModel.setStatus(response.getRawResponse().code());

                        mNetworkFeedModel.setMethod(response.getRawCall().request().method());
                        mNetworkFeedModel.setUrl(response.getRawCall().request().url().toString());

                        setCURLContent();

                        mTvRequestHeaders.setText(response.getRawCall().request().headers().toString());

                        mTvResponseHeaders.setText(response.getRawResponse().headers().toString());

                        mTvBody.setText(response.body());
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        mNetworkFeedModel = new NetworkFeedBean();
                        mNetworkFeedModel.setBody(response.body());
                        if (response.getRawResponse() != null) {
                            mNetworkFeedModel.setCreateTime(response.getRawResponse().sentRequestAtMillis());
                            mNetworkFeedModel.setCostTime(response.getRawResponse().receivedResponseAtMillis() - response.getRawResponse().sentRequestAtMillis());
                            mNetworkFeedModel.setStatus(response.getRawResponse().code());
                            mTvResponseHeaders.setText(response.getRawResponse().headers().toString());
                        }

                        if (response.getRawCall() != null) {

                            mNetworkFeedModel.setMethod(response.getRawCall().request().method());
                            mNetworkFeedModel.setUrl(response.getRawCall().request().url().toString());
                            mTvRequestHeaders.setText(response.getRawCall().request().headers().toString());
                        }

                        setCURLContent();


                        mTvBody.setText(response.body());
                    }
                });
/*
        ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<Object>() {
            @Override
            public Object doInBackground() throws Throwable {
                String testUrl = "https://www.wanandroid.com/friend/json";
                OkHttpManager.getInstance().get(testUrl, new Callback() {
                    @Override
                    public void onFailure(Call call, final IOException e) {
                        ToolLog.e(TAG, "onFailure: " + e.getMessage());
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToolLog.e(TAG, "run: " + (IDataPoolHandleImpl.getInstance().getNetworkFeedMap() == null));
                                if (IDataPoolHandleImpl.getInstance().getNetworkFeedMap() != null) {
                                    mNetworkFeedModel = IDataPoolHandleImpl.getInstance().getNetworkFeedModel("0");

                                    ToolLog.e(TAG, "run: " + JSON.toJSONString(mNetworkFeedModel));
                                    setCURLContent();
                                    setRequestHeaders();
                                    setResponseHeaders();
                                    setBody();
                                }

                                final String body;
                                try {
                                    body = response.body().string();
                                    Log.e(TAG, body);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
                return null;
            }

            @Override
            public void onSuccess(Object result) {

            }
        });
*/

    }

    private void setCURLContent() {

        Map<String, String> map = new LinkedHashMap<>();
        String url = mNetworkFeedModel.getUrl();
        if (url.length() > 40) {
            String substring = url.substring(0, 40);
            url = substring + "……";
            map.put("Request URL", url);
        } else {
            map.put("Request URL", url);
        }
        map.put("Request Method", mNetworkFeedModel.getMethod());
        int status = mNetworkFeedModel.getStatus();
        if (status == 200) {
            map.put("Status Code", "200" + "  ok");
        } else {
            map.put("Status Code", status + "  ok");
        }
        map.put("Remote Address", "待定");
        map.put("Referrer Policy", "待定");
        Format format = new DecimalFormat("#.00");
        String dataSize = format.format(mNetworkFeedModel.getSize() * 0.001) + " KB";
        map.put("size", dataSize);
        map.put("connectTimeoutMillis", mNetworkFeedModel.getConnectTimeoutMillis() + "");
        map.put("readTimeoutMillis", mNetworkFeedModel.getReadTimeoutMillis() + "");
        map.put("writeTimeoutMillis", mNetworkFeedModel.getWriteTimeoutMillis() + "");
        String string = parseHeadersMapToString(map);
        mTvUrlContent.setText(string);
    }

    private void setRequestHeaders() {
        Map<String, String> requestHeadersMap = mNetworkFeedModel.getRequestHeadersMap();
        String string = parseHeadersMapToString(requestHeadersMap);
        mTvRequestHeaders.setText(string);
    }

    private void setResponseHeaders() {
        Map<String, String> responseHeadersMap = mNetworkFeedModel.getResponseHeadersMap();
        String string = parseHeadersMapToString(responseHeadersMap);
        mTvResponseHeaders.setText(string);
    }

    private void setBody() {
        if (mNetworkFeedModel.getContentType().contains("json")) {
            mTvBody.setText(formatJson(mNetworkFeedModel.getBody()));
        } else {
            mTvBody.setText(mNetworkFeedModel.getBody());
        }
    }

    private String parseHeadersMapToString(Map<String, String> headers) {
        if (headers == null || headers.isEmpty()) {
            return "Header is Empty.";
        }
        StringBuilder headersBuilder = new StringBuilder();
        for (String name : headers.keySet()) {
            if (TextUtils.isEmpty(name)) {
                continue;
            }
            headersBuilder
                    .append(name)
                    .append(" : ")
                    .append(headers.get(name))
                    .append("\n");
        }
        return headersBuilder.toString().trim();
    }

    private String formatJson(String body) {
        String message;
        try {
            if (body.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(body);
                message = jsonObject.toString(JSON_INDENT);
            } else if (body.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(body);
                message = jsonArray.toString(JSON_INDENT);
            } else {
                message = body;
            }
        } catch (JSONException e) {
            message = body;
        }
        return message;
    }

}