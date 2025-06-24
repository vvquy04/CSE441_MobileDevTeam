# API Endpoints Summary - Faculty Module

## 🔐 Authentication Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/login` | Đăng nhập faculty | ❌ |
| POST | `/api/logout` | Đăng xuất | ✅ |
| GET | `/api/user` | Lấy thông tin user hiện tại | ✅ |

## 📊 Dashboard Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/faculty/dashboard` | Lấy dashboard tổng quan | ✅ |

## 📅 Booking Endpoints

### Lấy danh sách bookings

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/faculty/bookings` | Lấy tất cả bookings | ✅ |
| GET | `/api/faculty/bookings/pending` | Lấy bookings chờ duyệt | ✅ |
| GET | `/api/faculty/bookings/confirmed` | Lấy bookings đã xác nhận | ✅ |
| GET | `/api/faculty/bookings/by-date` | Lấy bookings theo ngày | ✅ |
| GET | `/api/faculty/bookings/by-week` | Lấy bookings theo tuần | ✅ |
| GET | `/api/faculty/bookings/by-status` | Lấy bookings theo trạng thái | ✅ |

### Thao tác với bookings

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/faculty/bookings/{id}/approve` | Duyệt booking | ✅ |
| POST | `/api/faculty/bookings/{id}/reject` | Từ chối booking | ✅ |
| POST | `/api/faculty/bookings/{id}/cancel` | Hủy booking | ✅ |
| POST | `/api/faculty/bookings/{id}/complete` | Đánh dấu hoàn thành | ✅ |

## ⏰ Slot Management Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/faculty/slots` | Lấy tất cả slots | ✅ |
| POST | `/api/faculty/slots` | Tạo slot mới | ✅ |
| PUT | `/api/faculty/slots/{id}` | Cập nhật slot | ✅ |
| DELETE | `/api/faculty/slots/{id}` | Xóa slot | ✅ |
| POST | `/api/faculty/slots/{id}/toggle` | Bật/tắt slot | ✅ |

## 👤 Profile Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/faculty/profile` | Lấy thông tin profile | ✅ |
| PUT | `/api/faculty/profile` | Cập nhật profile | ✅ |

## 🧪 Test Endpoints (Không cần auth)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/test-db` | Test kết nối database |
| GET | `/api/test-all-bookings` | Test lấy bookings (hardcode faculty_id=8) |
| GET | `/api/test-raw-bookings` | Test lấy raw booking data |
| GET | `/api/departments` | Lấy danh sách departments |

## 📋 Query Parameters

### `/api/faculty/bookings/by-date`
- `date` (required): Ngày cần lấy (YYYY-MM-DD)
- `status` (optional): Trạng thái lọc (pending, confirmed, completed, cancelled, rejected)

### `/api/faculty/bookings/by-week`
- `start_date` (required): Ngày bắt đầu (YYYY-MM-DD)
- `end_date` (required): Ngày kết thúc (YYYY-MM-DD)
- `status` (optional): Trạng thái lọc

### `/api/faculty/bookings/by-status`
- `status` (required): Trạng thái (pending, confirmed, completed, cancelled, rejected)
- `limit` (optional): Số lượng tối đa (mặc định: 50, tối đa: 100)

## 🔑 Authentication

Tất cả endpoints cần auth đều sử dụng **Bearer Token**:
```
Authorization: Bearer YOUR_TOKEN_HERE
```

## 📝 Request/Response Examples

### Login Request
```json
POST /api/login
{
    "email": "nguyentuant@tlu.edu.vn",
    "password": "password123"
}
```

### Login Response
```json
{
    "user": {
        "UserId": 8,
        "email": "nguyentuant@tlu.edu.vn",
        "role": "faculty"
    },
    "token": "1|abc123def456ghi789..."
}
```

### Get Bookings by Date Request
```
GET /api/faculty/bookings/by-date?date=2024-01-15&status=pending
```

### Get Bookings by Date Response
```json
{
    "date": "2024-01-15",
    "total_bookings": 3,
    "bookings": [
        {
            "BookingId": 1,
            "SlotId": 1,
            "StudentUserId": 2,
            "BookingTime": "2024-01-15T10:00:00.000000Z",
            "Purpose": "Hỏi về bài tập",
            "Status": "pending",
            "student": {
                "UserId": 2,
                "email": "student@tlu.edu.vn",
                "studentProfile": {
                    "StudentName": "Nguyễn Văn A",
                    "StudentCode": "SV001"
                }
            },
            "slot": {
                "SlotId": 1,
                "StartTime": "2024-01-15T10:00:00.000000Z",
                "EndTime": "2024-01-15T11:00:00.000000Z"
            }
        }
    ]
}
```

## 🚀 Quick Start

1. **Khởi động server:**
   ```bash
   cd Backend
   php artisan serve
   ```

2. **Login để lấy token:**
   ```bash
   POST http://localhost:8000/api/login
   ```

3. **Test API:**
   ```bash
   GET http://localhost:8000/api/faculty/bookings
   Authorization: Bearer YOUR_TOKEN
   ```

## 📊 Status Codes

- `200 OK`: Request thành công
- `201 Created`: Tạo mới thành công
- `400 Bad Request`: Dữ liệu không hợp lệ
- `401 Unauthorized`: Chưa đăng nhập hoặc token không hợp lệ
- `403 Forbidden`: Không có quyền truy cập
- `404 Not Found`: Không tìm thấy dữ liệu
- `422 Unprocessable Entity`: Validation error
- `500 Internal Server Error`: Lỗi server 