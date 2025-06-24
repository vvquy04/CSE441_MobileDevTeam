<?php

namespace App\Http\Controllers\Api\Student;

use App\Http\Controllers\Controller;
use App\Models\User;
use App\Models\StudentProfile;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Storage;
use Illuminate\Support\Facades\Log;
use App\Models\Booking;
use App\Models\AvailableSlot;
use App\Services\NotificationService;

class StudentController extends Controller
{
    protected $notificationService;

    public function __construct(NotificationService $notificationService)
    {
        $this->notificationService = $notificationService;
    }

    public function getUserProfile(Request $request)
    {
        Log::info('getUserProfile called');
        Log::info('Request headers', $request->headers->all());
        Log::info('Authorization header', ['auth' => $request->header('Authorization')]);
        
        $user = $request->user();
        Log::info('User from request', ['user' => $user ? $user->toArray() : null]);
        
        if (!$user) {
            Log::error('No authenticated user found');
            return response()->json([
                'message' => 'Unauthenticated.'
            ], 401);
        }
        
        $roles = $user->roles->pluck('RoleName')->toArray();
        Log::info('User roles', ['roles' => $roles]);
        
        // Kiểm tra xem user có phải là sinh viên không
        if (!in_array('student', $roles)) {
            Log::error('User is not a student', ['user_id' => $user->UserId, 'roles' => $roles]);
            return response()->json([
                'message' => 'Unauthorized. User is not a student.'
            ], 403);
        }
        
        $profile = StudentProfile::where('StudentUserId', $user->UserId)->first();
        Log::info('Student profile found', ['profile' => $profile ? $profile->toArray() : null]);
        
        $avatarUrl = null;
        
        if ($profile && $profile->avatar) {
            // Sử dụng IP address thay vì localhost cho Android
            $avatarUrl = 'http://10.0.2.2:8000/storage/' . $profile->avatar;
        }
        
        $response = [
            'user' => $user,
            'roles' => $roles,
            'profile' => $profile,
            'avatar_url' => $avatarUrl
        ];
        
        Log::info('Returning response', $response);
        
        return response()->json($response);
    }

    public function updateProfile(Request $request)
    {
        Log::info('Update profile request received', $request->all());
        Log::info('Request headers', $request->headers->all());
        Log::info('Request content type', ['content_type' => $request->header('Content-Type')]);
        Log::info('Request method', ['method' => $request->method()]);
        Log::info('Request body', ['body' => $request->getContent()]);
        
        $user = $request->user();
        $roles = $user->roles->pluck('RoleName')->toArray();
        
        Log::info('User info', ['user_id' => $user->UserId, 'roles' => $roles]);
        
        // Kiểm tra xem user có phải là sinh viên không
        if (!in_array('student', $roles)) {
            Log::error('User is not a student', ['user_id' => $user->UserId, 'roles' => $roles]);
            return response()->json([
                'message' => 'Unauthorized. User is not a student.'
            ], 403);
        }

        $request->validate([
            'StudentName' => 'nullable|string|max:255',
            'StudentCode' => 'nullable|string|max:50|unique:student_profiles,StudentCode,' . $user->UserId . ',StudentUserId',
            'ClassName' => 'nullable|string|max:50',
            'PhoneNumber' => 'nullable|string|max:20',
            'avatar' => 'nullable|image|mimes:jpeg,png,jpg,gif|max:2048',
        ]);

        $profile = StudentProfile::where('StudentUserId', $user->UserId)->first();
        
        Log::info('Found profile', ['profile' => $profile ? $profile->toArray() : null]);
        
        if (!$profile) {
            Log::error('Student profile not found', ['user_id' => $user->UserId]);
            return response()->json([
                'message' => 'Student profile not found.'
            ], 404);
        }

        // Cập nhật thông tin profile
        $updateData = $request->only([
            'StudentName', 'StudentCode', 'ClassName', 'PhoneNumber'
        ]);
        
        Log::info('Updating profile with data', $updateData);
        
        // Check if data is actually different
        $hasChanges = false;
        foreach ($updateData as $key => $value) {
            if ($profile->$key != $value) {
                Log::info("Field $key changed from '{$profile->$key}' to '$value'");
                $hasChanges = true;
            }
        }
        
        if (!$hasChanges) {
            Log::info('No changes detected, skipping update');
        } else {
            $profile->update($updateData);
            Log::info('Profile updated in database');
        }

        // Xử lý avatar nếu có
        if ($request->hasFile('avatar')) {
            Log::info('Processing avatar upload');
            // Xóa avatar cũ nếu có
            if ($profile->avatar) {
                Storage::disk('public')->delete($profile->avatar);
            }
            
            $avatarPath = $request->file('avatar')->store('avatars', 'public');
            $profile->update(['avatar' => $avatarPath]);
            Log::info('Avatar uploaded', ['path' => $avatarPath]);
        }

        $avatarUrl = $profile->avatar ? 'http://10.0.2.2:8000/storage/' . $profile->avatar : null;

        Log::info('Profile updated successfully', ['profile' => $profile->toArray()]);

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
        
        // Kiểm tra xem user có phải là sinh viên không
        if (!in_array('student', $roles)) {
            return response()->json([
                'message' => 'Unauthorized. User is not a student.'
            ], 403);
        }

        $profile = StudentProfile::where('StudentUserId', $user->UserId)->first();
        
        // Lấy thống kê booking (có thể thêm sau)
        $totalBookings = 0; // Placeholder
        $upcomingBookings = 0; // Placeholder

        return response()->json([
            'user' => $user,
            'profile' => $profile,
            'stats' => [
                'total_bookings' => $totalBookings,
                'upcoming_bookings' => $upcomingBookings
            ]
        ]);
    }

