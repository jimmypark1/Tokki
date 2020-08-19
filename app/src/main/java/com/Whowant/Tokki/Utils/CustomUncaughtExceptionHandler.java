package com.Whowant.Tokki.Utils;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CustomUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    private Thread.UncaughtExceptionHandler mHandler;

    public CustomUncaughtExceptionHandler() {
        mHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        final String logMsgParams = makeStackTrace(thread, ex);

        String path = Environment.getExternalStorageDirectory().getPath() + "/Exception/";
        File dir = new File(path);
        if (dir.exists() || dir.mkdirs()) {
            FileOutputStream fos = null;
            OutputStreamWriter osw = null;

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
                Date date = new Date(Calendar.getInstance().getTimeInMillis());
                String fileDate = sdf.format(date);

                fos = new FileOutputStream(path + "DUMP" + fileDate + ".txt",
                        true);
                osw = new OutputStreamWriter(fos, "UTF-8");
                osw.write(logMsgParams);
                osw.flush();
                fos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (osw != null) {
                        osw.close();
                    }
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                osw = null;
                fos = null;
            }
        }

        // TODO: to process exception for reporting.
        callDefaultUncaughtExceptionHandler(thread, ex);

        System.exit(1);
    }

    private static String makeStackTrace(Thread thread, Throwable e) {
        StringBuilder errLog = new StringBuilder();
        errLog.append("FATAL EXCEPTION: " + thread.getName());
        errLog.append("\n");
        errLog.append(e.toString());
        errLog.append("\n");

        StackTraceElement[] stack = e.getStackTrace();
        for (StackTraceElement element : stack) {
            errLog.append("    at " + element);
            errLog.append("\n");
        }

        StackTraceElement[] parentStack = stack;
        Throwable throwable = e.getCause();
        while (throwable != null) {
            errLog.append("Caused by: ");
            errLog.append(throwable.toString());
            errLog.append("\n");

            StackTraceElement[] currentStack = throwable.getStackTrace();
            int duplicates = countDuplicates(currentStack, parentStack);

            for (int i = 0; i < currentStack.length - duplicates; i++) {
                errLog.append("    at " + currentStack[i].toString());
                errLog.append("\n");
            }

            if (duplicates > 0) {
                errLog.append("    ... " + duplicates + " more");
            }

            parentStack = currentStack;
            throwable = throwable.getCause();
        }

        return errLog.toString();
    }

    private static int countDuplicates(StackTraceElement[] currentStack,
                                       StackTraceElement[] parentStack) {
        int duplicates = 0;
        int parentIndex = parentStack.length;

        for (int i = currentStack.length; --i >= 0 && --parentIndex >= 0;) {
            StackTraceElement parentFrame = parentStack[parentIndex];
            if (!parentFrame.equals(currentStack[i])) {
                break;
            }

            duplicates++;
        }

        return duplicates;
    }

    private void callDefaultUncaughtExceptionHandler(Thread thread, Throwable e) {
        if (mHandler != null) {
            mHandler.uncaughtException(thread, e);
        }
    }
}