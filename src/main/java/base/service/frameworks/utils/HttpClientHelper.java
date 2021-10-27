package base.service.frameworks.utils;

import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class HttpClientHelper {
	
	private static OkHttpClient httpClient = new OkHttpClient.Builder().build();
	
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	public static final MediaType XML = MediaType.parse("application/xml; charset=utf-8");
	public static final MediaType FORM_URLENCODED = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
	public static final MediaType TEXT = MediaType.parse("text/plain; charset=utf-8");
	
	
	public static enum DataType{
		JSON, FORM
	};
	
	
    public static String get(String url, Map<String, String> params) throws IOException {
        HttpUrl.Builder httpUrlBuilder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();
        for(String name : params.keySet()){
            httpUrlBuilder.addQueryParameter(name, params.get(name));
        }
        Request request = new Request.Builder().url(httpUrlBuilder.build()).build();
        Response response = httpClient.newCall(request).execute();
        if(response.isSuccessful() && response.body() != null){
            return response.body().string();
        }
        return null;
    }
    
    public static String get(String url,Map<String,String>headers, Map<String, String> params) throws IOException {
        HttpUrl.Builder httpUrlBuilder = HttpUrl.parse(url).newBuilder();
        if(params != null) {
	        for(String name : params.keySet()){
	            httpUrlBuilder.addQueryParameter(name, params.get(name));
	        }
        }
        Headers.Builder headersBuider = new Headers.Builder();
        for(String name: headers.keySet()){
        	headersBuider.add(name, headers.get(name));
        }
        
        Request request = new Request.Builder().url(httpUrlBuilder.build()).headers(headersBuider.build()).build();
        Response response = httpClient.newCall(request).execute();
        return response.body().string();
    }

    public static void main(String[] args) throws IOException {
        String addUrl = "http://192.168.27.4:20047/s6/favorite/add";
        String listUrl = "http://192.168.27.4:20047/s6/favorite/list";
        Map<String,String>param = new HashMap<>();
        param.put("uid","101089");
        param.put("token","ef805e7b3ada4b09bdf003b25ef7798c");
        param.put("realm","s6");
        param.put("process","app");
        param.put("batchList","[{\"type\":2,\"relatedId\":4}]");
        //param.put("uid","");

        System.out.println(post(listUrl,param));
//        System.out.println(post(addUrl,param));
//        System.out.println(post(listUrl,param));
    }

    public static String post(String url, Map<String, String> params) throws IOException {
        FormBody.Builder bodyBuilder = new FormBody.Builder();
        for(String name : params.keySet()){
            bodyBuilder.add(name, params.get(name));
        }
        Request request = new Request.Builder().url(url).post(bodyBuilder.build()).build();
        Response response = httpClient.newCall(request).execute();
        if(response.isSuccessful() && response.body() != null){
            return response.body().string();
        }
        return null;
    }
    
    public static String post(String url, String params) throws IOException {
    	RequestBody bodyData = RequestBody.create(JSON, params);
        Request request = new Request.Builder().url(url).post(bodyData).build();
        Response response = httpClient.newCall(request).execute();
        return response.body().string();
    }

    
    public static String postText(String url, String data) throws IOException {
        RequestBody bodyData = RequestBody.create(TEXT, data);
        Request request = new Request.Builder().url(url).post(bodyData).build();
        Response response = httpClient.newCall(request).execute();
        return response.body().string();
    }
    
    public static String postXML(String url, String xmlString) throws IOException {
        RequestBody bodyData = RequestBody.create(XML, xmlString);
        Request request = new Request.Builder().url(url).post(bodyData).build();
        Response response = httpClient.newCall(request).execute();
        return response.body().string();
    }
    

    public static void get(String url, Map<String, String> params, Callback responseCallback, Object tag) throws IOException {
        HttpUrl.Builder httpUrlBuilder = HttpUrl.parse(url).newBuilder();
        for(String name : params.keySet()){
            httpUrlBuilder.addQueryParameter(name, params.get(name));
        }
        Request request = new Request.Builder().tag(tag).url(httpUrlBuilder.build()).build();
        Call call = httpClient.newCall(request);
        call.enqueue(responseCallback);
    }

    public static void post(String url, Map<String, String> params, Callback responseCallback, Object tag) throws IOException {
        FormBody.Builder bodyBuilder = new FormBody.Builder();
        for(String name : params.keySet()){
            bodyBuilder.add(name, params.get(name));
        }
        Request request = new Request.Builder().tag(tag).url(url).post(bodyBuilder.build()).build();
        Call call = httpClient.newCall(request);
        call.enqueue(responseCallback);
    }
    
    public static void post(String url, String data, DataType type, Callback responseCallback, Object tag) throws IOException {
    	RequestBody bodyData = RequestBody.create(type== DataType.JSON?JSON:FORM_URLENCODED, data);
        Request request = new Request.Builder().tag(tag).url(url).post(bodyData).build();
        Call call = httpClient.newCall(request);
        call.enqueue(responseCallback);
    }


}