    public function bookAppointment(Request $request)
    {
        Log::info('bookAppointment called');
        Log::info('Request headers', $request->headers->all());
        Log::info('Authorization header', ['auth' => $request->header('Authorization')]);
        $user = $request->user();
        Log::info('User from request', ['user' => $user ? $user->toArray() : null]);
        if (!$user->hasRole('student')) {
            return response()->json(['message' => 'Unauthorized'], 403);
        }

        $request->validate([
            'SlotId' => 'required|exists:available_slots,SlotId',
            'Purpose' => 'nullable|string|max:500',
            'MemberCount' => 'nullable|integer|min:1'
        ]);

        $memberCount = $request->input('MemberCount', 1);

        $slot = AvailableSlot::find($request->SlotId);
        if (!$slot->IsAvailable) {
            return response()->json(['message' => 'Slot is not available'], 400);
        }

        // Đếm số booking đã có cho slot này (pending/confirmed)
        $currentCount = \App\Models\Booking::where('SlotId', $slot->SlotId)
            ->whereIn('Status', ['pending', 'confirmed'])
            ->count();

        if ($currentCount + $memberCount > $slot->MaxStudents) {
            return response()->json(['message' => 'Số lượng sinh viên vượt quá giới hạn cho slot này!'], 400);
        }

        // Kiểm tra đã có booking cho slot này chưa (nếu muốn mỗi sinh viên chỉ đặt 1 lần)
        $existing = Booking::where('SlotId', $slot->SlotId)
            ->where('StudentUserId', $user->UserId)
            ->first();
        if ($existing) {
            return response()->json(['message' => 'You have already booked this slot'], 400);
        }

        // Tạo booking
        $booking = Booking::create([
            'SlotId' => $slot->SlotId,
            'StudentUserId' => $user->UserId,
            'BookingTime' => now(),
            'Purpose' => $request->Purpose,
            'Status' => 'pending'
        ]);

        // Nếu slot chỉ cho 1 người, set IsAvailable = false
        if ($slot->MaxStudents <= 1) {
            $slot->IsAvailable = false;
            $slot->save();
        }

        // Notify faculty
        $this->notificationService->notifyOnBookingCreation($booking);

        return response()->json(['message' => 'Booking successful', 'booking' => $booking]);
    }

    public function getAppointments(Request $request)
    {
        $user = $request->user();
        $roles = $user->roles->pluck('RoleName')->toArray();
        if (!in_array('student', $roles)) {
            return response()->json([
                'message' => 'Unauthorized. User is not a student.'
            ], 403);
        }
        
        Log::info('Getting appointments for user', ['user_id' => $user->UserId]);
        
        $appointments = \App\Models\Booking::with(['slot.faculty.facultyProfile.department'])
            ->where('StudentUserId', $user->UserId)
            ->orderBy('BookingTime', 'desc')
            ->get();
            
        Log::info('Found bookings', ['count' => $appointments->count()]);
        
        $result = $appointments->map(function($booking) {
            $faculty = $booking->slot ? $booking->slot->faculty : null;
            $facultyProfile = $faculty ? $faculty->facultyProfile : null;
            $department = $facultyProfile ? $facultyProfile->department : null;
            
            // Lấy tên giảng viên từ FacultyProfile
            $teacherName = 'Unknown';
            if ($facultyProfile && $facultyProfile->faculty_name) {
                $teacherName = $facultyProfile->faculty_name;
                // Thêm degree nếu có
                if ($facultyProfile->degree) {
                    $teacherName = $facultyProfile->degree . '. ' . $teacherName;
                }
            }
            
            // Lấy avatar URL nếu có
            $avatarUrl = null;
            if ($facultyProfile && $facultyProfile->avatar) {
                $avatarUrl = asset('storage/' . $facultyProfile->avatar);
            }
            
            $appointment = [
                'id' => $booking->BookingId,
                'teacherName' => $teacherName,
                'department' => $department ? $department->DepartmentName : '',
                'time' => $booking->slot ? date('H:i', strtotime($booking->slot->StartTime)) . ' - ' . date('H:i', strtotime($booking->slot->EndTime)) : '',
                'date' => $booking->slot ? date('d/m/Y', strtotime($booking->slot->StartTime)) : '',
                'room' => $facultyProfile ? ($facultyProfile->office_location ?? '') : '',
                'purpose' => $booking->Purpose ?? 'Không có mục đích',
                'status' => strtolower($booking->Status ?? 'pending'),
                'cancellationReason' => $booking->CancellationReason ?? '',
                'faculty_user_id' => $faculty ? $faculty->UserId : null,
                'avatarUrl' => $avatarUrl,
            ];
            
            Log::info('Processed appointment', $appointment);
            return $appointment;
        });
        
        Log::info('Returning appointments', ['count' => $result->count()]);
        return response()->json($result);
    }

    // Thêm API hủy lịch hẹn
    public function cancelAppointment(Request $request, $id)
    {
        $user = $request->user();
        $booking = \App\Models\Booking::where('BookingId', $id)
            ->where('StudentUserId', $user->UserId)
            ->first();

        if (!$booking) {
            return response()->json(['message' => 'Không tìm thấy lịch hẹn!'], 404);
        }

        if (strtoupper($booking->Status) === 'CANCELLED') {
            return response()->json(['message' => 'Lịch hẹn đã bị hủy trước đó!'], 400);
        }

        $reason = $request->input('reason');
        $booking->Status = 'CANCELLED';
        $booking->CancellationReason = $reason;
        $booking->CancellationTime = now();
        $booking->save();

        // Notify faculty
        $this->notificationService->notifyOnBookingCancellation($booking);

        return response()->json(['message' => 'Đã hủy lịch thành công!']);
    }
}