package com.tongyong.ct.mycore.googlevolley.request;

import com.tongyong.ct.mycore.googlevolley.NetworkResponse;
import com.tongyong.ct.mycore.googlevolley.Request;
import com.tongyong.ct.mycore.googlevolley.Response;
import com.tongyong.ct.mycore.googlevolley.Response.ErrorListener;
import com.tongyong.ct.mycore.googlevolley.Response.Listener;
import com.tongyong.ct.mycore.googlevolley.error.AuthFailureError;
import com.tongyong.ct.mycore.googlevolley.error.ParseError;
import com.tongyong.ct.mycore.googlevolley.toolbox.HttpHeaderParser;
import com.tongyong.ct.mycore.gson.Gson;
import com.tongyong.ct.mycore.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.util.Map;
 
/**
 * Volley adapter for JSON requests that will be parsed into Java objects by Gson.
 */
public class GsonRequest<T> extends Request<T> {
    private final Gson gson = new Gson();
    private final Class<T> clazz;
    private final Map<String, String> headers;
    private final Map<String, String> params;
    private final Listener<T> listener;
 
    /**
     * Make a GET request and return a parsed object from JSON.
     *
     * @param get
     * @param url URL of the request to make
     * @param clazz Relevant class object, for Gson's reflection
     * @param headers Map of request headers
     */
    public GsonRequest(int get, String url, Class<T> clazz, Map<String, String> headers,
                       Listener<T> listener, ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.clazz = clazz;
        this.headers = headers;
        this.params = null;
        this.listener = listener;
    }
    
    /**
     * Make a request and return a parsed object from JSON.
     *
     * @param url URL of the request to make
     * @param clazz Relevant class object, for Gson's reflection
     * @param headers Map of request headers
     */
    public GsonRequest(int type, String url, Class<T> clazz, Map<String, String> headers,
    		Map<String, String> params,
            Listener<T> listener, ErrorListener errorListener) {
        super(type, url, errorListener);
        this.clazz = clazz;
        this.headers = headers;
        this.params = params;
        this.listener = listener;
    }
 
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }
    
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
    	return params != null ? params : super.getParams();
    }
 
    @Override
    protected void deliverResponse(T response) {
    	if(null != listener){
    		listener.onResponse(response);
    	}
    }

    public final Class<T> getClazz() {
        return clazz;
    }
 
    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(
                    response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(
                    gson.fromJson(json, clazz), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }
}