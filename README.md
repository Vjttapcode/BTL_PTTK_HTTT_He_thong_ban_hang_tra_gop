# Hệ Thống Bán Hàng Trả Góp - Tài Liệu Kỹ Thuật

## Tổng Quan

Hệ thống quản lý bán hàng trả góp được xây dựng bằng Java Servlet/JSP với kiến trúc MVC. Tài liệu này mô tả chi tiết luồng hoạt động và xử lý của các chức năng chính: **Đăng ký/Đăng nhập** và **Quản lý thông tin khách hàng**.

---

## 1. CHỨC NĂNG ĐĂNG KÝ VÀ ĐĂNG NHẬP

### 1.1. Kiến Trúc Tổng Quan

Hệ thống xác thực được xây dựng dựa trên:

- **Servlet**: `AuthServlet` - Xử lý các request đăng ký/đăng nhập
- **DAO Layer**: `ThanhVienDAO`, `NhanVienDAO` - Truy cập dữ liệu
- **Model**: `ThanhVien`, `NhanVien` - Đại diện cho dữ liệu
- **Filter**: `AuthFilter` - Bảo vệ các trang yêu cầu đăng nhập
- **Security**: BCrypt - Mã hóa mật khẩu (thực hiện trong `ThanhVienDAO.java`)

### 1.2. Luồng Đăng Ký (Registration)

#### 1.2.1. Hiển Thị Form Đăng Ký

**Luồng xử lý:**

```
GET /auth/register
  ↓
AuthServlet.doGet()
  ↓
subPath() → "/register"
  ↓
Forward đến /WEB-INF/views/register.jsp
```

**Chi tiết:**

- Người dùng truy cập URL `/auth/register`
- `AuthServlet` xử lý request GET, phân tích path info
- Hiển thị form đăng ký với các trường:
  - Họ tên (fullName)
  - Email
  - Chi nhánh (chiNhanh)
  - Username (bắt buộc)
  - Password (bắt buộc)

#### 1.2.2. Xử Lý Đăng Ký

**Luồng xử lý:**

```
POST /auth/register
  ↓
AuthServlet.doPost()
  ↓
handleRegister()
  ↓
[1] Validate dữ liệu đầu vào
  ↓
[2] ThanhVienDAO.create()
    ├─ Kiểm tra username đã tồn tại?
    ├─ Hash password bằng BCrypt (cost factor = 10)
    └─ Insert vào tblThanhVien
  ↓
[3] NhanVienDAO.createForThanhVien()
    └─ Insert vào tblNhanVien (liên kết với ThanhVien)
  ↓
[4] Forward đến login.jsp với thông báo thành công
```

**Chi tiết xử lý:**

1. **Validation (dòng 82-85):**

   ```java
   if (isBlank(username) || isBlank(password)) {
       req.setAttribute("error", "Username/password khong duoc de trong");
       // Hiển thị lại form với thông báo lỗi
   }
   ```

2. **Tạo tài khoản ThanhVien (ThanhVienDAO.create):**

   - Kiểm tra username đã tồn tại trong database
   - Nếu tồn tại → throw `IllegalStateException("Username da ton tai")`
   - Hash password bằng BCrypt với salt tự động (10 rounds)
   - Insert vào bảng `tblThanhVien` với các trường:
     - `username` (UNIQUE)
     - `password` (đã hash)
     - `ten` (fullName)
     - `email`
   - Trả về đối tượng `ThanhVien` với ID vừa tạo

3. **Tạo hồ sơ nhân viên (NhanVienDAO.createForThanhVien):**

   - Insert vào bảng `tblNhanVien`:
     - `tblThanhVienid` → ID của ThanhVien vừa tạo
     - `chiNhanh` → Chi nhánh từ form
     - `tblCuaHangid` → NULL (có thể gán sau)

4. **Xử lý kết quả:**
   - Thành công: Forward đến `login.jsp` với message "Dang ky thanh cong"
   - Lỗi: Forward lại `register.jsp` với thông báo lỗi

**Lưu ý bảo mật:**

- Mật khẩu được hash bằng BCrypt, không lưu plain text
- Username phải unique, được kiểm tra trước khi insert
- Sử dụng PreparedStatement để tránh SQL Injection

### 1.3. Luồng Đăng Nhập (Login)

#### 1.3.1. Hiển Thị Form Đăng Nhập

**Luồng xử lý:**

```
GET /auth/login
  ↓
AuthServlet.doGet()
  ↓
subPath() → "/login"
  ↓
Forward đến /WEB-INF/views/login.jsp
```

#### 1.3.2. Xử Lý Đăng Nhập

**Luồng xử lý:**

```
POST /auth/login
  ↓
AuthServlet.doPost()
  ↓
handleLogin()
  ↓
[1] Validate username và password không rỗng
  ↓
[2] ThanhVienDAO.checkLogin()
    ├─ Tìm ThanhVien theo username
    ├─ So sánh password với BCrypt.checkpw()
    └─ Trả về ThanhVien nếu đúng, null nếu sai
  ↓
[3] Nếu đăng nhập thành công:
    ├─ Tạo HttpSession
    ├─ Lưu ThanhVien vào session với key "CURRENT_USER"
    └─ Redirect đến /employee/home
  ↓
[4] Nếu thất bại:
    └─ Forward lại login.jsp với thông báo lỗi
```

**Chi tiết xử lý:**

1. **Validation (dòng 59-62):**

   ```java
   if (isBlank(username) || isBlank(password)) {
       req.setAttribute("error", "Vui long nhap day du thong tin");
       // Hiển thị lại form
   }
   ```

2. **Xác thực (ThanhVienDAO.checkLogin):**

   ```java
   ThanhVien tv = thanhVienDAO.findByUsername(username);
   if (tv == null) return null; // Username không tồn tại
   if (BCrypt.checkpw(rawPassword, tv.getPasswordHash())) {
       return tv; // Mật khẩu đúng
   }
   return null; // Mật khẩu sai
   ```

