<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
	<title>Giao diện hợp đồng</title>
	<meta name="viewport" content="width=device-width, initial-scale=1" />
	<style>
		body { font-family: "Segoe UI", Arial, sans-serif; background: #f6f8fb; margin: 0; }
		.wrapper { max-width: 900px; margin: 30px auto; background: #fff; border: 3px solid #000; border-radius: 20px; padding: 32px; }
		h1 { margin-top: 0; }
		.box { border: 2px solid #000; border-radius: 10px; padding: 10px; margin-bottom: 10px; display: inline-block; min-width: 120px; }
		table { width: 100%; border-collapse: collapse; margin-top: 16px; }
		th, td { border: 2px solid #000; padding: 8px; }
		th { background: #f2f5ff; }
		.section-title { font-weight: bold; margin-top: 24px; }
		.form-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 12px; margin-top: 12px; }
		.form-grid label { font-size: 14px; }
		.form-grid input { width: 100%; border: 2px solid #000; border-radius: 8px; padding: 8px; }
		.btn { background: #0a66c2; color: #fff; border: none; border-radius: 24px; padding: 10px 24px; cursor: pointer; font-size: 16px; min-width: 120px; }
		.actions { display: flex; justify-content: space-between; margin-top: 28px; }
		.notice { color: #b00020; margin-bottom: 12px; }
	</style>
</head>
<body>
<fmt:formatDate value="${draft.ngayKy}" pattern="yyyy-MM-dd" var="ngayKyValue" />
<c:url value="/contracts/submit" var="submitUrl" />
<div class="wrapper">
	<h1>Giao diện hợp đồng</h1>
	<p>Bước 6/6 &mdash; Kiểm tra thông tin trước khi in.</p>
	<c:if test="${not empty message}">
		<div class="notice">${message}</div>
	</c:if>
	<h3>Khách hàng:</h3>
	<div class="box">${draft.customer.ten}</div>
	<div class="box"><fmt:formatDate value="${draft.customer.ngaySinh}" pattern="dd/MM/yyyy"/></div>
	<div class="box">${draft.customer.cccd}</div>
	<div class="box">${draft.customer.sdt}</div>
	<div class="box">${draft.customer.email}</div>
	<div class="box">${draft.customer.diaChi}</div>

	<h3>Sản phẩm:</h3>
	<div class="box">Tên: ${draft.product.tenSp}</div>
	<div class="box">Số lượng: ${draft.product.soLuong}</div>
	<div class="box">Đơn giá: <fmt:formatNumber value="${draft.product.donGia}" type="currency" currencySymbol="₫"/></div>
	<div class="box">Mô tả: ${draft.product.moTa}</div>

	<h3>Đối tác:</h3>
	<div class="box">${draft.partner.tenDoiTac}</div>
	<div class="box">${draft.partner.maSoThue}</div>
	<div class="box">${draft.partner.loaiHinhToChuc}</div>
	<div class="box">Tối thiểu: ${draft.partner.mucTraTruocToiThieu}%</div>

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
		<c:forEach var="ky" items="${draft.schedule}">
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

	<form method="post" action="${submitUrl}">
		<div class="section-title">Hợp đồng:</div>
		<div class="form-grid">
			<label>Số HĐ<input type="text" name="soHopDong" value="${draft.soHopDong}" /></label>
			<label>Mức trả trước<input type="number" name="mucTraTruoc" value="${draft.mucTraTruoc}" readonly /></label>
			<label>Tổng vay<input type="text" value="${calculatedTongVay}" readonly /></label>
			<label>Dư nợ còn lại<input type="text" value="${calculatedDuNo}" readonly /></label>
			<label>Lãi suất (%/tháng)<input type="number" step="0.01" name="laiSuat" value="${draft.laiSuat}" /></label>
			<label>Ngày ký<input type="date" name="ngayKy" value="${ngayKyValue}" /></label>
			<label>Trạng thái<input type="text" name="trangThai" value="${draft.trangThai}" /></label>
		</div>
		<div class="actions">
			<button class="btn" type="submit" name="action" value="back">Return</button>
			<button class="btn" type="submit" name="action" value="print">In hợp đồng</button>
		</div>
	</form>
</div>
</body>
</html>

