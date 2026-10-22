package com.eldercare.common.web.filter;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 对请求参数、请求头与 JSON 请求体中的 "<"、">" 做转义，防范存储型/反射型 XSS。
 * multipart 请求（文件上传）不做处理，直接透传。
 */
public class XssRequestWrapper extends HttpServletRequestWrapper {

    /** 转义后的请求体（multipart 时为 null） */
    private final byte[] body;

    public XssRequestWrapper(HttpServletRequest request) {
        super(request);
        this.body = isMultipart(request) ? null : clean(readBody(request)).getBytes(StandardCharsets.UTF_8);
    }

    private boolean isMultipart(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null && contentType.toLowerCase().startsWith("multipart/");
    }

    private String readBody(HttpServletRequest request) {
        try (InputStream is = request.getInputStream()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "";
        }
    }

    private String clean(String value) {
        if (value == null) {
            return null;
        }
        // 仅转义尖括号，避免破坏 JSON 引号与 URL 查询参数
        return value.replace("<", "&lt;").replace(">", "&gt;");
    }

    @Override
    public String getParameter(String name) {
        return clean(super.getParameter(name));
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values == null) {
            return null;
        }
        return Arrays.stream(values).map(this::clean).toArray(String[]::new);
    }

    @Override
    public String getHeader(String name) {
        return clean(super.getHeader(name));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (body == null) {
            return super.getInputStream();
        }
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);
        return new ServletInputStream() {
            @Override
            public int read() {
                return byteArrayInputStream.read();
            }

            @Override
            public boolean isFinished() {
                return byteArrayInputStream.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                // 同步流，无需实现
            }
        };
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
    }
}