3. **Tạo Session (dòng 70-71):**

   ```java
   HttpSession session = req.getSession(true);
   session.setAttribute("CURRENT_USER", tv);
   ```

4. **Redirect:**
   - Thành công: Redirect đến `/employee/home`
   - Thất bại: Forward lại `login.jsp` với error message

### 1.4. Luồng Đăng Xuất (Logout)

**Luồng xử lý:**

```
GET /auth/logout
  ↓
AuthServlet.doGet()
  ↓
subPath() → "/logout"
  ↓
HttpSession.invalidate()
  ↓
Redirect đến /auth/login
```

**Chi tiết:**

- Lấy session hiện tại (nếu có)
- Gọi `session.invalidate()` để xóa session
- Redirect về trang đăng nhập

### 1.5. Bảo Vệ Tài Nguyên (AuthFilter)

**Mục đích:** Đảm bảo chỉ người dùng đã đăng nhập mới truy cập được các trang bảo vệ.

**Cấu hình:**

```java
@WebFilter(urlPatterns = {"/employee/*", "/customer/*"})
```

**Luồng xử lý:**

```
Request đến /employee/* hoặc /customer/*
  ↓
AuthFilter.doFilter()
  ↓
[1] Lấy session hiện tại
  ↓
[2] Kiểm tra CURRENT_USER trong session
  ↓
[3] Nếu không có:
    └─ Redirect đến /auth/login
  ↓
[4] Nếu có:
    ├─ Set cache headers (no-cache, no-store)
    └─ Cho phép request tiếp tục (chain.doFilter())
```

**Chi tiết:**

- Kiểm tra session và attribute `CURRENT_USER`
- Nếu chưa đăng nhập → redirect về `/auth/login`
- Set headers để ngăn cache các trang bảo vệ
- Chỉ cho phép request tiếp tục nếu đã xác thực

---

## 2. QUẢN LÝ THÔNG TIN KHÁCH HÀNG

### 2.1. Kiến Trúc Tổng Quan

Hệ thống quản lý khách hàng bao gồm:

- **Servlet**: `CustomerServlet` - Xử lý các request liên quan khách hàng
- **DAO Layer**: `KhachHangDAO`, `HopDongDAO` - Truy cập dữ liệu
- **Model**: `KhachHang`, `ThanhVien` - Đại diện dữ liệu
- **Views**: `customer-search.jsp`, `customer-detail.jsp`, `customer-history.jsp`

### 2.2. Cấu Trúc Dữ Liệu

**Bảng tblKhachHang:**

- `id` - Primary key
- `cccd` - Số CCCD (UNIQUE)
- `tblThanhVienid` - Foreign key đến tblThanhVien (UNIQUE, ON DELETE CASCADE)

**Bảng tblThanhVien (thông tin cá nhân):**

- `id` - Primary key
- `username` - Tên đăng nhập
- `password` - Mật khẩu đã hash
- `ten` - Họ tên
- `email` - Email
- `ngaySinh` - Ngày sinh
- `diaChi` - Địa chỉ
- `sdt` - Số điện thoại

**Quan hệ:**

- Một `KhachHang` liên kết với một `ThanhVien` (1-1)
- `KhachHang` chứa thông tin bổ sung (CCCD)
- Thông tin cá nhân được lưu trong `ThanhVien`

### 2.3. Tìm Kiếm Khách Hàng

#### 2.3.1. Hiển Thị Trang Tìm Kiếm

**Luồng xử lý:**

```
GET /customer/search hoặc GET /customer/
  ↓
CustomerServlet.doGet()
  ↓
normalizePath() → "/search" hoặc ""
  ↓
showSearch()
  ↓
[1] Kiểm tra flash message (CUSTOMER_ERROR)
  ↓
[2] Lấy keyword từ request parameter
  ↓
[3] Nếu có keyword:
    └─ KhachHangDAO.searchByKeyword()
  ↓
[4] Forward đến customer-search.jsp
```

**Chi tiết:**

1. **Xử lý flash message (dòng 51-55):**

   ```java
   Object flash = req.getSession().getAttribute("CUSTOMER_ERROR");
   if (flash != null) {
       req.setAttribute("error", flash.toString());
       req.getSession().removeAttribute("CUSTOMER_ERROR");
   }
   ```

   - Flash message được lưu trong session và chỉ hiển thị một lần

2. **Tìm kiếm (KhachHangDAO.searchByKeyword):**

   ```sql
   SELECT kh.id, kh.cccd, kh.tblThanhVienid,
          tv.ten, tv.ngaySinh, tv.sdt, tv.email, tv.diaChi
   FROM tblKhachHang kh
   JOIN tblThanhVien tv ON kh.tblThanhVienid = tv.id
   WHERE tv.ten LIKE ? OR kh.cccd LIKE ?
   ORDER BY tv.ten
   ```

   - Tìm kiếm theo tên hoặc CCCD (sử dụng LIKE với wildcard)
   - JOIN với bảng `tblThanhVien` để lấy thông tin đầy đủ
   - Kết quả được map thành danh sách `KhachHang` với `profile` là `ThanhVien`

3. **Hiển thị kết quả:**
   - Nếu có kết quả: Hiển thị danh sách
   - Nếu không có: Hiển thị message "Không tìm thấy khách hàng phù hợp"

#### 2.3.2. Xử Lý Tìm Kiếm (POST)

**Luồng xử lý:**

```
POST /customer/search
  ↓
CustomerServlet.doPost()
  ↓
normalizePath() → "/search"
  ↓
showSearch() (giống như GET)
```

- POST request cũng gọi `showSearch()` để xử lý form search

### 2.4. Xem Chi Tiết Khách Hàng

**Luồng xử lý:**

```
GET /customer/detail?id={customerId}
  ↓
CustomerServlet.doGet()
  ↓
normalizePath() → "/detail"
  ↓
showDetail()
  ↓
[1] Lấy id từ request parameter
  ↓
[2] Validate id
    ├─ Nếu null → Redirect về /customer/search
  ↓
[3] KhachHangDAO.findById()
    ├─ Nếu không tìm thấy → Lưu error vào session và redirect
    └─ Nếu tìm thấy → Set attribute "customer"
  ↓
[4] Forward đến customer-detail.jsp
```

