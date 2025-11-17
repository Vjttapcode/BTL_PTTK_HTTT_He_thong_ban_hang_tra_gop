<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"
language="java" isELIgnored="false" %> <%@ taglib prefix="c"
uri="http://java.sun.com/jsp/jstl/core" %> <%@ taglib prefix="fmt"
uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
  <head>
    <title>Chi tiết khách hàng</title>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <style>
      body {
        font-family: "Segoe UI", Tahoma, Arial, sans-serif;
        background: #f6f8fb;
        margin: 0;
      }
      .wrapper {
        max-width: 760px;
        margin: 30px auto;
        background: #fff;
        border: 3px solid #000;
        border-radius: 18px;
        padding: 32px;
      }
      h1 {
        margin-top: 0;
      }
      .row {
        display: flex;
        align-items: center;
        margin: 12px 0;
      }
      .row label {
        width: 160px;
        font-weight: 600;
      }
      .value-box {
        flex: 1;
        border: 2px solid #000;
        border-radius: 30px;
        padding: 10px 20px;
        background: #fdfdfd;
      }
      .actions {
        display: flex;
        gap: 18px;
        justify-content: center;
        margin-top: 30px;
      }
      .btn {
        background: #0a66c2;
        color: #fff;
        border: none;
        border-radius: 24px;
        padding: 12px 28px;
        cursor: pointer;
        min-width: 140px;
      }
    </style>
  </head>
  <body>
    <div class="wrapper">
      <c:url value="/customer/search" var="searchUrl" />
      <c:url value="/customer/history" var="historyUrl" />
      <c:if test="${empty customer}">
        <p>Không có dữ liệu để hiển thị.</p>
      </c:if>
      <c:if test="${not empty customer}">
        <h1>Chi tiết khách hàng</h1>
        <div class="row">
          <label>Họ và tên:</label>
          <div class="value-box">${customer.ten}</div>
        </div>
        <div class="row">
          <label>Ngày sinh:</label>
          <div class="value-box">
            <fmt:formatDate value="${customer.ngaySinh}" pattern="dd/MM/yyyy" />
          </div>
        </div>
        <div class="row">
          <label>Số CCCD:</label>
          <div class="value-box">${customer.cccd}</div>
        </div>
        <div class="row">
          <label>Số điện thoại:</label>
          <div class="value-box">${customer.sdt}</div>
        </div>
        <div class="row">
          <label>Email:</label>
          <div class="value-box">${customer.email}</div>
        </div>
        <div class="row">
          <label>Địa chỉ:</label>
          <div class="value-box">${customer.diaChi}</div>
        </div>
        <div class="actions">
          <button
            class="btn"
            type="button"
            onclick="window.location='${searchUrl}'"
          >
            Return
          </button>
          <button
            class="btn"
            type="button"
            onclick="window.location='${historyUrl}?id=${customer.id}'"
          >
            Xem lịch sử giao dịch
          </button>
        </div>
      </c:if>
    </div>
  </body>
</html>
