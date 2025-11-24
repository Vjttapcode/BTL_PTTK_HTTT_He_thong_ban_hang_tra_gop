<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
	<title>Thêm kỳ thanh toán</title>
	<meta name="viewport" content="width=device-width, initial-scale=1" />
	<style>
		body { font-family: "Segoe UI", Arial, sans-serif; background: #f6f8fb; margin: 0; }
		.wrapper { max-width: 900px; margin: 30px auto; background: #fff; border: 3px solid #6a34ff; border-radius: 24px; padding: 32px; }
		h2 { text-align: center; margin-top: 0; }
		form .row { display: flex; gap: 12px; margin-bottom: 14px; }
		form input { flex: 1; border: 2px solid #000; border-radius: 12px; padding: 10px 14px; font-size: 15px; }
		.btn { background: #0a66c2; color: #fff; border: none; border-radius: 24px; padding: 10px 24px; cursor: pointer; font-size: 16px; min-width: 120px; }
		.actions { display: flex; justify-content: space-between; margin-top: 28px; }
		table { width: 100%; border-collapse: collapse; margin-top: 20px; }
		th, td { border: 2px solid #000; padding: 8px; text-align: left; }
		th { background: #f2f5ff; }
		.notice { color: #b00020; margin-bottom: 16px; text-align: center; }
	</style>
</head>
<body>
<c:url value="/contracts/schedule/add" var="addUrl" />
<c:url value="/contracts/schedule/delete" var="deleteUrl" />
<c:url value="/contracts/partner" var="prevUrl" />
<c:url value="/contracts/down-payment" var="nextUrl" />
<div class="wrapper">
	<h2>Kỳ thanh toán</h2>
	<p style="text-align:center">Bước 4/6 &mdash; Thêm các kỳ thanh toán cho sản phẩm.</p>
	<c:if test="${not empty message}">
		<div class="notice">${message}</div>
	</c:if>
	<form method="post" action="${addUrl}">
		<div class="row">
			<input type="text" name="tenKy" placeholder="Tên kỳ (ví dụ: Kỳ 1)" required />
			<input type="date" name="ngayDenHan" required />
		</div>
		<div class="row">
			<input type="number" name="tienGoc" placeholder="Tiền gốc (VND)" step="100000" min="0" required />
			<input type="number" name="tienLai" placeholder="Tiền lãi (VND)" step="10000" min="0" />
		</div>
		<div class="row">
			<input type="text" name="trangThai" placeholder="Trạng thái (ví dụ: Chưa thanh toán)" />
			<button class="btn" type="submit" style="min-width: 100px">Add</button>
		</div>
	</form>
	<c:if test="${not empty draft.schedule}">
		<table>
			<thead>
			<tr>
				<th>Tên kỳ</th>
				<th>Ngày đến hạn</th>
				<th>Tiền nợ gốc</th>
				<th>Tiền nợ lãi</th>
				<th>Trạng thái</th>
				<th></th>
			</tr>
			</thead>
			<tbody>
			<c:forEach var="ky" items="${draft.schedule}" varStatus="status">
				<tr>
					<td>${ky.tenKy}</td>
					<td><fmt:formatDate value="${ky.ngayDenHan}" pattern="dd/MM/yyyy"/></td>
					<td><fmt:formatNumber value="${ky.tienGoc}" type="currency" currencySymbol="₫"/></td>
					<td><fmt:formatNumber value="${ky.tienLai}" type="currency" currencySymbol="₫"/></td>
					<td>${ky.trangThai}</td>
					<td>
						<form method="post" action="${deleteUrl}">
							<input type="hidden" name="index" value="${status.index}" />
							<button class="btn" type="submit" style="padding:6px 12px; min-width:auto">Xóa</button>
						</form>
					</td>
				</tr>
			</c:forEach>
			</tbody>
		</table>
	</c:if>
	<c:if test="${empty draft.schedule}">
		<p style="text-align:center">Chưa có kỳ thanh toán nào.</p>
	</c:if>
	<div class="actions">
		<button class="btn" type="button" onclick="window.location='${prevUrl}'">Return</button>
		<button class="btn" type="button" onclick="window.location='${nextUrl}'">Xác nhận</button>
	</div>
</div>
</body>
</html>

