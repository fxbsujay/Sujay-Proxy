package com.susu.proxy.server.web.entity;

import lombok.Data;
import javax.servlet.http.HttpServlet;
import java.util.Objects;

@Data
public class Servlet {

    private String name;

    private String path;

    private HttpServlet servlet;

    public Servlet(String path, HttpServlet servlet) {
        this(servlet.getClass().getSimpleName(), path, servlet);
    }

    public Servlet(String name, String path, HttpServlet servlet) {
        this.name = name;
        this.path = path;
        this.servlet = servlet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Servlet servlet1 = (Servlet) o;
        return Objects.equals(name, servlet1.name) && Objects.equals(path, servlet1.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, path);
    }
}
