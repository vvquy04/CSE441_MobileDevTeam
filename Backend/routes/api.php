<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Api\Auth\AuthController as ApiAuthController;

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

Route::middleware('auth:sanctum')->group(function () {
    Route::post('/logout', [ApiAuthController::class, 'logout']);
    Route::get('/user', function (Request $request) {
        return $request->user();
    });
});

Route::get('/hello', function () {
    return response()->json(['message' => 'Hello from API']);
});