**Chi tiết:**

1. **Lấy và validate ID (dòng 69-72):**

   ```java
   Integer id = intParam(req, "id");
   if (id == null) {
       resp.sendRedirect(req.getContextPath() + "/customer/search");
       return;
   }
   ```

2. **Tìm khách hàng (KhachHangDAO.findById):**

   ```sql
   SELECT kh.id, kh.cccd, kh.tblThanhVienid,
          tv.ten, tv.ngaySinh, tv.sdt, tv.email, tv.diaChi
   FROM tblKhachHang kh
   JOIN tblThanhVien tv ON kh.tblThanhVienid = tv.id
   WHERE kh.id = ?
   ```

   - Tìm theo ID khách hàng
   - JOIN với `tblThanhVien` để lấy thông tin đầy đủ
   - Map kết quả thành đối tượng `KhachHang` với `profile`

3. **Xử lý không tìm thấy (dòng 75-78):**

   ```java
   if (kh == null) {
       req.getSession().setAttribute("CUSTOMER_ERROR", "Không tìm thấy khách hàng.");
       resp.sendRedirect(req.getContextPath() + "/customer/search");
       return;
   }
   ```

   - Lưu error vào session (flash message)
   - Redirect về trang search

4. **Hiển thị:**
   - Set attribute `customer` vào request
   - Forward đến `customer-detail.jsp` để hiển thị thông tin

### 2.5. Xem Lịch Sử Hợp Đồng

**Luồng xử lý:**

```
GET /customer/history?id={customerId}
  ↓
CustomerServlet.doGet()
  ↓
normalizePath() → "/history"
  ↓
showHistory()
  ↓
[1] Lấy và validate id (giống showDetail)
  ↓
[2] KhachHangDAO.findById()
    └─ Validate khách hàng tồn tại
  ↓
[3] HopDongDAO.findByKhachHangId()
    └─ Lấy danh sách hợp đồng của khách hàng
  ↓
[4] Set attributes: "customer", "contracts"
  ↓
[5] Forward đến customer-history.jsp
```

**Chi tiết:**

1. **Validate khách hàng (dòng 90-94):**

   - Tương tự `showDetail()`, kiểm tra ID và sự tồn tại của khách hàng

2. **Lấy danh sách hợp đồng:**

   ```java
   List<HopDong> hopDongs = hopDongDAO.findByKhachHangId(id);
   ```

   - Lấy tất cả hợp đồng liên quan đến khách hàng

3. **Hiển thị:**
   - Set `customer` và `contracts` vào request
   - Forward đến `customer-history.jsp` để hiển thị lịch sử

### 2.6. Mapping Dữ Liệu (KhachHangDAO.map)

**Chi tiết:**

```java
private KhachHang map(ResultSet rs) throws SQLException {
    KhachHang kh = new KhachHang();
    kh.setId(rs.getInt("id"));
    kh.setThanhVienId(rs.getInt("tblThanhVienid"));
    kh.setCccd(rs.getString("cccd"));

    // Tạo đối tượng ThanhVien chứa thông tin cá nhân
    ThanhVien tv = new ThanhVien();
    tv.setId(rs.getInt("tblThanhVienid"));
    tv.setTen(rs.getString("ten"));
    tv.setNgaySinh(rs.getDate("ngaySinh"));
    tv.setSdt(rs.getString("sdt"));
    tv.setEmail(rs.getString("email"));
    tv.setDiaChi(rs.getString("diaChi"));

    kh.setProfile(tv);
    return kh;
}
```

**Đặc điểm:**

- `KhachHang` chứa thông tin nghiệp vụ (CCCD)
- `ThanhVien` (trong `profile`) chứa thông tin cá nhân
- Model `KhachHang` có các getter proxy để truy cập thông tin từ `profile`:
  - `getTen()`, `getNgaySinh()`, `getSdt()`, `getEmail()`, `getDiaChi()`

---

## 3. CHỨC NĂNG KÝ HỢP ĐỒNG TRẢ GÓP

### 3.1. Kiến Trúc Tổng Quan

Chức năng ký hợp đồng trả góp được xây dựng theo mô hình **Wizard (Multi-step Form)** với các thành phần:

- **Servlet**: `ContractServlet` - Điều phối toàn bộ luồng wizard
- **Service Layer**: `ContractService` - Xử lý logic nghiệp vụ và tính toán
- **DAO Layer**:
  - `SanPhamDAO` - Quản lý sản phẩm
  - `DoiTacDAO` - Quản lý đối tác tài chính
  - `KyThanhToanDAO` - Quản lý kỳ thanh toán
  - `HopDongDAO` - Quản lý hợp đồng (đã mở rộng)
  - `NhanVienDAO` - Lấy thông tin nhân viên
- **Model**:
  - `ContractDraft` - Lưu trữ tạm thời dữ liệu hợp đồng trong session
  - `SanPham`, `DoiTac`, `KyThanhToan` - Các thực thể nghiệp vụ
  - `HopDong` - Hợp đồng (đã mở rộng với foreign keys)
- **Views**: 7 trang JSP cho từng bước của wizard

### 3.2. Cấu Trúc Dữ Liệu

#### 3.2.1. Bảng tblSanPham

- `id` - Primary key
- `tenSp` - Tên sản phẩm (UNIQUE)
- `soLuong` - Số lượng tồn kho
- `donGia` - Đơn giá (DECIMAL 19,2)
- `moTa` - Mô tả sản phẩm

#### 3.2.2. Bảng tblDoiTac

- `id` - Primary key
- `tenDoiTac` - Tên đối tác tài chính
- `maSoThue` - Mã số thuế (UNIQUE)
- `loaiHinhToChuc` - Loại hình tổ chức
- `mucTraTruocToiThieu` - Mức trả trước tối thiểu (%, DECIMAL 5,2)

#### 3.2.3. Bảng tblHopDong (đã mở rộng)

