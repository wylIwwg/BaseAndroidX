package com.jdxy.wyl.baseandroidx.media.zip;

import android.content.Context;
import android.os.AsyncTask;

import com.blankj.utilcode.util.AppUtils;
import com.jdxy.wyl.baseandroidx.tools.ToolLog;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * Created by wyl on 2018/6/5.
 * 文件解压
 */

public class ZipExtractorTask extends AsyncTask<Void, Integer, Long> {
    private final String TAG = "ZipExtractorTask";
    private final File mInput;//压缩文件输入流
    private final File mOutput;//解压目标目录
    private int mProgress = 0;
    private long mTotalCount = 0;
    private final Context mContext;
    private boolean mReplaceAll;

    public ZipExtractorTask(String in, String out, Context context, boolean replaceAll) {
        this(new File(in), out, context, replaceAll);

    }

    public ZipExtractorTask(File zip, String out, Context context, boolean replaceAll) {
        super();
        mInput = zip;
        // mOutput = new File(SpUtils.init(context).getDIYString(Configs.SP.PATH_DATA));
        mOutput = new File(out);
        mContext = context;
        ToolLog.e(TAG, "ZipExtractorTask: zip " + mInput.getAbsolutePath());
        ToolLog.e(TAG, "ZipExtractorTask: out " + mOutput.getAbsolutePath());
        //目标目录不存在就创建
        if (!mOutput.exists()) {
            if (!mOutput.mkdirs()) {
                ToolLog.e(TAG, "Failed to make directories: " + mOutput.getAbsolutePath());
            }
        }

        mReplaceAll = replaceAll;
    }

    @Override
    protected Long doInBackground(Void... params) {
        // TODO Auto-generated method stub
        return unzip();
    }

    @Override
    protected void onPostExecute(Long result) {
        // TODO Auto-generated method stub
        //super.onPostExecute(result);
        if (isCancelled())
            return;
    }

    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        //super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        // TODO Auto-generated method stub
        //super.onProgressUpdate(values);
    }

    private long unzip() {
        long extractedSize = 0L;
        Enumeration<ZipEntry> entries;
        ZipFile zip = null;
        try {
            if (!mInput.exists())
                return 0;
            zip = new ZipFile(mInput);
            long uncompressedSize = getOriginalSize(zip);
            mTotalCount = uncompressedSize;
            publishProgress(0, (int) uncompressedSize);

            entries = (Enumeration<ZipEntry>) zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.isDirectory()) {
                    ToolLog.e(TAG, "unzip: " + entry.getName());
                    continue;
                }
                File destination = new File(mOutput, entry.getName());
                if (!destination.getParentFile().exists()) {
                    ToolLog.e(TAG, " make = " + destination.getParentFile().getAbsolutePath());
                    destination.getParentFile().mkdirs();
                }
                //如果不替换
                if (destination.exists() && mContext != null && !mReplaceAll) {

                }
                ProgressReportingOutputStream outStream = new ProgressReportingOutputStream(destination);
                extractedSize += copy(zip.getInputStream(entry), outStream);
                outStream.close();
            }

        } catch (ZipException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (zip != null)
                    zip.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return extractedSize;
    }

    private long getOriginalSize(ZipFile file) {
        Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) file.entries();
        long originalSize = 0L;
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (entry.getSize() >= 0) {
                originalSize += entry.getSize();
            }
        }
        return originalSize;
    }

    private int copy(InputStream input, OutputStream output) {
        byte[] buffer = new byte[1024 * 8];
        BufferedInputStream in = new BufferedInputStream(input, 1024 * 8);
        BufferedOutputStream out = new BufferedOutputStream(output, 1024 * 8);
        int count = 0, n = 0;
        try {
            while ((n = in.read(buffer, 0, 1024 * 8)) != -1) {
                out.write(buffer, 0, n);
                count += n;
            }
            out.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                in.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return count;
    }

    private final class ProgressReportingOutputStream extends FileOutputStream {

        public ProgressReportingOutputStream(File file)
                throws FileNotFoundException {
            super(file);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void write(byte[] buffer, int byteOffset, int byteCount)
                throws IOException {
            // TODO Auto-generated method stub
            super.write(buffer, byteOffset, byteCount);
            mProgress += byteCount;
            //  ToolLog.e(TAG, "解压write: " + mProgress + "  " + mTotalCount);
            publishProgress(mProgress);
            if (mProgress >= mTotalCount) {//解压完成
                //循环结束 解压完成
                if (mContext != null) {
                    ToolLog.e(TAG, "write: 解压完成  ");
                    /// mContext.sendBroadcast(new Intent("ZipExtractorTaskDone"));
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            AppUtils.relaunchApp(true);
                        }
                    }).start();
                }
            }
        }

    }
}
