<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"
language="java" isELIgnored="false" %> <%@ taglib prefix="c"
uri="http://java.sun.com/jsp/jstl/core" %> <%@ taglib prefix="fmt"
uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
  <head>
    <title>Lịch sử giao dịch khách hàng</title>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <style>
      body {
        font-family: "Segoe UI", Tahoma, Arial, sans-serif;
        background: #f6f8fb;
        margin: 0;
      }
      .wrapper {
        max-width: 960px;
        margin: 30px auto;
        background: #fff;
        border: 3px solid #000;
        border-radius: 18px;
        padding: 32px;
      }
      h1 {
        margin-top: 0;
      }
      .info-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
        gap: 6px 30px;
        margin-bottom: 18px;
      }
      .info-grid div {
        font-size: 15px;
      }
      .label {
        font-weight: 600;
        margin-right: 4px;
      }
      table {
        width: 100%;
        border-collapse: collapse;
      }
      th,
      td {
        border: 2px solid #000;
        padding: 10px;
        text-align: left;
      }
      th {
        background: #f2f5ff;
      }
      .actions {
        display: flex;
        justify-content: center;
        margin-top: 26px;
      }
      .btn {
        background: #0a66c2;
        color: #fff;
        border: none;
        border-radius: 24px;
        padding: 12px 28px;
        cursor: pointer;
        min-width: 130px;
      }
    </style>
  </head>
  <body>
    <div class="wrapper">
      <c:url value="/customer/search" var="searchUrl" />
      <h1>Lịch sử giao dịch khách hàng</h1>
      <c:if test="${empty customer}">
        <p>Không tìm thấy dữ liệu khách hàng.</p>
      </c:if>
      <c:if test="${not empty customer}">
        <div class="info-grid">
          <div><span class="label">Họ và tên:</span>${customer.ten}</div>
          <div><span class="label">Số điện thoại:</span>${customer.sdt}</div>
          <div>
            <span class="label">Ngày sinh:</span
            ><fmt:formatDate
              value="${customer.ngaySinh}"
              pattern="dd/MM/yyyy"
            />
          </div>
          <div><span class="label">Email:</span>${customer.email}</div>
          <div><span class="label">Số CCCD:</span>${customer.cccd}</div>
          <div><span class="label">Địa chỉ:</span>${customer.diaChi}</div>
        </div>
        <table>
          <thead>
            <tr>
              <th>Số hợp đồng</th>
              <th>Tổng vay (VND)</th>
              <th>Mức trả trước</th>
              <th>Lãi suất (%)</th>
              <th>Dư nợ còn lại</th>
              <th>Ngày ký</th>
              <th>Trạng thái</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="hd" items="${contracts}">
              <tr>
                <td>${hd.soHopDong}</td>
                <td><c:out value="${hd.tongVay}" /></td>
                <td><c:out value="${hd.mucTraTruoc}" /></td>
                <td><c:out value="${hd.laiSuat}" /></td>
                <td><c:out value="${hd.duNoConLai}" /></td>
                <td>
                  <fmt:formatDate value="${hd.ngayKy}" pattern="dd/MM/yyyy" />
                </td>
                <td>${hd.trangThai}</td>
              </tr>
            </c:forEach>
            <c:if test="${empty contracts}">
              <tr>
                <td colspan="7" style="text-align: center">
                  Chưa có hợp đồng nào.
                </td>
              </tr>
            </c:if>
          </tbody>
        </table>
      </c:if>
      <div class="actions">
        <button
          class="btn"
          type="button"
          onclick="window.location='${searchUrl}'"
        >
          Return
        </button>
      </div>
    </div>
  </body>
</html>