- `id` - Primary key
- `soHopDong` - Số hợp đồng
- `tongVay` - Tổng vay (tự động tính = giá sản phẩm - mức trả trước)
- `mucTraTruoc` - Mức trả trước
- `laiSuat` - Lãi suất (%/tháng)
- `duNoConLai` - Dư nợ còn lại (tự động = tổng vay khi tạo)
- `ngayKy` - Ngày ký hợp đồng
- `trangThai` - Trạng thái hợp đồng
- `tblKhachHangid` - Foreign key đến tblKhachHang
- `tblNhanVienid` - Foreign key đến tblNhanVien
- `tblSanPhamid` - Foreign key đến tblSanPham (mới)
- `tblDoiTacid` - Foreign key đến tblDoiTac (mới)

#### 3.2.4. Bảng tblKyThanhToan

- `id` - Primary key
- `tblHopDongid` - Foreign key đến tblHopDong (ON DELETE CASCADE)
- `tenKy` - Tên kỳ (ví dụ: "tháng 1", "Kỳ 1")
- `ngayDenHan` - Ngày đến hạn thanh toán
- `tienGoc` - Tiền gốc phải trả
- `tienLai` - Tiền lãi phải trả
- `trangThai` - Trạng thái (ví dụ: "Chưa thanh toán")

### 3.3. Luồng Hoạt Động Tổng Quan

Wizard được chia thành **6 bước tuần tự**, mỗi bước phụ thuộc vào bước trước:

```
Bước 1: Chọn khách hàng
   ↓
Bước 2: Chọn sản phẩm
   ↓
Bước 3: Chọn đối tác tài chính
   ↓
Bước 4: Thêm các kỳ thanh toán
   ↓
Bước 5: Nhập mức trả trước
   ↓
Bước 6: Xem lại và hoàn tất hợp đồng
```

**Đặc điểm:**

- Dữ liệu được lưu tạm trong `ContractDraft` (session)
- Mỗi bước có validation và kiểm tra điều kiện tiên quyết
- Không thể bỏ qua bước (enforced by `ensure*` methods)
- Tự động tính toán: `tongVay` và `duNoConLai`

### 3.4. Chi Tiết Từng Bước

#### 3.4.1. Bước 1: Chọn Khách Hàng

**URL:** `GET /contracts/customer` hoặc `GET /contracts/start`

**Luồng xử lý:**

```
GET /contracts/start hoặc /contracts/
  ↓
ContractServlet.doGet()
  ↓
resetDraft() → Xóa CONTRACT_DRAFT khỏi session
  ↓
Redirect → /contracts/customer
  ↓
showCustomer()
  ↓
[1] Lấy ContractDraft từ session (tạo mới nếu chưa có)
  ↓
[2] Lấy keyword từ parameter (nếu có)
  ↓
[3] Nếu có keyword:
    └─ KhachHangDAO.searchByKeyword()
  ↓
[4] Forward đến contract-customer.jsp
```

**Xử lý chọn khách hàng:**

```
POST /contracts/customer/select?id={customerId}
  ↓
ContractServlet.doPost()
  ↓
selectCustomer()
  ↓
[1] Validate id
  ↓
[2] KhachHangDAO.findById()
  ↓
[3] ContractDraft.resetAll() → Xóa dữ liệu cũ
  ↓
[4] draft.setCustomer(khachHang)
  ↓
[5] Redirect → /contracts/product
```

**Chi tiết:**

- Form tìm kiếm theo tên hoặc CCCD
- Hiển thị danh sách kết quả trong bảng
- Click "Chọn" để lưu khách hàng vào draft và chuyển bước

#### 3.4.2. Bước 2: Chọn Sản Phẩm

**URL:** `GET /contracts/product`

**Luồng xử lý:**

```
GET /contracts/product
  ↓
ensureCustomer() → Kiểm tra đã chọn khách hàng?
  ├─ Chưa có → Redirect về /contracts/customer
  └─ Đã có → Tiếp tục
  ↓
showProduct()
  ↓
[1] Lấy draft từ session
  ↓
[2] Lấy keyword từ parameter
  ↓
[3] SanPhamDAO.search(keyword)
  ↓
[4] Forward đến contract-product.jsp
```

**Xử lý chọn sản phẩm:**

```
POST /contracts/product/select?id={productId}
  ↓
selectProduct()
  ↓
[1] Validate id
  ↓
[2] SanPhamDAO.findById()
  ↓
[3] draft.resetAfterCustomer() → Xóa product/partner/schedule
  ↓
[4] draft.setProduct(sanPham)
  ↓
[5] Redirect → /contracts/partner
```

**Chi tiết:**

- Tìm kiếm sản phẩm theo tên
- Hiển thị: Tên SP, Số lượng, Đơn giá, Mô tả
- Click "Chọn" để lưu sản phẩm

#### 3.4.3. Bước 3: Chọn Đối Tác Tài Chính

**URL:** `GET /contracts/partner`

**Luồng xử lý:**

```
GET /contracts/partner
  ↓
ensureProduct() → Kiểm tra đã chọn sản phẩm?
  ├─ Chưa có → Redirect về /contracts/product
  └─ Đã có → Tiếp tục
  ↓
showPartner()
  ↓
[1] Lấy draft từ session
  ↓
[2] Lấy keyword từ parameter
  ↓
[3] DoiTacDAO.search(keyword)
  ↓
[4] Forward đến contract-partner.jsp
```

**Xử lý chọn đối tác:**

```
POST /contracts/partner/select?id={partnerId}
  ↓
selectPartner()
  ↓
[1] Validate id
  ↓
[2] DoiTacDAO.findById()
  ↓
[3] draft.resetAfterProduct() → Xóa partner/schedule
  ↓
[4] draft.setPartner(doiTac)
  ↓
[5] Redirect → /contracts/schedule
```

**Chi tiết:**

- Hiển thị: Tên đối tác, Mã số thuế, Loại hình, Mức trả trước tối thiểu
- Mức trả trước tối thiểu sẽ được dùng để validate ở bước 5

