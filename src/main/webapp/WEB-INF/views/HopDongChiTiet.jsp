<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"
language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
  <head>
    <title>Giao diện hợp đồng chi tiết</title>
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
      .section {
        margin-bottom: 30px;
        padding: 20px;
        background: #f9f9f9;
        border-radius: 8px;
        border: 1px solid #ddd;
      }
      .section h3 {
        margin-top: 0;
        margin-bottom: 15px;
        font-size: 18px;
      }
      .section ul {
        list-style: none;
        padding: 0;
        margin: 0;
      }
      .section li {
        padding: 5px 0;
      }
      .section li strong {
        display: inline-block;
        width: 150px;
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
    </style>
  </head>
  <body>
    <div class="wrapper">
      <h2>Giao diện hợp đồng chi tiết</h2>
      
      <c:if test="${not empty khachHang}">
        <div class="section">
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
          </ul>
        </div>
      </c:if>
      
      <c:if test="${not empty sanPham}">
        <div class="section">
          <h3>Sản phẩm</h3>
          <ul>
            <li><strong>Tên:</strong> ${sanPham.tenSp}</li>
            <li><strong>Số lượng:</strong> ${sanPham.soLuong}</li>
            <li><strong>Đơn giá:</strong> 
              <fmt:formatNumber value="${sanPham.donGia}" type="number" maxFractionDigits="0" /> VND
            </li>
            <li><strong>Mô tả:</strong> ${sanPham.moTa}</li>
          </ul>
        </div>
      </c:if>
      
      <c:if test="${not empty doiTac}">
        <div class="section">
          <h3>Đối tác</h3>
          <ul>
            <li><strong>Tên đối tác:</strong> ${doiTac.tenDoiTac}</li>
            <li><strong>Mã số thuế:</strong> ${doiTac.maSoThue}</li>
            <li><strong>Loại hình tổ chức:</strong> ${doiTac.loaiHinhToChuc}</li>
            <li><strong>Mức trả trước tối thiểu:</strong> 
              <fmt:formatNumber value="${doiTac.mucTraTruocToiThieu}" type="number" maxFractionDigits="2" />%
            </li>
          </ul>
        </div>
      </c:if>
      
      <c:if test="${not empty hopDong}">
        <div class="section">
          <h3>Hợp đồng</h3>
          <ul>
            <li><strong>Số HĐ:</strong> ${hopDong.soHopDong}</li>
            <li><strong>Tổng vay:</strong> 
              <fmt:formatNumber value="${hopDong.tongVay}" type="number" maxFractionDigits="0" /> VND
            </li>
            <li><strong>Lãi suất:</strong> 
              <fmt:formatNumber value="${hopDong.laiSuat}" type="number" maxFractionDigits="2" />%
            </li>
            <li><strong>Ngày ký:</strong> 
              <fmt:formatDate value="${hopDong.ngayKy}" pattern="dd/MM/yyyy" />
            </li>
            <li><strong>Mức trả trước:</strong> 
              <fmt:formatNumber value="${hopDong.mucTraTruoc}" type="number" maxFractionDigits="0" /> VND
            </li>
            <li><strong>Dư nợ còn lại:</strong> 
              <fmt:formatNumber value="${hopDong.duNoConLai}" type="number" maxFractionDigits="0" /> VND
            </li>
            <li><strong>Trạng thái:</strong> ${hopDong.trangThai}</li>
          </ul>
        </div>
      </c:if>
      
      <c:if test="${not empty kyThanhToanList}">
        <div class="section">
          <h3>Kỳ thanh toán</h3>
          <table>
            <thead>
              <tr>
                <th>Tên Kỳ</th>
                <th>Ngày đến hạn</th>
                <th>Tiền nợ gốc</th>
                <th>Tiền nợ lãi</th>
                <th>Trạng thái</th>
              </tr>
            </thead>
            <tbody>
              <c:forEach var="ky" items="${kyThanhToanList}">
                <tr>
                  <td>${ky.tenKy}</td>
                  <td>
                    <fmt:formatDate value="${ky.ngayDenHan}" pattern="dd/MM/yyyy" />
                  </td>
                  <td>
                    <fmt:formatNumber value="${ky.tienGoc}" type="number" maxFractionDigits="0" />
                  </td>
                  <td>
                    <fmt:formatNumber value="${ky.tienLai}" type="number" maxFractionDigits="0" />
                  </td>
                  <td>${ky.trangThai}</td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </div>
      </c:if>
      
      <c:if test="${empty kyThanhToanList}">
        <div class="section">
          <p>Chưa có kỳ thanh toán nào</p>
        </div>
      </c:if>
      
      <div class="btn-container">
        <c:url value="/debt-statistics/customer-detail" var="returnUrl" />
        <button class="btn" onclick="window.location='${returnUrl}'">
          Return
        </button>
      </div>
    </div>
  </body>
</html>

