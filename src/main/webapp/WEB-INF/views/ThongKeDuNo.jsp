<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"
language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
  <head>
    <title>Giao diện thống kê dư nợ</title>
    <meta charset="UTF-8" />
    <style>
      body {
        font-family: "Segoe UI", Tahoma, Arial, sans-serif;
        background: #f6f8fb;
        margin: 0;
      }
      .wrapper {
        max-width: 1200px;
        margin: 30px auto;
        background: #fff;
        border: 1px solid #000;
        border-radius: 10px;
        padding: 30px;
      }
      h2 {
        text-align: center;
        margin-top: 0;
        margin-bottom: 20px;
        font-size: 24px;
        font-weight: bold;
      }
      .info {
        text-align: center;
        margin-bottom: 20px;
        font-size: 16px;
      }
      table {
        width: 100%;
        border-collapse: collapse;
        margin-bottom: 20px;
      }
      th, td {
        border: 1px solid #000;
        padding: 10px;
        text-align: left;
      }
      th {
        background: #f2f5ff;
        font-weight: bold;
      }
      .btn {
        background: #0a66c2;
        color: #fff;
        border: none;
        border-radius: 20px;
        padding: 10px 24px;
        cursor: pointer;
        font-size: 16px;
        text-decoration: none;
        display: inline-block;
      }
      .btn:hover {
        background: #095195;
      }
      .btn-container {
        text-align: center;
        margin-top: 20px;
      }
      .link-detail {
        color: #0a66c2;
        text-decoration: underline;
        cursor: pointer;
      }
      .link-detail:hover {
        color: #095195;
      }
    </style>
  </head>
  <body>
    <div class="wrapper">
      <h2>Giao diện thống kê dư nợ</h2>
      
      <c:if test="${not empty duNo}">
        <div class="info">
          Ngưỡng dư nợ: <strong><fmt:formatNumber value="${duNo}" type="number" maxFractionDigits="0" /> VND</strong>
        </div>
      </c:if>
      
      <c:if test="${not empty listKhachHang}">
        <table>
          <thead>
            <tr>
              <th>Tên</th>
              <th>Ngày sinh</th>
              <th>Số CCCD</th>
              <th>SĐT</th>
              <th>Email</th>
              <th>Địa chỉ</th>
              <th>Tổng dư nợ</th>
              <th>Chi tiết</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="kh" items="${listKhachHang}">
              <tr>
                <td>${kh.ten}</td>
                <td>
                  <fmt:formatDate value="${kh.ngaySinh}" pattern="dd/MM/yyyy" />
                </td>
                <td>${kh.cccd}</td>
                <td>${kh.sdt}</td>
                <td>${kh.email}</td>
                <td>${kh.diaChi}</td>
                <td>
                  <fmt:formatNumber value="${kh.tongDuNo}" type="number" maxFractionDigits="0" />
                </td>
                <td>
                  <c:url value="/debt-statistics/customer-detail" var="detailUrl">
                    <c:param name="id" value="${kh.id}" />
                  </c:url>
                  <a href="${detailUrl}" class="link-detail">Xem</a>
                </td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </c:if>
      
      <c:if test="${empty listKhachHang}">
        <div class="info">Không có dữ liệu</div>
      </c:if>
      
      <div class="btn-container">
        <c:url value="/debt-statistics/report" var="returnUrl" />
        <button class="btn" onclick="window.location='${returnUrl}'">
          Return
        </button>
      </div>
    </div>
  </body>
</html>

