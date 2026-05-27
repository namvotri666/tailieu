# Social Network App 

Ứng dụng Mạng xã hội với các tính năng tương tác đa dạng, được phát triển phục vụ Bài tập lớn môn Lập trình Ứng dụng Thiết bị Di động. Trong đó, cá nhân **Nguyễn Đăng Nam (B22DCCN557)** đảm nhiệm phát triển hệ thống Back-end và Front-end cho 4 tính năng tương tác cốt lõi: **Like/Unlike bài viết**, **Bình luận**, **Theo dõi (Follow)** và **Tố cáo bài viết (Report)**.

---

## Mục tiêu & lợi ích

- **Tăng cường khả năng kết nối**: Giúp người dùng dễ dàng tương tác với các bài viết thông qua thả tim (Like) và thảo luận (Comment).
- **Mở rộng mạng lưới quan hệ**: Tính năng Follow cho phép người dùng xây dựng News Feed theo sở thích cá nhân, theo dõi các tác giả yêu thích.
- **Môi trường mạng xã hội lành mạnh**: Hệ thống Report giúp cộng đồng tự làm sạch nội dung, kết nối trực tiếp đến khu vực xử lý của Admin.
- **Trải nghiệm thời gian thực (Real-time)**: Các tương tác được cập nhật lên giao diện một cách nhanh chóng nhờ kỹ thuật Optimistic UI Update.

---

## Vai trò người dùng

- **USER (Người dùng thông thường)**: Đăng bài, thả like bài viết, viết bình luận, theo dõi người dùng khác và báo cáo các nội dung vi phạm.
- **ADMIN (Quản trị viên)**: Xem danh sách các bài viết bị báo cáo vi phạm, đánh giá nội dung và quyết định xóa bài hoặc cảnh cáo người dùng.

---

## Chi tiết Mã nguồn Cá nhân thực hiện 

Toàn bộ các file dưới đây đều đã được thêm **JavaDoc Comment** đầy đủ tại các Class, các hàm xử lý logic và các API. Dưới đây là danh sách các file chính chịu trách nhiệm cho 4 chức năng tôi đảm nhận:

### 1. Tầng Controller (Điều hướng API)
- [`UserController.java`](./backend/src/main/java/com/Man10h/social_network_app/controller/UserController.java): Tiếp nhận các Request từ người dùng (Like, Comment, Follow, Tạo Report). Validate dữ liệu, xác thực JWT Token và gọi xuống tầng Service.
- [`AdminController.java`](./backend/src/main/java/com/Man10h/social_network_app/controller/AdminController.java): Cung cấp các API dành riêng cho Admin để lấy danh sách Report (Tố cáo), xóa Report hoặc xem chi tiết.

### 2. Tầng Service (Xử lý Nghiệp vụ)
- [`PostLikeServiceImpl.java`](./backend/src/main/java/com/Man10h/social_network_app/service/impl/PostLikeServiceImpl.java): Xử lý logic Like/Unlike. Kiểm tra trạng thái Like hiện tại trong CSDL, tăng/giảm bộ đếm (Like Count) và bắn thông báo (Notification) cho chủ bài viết.
- [`CommentServiceImpl.java`](./backend/src/main/java/com/Man10h/social_network_app/service/impl/CommentServiceImpl.java): Khởi tạo bình luận mới, gắn thông tin người bình luận (Tên, Avatar) vào Entity để tối ưu hiệu năng hiển thị. Xử lý quyền tự xóa bình luận của người dùng.
- [`FollowerServiceImpl.java`](./backend/src/main/java/com/Man10h/social_network_app/service/impl/FollowerServiceImpl.java): Xử lý việc Theo dõi hoặc Hủy theo dõi tài khoản khác dựa trên ID. Đảm bảo người dùng không tự theo dõi chính mình.
- [`ReportServiceImpl.java`](./backend/src/main/java/com/Man10h/social_network_app/service/impl/ReportServiceImpl.java): Lưu trữ dữ liệu tố cáo (Tiêu đề, Nội dung chi tiết). Xử lý lỗi `UniqueConstraintException` khi một người cố tình Report một bài viết nhiều lần.

### 3. Tầng Entity (Cơ sở dữ liệu)
- [`PostLikeEntity.java`](./backend/src/main/java/com/Man10h/social_network_app/model/entity/PostLikeEntity.java): Bảng `post_like`. Ràng buộc `uk_like_post_user` đảm bảo 1 User chỉ Like 1 Post 1 lần.
- [`CommentEntity.java`](./backend/src/main/java/com/Man10h/social_network_app/model/entity/CommentEntity.java): Bảng `comment`. Lưu trữ nội dung, thời gian và ID bài viết liên quan.
- [`FollowerEntity.java`](./backend/src/main/java/com/Man10h/social_network_app/model/entity/FollowerEntity.java): Bảng `follower`. Ràng buộc `uk_user_follower` đảm bảo tính duy nhất của mối quan hệ theo dõi.
- [`ReportEntity.java`](./backend/src/main/java/com/Man10h/social_network_app/model/entity/ReportEntity.java): Bảng `report`. Chứa thông tin bài viết bị tố cáo để Admin quản lý.

