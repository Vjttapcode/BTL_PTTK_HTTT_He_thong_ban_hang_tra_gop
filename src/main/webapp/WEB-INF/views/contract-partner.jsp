<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
	<title>Chọn đối tác trả góp</title>
	<meta name="viewport" content="width=device-width, initial-scale=1" />
	<style>
		body { font-family: "Segoe UI", Arial, sans-serif; background: #f6f8fb; margin: 0; }
		.wrapper { max-width: 1100px; margin: 30px auto; background: #fff; border: 3px solid #000; border-radius: 24px; padding: 32px; }
		h1 { margin-top: 0; }
		.notice { color: #b00020; margin-bottom: 20px; }
		table { width: 100%; border-collapse: collapse; margin-top: 18px; }
		th, td { border: 2px solid #000; padding: 10px; }
		th { background: #f2f5ff; }
		.btn { background: #0a66c2; color: #fff; border: none; border-radius: 24px; padding: 10px 24px; cursor: pointer; font-size: 16px; min-width: 120px; }
		.actions { display: flex; justify-content: space-between; margin-top: 28px; }
		.search-row { display: flex; gap: 12px; margin-top: 10px; }
		.search-row input { flex: 1; border: 2px solid #000; border-radius: 30px; padding: 12px 20px; font-size: 16px; }
		.tag { display: inline-block; border: 2px solid #000; border-radius: 12px; padding: 6px 12px; margin-right: 6px; background: #fafafa; }
	</style>
</head>
<body>
<c:url value="/contracts/partner" var="selfUrl" />
<c:url value="/contracts/partner/select" var="selectUrl" />
<c:url value="/contracts/product" var="prevUrl" />
<c:url value="/contracts/schedule" var="nextUrl" />
<div class="wrapper">
	<h1>Chọn đối tác trả góp</h1>
	<p>Bước 3/6 &mdash; Sản phẩm: <span class="tag">${draft.product.tenSp}</span></p>
	<c:if test="${not empty message}">
		<div class="notice">${message}</div>
	</c:if>
	<form class="search-row" method="get" action="${selfUrl}">
		<input type="text" name="keyword" placeholder="Nhập tên hoặc mã số thuế" value="${keyword}" />
		<button class="btn" type="submit">Search</button>
	</form>
	<c:if test="${not empty partners}">
		<table>
			<thead>
			<tr>
				<th>Tên đối tác</th>
				<th>Mã số thuế</th>
				<th>Loại hình tổ chức</th>
				<th>Mức trả trước tối thiểu</th>
				<th>Chọn</th>
			</tr>
			</thead>
			<tbody>
			<c:forEach var="dt" items="${partners}">
				<tr>
					<td>${dt.tenDoiTac}</td>
					<td>${dt.maSoThue}</td>
					<td>${dt.loaiHinhToChuc}</td>
					<td>${dt.mucTraTruocToiThieu}%</td>
					<td style="text-align:center">
						<form method="post" action="${selectUrl}">
							<input type="hidden" name="id" value="${dt.id}" />
							<button class="btn" type="submit" style="padding:6px 16px">click</button>
						</form>
					</td>
				</tr>
			</c:forEach>
			</tbody>
		</table>
	</c:if>
	<div class="actions">
		<button class="btn" type="button" onclick="window.location='${prevUrl}'">Return</button>
		<button class="btn" type="button" onclick="window.location='${nextUrl}'">Xác nhận</button>
	</div>
	<c:if test="${draft.partner != null}">
		<p>Đang chọn: <strong>${draft.partner.tenDoiTac}</strong> (tối thiểu ${draft.partner.mucTraTruocToiThieu}%).</p>
	</c:if>
</div>
</body>
</html>

