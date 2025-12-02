<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"
language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
  <head>
    <title>Báo cáo thống kê</title>
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
      }
      h2 {
        margin-top: 0;
        margin-bottom: 30px;
        font-size: 24px;
        font-weight: bold;
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
    </style>
  </head>
  <body>
    <div class="wrapper">
      <h2>Báo cáo thống kê</h2>
      <div class="btn-container">
        <c:url value="/debt-statistics/choose-threshold" var="tkDuNoUrl" />
        <c:url value="/employee/home" var="returnUrl" />
        <button class="btn" onclick="window.location='${tkDuNoUrl}'">
          TK Dư nợ
        </button>
        <button class="btn" onclick="window.location='${returnUrl}'">
          Return
        </button>
      </div>
    </div>
  </body>
</html>

