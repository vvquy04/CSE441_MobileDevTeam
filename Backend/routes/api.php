<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Api\Auth\AuthController as ApiAuthController;
use App\Http\Controllers\Api\Student\TeacherController;
use App\Http\Controllers\Api\Student\StudentController;
use App\Http\Controllers\Api\Faculty\FacultyController;
use App\Http\Controllers\Api\NotificationController;

// Test API
Route::get('/test', function() {
    return response()->json(['message' => 'API routes working']);
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
});

Route::get('/hello', function () {
    return response()->json(['message' => 'Hello from API']);
});