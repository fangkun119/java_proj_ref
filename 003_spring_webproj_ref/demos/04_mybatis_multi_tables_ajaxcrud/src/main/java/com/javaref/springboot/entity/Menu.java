package com.javaref.springboot.entity;

import java.io.Serializable;
import java.util.Objects;

public class Menu implements Serializable {
    private Integer id;

    private String name;

    private String roles;

    private String index;

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

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null || getClass() != that.getClass()) {
            return false;
        }
        Menu menu = (Menu) that;
        return Objects.equals(this.getId(), menu.getId())
                && Objects.equals(this.getName(), menu.getName())
                && Objects.equals(this.getRoles(), menu.getRoles())
                && Objects.equals(this.getIndex(), menu.getIndex())
                ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), this.getName(), this.getRoles(), this.getIndex());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", roles=").append(roles);
        sb.append(", index=").append(index);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}