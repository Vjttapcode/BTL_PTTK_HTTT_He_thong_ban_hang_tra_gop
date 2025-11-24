<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"
language="java" isELIgnored="false" %> <%@ taglib prefix="c"
uri="http://java.sun.com/jsp/jstl/core" %>
<html>
  <head>
    <title>Trang chủ nhân viên</title>
    <style>
      body {
        font-family: "Segoe UI", Tahoma, Arial, sans-serif;
        background: #f6f8fb;
        margin: 0;
      }
      .header {
        background: #0a66c2;
        color: #fff;
        padding: 12px 20px;
        text-align: center;
        font-weight: bold;
      }
      .wrapper {
        max-width: 900px;
        margin: 30px auto;
        background: #fff;
        border: 2px solid #000;
        border-radius: 10px;
        padding: 24px 28px;
      }
      .row {
        display: flex;
        align-items: center;
        gap: 8px;
        color: #333;
      }
      .avatar {
        width: 36px;
        height: 36px;
        border-radius: 50%;
        background: #e0e7ff;
        color: #1f3bb3;
        display: inline-flex;
        align-items: center;
        justify-content: center;
        font-weight: 600;
      }
      h2 {
        text-align: center;
      }
      .btn {
        background: #0a66c2;
        color: #fff;
        border: none;
        border-radius: 20px;
        padding: 10px 18px;
        cursor: pointer;
      }
      .center {
        text-align: center;
      }
      .topbar {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 14px;
      }
      .link {
        color: #0a66c2;
        text-decoration: underline;
      }
    </style>
    <meta charset="UTF-8" />
  </head>
  <body>
    <div class="header">HỆ THỐNG QUẢN LÝ BÁN HÀNG TRẢ GÓP</div>
    <div class="wrapper">
      <c:url value="/auth/logout" var="logoutUrl" />
      <c:url value="/customer/search" var="customerUrl" />
      <c:url value="/contracts/start" var="contractUrl" />
      <div class="topbar">
        <div class="row">
          <div class="avatar">${initials}</div>
          <div>&nbsp;Xin chào <strong>${ten}</strong></div>
        </div>
        <div>
          <a class="link" href="${logoutUrl}">Đăng xuất</a>
        </div>
      </div>
      <h2>Trang chủ nhân viên</h2>
      <div class="center" style="margin-top: 20px">
        <div
          style="
            display: flex;
            gap: 16px;
            flex-wrap: wrap;
            justify-content: center;
          "
        >
          <button class="btn" onclick="window.location='${customerUrl}'">
            Quản lý thông tin khách hàng
          </button>
          <button
            class="btn"
            onclick="window.location='${contractUrl}'"
          >
            Ký hợp đồng trả góp
          </button>
          <button
            class="btn"
            onclick="alert('Thống kê khách hàng theo dư nợ còn lại sẽ được phát triển sau.')"
          >
            Thống kê khách hàng theo dư nợ còn lại
          </button>
        </div>
      </div>
    </div>
  </body>
</html>
