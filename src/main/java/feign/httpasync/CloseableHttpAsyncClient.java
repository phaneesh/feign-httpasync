package feign.httpasync;

import feign.Client;
import feign.Response;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class CloseableHttpAsyncClient implements Client {

    private final org.apache.http.impl.nio.client.CloseableHttpAsyncClient delegate;

    public CloseableHttpAsyncClient() {
        this(HttpAsyncClients.createDefault());
    }

    public CloseableHttpAsyncClient(org.apache.http.impl.nio.client.CloseableHttpAsyncClient delegate) {
        this.delegate = delegate;
        this.delegate.start();
    }

    private static void addHeaders(feign.Request input, HttpRequestBase request) {
        boolean hasAcceptHeader = false;
        for (String field : input.headers().keySet()) {
            if (field.equalsIgnoreCase("Accept")) {
                hasAcceptHeader = true;
            }
            input.headers().forEach((key, values) -> values.forEach(value -> request.addHeader(key, value)));
        }
        // Some servers choke on the default accept string.
        if (!hasAcceptHeader) {
            request.addHeader("Accept", "*/*");
        }
    }

    private static byte[] getBody(feign.Request input) {
        if(input.body() == null) {
            return new byte[0];
        } else {
            return input.body();
        }
    }

    private Response execute(HttpGet request) throws Exception {
        return toFeignResponse(delegate.execute(request, null).get());
    }

    private Response execute(HttpPost request) throws Exception {
        return toFeignResponse(delegate.execute(request, null).get());
    }

    private Response execute(HttpPut request) throws Exception {
        return toFeignResponse(delegate.execute(request, null).get());
    }

    private Response execute(HttpDelete request) throws Exception {
        return toFeignResponse(delegate.execute(request, null).get());
    }

    private Response execute(HttpOptions request) throws Exception {
        return toFeignResponse(delegate.execute(request, null).get());
    }

    private Response execute(HttpHead request) throws Exception {
        return toFeignResponse(delegate.execute(request, null).get());
    }

    private Response execute(HttpPatch request) throws Exception {
        return toFeignResponse(delegate.execute(request, null).get());
    }


    private Response execute(feign.Request input) throws Exception {
        switch (input.method()) {
            case "GET":
                HttpGet get = new HttpGet(input.url());
                addHeaders(input, get);
                return execute(get);
            case "POST":
                HttpPost post = new HttpPost(input.url());
                addHeaders(input, post);
                post.setEntity(new ByteArrayEntity(getBody(input)));
                return execute(post);
            case "PUT":
                HttpPut put = new HttpPut(input.url());
                addHeaders(input, put);
                put.setEntity(new ByteArrayEntity(getBody(input)));
                return execute(put);
            case "DELETE":
                HttpDelete delete = new HttpDelete(input.url());
                addHeaders(input, delete);
                return execute(delete);
            case "OPTIONS":
                HttpOptions options = new HttpOptions(input.url());
                addHeaders(input, options);
                return execute(options);
            case "HEAD":
                HttpHead head = new HttpHead(input.url());
                addHeaders(input, head);
                return execute(head);
            case "PATCH":
                HttpPatch patch = new HttpPatch(input.url());
                addHeaders(input, patch);
                return execute(patch);
        }
        return null;
    }

    private static feign.Response toFeignResponse(HttpResponse input) throws IOException {
        return feign.Response.builder()
                .status(input.getStatusLine().getStatusCode())
                .reason(input.getStatusLine().getReasonPhrase())
                .headers(toMap(input.getAllHeaders()))
                .body(toBody(input.getEntity()))
                .build();
    }

    private static Map<String, Collection<String>> toMap(Header[] headers) {
        Map<String, Collection<String>> allHeaders = new HashMap<>();
        for(Header header : headers) {
            if(allHeaders.containsKey(header.getName())) {
                allHeaders.get(header.getName()).add(header.getValue());
            } else {
                allHeaders.put(header.getName(), Collections.singletonList(header.getValue()));
            }
        }
        return allHeaders;
    }

    private static feign.Response.Body toBody(final HttpEntity input) throws IOException {
        if (input == null || input.getContentLength() == 0) {
            if (input != null) {
                EntityUtils.consumeQuietly(input);
            }
            return null;
        }
        final Integer length = input.getContentLength() >= 0 && input.getContentLength() <= Integer.MAX_VALUE ?
                (int) input.getContentLength() : null;
        return new feign.Response.Body() {

            @Override
            public void close() throws IOException {
                EntityUtils.consumeQuietly(input);
            }

            @Override
            public Integer length() {
                return length;
            }

            @Override
            public boolean isRepeatable() {
                return false;
            }

            @Override
            public InputStream asInputStream() throws IOException {
                return input.getContent();
            }

            @Override
            public Reader asReader() throws IOException {
                return new InputStreamReader(input.getContent());
            }
        };
    }

    @Override
    public feign.Response execute(feign.Request input, feign.Request.Options options)
            throws IOException {
        try {
            Response response = execute(input);
            if(response != null) {
                return  response.toBuilder().request(input).build();
            }
            return null;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

}
