package com.javaref.springboot.entity;

import java.io.Serializable;
import java.util.Objects;

public class Role implements Serializable {
    private Integer id;

    private String name;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object role) {
        if (this == role) {
            return true;
        }
        if (role == null || getClass() != role.getClass()) {
            return false;
        }
        return Objects.equals(this.getId(),   ((Role) role).getId())
            && Objects.equals(this.getName(), ((Role) role).getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), this.getName());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}