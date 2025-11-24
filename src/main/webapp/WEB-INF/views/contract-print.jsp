<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
	<title>In hợp đồng</title>
	<meta name="viewport" content="width=device-width, initial-scale=1" />
	<style>
		body { font-family: "Segoe UI", Arial, sans-serif; margin: 0; background: #f6f8fb; }
		.wrapper { max-width: 900px; margin: 30px auto; background: #fff; border: 3px solid #000; border-radius: 20px; padding: 32px; }
		h1 { text-align: center; }
		table { width: 100%; border-collapse: collapse; margin-top: 16px; }
		th, td { border: 2px solid #000; padding: 8px; }
		th { background: #f2f5ff; }
		.btn { background: #0a66c2; color: #fff; border: none; border-radius: 24px; padding: 10px 24px; cursor: pointer; font-size: 16px; min-width: 150px; }
		.actions { display: flex; justify-content: center; margin-top: 24px; gap: 16px; }
		.box { border: 2px solid #000; border-radius: 10px; padding: 8px 12px; display: inline-block; margin: 4px; }
	</style>
</head>
<body>
<c:url value="/employee/home" var="homeUrl" />
<div class="wrapper" id="print-area">
	<h1>Hợp đồng trả góp #${contractId}</h1>
	<h3>Khách hàng:</h3>
	<div class="box">${draft.customer.ten}</div>
	<div class="box">${draft.customer.cccd}</div>
	<div class="box">${draft.customer.sdt}</div>
	<div class="box">${draft.customer.email}</div>

	<h3>Sản phẩm:</h3>
	<div class="box">${draft.product.tenSp}</div>
	<div class="box"><fmt:formatNumber value="${draft.product.donGia}" type="currency" currencySymbol="₫"/></div>

	<h3>Đối tác:</h3>
	<div class="box">${draft.partner.tenDoiTac}</div>
	<div class="box">${draft.partner.loaiHinhToChuc}</div>

	<h3>Thông tin hợp đồng:</h3>
	<div class="box">Số HĐ: ${draft.soHopDong}</div>
	<div class="box">Tổng vay: <fmt:formatNumber value="${draft.tongVay}" type="currency" currencySymbol="₫"/></div>
	<div class="box">Mức trả trước: <fmt:formatNumber value="${draft.mucTraTruoc}" type="currency" currencySymbol="₫"/></div>
	<div class="box">Lãi suất: ${draft.laiSuat}%</div>
	<div class="box">Ngày ký: <fmt:formatDate value="${draft.ngayKy}" pattern="dd/MM/yyyy"/></div>
	<div class="box">Trạng thái: ${draft.trangThai}</div>

	<h3>Kỳ thanh toán:</h3>
	<table>
		<thead>
		<tr>
			<th>Tên kỳ</th>
			<th>Ngày đến hạn</th>
			<th>Tiền nợ gốc</th>
			<th>Tiền nợ lãi</th>
			<th>Trạng thái</th>
		</tr>
		</thead>
		<tbody>
		<c:forEach var="ky" items="${schedule}">
			<tr>
				<td>${ky.tenKy}</td>
				<td><fmt:formatDate value="${ky.ngayDenHan}" pattern="dd/MM/yyyy"/></td>
				<td><fmt:formatNumber value="${ky.tienGoc}" type="currency" currencySymbol="₫"/></td>
				<td><fmt:formatNumber value="${ky.tienLai}" type="currency" currencySymbol="₫"/></td>
				<td>${ky.trangThai}</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
</div>
<div class="actions">
	<button class="btn" onclick="window.location='${homeUrl}'">Về trang nhân viên</button>
</div>
</body>
</html>