#### 3.4.4. Bước 4: Thêm Các Kỳ Thanh Toán

**URL:** `GET /contracts/schedule`

**Luồng xử lý:**

```
GET /contracts/schedule
  ↓
ensurePartner() → Kiểm tra đã chọn đối tác?
  ├─ Chưa có → Redirect về /contracts/partner
  └─ Đã có → Tiếp tục
  ↓
showSchedule()
  ↓
[1] Lấy draft từ session
  ↓
[2] Forward đến contract-schedule.jsp
    (Hiển thị form nhập + danh sách kỳ đã thêm)
```

**Thêm kỳ thanh toán:**

```
POST /contracts/schedule/add
  ↓
addSchedule()
  ↓
[1] Lấy các tham số: tenKy, ngayDenHan, tienGoc, tienLai, trangThai
  ↓
[2] Validate: tenKy, ngayDenHan, tienGoc không được rỗng
  ↓
[3] Tạo đối tượng KyThanhToan
  ↓
[4] draft.getSchedule().add(kyThanhToan)
  ↓
[5] Redirect → /contracts/schedule (hiển thị lại với kỳ mới)
```

**Xóa kỳ thanh toán:**

```
POST /contracts/schedule/delete?index={index}
  ↓
deleteSchedule()
  ↓
[1] Validate index
  ↓
[2] draft.getSchedule().remove(index)
  ↓
[3] Redirect → /contracts/schedule
```

**Chi tiết:**

- Form nhập: Tên kỳ, Ngày đến hạn, Tiền gốc, Tiền lãi, Trạng thái
- Danh sách kỳ đã thêm hiển thị dưới form
- Có thể thêm nhiều kỳ, xóa kỳ đã thêm
- Bắt buộc phải có ít nhất 1 kỳ trước khi chuyển bước

#### 3.4.5. Bước 5: Nhập Mức Trả Trước

**URL:** `GET /contracts/down-payment`

**Luồng xử lý:**

```
GET /contracts/down-payment
  ↓
ensureSchedule() → Kiểm tra đã có kỳ thanh toán?
  ├─ Chưa có → Redirect về /contracts/schedule
  └─ Đã có → Tiếp tục
  ↓
showDownPayment()
  ↓
[1] Lấy draft từ session
  ↓
[2] Tính suggested = giá sản phẩm × (mức trả trước tối thiểu / 100)
  ↓
[3] Forward đến contract-downpayment.jsp
    (Hiển thị giá trị gợi ý)
```

**Lưu mức trả trước:**

```
POST /contracts/down-payment/save
  ↓
saveDownPayment()
  ↓
[1] Parse mucTraTruoc từ parameter
  ↓
[2] Validate: giá trị không null
  ↓
[3] Tính min = giá sản phẩm × (mức trả trước tối thiểu / 100)
  ↓
[4] Kiểm tra: mucTraTruoc >= min
  ├─ Không đạt → Flash error và redirect
  └─ Đạt → Tiếp tục
  ↓
[5] draft.setMucTraTruoc(mucTraTruoc)
  ↓
[6] Redirect → /contracts/review
```

**Chi tiết:**

- Hệ thống tự động tính và hiển thị mức trả trước tối thiểu
- Validation: mức trả trước phải >= (giá sản phẩm × % tối thiểu của đối tác)
- Ví dụ: Sản phẩm 44.490.000 VND, đối tác yêu cầu 20% → Tối thiểu 8.898.000 VND

#### 3.4.6. Bước 6: Xem Lại và Hoàn Tất

**URL:** `GET /contracts/review`

**Luồng xử lý:**

```
GET /contracts/review
  ↓
ensureDownPayment() → Kiểm tra đã nhập mức trả trước?
  ├─ Chưa có → Redirect về /contracts/down-payment
  └─ Đã có → Tiếp tục
  ↓
showReview()
  ↓
[1] Lấy draft từ session
  ↓
[2] Tính toán tự động:
    - calculatedTongVay = giá sản phẩm - mức trả trước
    - calculatedDuNo = calculatedTongVay (bằng tổng vay khi tạo)
  ↓
[3] Forward đến contract-review.jsp
    (Hiển thị tất cả thông tin + form nhập các trường còn lại)
```

**Hoàn tất hợp đồng:**

```
POST /contracts/submit
  ↓
finalizeContract()
  ↓
[1] Validate: action != "back"
    ├─ Nếu "back" → Redirect về /contracts/down-payment
  ↓
[2] Lấy các tham số từ form:
    - soHopDong, tongVay, laiSuat, ngayKy, trangThai, duNoConLai
  ↓
[3] Validate: schedule không rỗng, mucTraTruoc đã có
  ↓
[4] Lấy NhanVien từ session (CURRENT_USER)
  ↓
[5] Tính toán tự động:
    - tongVay = giá sản phẩm - mức trả trước (nếu chưa có)
    - duNoConLai = tongVay (nếu chưa có)
  ↓
[6] ContractService.createContract(draft, nhanVienId)
    ├─ Tạo đối tượng HopDong
    ├─ Set các giá trị (có default nếu thiếu)
    ├─ HopDongDAO.insert() → Lưu hợp đồng
    └─ KyThanhToanDAO.insertBatch() → Lưu các kỳ thanh toán
  ↓
[7] Xóa CONTRACT_DRAFT khỏi session
  ↓
[8] Forward đến contract-print.jsp (hiển thị thông tin hợp đồng)
```

**Chi tiết:**

- Hiển thị tất cả thông tin đã nhập: Khách hàng, Sản phẩm, Đối tác, Kỳ thanh toán
- Form nhập các trường còn lại: Số HĐ, Lãi suất, Ngày ký, Trạng thái
- **Tổng vay** và **Dư nợ còn lại** được tính tự động và hiển thị readonly
- Sau khi hoàn tất, hiển thị trang thông tin hợp đồng và nút "Về trang nhân viên"

### 3.5. Tính Toán Tự Động

#### 3.5.1. Tổng Vay (tongVay)

**Công thức:**

