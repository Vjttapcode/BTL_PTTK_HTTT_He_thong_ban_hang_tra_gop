<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
	<title>Tìm kiếm khách hàng - Ký hợp đồng</title>
	<meta name="viewport" content="width=device-width, initial-scale=1" />
	<style>
		body { font-family: "Segoe UI", Tahoma, Arial, sans-serif; background: #f6f8fb; margin: 0; }
		.wrapper { max-width: 1100px; margin: 30px auto; background: #fff; border: 3px solid #000; border-radius: 24px; padding: 32px; }
		h1 { margin-top: 0; font-size: 32px; }
		.notice { color: #b00020; margin-bottom: 20px; }
		table { width: 100%; border-collapse: collapse; margin-top: 16px; }
		th, td { border: 2px solid #000; padding: 10px; text-align: left; }
		th { background: #f2f5ff; }
		.btn { background: #0a66c2; color: #fff; border: none; border-radius: 24px; padding: 10px 24px; cursor: pointer; font-size: 16px; min-width: 120px; }
		.actions { display: flex; justify-content: space-between; margin-top: 28px; }
		.search-row { display: flex; gap: 12px; margin-top: 10px; }
		.search-row input { flex: 1; border: 2px solid #000; border-radius: 30px; padding: 12px 20px; font-size: 16px; }
		.small { font-size: 14px; color: #666; }
	</style>
</head>
<body>
<c:url value="/contracts/customer/select" var="selectUrl" />
<c:url value="/contracts/customer" var="selfUrl" />
<c:url value="/employee/home" var="homeUrl" />
<div class="wrapper">
	<h1>Tìm kiếm khách hàng</h1>
	<p class="small">Bước 1/6 &mdash; chọn khách hàng cần ký hợp đồng.</p>
	<c:if test="${not empty message}">
		<div class="notice">${message}</div>
	</c:if>
	<form class="search-row" method="get" action="${selfUrl}">
		<input type="text" name="keyword" placeholder="Nhập tên hoặc số CCCD" value="${keyword}" />
		<button class="btn" type="submit">Search</button>
	</form>
	<c:if test="${not empty customers}">
		<table>
			<thead>
			<tr>
				<th>Tên</th>
				<th>Ngày sinh</th>
				<th>Số CCCD</th>
				<th>SĐT</th>
				<th>Email</th>
				<th>Địa chỉ</th>
				<th>Chọn</th>
			</tr>
			</thead>
			<tbody>
			<c:forEach var="cst" items="${customers}">
				<tr>
					<td>${cst.ten}</td>
					<td><fmt:formatDate value="${cst.ngaySinh}" pattern="dd/MM/yyyy"/></td>
					<td>${cst.cccd}</td>
					<td>${cst.sdt}</td>
					<td>${cst.email}</td>
					<td>${cst.diaChi}</td>
					<td style="text-align:center">
						<form method="post" action="${selectUrl}">
							<input type="hidden" name="id" value="${cst.id}" />
							<button class="btn" type="submit" style="padding:6px 16px">Click</button>
						</form>
					</td>
				</tr>
			</c:forEach>
			</tbody>
		</table>
	</c:if>
	<div class="actions">
		<button class="btn" type="button" onclick="window.location='${homeUrl}'">Return</button>
		<button class="btn" type="button" onclick="window.location='${pageContext.request.contextPath}/contracts/product'">Next</button>
	</div>
	<c:if test="${draft.customer != null}">
		<p class="small">Đang chọn: <strong>${draft.customer.ten}</strong> - ${draft.customer.cccd}</p>
	</c:if>
</div>
</body>
</html>

