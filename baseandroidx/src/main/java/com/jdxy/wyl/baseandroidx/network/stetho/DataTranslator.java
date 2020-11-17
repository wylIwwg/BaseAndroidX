package com.jdxy.wyl.baseandroidx.network.stetho;

import android.net.Uri;
import android.text.TextUtils;


import com.jdxy.wyl.baseandroidx.base.NetworkFeedBean;
import com.jdxy.wyl.baseandroidx.network.NetworkManager;
import com.jdxy.wyl.baseandroidx.network.NetworkRecord;
import com.jdxy.wyl.baseandroidx.tools.ToolLog;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;


public class DataTranslator {


    private static final String TAG = "DataTranslator";
    private static final String GZIP_ENCODING = "gzip";
    private Map<String, Long> mStartTimeMap = new HashMap();


    public void saveInspectorRequest(NetworkEventReporter.InspectorRequest request) {
        String requestId = request.id();
        mStartTimeMap.put(request.id(), System.currentTimeMillis());
        ToolLog.e("DataTranslator-----saveInspectorRequest----"+request);
        createRecord(requestId, request);
    }


    private void createRecord(String requestId, NetworkEventReporter.InspectorRequest inspectorRequest) {
        NetworkRecord record = new NetworkRecord();
        record.setRequestId(requestId);
        record.setMethod(inspectorRequest.method());
        record.setRequestLength(readBodyLength(inspectorRequest));
        NetworkManager.get().addRecord(requestId, record);
    }

    private long readBodyLength(NetworkEventReporter.InspectorRequest request) {
        try {
            byte[] body = request.body();
            if (body != null) {
                return body.length;
            }
        } catch (IOException | OutOfMemoryError e) {
            //e.printStackTrace();
        }
        return 0;
    }

    public void saveInspectorResponse(NetworkEventReporter.InspectorResponse response) {
        ToolLog.e("DataTranslator-----saveInspectorResponse----"+response);
        String requestId = response.requestId();
        long costTime;
        if (mStartTimeMap!=null){
            if (mStartTimeMap.containsKey(requestId)) {
                long aLong = mStartTimeMap.get(requestId);
                costTime = System.currentTimeMillis() - aLong;
                ToolLog.e(TAG, "cost time = " + costTime + "ms");
            } else {
                costTime = -1;
            }
        }
    }

    public InputStream saveInterpretResponseStream(String requestId, String contentType, String contentEncoding, InputStream inputStream) {
        if (isSupportType(contentType)) {
            ByteArrayOutputStream byteArrayOutputStream = parseAndSaveBody(inputStream, null, contentEncoding);
            InputStream newInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            try {
                byteArrayOutputStream.close();
            } catch (IOException e) {
                ToolLog.e(TAG+ "----saveInterpretResponseStream---"+ e);
            }
            return newInputStream;
        } else {
            return inputStream;
        }
    }

    private ByteArrayOutputStream parseAndSaveBody(InputStream inputStream, NetworkFeedBean networkFeedModel, String contentEncoding) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buffer)) > -1) {
                byteArrayOutputStream.write(buffer, 0, len);
            }
            byteArrayOutputStream.flush();
            InputStream newStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            BufferedReader bufferedReader;
            if (GZIP_ENCODING.equals(contentEncoding)) {
                GZIPInputStream gzipInputStream = new GZIPInputStream(newStream);
                bufferedReader = new BufferedReader(new InputStreamReader(gzipInputStream));
            } else {
                bufferedReader = new BufferedReader(new InputStreamReader(newStream));
            }
            StringBuilder bodyBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                bodyBuilder.append(line + '\n');
            }
            String body = bodyBuilder.toString();
            networkFeedModel.setBody(body);
            networkFeedModel.setSize(body.getBytes().length);

            //设置响应数据大小
            NetworkRecord record = NetworkManager.get().getRecord(networkFeedModel.getRequestId());
            record.setResponseLength(body.getBytes().length);
        } catch (IOException e) {
            ToolLog.e(TAG+ "----parseAndSaveBody---"+ e);
        }
        return byteArrayOutputStream;
    }

    private boolean isSupportType(String contentType) {
        return contentType.contains("text") || contentType.contains("json");
    }
}
