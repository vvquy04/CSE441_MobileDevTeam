<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Api\Auth\AuthController as ApiAuthController;
//HEAD
use App\Http\Controllers\Api\Faculty\FacultyController;
//
use App\Http\Controllers\Api\Student\TeacherController;
use App\Http\Controllers\Api\Student\StudentController;
use App\Http\Controllers\Api\Faculty\FacultyController;
use App\Http\Controllers\Api\NotificationController;
// vanquy_refactor

// Test API
Route::get('/test', function() {
    return response()->json(['message' => 'API routes working']);
});

// Test database connection and bookings data
Route::get('/test-db', function() {
    try {
        $totalBookings = \App\Models\Booking::count();
        $totalSlots = \App\Models\AvailableSlot::count();
        $totalUsers = \App\Models\User::count();
        
        return response()->json([
            'database_connection' => 'OK',
            'total_bookings' => $totalBookings,
            'total_slots' => $totalSlots,
            'total_users' => $totalUsers,
            'sample_bookings' => \App\Models\Booking::with(['student.studentProfile', 'slot'])->limit(3)->get()
        ]);
    } catch (Exception $e) {
        return response()->json([
            'error' => $e->getMessage(),
            'database_connection' => 'FAILED'
        ], 500);
    }
});

// Test simple bookings
Route::get('/test-simple', function() {
    $bookings = \App\Models\Booking::all();
    return response()->json([
        'total_bookings' => $bookings->count(),
        'bookings' => $bookings->toArray()
    ]);
});

// Test bookings debug
Route::get('/test-bookings', function() {
    $facultyId = 8; // nguyentuant@tlu.edu.vn
    
    $bookings = \App\Models\Booking::whereHas('slot', function($query) use ($facultyId) {
        $query->where('faculty_user_id', $facultyId);
    })
    ->with(['student.studentProfile', 'slot'])
    ->get();
    
    return response()->json([
        'faculty_id' => $facultyId,
        'total_bookings' => $bookings->count(),
        'bookings' => $bookings,
        'raw_sql' => \DB::getQueryLog()
    ]);
});

// Test bookings without auth
Route::get('/test-bookings-no-auth', function() {
    $facultyId = 8; // nguyentuant@tlu.edu.vn
    
    $bookings = \App\Models\Booking::whereHas('slot', function($query) use ($facultyId) {
        $query->where('faculty_user_id', $facultyId);
    })
    ->with(['student.studentProfile', 'slot'])
    ->get();
    
    return response()->json([
        'faculty_id' => $facultyId,
        'total_bookings' => $bookings->count(),
        'bookings' => $bookings->toArray()
    ]);
});

// Test faculty bookings without auth
Route::get('/test-faculty-bookings', function() {
    $facultyId = 8; // nguyentuant@tlu.edu.vn
    
    $bookings = \App\Models\Booking::whereHas('slot', function($query) use ($facultyId) {
        $query->where('faculty_user_id', $facultyId);
    })
    ->with(['student.studentProfile', 'slot'])
    ->get();
    
    return response()->json($bookings);
});

// Test all bookings without filter
Route::get('/test-all-bookings', function() {
    $facultyId = 8; // nguyentuant@tlu.edu.vn
    
    $bookings = \App\Models\Booking::whereHas('slot', function($query) use ($facultyId) {
        $query->where('faculty_user_id', $facultyId);
    })
    ->with(['student.studentProfile', 'slot'])
    ->get();
    
    return response()->json([
        'faculty_id' => $facultyId,
        'total_bookings' => $bookings->count(),
        'bookings' => $bookings->toArray(),
        'message' => 'All bookings for faculty without date filter'
    ]);
});

// Test raw booking data
Route::get('/test-raw-bookings', function() {
    $facultyId = 8; // nguyentuant@tlu.edu.vn
    
    $bookings = \App\Models\Booking::whereHas('slot', function($query) use ($facultyId) {
        $query->where('FacultyUserId', $facultyId);
    })
    ->with(['student.studentProfile', 'slot'])
    ->get();
    
    // Return raw data to see the structure
    $rawData = [];
    foreach ($bookings as $booking) {
        $rawData[] = [
            'booking_id' => $booking->BookingId,
            'slot_id' => $booking->SlotId,
            'student_user_id' => $booking->StudentUserId,
            'booking_time' => $booking->BookingTime,
            'purpose' => $booking->Purpose,
            'status' => $booking->Status,
            'student' => $booking->student ? [
                'user_id' => $booking->student->UserId,
                'email' => $booking->student->email,
                'student_profile' => $booking->student->studentProfile ? [
                    'student_name' => $booking->student->studentProfile->StudentName,
                    'student_code' => $booking->student->studentProfile->StudentCode
                ] : null
            ] : null,
            'slot' => $booking->slot ? [
                'slot_id' => $booking->slot->SlotId,
                'start_time' => $booking->slot->StartTime,
                'end_time' => $booking->slot->EndTime
            ] : null
        ];
    }
    
    return response()->json([
        'faculty_id' => $facultyId,
        'total_bookings' => $bookings->count(),
        'raw_data' => $rawData
    ]);
});

// Test email validation
Route::post('/test-email', function(Request $request) {
    $email = $request->input('email', 'nguyentuant@tlu.edu.vn');
    
    $validation = validator(['email' => $email], [
        'email' => ['required', 'email', 'unique:users,email', 'regex:/^[a-zA-Z0-9._%+-]+@tlu\\.edu\\.vn$/']
    ]);
    
    if ($validation->fails()) {
        return response()->json([
            'valid' => false,
            'errors' => $validation->errors(),
            'email' => $email
        ], 422);
    }
    
    return response()->json([
        'valid' => true,
        'email' => $email
    ]);
});

