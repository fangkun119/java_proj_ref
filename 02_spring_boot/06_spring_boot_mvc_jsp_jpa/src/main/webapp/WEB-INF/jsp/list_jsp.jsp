<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head><meta charset="UTF-8"></head>
<body>
    <table>
        <tr><th>ID</th><th>Name</th></tr>
        <c:forEach items="${list}" var="city_item">
            <tr><td>${city_item.id}</td><td>${city_item.name}</td></tr>
        </c:forEach>
    </table>
</body>
</html>