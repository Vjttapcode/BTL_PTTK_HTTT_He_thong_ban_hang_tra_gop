<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"
language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
  <head>
    <title>Giao diện thống kê chi tiết</title>
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
      .customer-info {
        margin-bottom: 30px;
        padding: 20px;
        background: #f9f9f9;
        border-radius: 8px;
        border: 1px solid #ddd;
      }
      .customer-info h3 {
        margin-top: 0;
        margin-bottom: 15px;
        font-size: 18px;
      }
      .customer-info ul {
        list-style: none;
        padding: 0;
        margin: 0;
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 10px;
      }
      .customer-info li {
        padding: 5px 0;
      }
      .customer-info li strong {
        display: inline-block;
        width: 120px;
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
      <h2>Giao diện thống kê chi tiết</h2>
      
      <c:if test="${not empty khachHang}">
        <div class="customer-info">
          <h3>Khách hàng</h3>
          <ul>
            <li><strong>Tên:</strong> ${khachHang.ten}</li>
            <li><strong>Ngày sinh:</strong> 
              <fmt:formatDate value="${khachHang.ngaySinh}" pattern="dd/MM/yyyy" />
            </li>
            <li><strong>Số CCCD:</strong> ${khachHang.cccd}</li>
            <li><strong>SĐT:</strong> ${khachHang.sdt}</li>
            <li><strong>Email:</strong> ${khachHang.email}</li>
            <li><strong>Địa chỉ:</strong> ${khachHang.diaChi}</li>
            <li><strong>Tổng dư nợ:</strong> 
              <fmt:formatNumber value="${khachHang.tongDuNo}" type="number" maxFractionDigits="0" /> VND
            </li>
          </ul>
        </div>
      </c:if>
      
      <c:if test="${not empty listHopDong}">
        <table>
          <thead>
            <tr>
              <th>Số Hợp đồng</th>
              <th>Tổng vay (VND)</th>
              <th>Mức trả trước</th>
              <th>Lãi suất</th>
              <th>Dư nợ còn lại</th>
              <th>Ngày ký</th>
              <th>Trạng thái</th>
              <th>Chi tiết</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="hd" items="${listHopDong}">
              <tr>
                <td>${hd.soHopDong}</td>
                <td>
                  <fmt:formatNumber value="${hd.tongVay}" type="number" maxFractionDigits="0" />
                </td>
                <td>
                  <fmt:formatNumber value="${hd.mucTraTruoc}" type="number" maxFractionDigits="0" />
                </td>
                <td>
                  <fmt:formatNumber value="${hd.laiSuat}" type="number" maxFractionDigits="2" />%
                </td>
                <td>
                  <fmt:formatNumber value="${hd.duNoConLai}" type="number" maxFractionDigits="0" />
                </td>
                <td>
                  <fmt:formatDate value="${hd.ngayKy}" pattern="dd/MM/yyyy" />
                </td>
                <td>${hd.trangThai}</td>
                <td>
                  <c:url value="/debt-statistics/contract-detail" var="detailUrl">
                    <c:param name="id" value="${hd.id}" />
                  </c:url>
                  <a href="${detailUrl}" class="link-detail">Xem</a>
                </td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </c:if>
      
      <c:if test="${empty listHopDong}">
        <div style="text-align: center; padding: 20px;">
          Không có hợp đồng nào
        </div>
      </c:if>
      
      <div class="btn-container">
        <c:url value="/debt-statistics/list" var="returnUrl" />
        <button class="btn" onclick="window.location='${returnUrl}'">
          Return
        </button>
      </div>
    </div>
  </body>
</html>

