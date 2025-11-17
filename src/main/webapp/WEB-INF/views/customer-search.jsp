<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"
language="java" isELIgnored="false" %> <%@ taglib prefix="c"
uri="http://java.sun.com/jsp/jstl/core" %> <%@ taglib prefix="fmt"
uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
  <head>
    <title>Tìm kiếm khách hàng</title>
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
      .search-box {
        display: flex;
        align-items: center;
        gap: 12px;
        margin-bottom: 24px;
      }
      .search-box input {
        flex: 1;
        border: 2px solid #000;
        border-radius: 30px;
        padding: 12px 20px;
        font-size: 16px;
      }
      .btn {
        background: #0a66c2;
        color: #fff;
        border: none;
        border-radius: 24px;
        padding: 10px 22px;
        cursor: pointer;
        min-width: 110px;
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
      .table-wrapper {
        overflow-x: auto;
      }
      .notice {
        margin-bottom: 16px;
        color: #b00020;
      }
      .actions {
        display: flex;
        justify-content: center;
        margin-top: 24px;
      }
      .link-btn {
        text-decoration: none;
        display: inline-block;
      }
    </style>
  </head>
  <body>
    <div class="wrapper">
      <c:url value="/customer/search" var="searchUrl" />
      <c:url value="/employee/home" var="homeUrl" />
      <c:url value="/customer/detail" var="detailBase" />
      <h1>Tìm kiếm</h1>
      <c:if test="${not empty error}">
        <div class="notice">${error}</div>
      </c:if>
      <form class="search-box" method="get" action="${searchUrl}">
        <input
          type="text"
          name="keyword"
          placeholder="Nhập tên hoặc số CCCD"
          value="${keyword}"
        />
        <button class="btn" type="submit">Search</button>
      </form>

      <c:if test="${not empty customers}">
        <div class="table-wrapper">
          <table>
            <thead>
              <tr>
                <th>Tên</th>
                <th>Ngày sinh</th>
                <th>Số CCCD</th>
                <th>SĐT</th>
                <th>Email</th>
                <th>Địa chỉ</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              <c:forEach var="c" items="${customers}">
                <tr>
                  <td>${c.ten}</td>
                  <td>
                    <fmt:formatDate
                      value="${c.ngaySinh}"
                      pattern="dd/MM/yyyy"
                    />
                  </td>
                  <td>${c.cccd}</td>
                  <td>${c.sdt}</td>
                  <td>${c.email}</td>
                  <td>${c.diaChi}</td>
                  <td>
                    <a
                      class="link-btn btn"
                      style="text-align: center; padding: 6px 12px"
                      href="${detailBase}?id=${c.id}"
                      >Chi tiết</a
                    >
                  </td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </div>
      </c:if>
      <c:if test="${empty customers and not empty keyword}">
        <div>${message}</div>
      </c:if>
      <div class="actions">
        <button
          class="btn"
          type="button"
          onclick="window.location='${homeUrl}'"
        >
          Return
        </button>
      </div>
    </div>
  </body>
</html>
