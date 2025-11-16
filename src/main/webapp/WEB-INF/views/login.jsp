<%@ page contentType="text/html;charset=UTF-8" language="java"
isELIgnored="false" %> <%@ taglib prefix="c"
uri="http://java.sun.com/jsp/jstl/core" %>
<html>
  <head>
    <title>Đăng nhập</title>
    <style>
      body {
        font-family: Arial, sans-serif;
        background: #f6f8fb;
      }
      .container {
        max-width: 520px;
        margin: 60px auto;
        background: #fff;
        border: 2px solid #000;
        border-radius: 8px;
        padding: 24px 32px;
      }
      h1 {
        text-align: center;
        margin-top: 0;
      }
      .form-row {
        margin: 12px 0;
        display: flex;
        align-items: center;
      }
      .form-row label {
        width: 120px;
      }
      .input {
        flex: 1;
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
      .link {
        color: #0a66c2;
        text-decoration: none;
      }
    </style>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
  </head>
  <body>
    <div class="container">
      <c:url value="/auth/login" var="loginAction" />
      <c:url value="/auth/register" var="registerUrl" />
      <h1>ĐĂNG NHẬP</h1>
      <c:if test="${not empty error}">
        <div class="error">${error}</div>
      </c:if>
      <form method="post" action="${loginAction}">
        <div class="form-row">
          <label>username</label>
          <input class="input" type="text" name="username" required />
        </div>
        <div class="form-row">
          <label>password</label>
          <input class="input" type="password" name="password" required />
        </div>
        <div class="center" style="margin-top: 18px">
          <button class="btn" type="submit">Login</button>
        </div>
      </form>
      <div class="center" style="margin-top: 16px">
        <a class="link" href="${registerUrl}">Chưa có tài khoản? Đăng ký</a>
      </div>
    </div>
  </body>
</html>
