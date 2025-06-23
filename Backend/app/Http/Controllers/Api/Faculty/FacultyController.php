<?php

namespace App\Http\Controllers\Api\Faculty;

use App\Http\Controllers\Controller;
use App\Models\User;
use App\Models\FacultyProfile;
use App\Models\AvailableSlot;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Storage;

class FacultyController extends Controller
{
    public function getUserProfile(Request $request)
    {
        $user = $request->user();
        $roles = $user->roles->pluck('RoleName')->toArray();
        
        // Kiểm tra xem user có phải là giảng viên không
        if (!in_array('faculty', $roles)) {
            return response()->json([
                'message' => 'Unauthorized. User is not a faculty.'
            ], 403);
        }
        
        $profile = FacultyProfile::where('faculty_user_id', $user->UserId)->first();
        $avatarUrl = null;
        
        if ($profile && $profile->avatar) {
            $avatarUrl = asset('storage/' . $profile->avatar);
        }
        
        return response()->json([
            'user' => $user,
            'roles' => $roles,
            'profile' => $profile,
            'avatar_url' => $avatarUrl
        ]);
    }

    public function updateProfile(Request $request)
    {
        $user = $request->user();
        $roles = $user->roles->pluck('RoleName')->toArray();
        
        // Kiểm tra xem user có phải là giảng viên không
        if (!in_array('faculty', $roles)) {
            return response()->json([
                'message' => 'Unauthorized. User is not a faculty.'
            ], 403);
        }

        $request->validate([
            'faculty_name' => 'nullable|string|max:255',
            'department_id' => 'nullable|string|exists:departments,DepartmentId',
            'degree' => 'nullable|string|max:255',
            'phone_number' => 'nullable|string|max:20',
            'office_location' => 'nullable|string|max:255',
            'avatar' => 'nullable|image|mimes:jpeg,png,jpg,gif|max:2048',
        ]);

        $profile = FacultyProfile::where('faculty_user_id', $user->UserId)->first();
        
        if (!$profile) {
            return response()->json([
                'message' => 'Faculty profile not found.'
            ], 404);
        }

        // Cập nhật thông tin profile
        $profile->update($request->only([
            'faculty_name', 'department_id', 'degree', 'phone_number', 'office_location'
        ]));

        // Xử lý avatar nếu có
        if ($request->hasFile('avatar')) {
            // Xóa avatar cũ nếu có
            if ($profile->avatar) {
                Storage::disk('public')->delete($profile->avatar);
            }
            
            $avatarPath = $request->file('avatar')->store('avatars', 'public');
            $profile->update(['avatar' => $avatarPath]);
        }

        $avatarUrl = $profile->avatar ? asset('storage/' . $profile->avatar) : null;

        return response()->json([
            'message' => 'Profile updated successfully.',
            'profile' => $profile,
            'avatar_url' => $avatarUrl
        ]);
    }

    public function getDashboardData(Request $request)
    {
        $user = $request->user();
        $roles = $user->roles->pluck('RoleName')->toArray();
        
        // Kiểm tra xem user có phải là giảng viên không
        if (!in_array('faculty', $roles)) {
            return response()->json([
                'message' => 'Unauthorized. User is not a faculty.'
            ], 403);
        }

        $profile = FacultyProfile::where('faculty_user_id', $user->UserId)->first();
        
        // Lấy thống kê office hours (có thể thêm sau)
        $totalOfficeHours = 0; // Placeholder
        $upcomingBookings = 0; // Placeholder

        return response()->json([
            'user' => $user,
            'profile' => $profile,
            'stats' => [
                'total_office_hours' => $totalOfficeHours,
                'upcoming_bookings' => $upcomingBookings
            ]
        ]);
    }

    public function getAvailableSlots(Request $request, $facultyUserId)
    {
        $date = $request->query('date');
        if (!$date) {
            return response()->json(['message' => 'Missing date parameter'], 400);
        }
        $slots = \App\Models\AvailableSlot::where('faculty_user_id', $facultyUserId)
            ->whereDate('StartTime', $date)
            ->get()
            ->map(function($slot) {
                $status = $slot->IsAvailable ? 'AVAILABLE' : 'BOOKED';
                return [
                    'id' => $slot->SlotId,
                    'time' => date('H:i', strtotime($slot->StartTime)) . ' - ' . date('H:i', strtotime($slot->EndTime)),
                    'status' => $status,
                ];
            });
        return response()->json($slots);
    }
} 