```
tongVay = giá_sản_phẩm - mức_trả_trước
```

**Vị trí tính toán:**

- Trong `ContractServlet.showReview()`: Tính để hiển thị
- Trong `ContractServlet.finalizeContract()`: Tính và set vào draft
- Trong `ContractService.createContract()`: Tính lại nếu chưa có (fallback)

**Ví dụ:**

- Giá sản phẩm: 44.490.000 VND
- Mức trả trước: 14.490.000 VND
- → Tổng vay: 30.000.000 VND

#### 3.5.2. Dư Nợ Còn Lại (duNoConLai)

**Công thức:**

```
duNoConLai = tongVay (khi tạo hợp đồng)
```

**Lưu ý:**

- Khi tạo hợp đồng mới, dư nợ còn lại luôn bằng tổng vay
- Sau này khi thanh toán, dư nợ sẽ được cập nhật giảm dần

**Vị trí tính toán:**

- Trong `ContractServlet.showReview()`: Tính để hiển thị
- Trong `ContractServlet.finalizeContract()`: Tính và set vào draft
- Trong `ContractService.createContract()`: Set = tongVay nếu chưa có

### 3.6. Quản Lý Session (ContractDraft)

**Mục đích:** Lưu trữ tạm thời dữ liệu hợp đồng trong quá trình wizard

**Cấu trúc ContractDraft:**

```java
public class ContractDraft implements Serializable {
    private KhachHang customer;
    private SanPham product;
    private DoiTac partner;
    private List<KyThanhToan> schedule;
    private BigDecimal mucTraTruoc;
    private String soHopDong;
    private BigDecimal tongVay;
    private BigDecimal laiSuat;
    private BigDecimal duNoConLai;
    private Date ngayKy;
    private String trangThai;
}
```

**Quản lý trong ContractServlet:**

- **Lấy draft:** `getDraft(req)` - Lấy từ session, tạo mới nếu chưa có
- **Reset draft:** `resetDraft(req)` - Xóa khỏi session (khi bắt đầu wizard mới)
- **Reset có điều kiện:**
  - `resetAll()` - Xóa tất cả (khi chọn khách hàng mới)
  - `resetAfterCustomer()` - Xóa product/partner/schedule (khi chọn sản phẩm mới)
  - `resetAfterProduct()` - Xóa partner/schedule (khi chọn đối tác mới)

**Session Key:** `"CONTRACT_DRAFT"`

### 3.7. Validation và Bảo Vệ Luồng

#### 3.7.1. Các Method Ensure

Đảm bảo không thể bỏ qua bước:

```java
ensureCustomer() → Kiểm tra đã chọn khách hàng
ensureProduct() → Kiểm tra đã chọn sản phẩm (và khách hàng)
ensurePartner() → Kiểm tra đã chọn đối tác (và sản phẩm, khách hàng)
ensureSchedule() → Kiểm tra đã có kỳ thanh toán (và đối tác, sản phẩm, khách hàng)
ensureDownPayment() → Kiểm tra đã nhập mức trả trước (và tất cả bước trước)
```

**Cơ chế:**

- Mỗi method kiểm tra điều kiện tiên quyết
- Nếu thiếu → Redirect về bước tương ứng
- Nếu đủ → Trả về `true` và cho phép tiếp tục

#### 3.7.2. Validation Dữ Liệu

**Bước 4 (Kỳ thanh toán):**

- `tenKy`, `ngayDenHan`, `tienGoc` không được rỗng
- Phải có ít nhất 1 kỳ trước khi chuyển bước

**Bước 5 (Mức trả trước):**

- Giá trị không được null
- Phải >= (giá sản phẩm × mức trả trước tối thiểu / 100)

**Bước 6 (Hoàn tất):**

- Schedule không rỗng
- MucTraTruoc đã có
- Tổng vay có thể tính được (có giá sản phẩm và mức trả trước)

### 3.8. Xử Lý Lỗi và Flash Messages

**Cơ chế:**

- Sử dụng session attribute `"CONTRACT_FLASH"` để lưu thông báo
- Method `setFlash()` để lưu message
- Method `applyFlash()` để lấy và xóa message (chỉ hiển thị 1 lần)

**Các trường hợp lỗi:**

- Không tìm thấy khách hàng/sản phẩm/đối tác
- Validation thất bại (mức trả trước, kỳ thanh toán)
- Thiếu thông tin khi hoàn tất

### 3.9. Database Operations

#### 3.9.1. Tạo Hợp Đồng (ContractService.createContract)

**Luồng:**

```
1. Validate draft.isReadyForReview()
2. Tạo đối tượng HopDong từ draft
3. Tính toán tự động:
   - tongVay = giá sản phẩm - mức trả trước (nếu chưa có)
   - duNoConLai = tongVay (nếu chưa có)
4. Set giá trị mặc định:
   - ngayKy = hôm nay (nếu null)
   - trangThai = "Mới" (nếu null)
   - soHopDong = "HD-" + timestamp (nếu null)
   - laiSuat = 0 (nếu null)
5. HopDongDAO.insert() → Lưu hợp đồng, trả về ID
6. KyThanhToanDAO.insertBatch() → Lưu tất cả kỳ thanh toán
7. Trả về hopDongId
```

**Transaction:**

- Hiện tại chưa có transaction management
- Nếu insert kỳ thanh toán thất bại, hợp đồng vẫn được tạo (có thể cải thiện)

#### 3.9.2. Insert Hợp Đồng (HopDongDAO.insert)

**SQL:**

```sql
INSERT INTO tblHopDong (
    soHopDong, tongVay, mucTraTruoc, laiSuat, duNoConLai,
    ngayKy, trangThai,
    tblKhachHangid, tblNhanVienid, tblSanPhamid, tblDoiTacid
) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
```

**Đặc điểm:**

- Sử dụng `Statement.RETURN_GENERATED_KEYS` để lấy ID vừa tạo
- Foreign keys: khachHangId, nhanVienId, sanPhamId, doiTacId

#### 3.9.3. Insert Batch Kỳ Thanh Toán (KyThanhToanDAO.insertBatch)

