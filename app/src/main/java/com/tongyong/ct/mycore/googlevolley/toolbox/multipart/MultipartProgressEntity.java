package com.tongyong.ct.mycore.googlevolley.toolbox.multipart;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.tongyong.ct.mycore.googlevolley.Response.ProgressListener;

public class MultipartProgressEntity extends MultipartEntity {

    private ProgressListener listener;

    public void setListener(ProgressListener listener) {
        this.listener = listener;
    }

    @Override
    public void writeTo(OutputStream outstream) throws IOException {
        if (listener == null) {
            super.writeTo(outstream);
        } else {
            super.writeTo(new OutputStreamProgress(outstream, this.listener));
        }
    }
}