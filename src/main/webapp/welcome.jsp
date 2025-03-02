<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Welcome</title>
</head>
<body>
<h1>Welcome to the Medical Appointments Application!</h1>

<ul>
    <li><a href="${pageContext.request.contextPath}/medecins/">Manage Doctors</a></li>
    <li><a href="${pageContext.request.contextPath}/patients/">Manage Patients</a></li>
    <li><a href="#">Manage Appointments </a></li>  <!-- Replace with your actual link -->
</ul>
</body>
</html>