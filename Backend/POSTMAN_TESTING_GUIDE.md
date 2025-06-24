# Hướng dẫn Test API bằng Postman - Lấy Lịch Hẹn

## 1. Cài đặt và Chuẩn bị

### 1.1 Cài đặt Postman
- Tải và cài đặt Postman từ: https://www.postman.com/downloads/
- Tạo tài khoản Postman (miễn phí)

### 1.2 Khởi động Laravel Backend
```bash
cd Backend
php artisan serve
```
Server sẽ chạy tại: `http://localhost:8000`

## 2. Test API Không Cần Authentication

### 2.1 Test Kết nối Database
**Request:**
- Method: `GET`
- URL: `http://localhost:8000/api/test-db`

**Response mong đợi:**
```json
{
    "database_connection": "OK",
    "total_bookings": 5,
    "total_slots": 10,
    "total_users": 15,
    "sample_bookings": [...]
}
```

### 2.2 Test Lấy Tất Cả Bookings (Không Auth)
**Request:**
- Method: `GET`
- URL: `http://localhost:8000/api/test-all-bookings`

**Response mong đợi:**
```json
{
    "faculty_id": 8,
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

### 2.3 Test Raw Booking Data
**Request:**
- Method: `GET`
- URL: `http://localhost:8000/api/test-raw-bookings`

## 3. Test API Cần Authentication

### 3.1 Đăng nhập để lấy Token
**Request:**
- Method: `POST`
- URL: `http://localhost:8000/api/login`
- Headers: 
  - `Content-Type: application/json`
  - `Accept: application/json`
- Body (raw JSON):
```json
{
    "email": "nguyentuant@tlu.edu.vn",
    "password": "password123"
}
```

**Response mong đợi:**
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

**Lưu token này để sử dụng cho các request tiếp theo!**

### 3.2 Lấy Dashboard (Cần Auth)
**Request:**
- Method: `GET`
- URL: `http://localhost:8000/api/faculty/dashboard`
- Headers:
  - `Authorization: Bearer YOUR_TOKEN_HERE`
  - `Accept: application/json`

**Response mong đợi:**
```json
{
    "totalSlots": 10,
    "availableSlots": 8,
    "totalBookings": 5,
    "pendingBookings": 2,
    "confirmedBookings": 3,
    "recentBookings": [...],
    "upcomingSlots": [...]
}
```

### 3.3 Lấy Tất Cả Bookings (Cần Auth)
**Request:**
- Method: `GET`
- URL: `http://localhost:8000/api/faculty/bookings`
- Headers:
  - `Authorization: Bearer YOUR_TOKEN_HERE`
  - `Accept: application/json`

### 3.4 Lấy Pending Bookings
**Request:**
- Method: `GET`
- URL: `http://localhost:8000/api/faculty/bookings/pending`
- Headers:
  - `Authorization: Bearer YOUR_TOKEN_HERE`
  - `Accept: application/json`

### 3.5 Lấy Confirmed Bookings
**Request:**
- Method: `GET`
- URL: `http://localhost:8000/api/faculty/bookings/confirmed`
- Headers:
  - `Authorization: Bearer YOUR_TOKEN_HERE`
  - `Accept: application/json`

### 3.6 Lấy Bookings theo Ngày
**Request:**
- Method: `GET`
- URL: `http://localhost:8000/api/faculty/bookings/by-date?date=2024-01-15&status=pending`
- Headers:
  - `Authorization: Bearer YOUR_TOKEN_HERE`
  - `Accept: application/json`

**Query Parameters:**
- `date` (required): Ngày cần lấy (format: YYYY-MM-DD)
- `status` (optional): Trạng thái lọc (pending, confirmed, completed, cancelled, rejected)

**Response mong đợi:**
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

### 3.7 Lấy Bookings theo Tuần
**Request:**
- Method: `GET`
- URL: `http://localhost:8000/api/faculty/bookings/by-week?start_date=2024-01-15&end_date=2024-01-21&status=confirmed`
- Headers:
  - `Authorization: Bearer YOUR_TOKEN_HERE`
  - `Accept: application/json`

**Query Parameters:**
- `start_date` (required): Ngày bắt đầu (format: YYYY-MM-DD)
- `end_date` (required): Ngày kết thúc (format: YYYY-MM-DD)
- `status` (optional): Trạng thái lọc

**Response mong đợi:**
```json
{
    "start_date": "2024-01-15",
    "end_date": "2024-01-21",
    "total_bookings": 5,
    "bookings_by_date": {
        "2024-01-15": [
            {
                "BookingId": 1,
                "BookingTime": "2024-01-15T10:00:00.000000Z",
                "Status": "confirmed",
                ...
            }
        ],
        "2024-01-16": [
            {
                "BookingId": 2,
                "BookingTime": "2024-01-16T14:00:00.000000Z",
                "Status": "confirmed",
                ...
            }
        ]
    },
    "all_bookings": [...]
}
```

