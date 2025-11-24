<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
	<title>Chọn mức trả trước</title>
	<meta name="viewport" content="width=device-width, initial-scale=1" />
	<style>
		body { font-family: "Segoe UI", Arial, sans-serif; background: #f6f8fb; margin: 0; }
		.wrapper { max-width: 600px; margin: 40px auto; background: #fff; border: 3px solid #6a34ff; border-radius: 24px; padding: 32px; text-align: center; }
		h2 { margin-top: 0; }
		input { width: 100%; border: 2px solid #000; border-radius: 12px; padding: 12px; font-size: 18px; margin: 20px 0; text-align: center; }
		.btn { background: #0a66c2; color: #fff; border: none; border-radius: 24px; padding: 10px 24px; cursor: pointer; font-size: 16px; min-width: 120px; }
		.actions { display: flex; justify-content: space-between; margin-top: 28px; }
		.notice { color: #b00020; margin-bottom: 12px; }
	</style>
</head>
<body>
<c:url value="/contracts/down-payment/save" var="saveUrl" />
<c:url value="/contracts/schedule" var="prevUrl" />
<c:url value="/contracts/review" var="nextUrl" />
<div class="wrapper">
	<h2>Mức trả trước</h2>
	<p>Bước 5/6 &mdash; Đối tác yêu cầu tối thiểu: ${draft.partner.mucTraTruocToiThieu}% (<fmt:formatNumber value="${suggested}" type="currency" currencySymbol="₫"/>)</p>
	<c:if test="${not empty message}">
		<div class="notice">${message}</div>
	</c:if>
	<form method="post" action="${saveUrl}">
		<input type="number" name="mucTraTruoc" step="50000" min="0" value="${draft.mucTraTruoc}" placeholder="Nhập số tiền VND" required />
		<div class="actions">
			<button class="btn" type="button" onclick="window.location='${prevUrl}'">Return</button>
			<button class="btn" type="submit">Xác nhận</button>
		</div>
	</form>
</div>
</body>
</html>

