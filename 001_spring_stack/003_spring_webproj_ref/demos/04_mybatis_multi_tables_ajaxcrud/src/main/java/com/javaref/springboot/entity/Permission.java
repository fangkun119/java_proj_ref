package com.javaref.springboot.entity;

import java.io.Serializable;
import java.util.Objects;

public class Permission implements Serializable {
    private Integer id;

    private String uri;

    private String name;

    private Boolean c;

    private Boolean r;

    private Boolean u;

    private Boolean d;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getC() {
        return c;
    }

    public void setC(Boolean c) {
        this.c = c;
    }

    public Boolean getR() {
        return r;
    }

    public void setR(Boolean r) {
        this.r = r;
    }

    public Boolean getU() {
        return u;
    }

    public void setU(Boolean u) {
        this.u = u;
    }

    public Boolean getD() {
        return d;
    }

    public void setD(Boolean d) {
        this.d = d;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null || getClass() != that.getClass()) {
            return false;
        }
        Permission permission = (Permission) that;
        return Objects.equals(this.getId(),   permission.getId())
                && Objects.equals(this.getUri(),  permission.getUri())
                && Objects.equals(this.getName(), permission.getName())
                && Objects.equals(this.getC(),    permission.getC())
                && Objects.equals(this.getR(),    permission.getR())
                && Objects.equals(this.getU(),    permission.getU())
                && Objects.equals(this.getD(),    permission.getD())
                ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUri(), getName(), getC(), getR(), getU(), getD());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", uri=").append(uri);
        sb.append(", name=").append(name);
        sb.append(", c=").append(c);
        sb.append(", r=").append(r);
        sb.append(", u=").append(u);
        sb.append(", d=").append(d);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}