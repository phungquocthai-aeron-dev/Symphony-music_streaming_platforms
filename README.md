# XÂY DỰNG NỀN TẢNG NGHE NHẠC TRỰC TUYẾN THÔNG MINH

## SPRING BOOT VÀ ANGULAR

## Giới thiệu

Dự án xây dựng nền tảng nghe nhạc trực tuyến theo mô hình Frontend – Backend tách biệt.  
Backend sử dụng Spring Boot để cung cấp API, xử lý nghiệp vụ và bảo mật; Frontend sử dụng Angular để xây dựng giao diện SPA mượt mà, thân thiện và dễ mở rộng.

Hệ thống hỗ trợ các tính năng thông minh như tìm kiếm bằng giọng nói, tìm kiếm bài hát bằng giai điệu ngân nga và gợi ý bài hát dựa trên hành vi người dùng.

---

## Tính năng hệ thống

### Người dùng

- Đăng ký, đăng nhập, đăng xuất.
- Cập nhật hồ sơ cá nhân.
- Nghe nhạc, xem thông tin bài hát và lời bài hát chạy đồng bộ.
- Tìm kiếm bài hát bằng văn bản hoặc giọng nói.
- Tìm kiếm bài hát bằng giai điệu ngân nga.
- Xem bài hát theo chủ đề, thịnh hành, xếp hạng theo giờ.
- Quản lý playlist: tạo, sửa, xóa và thêm bài hát vào playlist.
- Quản lý danh sách yêu thích.
- Xem lịch sử nghe nhạc.
- Nâng cấp tài khoản thông qua VNPay.

### Ca sĩ

- Xem và cập nhật hồ sơ.
- Thêm, sửa, xóa bài hát sở hữu.
- Tạo và quản lý album.
- Quản lý bài hát trong album.

### Quản trị viên

- Quản lý người dùng: xem, cập nhật, vô hiệu hóa, cấp quyền ca sĩ, xuất Excel.
- Quản lý ca sĩ.
- Quản lý bài hát: thêm, sửa, ẩn, xuất Excel.
- Báo cáo thống kê theo doanh thu và lượt nghe.

---

## Tính năng AI

### Tìm kiếm bài hát bằng giai điệu ngân nga

- Người dùng ghi âm hoặc tải file âm thanh.
- Hệ thống Python xử lý âm thanh, áp dụng thuật toán DTW.
- Trả về danh sách bài hát gần nhất với giai điệu mẫu.

### Tìm kiếm bằng giọng nói

- Tích hợp Web Speech API.
- Hỗ trợ tìm kiếm và điều khiển phát nhạc qua khẩu lệnh.

### Gợi ý bài hát thông minh

- Dựa trên lịch sử nghe, sở thích và thói quen sử dụng.

---

## Kiến trúc hệ thống

### Frontend – Angular

- SPA với Routing, Guards, Interceptor.
- Tách module theo tính năng.
- Hỗ trợ phát nhạc, đồng bộ lời bài hát, tìm kiếm thời gian thực.

### Backend – Spring Boot

- Spring Web, Spring Security, Spring Data JPA.
- Xác thực bằng JWT.
- API RESTful, phân quyền theo từng vai trò User – Singer – Admin.
- Hỗ trợ upload và streaming file âm thanh.

### Cơ sở dữ liệu – MySQL

Các bảng chính:

- User, Singer, Song, Album
- Playlist, Favorite, Category
- Subscription, Listen, Notification
- Invalidated Token

---

## Công nghệ sử dụng

| Thành phần         | Công nghệ                         |
| ------------------ | --------------------------------- |
| Frontend           | Angular, TypeScript               |
| Backend            | Spring Boot, JPA, Spring Security |
| Database           | MySQL                             |
| AI                 | Python, DTW                       |
| Tìm kiếm giọng nói | Web Speech API                    |
| Thanh toán         | VNPay                             |

---

## Cấu hình `application.properties`

Template hướng dẫn cấu hình chạy dự án

```properties
# Application Context Path
server.servlet.context-path=/symphony

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/symphony_sa
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# File Upload Configuration
spring.servlet.multipart.max-file-size=50MB
spring.servlet.multipart.max-request-size=50MB

# JWT Configuration
jwt.secretKey=your_jwt_secret_key
jwt.validDuration=604800
jwt.refreshableDuration=2592000

# AI Service URL (Python Backend)
ai.service.url=http://localhost:8000
```

---

## Kiểm thử

Hệ thống được kiểm thử trên các chức năng sau:

### Chức năng người dùng

- Truy cập và sử dụng giao diện người dùng.
- Quản lý playlist và danh sách yêu thích.
- Phát nhạc trực tuyến.

### Chức năng ca sĩ

- Quản lý thông tin ca sĩ và bài hát.
- Cập nhật, thêm, xóa bài hát.

### Chức năng quản trị

- Quản lý người dùng, ca sĩ, bài hát.
- Phân quyền và quản lý hệ thống.

### Phát nhạc và streaming

- Phát nhạc theo yêu cầu.
- Hỗ trợ các thao tác dừng, tua, chuyển bài.

### Tìm kiếm giọng nói và tìm kiếm theo giai điệu

- Nhận diện giọng nói để tìm kiếm bài hát.
- Tìm bài hát dựa trên giai điệu (còn hạn chế chất lượng).

### Xác thực – phân quyền

- Kiểm thử chức năng đăng nhập, đăng ký.
- Phân quyền người dùng, ca sĩ, quản trị viên.

### Tính toàn vẹn dữ liệu

- Kiểm tra dữ liệu bài hát, playlist, thông tin người dùng.
- Đảm bảo dữ liệu không bị mất mát hoặc sai lệch.

---

## Kết quả đạt được

- Xây dựng đầy đủ hệ thống nghe nhạc trực tuyến với giao diện hiện đại.
- Tích hợp AI vào nhiều tính năng.
- Hoàn thiện hệ thống quản trị mạnh mẽ.
- Hỗ trợ thanh toán và nâng cấp tài khoản.
- Tối ưu trải nghiệm người dùng thông qua SPA.

---

## Hạn chế

- Tìm kiếm theo giai điệu phụ thuộc chất lượng ghi âm.
- Kho dữ liệu bài hát còn hạn chế.
- Chưa hỗ trợ streaming chất lượng cao (HLS/DASH).

---

## Hướng phát triển

- Ứng dụng mô hình học sâu để cá nhân hóa gợi ý nhạc.
- Nâng cấp thuật toán tìm kiếm giai điệu.
- Phát triển ứng dụng mobile.
- Bổ sung tính năng tương tác cộng đồng.
- Áp dụng phân tích dữ liệu nâng cao cho hành vi người dùng.

```

```