### 4. Tầng Client (Frontend React Native)
- [`PostCard.jsx`](./frontend/src/components/PostCard.jsx): Component tái sử dụng để hiển thị bài viết. Chứa Logic giao diện gọi API Like/Unlike (sử dụng Optimistic UI) và gọi Modal Report.
- [`PostDetailScreen.jsx`](./frontend/src/screens/PostDetailScreen.jsx): Màn hình chi tiết bài viết, hiển thị toàn bộ chuỗi Comment và thanh Input để gửi bình luận mới.

---

## Tính năng chính & Thống kê API

Base URL mặc định: `http://<host>:8080/api/v1`

### 1. Like/Unlike (Thích bài viết)
- `POST /user/posts/{id}/like`: Toggle trạng thái Thích / Hủy thích bài viết.
- `GET /user/posts/{id}/user-like`: Kiểm tra xem người dùng hiện tại đã thích bài viết này chưa.

### 2. Bình luận (Comment)
- `POST /user/posts/{id}/comments`: Đăng một bình luận mới vào bài viết.
- `DELETE /user/comments/{id}`: Người dùng tự xóa bình luận của chính mình.

### 3. Theo dõi người dùng (Follow)
- `POST /user/followers/{followerId}`: Toggle trạng thái Theo dõi / Hủy theo dõi một tài khoản.
- `GET /user/followers`: Lấy danh sách những người đang theo dõi mình.

### 4. Báo cáo vi phạm (Report)
- **User API**:
  - `POST /user/posts/{id}/reports`: Gửi một báo cáo tố cáo bài viết vi phạm.
  - `GET /user/reports`: Xem danh sách các báo cáo mình đã gửi.
  - `DELETE /user/reports/{id}`: Rút lại báo cáo đã gửi.
- **Admin API**:
  - `GET /admin/reports`: Lấy danh sách toàn bộ báo cáo trên hệ thống.
  - `GET /admin/posts/{id}/reports`: Xem các báo cáo thuộc về một bài viết cụ thể.
  - `DELETE /admin/reports/{id}`: Admin xóa báo cáo sau khi đã xử lý xong.

---

## Hướng dẫn chạy Database (MySQL)

1) Tạo CSDL:
Sử dụng MySQL Client tạo database:
```sql
CREATE DATABASE social_network_app;
```

2) **Lưu ý quan trọng**: Dự án đang sử dụng Liquibase. Khi khởi động Backend, các bảng (Bảng PostLike, Comment, Follower, Report...) sẽ được tự động sinh ra dựa trên kịch bản trong `db/changelog`, không cần phải import file `.sql` thủ công.

---

## Hướng dẫn chạy Backend (Spring Boot)

### Yêu cầu
- Java **17+**
- Maven
- MySQL

### Cấu hình
Sửa `backend/src/main/resources/application.yml` (host/user/pass của MySQL, JWT secret nếu có).

### Run (PowerShell)

```powershell
cd e:\TaiLieu\MAD\Code\backend

# Đảm bảo JAVA_HOME trỏ đúng JDK 17
mvn clean package -DskipTests
java -jar target/social_network_app-0.0.1-SNAPSHOT.jar
# (Hoặc gõ ./mvnw spring-boot:run)
```
Backend sẽ khởi chạy ở cổng `8080`.

---

## Hướng dẫn chạy Frontend (Expo React Native / React)

### Yêu cầu
- Node.js + npm/yarn

### Cài và chạy

```powershell
cd e:\TaiLieu\MAD\Code\frontend
npm install
npm start
# hoặc npx expo start nếu dùng React Native
```

### Cấu hình API URL
Cần thay đổi biến môi trường trỏ đến IP máy tính thực tế (không dùng `localhost` nếu test bằng điện thoại thật):
```javascript
// Thay đổi trong file cấu hình Axios (ví dụ: services/api.js hoặc .env)
const BASE_URL = "http://192.168.1.100:8080/api/v1";
```

---

## Cấu trúc thư mục

- `backend/`: Spring Boot API (Controller, Service, Repository, Entity, DTO, Config)
- `frontend/`: React / React Native Client App (Screens, Components, Services)
- `Bao_Cao_BTL_Nguyen_Dang_Nam_Official.docx`: Báo cáo kỹ thuật bài tập lớn định dạng Word.
