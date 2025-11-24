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

## 3. SƠ ĐỒ LUỒNG TỔNG QUAN

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
│   └── CustomerServlet.java      # Xử lý tìm kiếm, xem chi tiết khách hàng
├── dao/
│   ├── ThanhVienDAO.java         # CRUD cho thành viên
│   ├── NhanVienDAO.java          # CRUD cho nhân viên
│   └── KhachHangDAO.java         # Tìm kiếm và lấy thông tin khách hàng
├── model/
│   ├── ThanhVien.java            # Model thành viên
│   ├── NhanVien.java             # Model nhân viên
│   └── KhachHang.java            # Model khách hàng
├── filter/
│   └── AuthFilter.java           # Filter bảo vệ các trang yêu cầu đăng nhập
└── config/
    └── ConnectionFactory.java    # Quản lý kết nối database

src/main/webapp/WEB-INF/views/
├── login.jsp                     # Form đăng nhập
├── register.jsp                  # Form đăng ký
├── customer-search.jsp           # Trang tìm kiếm khách hàng
├── customer-detail.jsp           # Trang chi tiết khách hàng
└── customer-history.jsp          # Trang lịch sử hợp đồng

src/main/resources/db/
└── schema.sql                    # Script tạo database và bảng
```

---

## 6. KẾT LUẬN

Hệ thống được thiết kế với kiến trúc rõ ràng, tách biệt các lớp xử lý. Các chức năng đăng ký/đăng nhập và quản lý khách hàng được triển khai với các biện pháp bảo mật cơ bản (BCrypt, PreparedStatement, Session management). Code có tính tái sử dụng và dễ bảo trì.

**Điểm mạnh:**

- Bảo mật mật khẩu bằng BCrypt
- Sử dụng PreparedStatement chống SQL Injection
- Filter bảo vệ tài nguyên
- Kiến trúc phân lớp rõ ràng

**Có thể cải thiện:**

- Xử lý exception chi tiết hơn
- Thêm logging
- Validation phía server mạnh hơn
- Thêm unit tests
