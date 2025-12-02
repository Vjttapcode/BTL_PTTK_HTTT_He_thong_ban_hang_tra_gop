<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"
language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
  <head>
    <title>Nhập ngưỡng dư nợ</title>
    <meta charset="UTF-8" />
    <style>
      body {
        font-family: "Segoe UI", Tahoma, Arial, sans-serif;
        background: #f6f8fb;
        margin: 0;
        display: flex;
        justify-content: center;
        align-items: center;
        min-height: 100vh;
      }
      .wrapper {
        background: #fff;
        border: 1px solid #000;
        border-radius: 10px;
        padding: 40px 60px;
        text-align: center;
        min-width: 400px;
      }
      h2 {
        margin-top: 0;
        margin-bottom: 30px;
        font-size: 24px;
        font-weight: bold;
      }
      .form-group {
        margin-bottom: 30px;
      }
      .form-group input {
        width: 100%;
        padding: 12px;
        border: 1px solid #000;
        border-radius: 4px;
        font-size: 16px;
        box-sizing: border-box;
      }
      .btn-container {
        display: flex;
        gap: 16px;
        justify-content: center;
      }
      .btn {
        background: #0a66c2;
        color: #fff;
        border: none;
        border-radius: 20px;
        padding: 10px 24px;
        cursor: pointer;
        font-size: 16px;
      }
      .btn:hover {
        background: #095195;
      }
      .error-message {
        color: #b00020;
        margin-bottom: 20px;
        padding: 10px;
        background: #ffebee;
        border-radius: 4px;
      }
      .success-message {
        color: #2e7d32;
        margin-bottom: 20px;
        padding: 10px;
        background: #e8f5e9;
        border-radius: 4px;
      }
    </style>
  </head>
  <body>
    <div class="wrapper">
      <h2>Nhập ngưỡng dư nợ</h2>
      
      <c:if test="${error == true}">
        <div class="error-message">
          Không có hợp đồng trong ngưỡng dư nợ này
          <c:if test="${not empty nguongDuNo}">
            <br />Ngưỡng dư nợ: <fmt:formatNumber value="${nguongDuNo}" type="number" maxFractionDigits="0" /> VND
          </c:if>
        </div>
      </c:if>
      
      <c:if test="${error == false}">
        <form method="post" action="<c:url value='/debt-statistics/process' />">
          <div class="form-group">
            <input
              type="text"
              name="nguongDuNo"
              placeholder="20.000.000 VND"
              value=""
              required
            />
          </div>
          <div class="btn-container">
            <c:url value="/debt-statistics/report" var="returnUrl" />
            <button type="button" class="btn" onclick="window.location='${returnUrl}'">
              Return
            </button>
            <button type="submit" class="btn">
              Tiếp tục
            </button>
          </div>
        </form>
      </c:if>
      
      <c:if test="${error == true}">
        <div class="btn-container" style="margin-top: 20px">
          <c:url value="/debt-statistics/report" var="returnUrl" />
          <button type="button" class="btn" onclick="window.location='${returnUrl}'">
            Return
          </button>
        </div>
      </c:if>
    </div>
  </body>
</html>