**SQL (cho mỗi kỳ):**

```sql
INSERT INTO tblKyThanhToan (
    tblHopDongid, tenKy, ngayDenHan, tienGoc, tienLai, trangThai
) VALUES (?, ?, ?, ?, ?, ?)
```

**Đặc điểm:**

- Insert từng kỳ trong vòng lặp
- Tất cả kỳ liên kết với cùng một `hopDongId`

### 3.10. Sơ Đồ Luồng Tổng Quan

```
[Nhân viên] → Click "Ký hợp đồng trả góp" (employee-home.jsp)
              ↓
         GET /contracts/start
              ↓
    [ContractServlet] → Reset draft → Redirect /contracts/customer
              ↓
    ┌─────────────────────────────────────────────────────────┐
    │ BƯỚC 1: Chọn Khách Hàng                                  │
    │ GET /contracts/customer                                  │
    │ - Tìm kiếm khách hàng                                    │
    │ POST /contracts/customer/select?id=X                     │
    │ - Lưu vào draft.customer                                 │
    └─────────────────────────────────────────────────────────┘
              ↓
    ┌─────────────────────────────────────────────────────────┐
    │ BƯỚC 2: Chọn Sản Phẩm                                    │
    │ GET /contracts/product                                   │
    │ - Tìm kiếm sản phẩm                                      │
    │ POST /contracts/product/select?id=X                     │
    │ - Lưu vào draft.product                                  │
    └─────────────────────────────────────────────────────────┘
              ↓
    ┌─────────────────────────────────────────────────────────┐
    │ BƯỚC 3: Chọn Đối Tác                                     │
    │ GET /contracts/partner                                   │
    │ - Tìm kiếm đối tác                                       │
    │ POST /contracts/partner/select?id=X                     │
    │ - Lưu vào draft.partner                                  │
    └─────────────────────────────────────────────────────────┘
              ↓
    ┌─────────────────────────────────────────────────────────┐
    │ BƯỚC 4: Thêm Kỳ Thanh Toán                               │
    │ GET /contracts/schedule                                  │
    │ - Hiển thị form + danh sách kỳ                          │
    │ POST /contracts/schedule/add                            │
    │ - Thêm kỳ vào draft.schedule                             │
    │ POST /contracts/schedule/delete?index=X                 │
    │ - Xóa kỳ khỏi draft.schedule                            │
    └─────────────────────────────────────────────────────────┘
              ↓
    ┌─────────────────────────────────────────────────────────┐
    │ BƯỚC 5: Nhập Mức Trả Trước                                │
    │ GET /contracts/down-payment                              │
    │ - Hiển thị giá trị gợi ý                                 │
    │ POST /contracts/down-payment/save                        │
    │ - Validate >= mức tối thiểu                              │
    │ - Lưu vào draft.mucTraTruoc                               │
    └─────────────────────────────────────────────────────────┘
              ↓
    ┌─────────────────────────────────────────────────────────┐
    │ BƯỚC 6: Xem Lại và Hoàn Tất                              │
    │ GET /contracts/review                                    │
    │ - Tính toán: tongVay, duNoConLai                         │
    │ - Hiển thị tất cả thông tin + form                      │
    │ POST /contracts/submit                                   │
    │ - Lấy các trường còn lại từ form                        │
    │ - ContractService.createContract()                       │
    │   ├─ HopDongDAO.insert()                                 │
    │   └─ KyThanhToanDAO.insertBatch()                       │
    │ - Xóa draft khỏi session                                 │
    │ - Forward contract-print.jsp                             │
    └─────────────────────────────────────────────────────────┘
              ↓
    [Hiển thị thông tin hợp đồng] → Nút "Về trang nhân viên"
```

---

## 4. SƠ ĐỒ LUỒNG TỔNG QUAN

### 3.1. Luồng Đăng Ký

```
[User] → GET /auth/register
         ↓
    [AuthServlet] → register.jsp
         ↓
[User nhập form] → POST /auth/register
         ↓
    [AuthServlet.handleRegister()]
         ↓
    [Validate] → Lỗi? → register.jsp (error)
         ↓
    [ThanhVienDAO.create()]
         ↓
    Username tồn tại? → register.jsp (error)
         ↓
    [Hash password] → [Insert tblThanhVien]
         ↓
    [NhanVienDAO.createForThanhVien()]
         ↓
    [Insert tblNhanVien]
         ↓
    login.jsp (success)
```

### 3.2. Luồng Đăng Nhập

```
[User] → GET /auth/login
         ↓
    [AuthServlet] → login.jsp
         ↓
[User nhập form] → POST /auth/login
         ↓
    [AuthServlet.handleLogin()]
         ↓
    [Validate] → Lỗi? → login.jsp (error)
         ↓
    [ThanhVienDAO.checkLogin()]
         ↓
    Username không tồn tại? → login.jsp (error)
         ↓
    [BCrypt.checkpw()] → Sai? → login.jsp (error)
         ↓
    [Tạo Session] → [Lưu CURRENT_USER]
         ↓
    Redirect → /employee/home
```

### 3.3. Luồng Quản Lý Khách Hàng

```
[User đã đăng nhập] → GET /customer/search
                      ↓
                 [AuthFilter] → Kiểm tra session
                      ↓
                 [CustomerServlet.showSearch()]
                      ↓
                 [KhachHangDAO.searchByKeyword()]
                      ↓
                 customer-search.jsp
                      ↓
[Click vào khách hàng] → GET /customer/detail?id=X
                      ↓
                 [CustomerServlet.showDetail()]
                      ↓
                 [KhachHangDAO.findById()]
                      ↓
                 customer-detail.jsp
                      ↓
[Click lịch sử] → GET /customer/history?id=X
                      ↓
                 [CustomerServlet.showHistory()]
                      ↓
                 [HopDongDAO.findByKhachHangId()]
                      ↓
                 customer-history.jsp
```

---

## 4. CÁC ĐIỂM QUAN TRỌNG

### 4.1. Bảo Mật

