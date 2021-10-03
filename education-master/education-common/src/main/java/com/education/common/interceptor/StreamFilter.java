package com.education.common.interceptor;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;

/**
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/5/10 9:55
 */
@Component
public class StreamFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        ServletRequest requestWrapper = null;
        if (servletRequest instanceof HttpServletRequest) {
            requestWrapper = new BodyReaderHttpServletRequestWrapper((HttpServletRequest) servletRequest);
        }
        if (requestWrapper == null) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            filterChain.doFilter(requestWrapper, servletResponse);
        }
    }

    static class BodyReaderHttpServletRequestWrapper extends HttpServletRequestWrapper {

        private final byte[] body;

        public BodyReaderHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
            super(request);
            this.body = toByteArray(request.getInputStream());
        }

        private byte[] toByteArray(InputStream in) throws IOException {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024 * 4];
            int i = 0;
            while ((i = in.read(buffer)) != -1) {
                out.write(buffer, 0, i);
            }
            return out.toByteArray();
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.body);
            return new ServletInputStreamImpl(byteArrayInputStream);
        }

        @Override
        public BufferedReader getReader() throws IOException {
           return new BufferedReader(new InputStreamReader(getInputStream()));
        }
    }

    static class ServletInputStreamImpl extends ServletInputStream {

        private ByteArrayInputStream byteArrayInputStream;

        public ServletInputStreamImpl(ByteArrayInputStream byteArrayInputStream) {
            this.byteArrayInputStream = byteArrayInputStream;
        }

        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setReadListener(ReadListener readListener) {

        }

        @Override
        public int read() throws IOException {
            return this.byteArrayInputStream.read();
        }
    }
}
