<?php

namespace App\Http\Controllers\Api\Auth;

use App\Http\Controllers\Controller;
use App\Models\User;
use App\Models\Role;
use App\Models\FacultyProfile;
use App\Models\StudentProfile;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;
use Illuminate\Validation\ValidationException;

class AuthController extends Controller
{
    public function test()
    {
        return response()->json(['message' => 'AuthController working']);
    }

    public function registerFaculty(Request $request)
    {
        $request->validate([
            'email' => ['required', 'email', 'unique:users,email', 'regex:/^[a-zA-Z0-9._%+-]+@tlu\\.edu\\.vn$/'],
            'password' => 'required|min:6|confirmed',
            'faculty_name' => 'required|string|max:255',
            'department_id' => 'required|string|exists:departments,DepartmentId',
            'degree' => 'nullable|string|max:255',
            'phone_number' => 'nullable|string|max:20',
            'office_room' => 'nullable|string|max:255',
            'avatar' => 'nullable|image|mimes:jpeg,png,jpg,gif|max:2048',
        ]);

        $user = User::create([
            'email' => $request->email,
            'password' => Hash::make($request->password),
        ]);

        $facultyRole = Role::where('RoleName', 'faculty')->first();
        if ($facultyRole) {
            $user->roles()->attach($facultyRole->RoleId);
        }

        $avatarPath = null;
        if ($request->hasFile('avatar')) {
            $avatarPath = $request->file('avatar')->store('avatars', 'public');
        }

        FacultyProfile::create([
            'faculty_user_id' => $user->UserId,
            'faculty_name' => $request->faculty_name,
            'department_id' => $request->department_id,
            'degree' => $request->degree,
            'phone_number' => $request->phone_number,
            'office_location' => $request->office_room,
            'avatar' => $avatarPath,
        ]);

        $token = $user->createToken('auth_token')->plainTextToken;

        return response()->json([
            'message' => 'Đăng ký giảng viên thành công.',
            'access_token' => $token,
            'token_type' => 'Bearer',
            'user' => $user,
            'role' => 'faculty',
            'avatar_url' => $avatarPath ? asset('storage/' . $avatarPath) : null,
        ]);
    }

    public function registerStudent(Request $request)
    {
        $request->validate([
            'email' => ['required', 'email', 'unique:users,email', 'regex:/^[a-zA-Z0-9._%+-]+@e\.tlu\.edu\.vn$/'],
            'password' => 'required|min:6|confirmed',
            'StudentName' => 'required|string|max:255',
            'StudentCode' => 'required|string|max:50|unique:student_profiles,StudentCode',
            'ClassName' => 'nullable|string|max:50',
            'PhoneNumber' => 'nullable|string|max:20',
            'avatar' => 'nullable|image|mimes:jpeg,png,jpg,gif|max:2048',
        ]);

        $user = User::create([
            'email' => $request->email,
            'password' => Hash::make($request->password),
        ]);

        $studentRole = Role::where('RoleName', 'student')->first();
        if ($studentRole) {
            $user->roles()->attach($studentRole->RoleId);
        }

        $avatarPath = null;
        if ($request->hasFile('avatar')) {
            $avatarPath = $request->file('avatar')->store('avatars', 'public');
        }

        StudentProfile::create([
            'StudentUserId' => $user->UserId,
            'StudentName' => $request->StudentName,
            'StudentCode' => $request->StudentCode,
            'ClassName' => $request->ClassName ?? '',
            'PhoneNumber' => $request->PhoneNumber,
            'avatar' => $avatarPath,
        ]);

        $token = $user->createToken('auth_token')->plainTextToken;

        return response()->json([
            'message' => 'Đăng ký sinh viên thành công.',
            'access_token' => $token,
            'token_type' => 'Bearer',
            'user' => $user,
            'role' => 'student',
            'avatar_url' => $avatarPath ? asset('storage/' . $avatarPath) : null,
        ]);
    }

    public function login(Request $request)
    {
        $request->validate([
            'email' => 'required|email',
            'password' => 'required',
        ]);

        $user = User::where('email', $request->email)->first();

        if (! $user || ! Hash::check($request->password, $user->password)) {
            throw ValidationException::withMessages([
                'email' => ['Thông tin đăng nhập không hợp lệ.'],
            ]);
        }

        $token = $user->createToken('auth_token')->plainTextToken;

        $roles = $user->roles->pluck('RoleName')->toArray();

        return response()->json([
            'access_token' => $token,
            'token_type' => 'Bearer',
            'user' => $user,
            'roles' => $roles
        ]);
    }

    public function logout(Request $request)
    {
        $request->user()->currentAccessToken()->delete();

        return response()->json(['message' => 'Đăng xuất thành công.']);
    }

    public function getUserProfile(Request $request)
    {
        $user = $request->user();
        $roles = $user->roles->pluck('RoleName')->toArray();
        
        $profile = null;
        $avatarUrl = null;
        
        if (in_array('student', $roles)) {
            $profile = StudentProfile::where('StudentUserId', $user->UserId)->first();
            if ($profile && $profile->avatar) {
                $avatarUrl = asset('storage/' . $profile->avatar);
            }
        } elseif (in_array('faculty', $roles)) {
            $profile = FacultyProfile::where('faculty_user_id', $user->UserId)->first();
            if ($profile && $profile->avatar) {
                $avatarUrl = asset('storage/' . $profile->avatar);
            }
        }
        
        return response()->json([
            'user' => $user,
            'roles' => $roles,
            'profile' => $profile,
            'avatar_url' => $avatarUrl
        ]);
    }
} 