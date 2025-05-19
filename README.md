# Car Rental - Hệ Thống Cho Thuê Xe 

Đây là ứng dụng web full-stack giúp quản lý hoạt động thuê xe, bao gồm tính năng trò chuyện giữa quản trị viên và khách hàng. Dự án được xây dựng bằng Spring Boot, Node.js, Tailwind CSS và triển khai thông qua Docker.

## 👥 Nhóm phát triển

Dự án được thực hiện trong khuôn khổ môn Phát triển Ứng dụng Phát triển Doanh Nghiệp - INT3236E 1.

### Thành viên nhóm:
- Đỗ Tuấn Thành - 22024541
- Vũ Hải Long - 22024539

## 🌟 Tính năng chính

- Quản lý danh sách xe cho thuê
- Giao diện trò chuyện giữa quản trị viên và người dùng
- Giao diện thân thiện, responsive với Tailwind CSS
- Hệ thống dễ triển khai nhờ Docker hóa

## 🛠 Công nghệ sử dụng

- **Backend**: Java Spring Boot (Maven)
- **Frontend**: HTML/CSS, JavaScript, Tailwind CSS
- **Quản lý phụ thuộc & build**: Maven, Node.js
- **Triển khai**: Docker, Docker Compose
- **Web Server**: Nginx (cấu hình trong frontend)

## 📁 Cấu trúc thư mục

```
PTUD/
├── backend/                  # Mã nguồn backend Spring Boot
│   ├── src/                 # Tệp mã nguồn Java
│   ├── pom.xml              # Cấu hình Maven
│   └── Dockerfile
├── frontend/                 # Giao diện web người dùng
│   ├── src/                 # Mã nguồn JS/HTML/CSS
│   ├── package.json         # Các phụ thuộc frontend
│   ├── nginx.conf           # Cấu hình Nginx khi triển khai
│   └── Dockerfile
├── docker-compose.yml       # Tệp Docker Compose để khởi động toàn bộ hệ thống
└── README.md
```

## 🚀 Hướng dẫn chạy ứng dụng

### Yêu cầu

- Đã cài đặt Docker & Docker Compose

### Chạy bằng Docker Compose

```bash
docker-compose up --build
```

Tự động:
- Build backend bằng Spring Boot
- Chạy frontend với Nginx
- Kết nối các dịch vụ với nhau qua mạng Docker

### Chạy thủ công không cần Docker

#### Backend

```bash
cd backend
./mvnw spring-boot:run
```

#### Frontend

```bash
cd frontend
npm install
npm run start
```

> Lưu ý backend cần chạy ở cổng phù hợp (mặc định: `8080`).
