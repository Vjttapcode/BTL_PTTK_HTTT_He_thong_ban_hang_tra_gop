<%@ page contentType="text/html;charset=UTF-8" language="java"
isELIgnored="false" %> <%@ taglib prefix="c"
uri="http://java.sun.com/jsp/jstl/core" %>
<html>
  <head>
    <title>Đăng ký</title>
    <style>
      body {
        font-family: Arial, sans-serif;
        background: #f6f8fb;
      }
      .container {
        max-width: 620px;
        margin: 40px auto;
        background: #fff;
        border: 2px solid #000;
        border-radius: 8px;
        padding: 24px 32px;
      }
      h1 {
        text-align: center;
        margin-top: 0;
      }
      .grid {
        display: grid;
        grid-template-columns: 160px 1fr;
        gap: 10px 14px;
        align-items: center;
      }
      .input {
        padding: 8px;
        border: 1px solid #bbb;
        border-radius: 4px;
      }
      .btn {
        background: #0a66c2;
        color: #fff;
        border: none;
        border-radius: 20px;
        padding: 10px 22px;
        cursor: pointer;
      }
      .center {
        text-align: center;
      }
      .error {
        color: #b00020;
        margin-bottom: 12px;
        text-align: center;
      }
      .success {
        color: #0a7a2a;
        margin-bottom: 12px;
        text-align: center;
      }
    </style>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
  </head>
  <body>
    <div class="container">
      <c:url value="/auth/register" var="registerAction" />
      <c:url value="/auth/login" var="loginUrl" />
      <h1>ĐĂNG KÝ NHÂN VIÊN</h1>
      <c:if test="${not empty error}">
        <div class="error">${error}</div>
      </c:if>
      <c:if test="${not empty success}">
        <div class="success">${success}</div>
      </c:if>
      <form method="post" action="${registerAction}">
        <div class="grid">
          <label>Họ tên</label>
          <input class="input" type="text" name="fullName" />
          <label>Email</label>
          <input class="input" type="email" name="email" />
          <label>Chi nhánh</label>
          <input class="input" type="text" name="chiNhanh" />
          <label>Username</label>
          <input class="input" type="text" name="username" required />
          <label>Password</label>
          <input class="input" type="password" name="password" required />
        </div>
        <div class="center" style="margin-top: 18px">
          <button class="btn" type="submit">Đăng ký</button>
        </div>
      </form>
      <div class="center" style="margin-top: 16px">
        <a href="${loginUrl}">Quay lại đăng nhập</a>
      </div>
    </div>
  </body>
</html>