// AuthController test
Route::get('/test-controller', [ApiAuthController::class, 'test']);

// Lấy danh sách departments
Route::get('/departments', function() {
    $departments = \App\Models\Department::select('DepartmentId', 'DepartmentName as Name')->get();
    return response()->json($departments);
});

// Đăng ký, đăng nhập
Route::post('/auth/register-faculty', [ApiAuthController::class, 'registerFaculty']);
Route::post('/auth/register-student', [ApiAuthController::class, 'registerStudent']);
Route::post('/login', [ApiAuthController::class, 'login']);

// API cho sinh viên
Route::get('/student/featured-teachers', [TeacherController::class, 'getFeaturedTeachers']);
Route::get('/student/teachers-by-department/{departmentId}', [TeacherController::class, 'getTeachersByDepartment']);
Route::get('/student/search-teachers', [TeacherController::class, 'searchTeachers']);
Route::get('/student/teacher/{facultyUserId}', [TeacherController::class, 'getTeacherDetail']);

// API lấy slot trống của giảng viên theo ngày
Route::get('/faculty/{facultyUserId}/available-slots', [FacultyController::class, 'getAvailableSlots']);

Route::middleware('auth:sanctum')->group(function () {
    Route::post('/logout', [ApiAuthController::class, 'logout']);
    Route::get('/user', function (Request $request) {
        return $request->user();
    });
    
//HEAD
    // Faculty API Routes
    Route::prefix('faculty')->group(function () {
        Route::get('/dashboard', [FacultyController::class, 'getDashboard']);
        Route::get('/bookings', [FacultyController::class, 'getBookings']);
        Route::get('/bookings/pending', [FacultyController::class, 'getPendingBookings']);
        Route::get('/bookings/confirmed', [FacultyController::class, 'getConfirmedBookings']);
        Route::get('/bookings/by-date', [FacultyController::class, 'getBookingsByDate']);
        Route::get('/bookings/by-week', [FacultyController::class, 'getBookingsByWeek']);
        Route::get('/bookings/by-status', [FacultyController::class, 'getBookingsByStatus']);
        Route::post('/bookings/{id}/approve', [FacultyController::class, 'approveBooking']);
        Route::post('/bookings/{id}/reject', [FacultyController::class, 'rejectBooking']);
        Route::post('/bookings/{id}/cancel', [FacultyController::class, 'cancelBooking']);
        Route::post('/bookings/{id}/complete', [FacultyController::class, 'markBookingCompleted']);
        
        Route::get('/slots', [FacultyController::class, 'getSlots']);
        Route::post('/slots', [FacultyController::class, 'createSlot']);
        Route::post('/slots/multiple', [FacultyController::class, 'createMultipleSlots']);
        Route::post('/slots/monthly', [FacultyController::class, 'createMonthlySchedule']);
        Route::put('/slots/{id}', [FacultyController::class, 'updateSlot']);
        Route::delete('/slots/{id}', [FacultyController::class, 'deleteSlot']);
        Route::post('/slots/{id}/toggle', [FacultyController::class, 'toggleSlotAvailability']);
        
        Route::get('/profile', [FacultyController::class, 'getProfile']);
        Route::put('/profile', [FacultyController::class, 'updateProfile']);
    });
//
    // Student specific routes
    Route::prefix('student')->group(function () {
        Route::get('/profile', [StudentController::class, 'getUserProfile']);
        Route::put('/profile', [StudentController::class, 'updateProfile']);
        Route::get('/dashboard', [StudentController::class, 'getDashboardData']);
        Route::post('/book-appointment', [StudentController::class, 'bookAppointment']);
        Route::get('/appointments', [StudentController::class, 'getAppointments']);
        Route::post('/appointments/{id}/cancel', [StudentController::class, 'cancelAppointment']);
    });
    
    // Faculty specific routes
    Route::prefix('faculty')->group(function () {
        Route::get('/profile', [FacultyController::class, 'getUserProfile']);
        Route::put('/profile', [FacultyController::class, 'updateProfile']);
        Route::get('/dashboard', [FacultyController::class, 'getDashboardData']);
    });
    
    // General user profile (for backward compatibility)
    Route::get('/user/profile', [ApiAuthController::class, 'getUserProfile']);

    Route::get('/notifications', [NotificationController::class, 'index']);
    Route::post('/notifications/{id}/read', [NotificationController::class, 'markAsRead']);
// vanquy_refactor
});

Route::get('/hello', function () {
    return response()->json(['message' => 'Hello from API']);
});

// Test faculty bookings by week without auth
Route::get('/test-faculty-bookings-by-week', function(Request $request) {
    $facultyId = 8; // nguyentuant@tlu.edu.vn
    
    $startDate = $request->get('start_date', '2025-06-16');
    $endDate = $request->get('end_date', '2025-06-22');
    
    $bookings = \App\Models\Booking::whereHas('slot', function($query) use ($facultyId) {
        $query->where('FacultyUserId', $facultyId);
    })
    ->whereBetween('BookingTime', [$startDate . ' 00:00:00', $endDate . ' 23:59:59'])
    ->with(['student.studentProfile', 'slot'])
    ->orderBy('BookingTime', 'asc')
    ->get();
    
    // Group bookings by date
    $groupedBookings = $bookings->groupBy(function($booking) {
        return \Carbon\Carbon::parse($booking->BookingTime)->format('Y-m-d');
    });
    
    return response()->json([
        'start_date' => $startDate,
        'end_date' => $endDate,
        'total_bookings' => $bookings->count(),
        'bookings_by_date' => $groupedBookings,
        'all_bookings' => $bookings
    ]);
});