1. **Mật khẩu:**

   - Sử dụng BCrypt với cost factor = 10
   - Mật khẩu không bao giờ lưu dạng plain text
   - So sánh mật khẩu bằng `BCrypt.checkpw()`

2. **SQL Injection:**

   - Tất cả queries sử dụng `PreparedStatement`
   - Parameters được bind an toàn

3. **Session Management:**

   - Session được tạo khi đăng nhập thành công
   - `AuthFilter` bảo vệ các trang yêu cầu đăng nhập
   - Session được invalidate khi đăng xuất

4. **Cache Control:**
   - `AuthFilter` set headers để ngăn cache các trang bảo vệ

### 4.2. Xử Lý Lỗi

1. **Validation:**

   - Kiểm tra dữ liệu đầu vào trước khi xử lý
   - Hiển thị thông báo lỗi rõ ràng

2. **Flash Messages:**

   - Sử dụng session để lưu thông báo lỗi/thành công
   - Tự động xóa sau khi hiển thị

3. **Exception Handling:**
   - `IllegalStateException` cho username trùng
   - `RuntimeException` cho lỗi database (có thể cải thiện)

### 4.3. Kiến Trúc

1. **Separation of Concerns:**

   - Servlet: Xử lý HTTP request/response
   - DAO: Truy cập database
   - Model: Đại diện dữ liệu
   - View: Hiển thị (JSP)

2. **Code Reusability:**

   - Helper methods: `subPath()`, `param()`, `isBlank()`, `normalizePath()`
   - Base SQL query trong `KhachHangDAO`

3. **Database Design:**
   - Normalization: Tách thông tin cá nhân (ThanhVien) và nghiệp vụ (KhachHang, NhanVien)
   - Foreign keys với CASCADE để đảm bảo tính toàn vẹn

---

## 5. CẤU TRÚC FILE

```
src/main/java/com/htbh/
├── servlet/
│   ├── AuthServlet.java          # Xử lý đăng ký/đăng nhập/đăng xuất
│   ├── CustomerServlet.java      # Xử lý tìm kiếm, xem chi tiết khách hàng
│   └── ContractServlet.java # Điều phối wizard ký hợp đồng trả góp
├── dao/
│   ├── ThanhVienDAO.java         # CRUD cho thành viên
│   ├── NhanVienDAO.java          # CRUD cho nhân viên (đã mở rộng)
│   ├── KhachHangDAO.java         # Tìm kiếm và lấy thông tin khách hàng
│   ├── SanPhamDAO.java           # Tìm kiếm và lấy thông tin sản phẩm
│   ├── DoiTacDAO.java            # Tìm kiếm và lấy thông tin đối tác
│   ├── HopDongDAO.java           # CRUD cho hợp đồng (đã mở rộng)
│   └── KyThanhToanDAO.java       # Quản lý kỳ thanh toán
├── model/
│   ├── ThanhVien.java            # Model thành viên
│   ├── NhanVien.java             # Model nhân viên
│   ├── KhachHang.java            # Model khách hàng
│   ├── SanPham.java              # Model sản phẩm
│   ├── DoiTac.java               # Model đối tác tài chính
│   ├── KyThanhToan.java          # Model kỳ thanh toán
│   ├── HopDong.java              # Model hợp đồng (đã mở rộng)
│   └── ContractDraft.java        # Model lưu trữ tạm thời trong session
├── service/
│   └── ContractService.java      # Xử lý logic nghiệp vụ hợp đồng
├── filter/
│   └── AuthFilter.java           # Filter bảo vệ các trang yêu cầu đăng nhập
└── config/
    └── ConnectionFactory.java    # Quản lý kết nối database

src/main/webapp/WEB-INF/views/
├── login.jsp                     # Form đăng nhập
├── register.jsp                  # Form đăng ký
├── employee-home.jsp             # Trang chủ nhân viên
├── customer-search.jsp           # Trang tìm kiếm khách hàng
├── customer-detail.jsp           # Trang chi tiết khách hàng
├── customer-history.jsp          # Trang lịch sử hợp đồng
├── contract-customer.jsp         # Bước 1: Chọn khách hàng
├── contract-product.jsp          # Bước 2: Chọn sản phẩm
├── contract-partner.jsp          # Bước 3: Chọn đối tác
├── contract-schedule.jsp         # Bước 4: Thêm kỳ thanh toán
├── contract-downpayment.jsp      # Bước 5: Nhập mức trả trước
├── contract-review.jsp           # Bước 6: Xem lại và hoàn tất
└── contract-print.jsp            # Hiển thị thông tin hợp đồng đã tạo

src/main/resources/db/
└── schema.sql                    # Script tạo database và bảng (đã mở rộng)
```

---

## 6. KẾT LUẬN

Hệ thống được thiết kế với kiến trúc rõ ràng, tách biệt các lớp xử lý. Các chức năng đăng ký/đăng nhập, quản lý khách hàng và ký hợp đồng trả góp được triển khai với các biện pháp bảo mật cơ bản (BCrypt, PreparedStatement, Session management). Code có tính tái sử dụng và dễ bảo trì.

**Điểm mạnh:**

- Bảo mật mật khẩu bằng BCrypt
- Sử dụng PreparedStatement chống SQL Injection
- Filter bảo vệ tài nguyên
- Kiến trúc phân lớp rõ ràng (Servlet → Service → DAO)
- Wizard pattern cho luồng phức tạp (ký hợp đồng)
- Session management cho dữ liệu tạm thời (ContractDraft)
- Tính toán tự động (tổng vay, dư nợ còn lại)
- Validation và bảo vệ luồng (ensure methods)

**Có thể cải thiện:**

- Xử lý exception chi tiết hơn
- Thêm logging
- Validation phía server mạnh hơn
- Thêm unit tests
- Transaction management cho việc tạo hợp đồng (đảm bảo atomicity)
- Hỗ trợ chỉnh sửa hợp đồng đã tạo
- Tính toán lãi suất tự động dựa trên đối tác
- Export hợp đồng ra PDF