### 3.8 Lấy Bookings theo Status
**Request:**
- Method: `GET`
- URL: `http://localhost:8000/api/faculty/bookings/by-status?status=pending&limit=20`
- Headers:
  - `Authorization: Bearer YOUR_TOKEN_HERE`
  - `Accept: application/json`

**Query Parameters:**
- `status` (required): Trạng thái (pending, confirmed, completed, cancelled, rejected)
- `limit` (optional): Số lượng tối đa (mặc định: 50, tối đa: 100)

**Response mong đợi:**
```json
{
    "status": "pending",
    "total_bookings": 3,
    "bookings": [
        {
            "BookingId": 1,
            "BookingTime": "2024-01-15T10:00:00.000000Z",
            "Status": "pending",
            ...
        }
    ]
}
```

## 4. Test Các Action trên Booking

### 4.1 Approve Booking
**Request:**
- Method: `POST`
- URL: `http://localhost:8000/api/faculty/bookings/1/approve`
- Headers:
  - `Authorization: Bearer YOUR_TOKEN_HERE`
  - `Accept: application/json`

### 4.2 Reject Booking
**Request:**
- Method: `POST`
- URL: `http://localhost:8000/api/faculty/bookings/1/reject`
- Headers:
  - `Authorization: Bearer YOUR_TOKEN_HERE`
  - `Accept: application/json`
- Body (raw JSON):
```json
{
    "reason": "Lịch đã bị trùng, vui lòng chọn thời gian khác"
}
```

### 4.3 Cancel Booking
**Request:**
- Method: `POST`
- URL: `http://localhost:8000/api/faculty/bookings/1/cancel`
- Headers:
  - `Authorization: Bearer YOUR_TOKEN_HERE`
  - `Accept: application/json`
- Body (raw JSON):
```json
{
    "reason": "Có việc đột xuất, không thể gặp được"
}
```

### 4.4 Mark Booking Completed
**Request:**
- Method: `POST`
- URL: `http://localhost:8000/api/faculty/bookings/1/complete`
- Headers:
  - `Authorization: Bearer YOUR_TOKEN_HERE`
  - `Accept: application/json`

## 5. Test Slots Management

### 5.1 Lấy Tất Cả Slots
**Request:**
- Method: `GET`
- URL: `http://localhost:8000/api/faculty/slots`
- Headers:
  - `Authorization: Bearer YOUR_TOKEN_HERE`
  - `Accept: application/json`

### 5.2 Tạo Slot Mới
**Request:**
- Method: `POST`
- URL: `http://localhost:8000/api/faculty/slots`
- Headers:
  - `Authorization: Bearer YOUR_TOKEN_HERE`
  - `Accept: application/json`
- Body (raw JSON):
```json
{
    "start_time": "2024-01-20 14:00:00",
    "end_time": "2024-01-20 15:00:00",
    "is_available": true
}
```

## 6. Các Status Code Cần Biết

- `200 OK`: Request thành công
- `201 Created`: Tạo mới thành công
- `400 Bad Request`: Dữ liệu không hợp lệ
- `401 Unauthorized`: Chưa đăng nhập hoặc token không hợp lệ
- `403 Forbidden`: Không có quyền truy cập
- `404 Not Found`: Không tìm thấy dữ liệu
- `422 Unprocessable Entity`: Validation error
- `500 Internal Server Error`: Lỗi server

## 7. Tips khi Test

### 7.1 Sử dụng Environment Variables
1. Tạo Environment mới trong Postman
2. Thêm variable `base_url` = `http://localhost:8000`
3. Thêm variable `token` để lưu token sau khi login
4. Sử dụng `{{base_url}}/api/...` và `{{token}}` trong requests

### 7.2 Tạo Collection
1. Tạo Collection "TLU Office Hours API"
2. Tổ chức requests theo nhóm:
   - Auth
   - Dashboard
   - Bookings
   - Slots
   - Profile

### 7.3 Test Flow
1. Test connection → `GET /api/test-db`
2. Login → `POST /api/login`
3. Test dashboard → `GET /api/faculty/dashboard`
4. Test bookings → `GET /api/faculty/bookings`
5. Test actions → `POST /api/faculty/bookings/{id}/approve`

## 8. Troubleshooting

### 8.1 Lỗi CORS
Nếu gặp lỗi CORS, kiểm tra:
- Laravel đã chạy đúng port 8000
- Headers có `Accept: application/json`

### 8.2 Lỗi Authentication
- Token có đúng format `Bearer TOKEN`
- Token chưa hết hạn
- User có role faculty

### 8.3 Lỗi Database
- Kiểm tra kết nối database
- Chạy migrations: `php artisan migrate`
- Chạy seeders: `php artisan db:seed`

## 9. Export Collection

Sau khi test xong, bạn có thể export collection để chia sẻ với team:
1. Click vào Collection
2. Click "..." → "Export"
3. Chọn format JSON
4. Lưu file và chia sẻ 