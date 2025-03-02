<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title><c:choose>
    <c:when test="${register}">Register</c:when>
    <c:otherwise>Login</c:otherwise>
  </c:choose></title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<header>
  <div class="container">
    <h1><c:choose>
      <c:when test="${register}">Register</c:when>
      <c:otherwise>Login</c:otherwise>
    </c:choose></h1>
  </div>
</header>

<main>
  <div class="container">
    <c:if test="${not empty errorMessage}">
      <div class="message error"><c:out value="${errorMessage}" /></div>
    </c:if>

    <c:if test="${not empty successMessage}">
      <div class="message success"><c:out value="${successMessage}" /></div>
    </c:if>

    <c:choose>
      <c:when test="${register}">
        <h2>Register</h2>
        <form action="${pageContext.request.contextPath}/auth/" method="post">
          <input type="hidden" name="action" value="register">

          <label for="reg_username">Username:</label><br>
          <input type="text" id="reg_username" name="username" required><br><br>

          <label for="reg_password">Password:</label><br>
          <input type="password" id="reg_password" name="password" required><br><br>

          <label for="reg_email">Email:</label><br>
          <input type="email" id="reg_email" name="email" required><br><br>

          <label for="reg_telephone">Telephone:</label><br>
          <input type="text" id="reg_telephone" name="telephone" required><br><br>

          <button type="submit">Register</button>
          <a href="${pageContext.request.contextPath}/auth/" class="button">Login</a>
        </form>
      </c:when>
      <c:otherwise>
        <h2>Login</h2>
        <form action="${pageContext.request.contextPath}/auth/" method="post">
          <input type="hidden" name="action" value="login">

          <label for="log_username">Username:</label><br>
          <input type="text" id="log_username" name="username" required><br><br>

          <label for="log_password">Password:</label><br>
          <input type="password" id="log_password" name="password" required><br><br>

          <button type="submit">Login</button>
          <a href="${pageContext.request.contextPath}/auth/register" class="button">Register</a>
        </form>
      </c:otherwise>
    </c:choose>

  </div>
</main>

<footer>
  <div class="container">
    <p>Medical Appointments Application</p>
  </div>
</footer>

</body>
</html>