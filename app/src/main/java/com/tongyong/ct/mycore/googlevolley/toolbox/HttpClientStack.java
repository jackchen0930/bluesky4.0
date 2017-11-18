/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tongyong.ct.mycore.googlevolley.toolbox;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.tongyong.ct.mycore.googlevolley.Request;
import com.tongyong.ct.mycore.googlevolley.Request.Method;
import com.tongyong.ct.mycore.googlevolley.Response.ProgressListener;
import com.tongyong.ct.mycore.googlevolley.error.AuthFailureError;
import com.tongyong.ct.mycore.googlevolley.request.MultiPartRequest;
import com.tongyong.ct.mycore.googlevolley.request.MultiPartRequest.MultiPartParam;
import com.tongyong.ct.mycore.googlevolley.toolbox.multipart.FilePart;
import com.tongyong.ct.mycore.googlevolley.toolbox.multipart.MultipartProgressEntity;
import com.tongyong.ct.mycore.googlevolley.toolbox.multipart.StringPart;

/**
 * An HttpStack that performs request over an {@link HttpClient}.
 */
public class HttpClientStack implements HttpStack {
	protected final HttpClient mClient;

	private static final String CONTENT_TYPE_MULTIPART = "multipart/form-data; charset=%s; boundary=%s";
	private final static String HEADER_CONTENT_TYPE = "Content-Type";

	public HttpClientStack(HttpClient client) {
		mClient = client;
	}

	private static void addHeaders(HttpUriRequest httpRequest, Map<String, String> headers) {
		for (String key : headers.keySet()) {
			httpRequest.setHeader(key, headers.get(key));
		}
	}

	@SuppressWarnings("unused")
	private static List<NameValuePair> getPostParameterPairs(Map<String, String> postParams) {
		List<NameValuePair> result = new ArrayList<NameValuePair>(postParams.size());
		for (String key : postParams.keySet()) {
			result.add(new BasicNameValuePair(key, postParams.get(key)));
		}
		return result;
	}

	@Override
	public HttpResponse performRequest(Request<?> request, Map<String, String> additionalHeaders) throws IOException, AuthFailureError {
		HttpUriRequest httpRequest = createHttpRequest(request, additionalHeaders);
		addHeaders(httpRequest, additionalHeaders);
		addHeaders(httpRequest, request.getHeaders());
		onPrepareRequest(httpRequest);
		HttpParams httpParams = httpRequest.getParams();
		int timeoutMs = request.getTimeoutMs();
		// TODO: Reevaluate this connection timeout based on more wide-scale
		// data collection and possibly different for wifi vs. 3G.
		HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
		HttpConnectionParams.setSoTimeout(httpParams, timeoutMs);
		return mClient.execute(httpRequest);
	}

	/**
	 * Creates the appropriate subclass of HttpUriRequest for passed in request.
	 */
	@SuppressWarnings("deprecation")
	/* protected */static HttpUriRequest createHttpRequest(Request<?> request, Map<String, String> additionalHeaders) throws AuthFailureError,
			IOException {
		switch (request.getMethod()) {
		case Method.DEPRECATED_GET_OR_POST: {
			// This is the deprecated way that needs to be handled for backwards
			// compatibility.
			// If the request's post body is null, then the assumption is that
			// the request is
			// GET. Otherwise, it is assumed that the request is a POST.
			byte[] postBody = request.getPostBody();
			if (postBody != null) {
				HttpPost postRequest = new HttpPost(request.getUrl());
				postRequest.addHeader(HEADER_CONTENT_TYPE, request.getPostBodyContentType());
				HttpEntity entity;
				entity = new ByteArrayEntity(postBody);
				postRequest.setEntity(entity);
				return postRequest;
			} else {
				return new HttpGet(request.getUrl());
			}
		}
		case Method.GET:
			return new HttpGet(request.getUrl());
		case Method.DELETE:
			return new HttpDelete(request.getUrl());
		case Method.POST: {
			HttpPost postRequest = new HttpPost(request.getUrl());
			setEntityIfNonEmptyBody(postRequest, request);
			return postRequest;
		}
		case Method.PUT: {
			HttpPut putRequest = new HttpPut(request.getUrl());
			putRequest.addHeader(HEADER_CONTENT_TYPE, request.getBodyContentType());
			setEntityIfNonEmptyBody(putRequest, request);
			return putRequest;
		}
		case Method.HEAD:
			return new HttpHead(request.getUrl());
		case Method.OPTIONS:
			return new HttpOptions(request.getUrl());
		case Method.TRACE:
			return new HttpTrace(request.getUrl());
		case Method.PATCH: {
			HttpPatch patchRequest = new HttpPatch(request.getUrl());
			setEntityIfNonEmptyBody(patchRequest, request);
			return patchRequest;
		}
		default:
			throw new IllegalStateException("Unknown request method.");
		}
	}

	private static void setEntityIfNonEmptyBody(HttpEntityEnclosingRequestBase httpRequest, Request<?> request) throws IOException, AuthFailureError {

		if (request instanceof MultiPartRequest) {
			ProgressListener progressListener = null;
			if (request instanceof ProgressListener) {
				progressListener = (ProgressListener) request;
			}
            MultipartProgressEntity multipartEntity = new MultipartProgressEntity();
            multipartEntity.setListener(progressListener);
			final String charset = ((MultiPartRequest<?>) request).getProtocolCharset();
			httpRequest.addHeader(HEADER_CONTENT_TYPE, String.format(CONTENT_TYPE_MULTIPART, charset, multipartEntity.getBoundary()));

			final Map<String, MultiPartParam> multipartParams = ((MultiPartRequest<?>) request).getMultipartParams();
			final Map<String, String> filesToUpload = ((MultiPartRequest<?>) request).getFilesToUpload();

			for (String key : multipartParams.keySet()) {
				multipartEntity.addPart(new StringPart(key, multipartParams.get(key).value));
			}

			for (String key : filesToUpload.keySet()) {
				File file = new File(filesToUpload.get(key));

				if (!file.exists()) {
					throw new IOException(String.format("File not found: %s", file.getAbsolutePath()));
				}

				if (file.isDirectory()) {
					throw new IOException(String.format("File is a directory: %s", file.getAbsolutePath()));
				}

				FilePart filePart = new FilePart(key, file, null, null);
				multipartEntity.addPart(filePart);
			}
			httpRequest.setEntity(multipartEntity);

		} else {
			httpRequest.addHeader(HEADER_CONTENT_TYPE, request.getBodyContentType());
			byte[] body = request.getBody();
			if (body != null) {
				HttpEntity entity = new ByteArrayEntity(body);
				httpRequest.setEntity(entity);
			}
		}
	}

	/**
	 * Called before the request is executed using the underlying HttpClient.
	 * 
	 * <p>
	 * Overwrite in subclasses to augment the request.
	 * </p>
	 */
	protected void onPrepareRequest(HttpUriRequest request) throws IOException {
		// Nothing.
	}

	/**
	 * The HttpPatch class does not exist in the Android framework, so this has
	 * been defined here.
	 */
	public static final class HttpPatch extends HttpEntityEnclosingRequestBase {

		public final static String METHOD_NAME = "PATCH";

		public HttpPatch() {
			super();
		}

		public HttpPatch(final URI uri) {
			super();
			setURI(uri);
		}

		/**
		 * @throws IllegalArgumentException
		 *             if the uri is invalid.
		 */
		public HttpPatch(final String uri) {
			super();
			setURI(URI.create(uri));
		}

		@Override
		public String getMethod() {
			return METHOD_NAME;
		}

	}
}
