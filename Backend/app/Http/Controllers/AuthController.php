<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;
use App\Models\User;

class AuthController extends Controller
{
     public function register(Request $request)
    {
        // Validate dữ liệu đầu vào
        $request->validate([
            'email' => [
                'required',
                'string',
                'email',
                'max:255',
                'ends_with:e.tlu.edu.vn',
                'unique:users'
            ],
            'password' => [
                'required',
                'string',
                'min:6'
            ]
        ]);
        // Tạo người dùng mới
        $user = User::create([
            'email' => $request->email,
            'password' => Hash::make($request->password),
        ]);

        return response()->json([
            'message' => 'Đăng ký thành công!',
            'user' => $user
        ], 201);
    }
}
