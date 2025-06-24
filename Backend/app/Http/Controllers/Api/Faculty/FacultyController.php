<?php

namespace App\Http\Controllers\Api\Faculty;

use App\Http\Controllers\Controller;

//code của main
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use App\Models\Booking;
use App\Models\AvailableSlot;
use App\Models\User;
use App\Models\FacultyProfile;
use App\Models\Department;
use App\Models\OfficeHourDefinition;
use Carbon\Carbon;

class FacultyController extends Controller
{
    public function getDashboard()
    {
        $facultyId = Auth::id();
        
        // Get total slots
        $totalSlots = AvailableSlot::where('faculty_user_id', $facultyId)->count();
        $availableSlots = AvailableSlot::where('faculty_user_id', $facultyId)
            ->where('IsAvailable', true)
            ->count();
        
        // Get bookings stats
        $totalBookings = Booking::whereHas('slot', function($query) use ($facultyId) {
            $query->where('faculty_user_id', $facultyId);
        })->count();
        
        $pendingBookings = Booking::whereHas('slot', function($query) use ($facultyId) {
            $query->where('faculty_user_id', $facultyId);
        })->where('Status', 'pending')->count();
        
        $confirmedBookings = Booking::whereHas('slot', function($query) use ($facultyId) {
            $query->where('faculty_user_id', $facultyId);
        })->where('Status', 'confirmed')->count();
        
        // Get recent bookings
        $recentBookings = Booking::whereHas('slot', function($query) use ($facultyId) {
            $query->where('faculty_user_id', $facultyId);
        })
        ->with(['student.studentProfile', 'slot'])
        ->orderBy('BookingTime', 'desc')
        ->limit(5)
        ->get();
        
        // Get upcoming slots
        $upcomingSlots = AvailableSlot::where('faculty_user_id', $facultyId)
            ->where('StartTime', '>', now())
            ->orderBy('StartTime', 'asc')
            ->limit(5)
            ->get();
        
        return response()->json([
            'totalSlots' => $totalSlots,
            'availableSlots' => $availableSlots,
            'totalBookings' => $totalBookings,
            'pendingBookings' => $pendingBookings,
            'confirmedBookings' => $confirmedBookings,
            'recentBookings' => $recentBookings,
            'upcomingSlots' => $upcomingSlots
        ]);
    }
    
    public function getBookings()
    {
        $facultyId = Auth::id();
        
        $bookings = Booking::whereHas('slot', function($query) use ($facultyId) {
            $query->where('faculty_user_id', $facultyId);
        })
        ->with(['student.studentProfile', 'slot'])
        ->orderBy('BookingTime', 'desc')
        ->get();
        
        return response()->json($bookings);
    }
    
    public function getPendingBookings()
    {
        $facultyId = Auth::id();
        
        $bookings = Booking::whereHas('slot', function($query) use ($facultyId) {
            $query->where('faculty_user_id', $facultyId);
        })
        ->where('Status', 'pending')
        ->with(['student.studentProfile', 'slot'])
        ->orderBy('BookingTime', 'asc')
        ->get();
        
        return response()->json($bookings);
    }
    
    public function getConfirmedBookings()
    {
        $facultyId = Auth::id();
        
        $bookings = Booking::whereHas('slot', function($query) use ($facultyId) {
            $query->where('faculty_user_id', $facultyId);
        })
        ->where('Status', 'confirmed')
        ->with(['student.studentProfile', 'slot'])
        ->orderBy('BookingTime', 'asc')
        ->get();
        
        return response()->json($bookings);
    }
    
    public function getBookingsByDate(Request $request)
    {
        $facultyId = Auth::id();
        
        $request->validate([
            'date' => 'required|date',
            'status' => 'nullable|in:pending,confirmed,completed,cancelled,rejected'
        ]);
        
        $date = Carbon::parse($request->date)->format('Y-m-d');
        $status = $request->status;
        
        $query = Booking::whereHas('slot', function($query) use ($facultyId) {
            $query->where('faculty_user_id', $facultyId);
        })
        ->whereDate('BookingTime', $date)
        ->with(['student.studentProfile', 'slot']);
        
        if ($status) {
            $query->where('Status', $status);
        }
        
        $bookings = $query->orderBy('BookingTime', 'asc')->get();
        
        return response()->json([
            'date' => $date,
            'total_bookings' => $bookings->count(),
            'bookings' => $bookings
        ]);
    }
    
    public function getBookingsByWeek(Request $request)
    {
        $facultyId = Auth::id();
        
        // Log authentication info
        \Log::info('getBookingsByWeek called', [
            'faculty_id' => $facultyId,
            'user' => Auth::user() ? Auth::user()->email : 'null',
            'headers' => $request->headers->all(),
            'authorization' => $request->header('Authorization')
        ]);
        
        $request->validate([
            'start_date' => 'required|date',
            'end_date' => 'required|date|after_or_equal:start_date',
            'status' => 'nullable|in:pending,confirmed,completed,cancelled,rejected'
        ]);
        
        $startDate = Carbon::parse($request->start_date)->format('Y-m-d');
        $endDate = Carbon::parse($request->end_date)->format('Y-m-d');
        $status = $request->status;
        
        $query = Booking::whereHas('slot', function($query) use ($facultyId) {
            $query->where('faculty_user_id', $facultyId);
        })
        ->whereBetween('BookingTime', [$startDate . ' 00:00:00', $endDate . ' 23:59:59'])
        ->with(['student.studentProfile', 'slot']);
        
        if ($status) {
            $query->where('Status', $status);
        }
        
        $bookings = $query->orderBy('BookingTime', 'asc')->get();
        
        // Group bookings by date
        $groupedBookings = $bookings->groupBy(function($booking) {
            return Carbon::parse($booking->BookingTime)->format('Y-m-d');
        });
        
        \Log::info('getBookingsByWeek result', [
            'faculty_id' => $facultyId,
            'total_bookings' => $bookings->count(),
            'start_date' => $startDate,
            'end_date' => $endDate
        ]);
        
        return response()->json([
            'start_date' => $startDate,
            'end_date' => $endDate,
            'total_bookings' => $bookings->count(),
            'bookings_by_date' => $groupedBookings,
            'all_bookings' => $bookings
        ]);
    }
    
    public function getBookingsByStatus(Request $request)
    {
        $facultyId = Auth::id();
        
        $request->validate([
            'status' => 'required|in:pending,confirmed,completed,cancelled,rejected',
            'limit' => 'nullable|integer|min:1|max:100'
        ]);
        
        $status = $request->status;
        $limit = $request->limit ?? 50;
        
        $bookings = Booking::whereHas('slot', function($query) use ($facultyId) {
            $query->where('faculty_user_id', $facultyId);
        })
        ->where('Status', $status)
        ->with(['student.studentProfile', 'slot'])
        ->orderBy('BookingTime', 'desc')
        ->limit($limit)
        ->get();
        
        return response()->json([
            'status' => $status,
            'total_bookings' => $bookings->count(),
            'bookings' => $bookings
        ]);
    }
    
    public function approveBooking($id)
    {
        $facultyId = Auth::id();
        
        $booking = Booking::whereHas('slot', function($query) use ($facultyId) {
            $query->where('faculty_user_id', $facultyId);
        })
        ->where('Status', 'pending')
        ->findOrFail($id);

        $booking->Status = 'confirmed';
        $booking->save();

        return response()->json([
            'message' => 'Booking đã được phê duyệt thành công!',
            'booking' => $booking->load(['student.studentProfile', 'slot'])
        ]);
    }
    
    public function rejectBooking(Request $request, $id)
    {
        $facultyId = Auth::id();
        
        $request->validate([
            'reason' => 'required|string|max:500'
        ]);
        
        $booking = Booking::whereHas('slot', function($query) use ($facultyId) {
            $query->where('faculty_user_id', $facultyId);
        })
        ->where('Status', 'pending')
        ->findOrFail($id);

        $booking->Status = 'rejected';
        $booking->CancellationReason = $request->reason;
        $booking->CancellationTime = now();
        $booking->save();

        return response()->json([
            'message' => 'Booking đã được từ chối!',
            'booking' => $booking->load(['student.studentProfile', 'slot'])
        ]);
    }
    
    public function cancelBooking(Request $request, $id)
    {
        $facultyId = Auth::id();
        
        $request->validate([
            'reason' => 'required|string|max:500'
        ]);
        
        $booking = Booking::whereHas('slot', function($query) use ($facultyId) {
            $query->where('faculty_user_id', $facultyId);
        })
        ->whereIn('Status', ['confirmed', 'pending'])
        ->findOrFail($id);

        $booking->Status = 'cancelled';
        $booking->CancellationReason = $request->reason;
        $booking->CancellationTime = now();
        $booking->save();

        return response()->json([
            'message' => 'Booking đã được hủy thành công!',
            'booking' => $booking->load(['student.studentProfile', 'slot'])
        ]);
    }
    
    public function markBookingCompleted($id)
    {
        $facultyId = Auth::id();
        
        $booking = Booking::whereHas('slot', function($query) use ($facultyId) {
            $query->where('faculty_user_id', $facultyId);
        })
        ->where('Status', 'confirmed')
        ->findOrFail($id);

        $booking->Status = 'completed';
        $booking->save();

        return response()->json([
            'message' => 'Booking đã được đánh dấu hoàn thành!',
            'booking' => $booking->load(['student.studentProfile', 'slot'])
        ]);
    }
    
    public function getSlots()
    {
        $facultyId = Auth::id();
        
        $slots = AvailableSlot::where('faculty_user_id', $facultyId)
            ->with(['bookings.student.studentProfile'])
            ->orderBy('StartTime', 'desc')
            ->get();
        
        return response()->json($slots);
    }
    
    public function createSlot(Request $request)
    {
        $request->validate([
            'startTime' => 'required|date|after:now',
            'endTime' => 'required|date|after:startTime',
            'maxStudents' => 'required|integer|min:1|max:10',
            'definitionId' => 'nullable|exists:office_hour_definitions,DefinitionId'
        ]);

        $facultyId = Auth::id();
        
        $slot = new AvailableSlot();
        $slot->faculty_user_id = $facultyId;
        $slot->StartTime = $request->startTime;
        $slot->EndTime = $request->endTime;
        $slot->MaxStudents = $request->maxStudents;
        $slot->IsAvailable = true;
        $slot->DefinitionId = $request->definitionId;
        $slot->save();

        return response()->json([
            'message' => 'Slot office hours đã được tạo thành công!',
            'slot' => $slot
        ]);
    }
    
    public function updateSlot(Request $request, $id)
    {
        $request->validate([
            'startTime' => 'required|date',
            'endTime' => 'required|date|after:startTime',
            'maxStudents' => 'required|integer|min:1|max:10',
            'isAvailable' => 'required|boolean',
            'definitionId' => 'nullable|exists:office_hour_definitions,DefinitionId'
        ]);

        $facultyId = Auth::id();
        
        $slot = AvailableSlot::where('faculty_user_id', $facultyId)
            ->findOrFail($id);
        
        $slot->StartTime = $request->startTime;
        $slot->EndTime = $request->endTime;
        $slot->MaxStudents = $request->maxStudents;
        $slot->IsAvailable = $request->isAvailable;
        $slot->DefinitionId = $request->definitionId;
        $slot->save();

        return response()->json([
            'message' => 'Slot đã được cập nhật thành công!',
            'slot' => $slot
        ]);
    }
    
    public function deleteSlot($id)
    {
        $facultyId = Auth::id();
        
        $slot = AvailableSlot::where('faculty_user_id', $facultyId)
            ->findOrFail($id);
        
        $slot->delete();

        return response()->json([
            'message' => 'Slot đã được xóa thành công!'
        ]);
    }
    
    public function toggleSlotAvailability($id)
    {
        $facultyId = Auth::id();
        
        $slot = AvailableSlot::where('faculty_user_id', $facultyId)
            ->findOrFail($id);
        
        $slot->IsAvailable = !$slot->IsAvailable;
        $slot->save();

        return response()->json([
            'message' => 'Trạng thái slot đã được thay đổi!',
            'slot' => $slot
        ]);
    }
    
    public function getProfile()
    {
        $facultyId = Auth::id();
        
        $user = User::with(['facultyProfile.department'])
            ->findOrFail($facultyId);
        
        return response()->json($user);
    }
    
    public function updateProfile(Request $request)
    {
        $request->validate([
            'facultyName' => 'required|string|max:255',
            'departmentId' => 'required|string|exists:departments,DepartmentId',
            'degree' => 'nullable|string|max:255',
            'phoneNumber' => 'nullable|string|max:20',
            'officeLocation' => 'nullable|string|max:255',
            'avatar' => 'nullable|image|mimes:jpeg,png,jpg,gif|max:2048',
        ]);

        $facultyId = Auth::id();
        
        $profile = FacultyProfile::where('faculty_user_id', $facultyId)->first();
        
        if (!$profile) {
            $profile = new FacultyProfile();
            $profile->faculty_user_id = $facultyId;
        }

        $profile->faculty_name = $request->facultyName;
        $profile->department_id = $request->departmentId;
        $profile->degree = $request->degree;
        $profile->phone_number = $request->phoneNumber;
        $profile->office_location = $request->officeLocation;

        // Handle avatar upload
        if ($request->hasFile('avatar')) {
            // Delete old avatar if exists
            if ($profile->avatar && \Storage::disk('public')->exists($profile->avatar)) {
                \Storage::disk('public')->delete($profile->avatar);
            }
            
            $avatarPath = $request->file('avatar')->store('faculty-avatars', 'public');
            $profile->avatar = $avatarPath;
        }

        $profile->save();

        return response()->json([
            'message' => 'Thông tin profile đã được cập nhật thành công!',
            'user' => User::with(['facultyProfile.department'])->find($facultyId)
        ]);
    }

    public function createMultipleSlots(Request $request)
    {
        $facultyId = Auth::id();

        $request->validate([
            'slots' => 'required|array',
            'slots.*.start_time' => 'required|date_format:Y-m-d H:i:s',
            'slots.*.end_time' => 'required|date_format:Y-m-d H:i:s|after:slots.*.start_time',
            'max_students' => 'required|integer|min:1',
            'slot_duration' => 'required|integer|min:15',
            'notes' => 'nullable|string|max:500'
        ]);

        // Tạo office hour definition trước
        $definition = OfficeHourDefinition::create([
            'faculty_user_id' => $facultyId,
            'DayOfWeek' => Carbon::parse($request->slots[0]['start_time'])->dayOfWeek,
            'StartTime' => Carbon::parse($request->slots[0]['start_time'])->format('H:i:s'),
            'EndTime' => Carbon::parse($request->slots[count($request->slots) - 1]['end_time'])->format('H:i:s'),
            'SlotDurationMinutes' => $request->slot_duration,
            'MaxStudentsPerSlot' => $request->max_students,
            'Note' => $request->notes
        ]);

        $createdSlots = [];

        // Tạo các slot và liên kết với definition
        foreach ($request->slots as $slotData) {
            $createdSlots[] = AvailableSlot::create([
                'faculty_user_id' => $facultyId,
                'StartTime' => $slotData['start_time'],
                'EndTime' => $slotData['end_time'],
                'MaxStudents' => $request->max_students,
                'IsAvailable' => true,
                'DefinitionId' => $definition->DefinitionId
            ]);
        }

        return response()->json([
            'message' => 'Đã tạo thành công ' . count($createdSlots) . ' lịch trống!',
            'definition' => $definition,
            'slots' => $createdSlots
        ], 201);
    }

    public function createMonthlySchedule(Request $request)
    {
        $facultyId = Auth::id();

        $request->validate([
            'slots' => 'required|array',
            'slots.*.start_time' => 'required|date_format:Y-m-d H:i:s',
            'slots.*.end_time' => 'required|date_format:Y-m-d H:i:s|after:slots.*.start_time',
            'max_students' => 'required|integer|min:1',
            'slot_duration' => 'required|integer|min:15',
            'notes' => 'nullable|string|max:500',
            'apply_monthly' => 'required|boolean',
            'month' => 'nullable|integer|min:1|max:12',
            'year' => 'nullable|integer|min:1970',
        ]);

        $createdDefinitions = [];
        $createdSlots = [];

        // Lấy tháng/năm từ request hoặc mặc định là tháng/năm hiện tại
        $month = $request->input('month', now()->month);
        $year = $request->input('year', now()->year);
        $start = \Carbon\Carbon::create($year, $month, 1)->startOfMonth();
        $end = \Carbon\Carbon::create($year, $month, 1)->endOfMonth();

        // Nhóm slots theo ngày trong tuần (1=Monday, ..., 7=Sunday)
        $slotsByDayOfWeek = [];
        foreach ($request->slots as $slotData) {
            $slotDate = \Carbon\Carbon::parse($slotData['start_time']);
            $dayOfWeek = $slotDate->dayOfWeekIso; // 1=Monday, 7=Sunday
            if (!isset($slotsByDayOfWeek[$dayOfWeek])) {
                $slotsByDayOfWeek[$dayOfWeek] = [];
            }
            $slotsByDayOfWeek[$dayOfWeek][] = $slotData;
        }

        // Lặp từng ngày trong tháng, chỉ tạo slot cho đúng pattern
        for ($date = $start->copy(); $date->lte($end); $date->addDay()) {
            $dayOfWeek = $date->dayOfWeekIso; // 1=Monday, 7=Sunday
            if (isset($slotsByDayOfWeek[$dayOfWeek])) {
                // Tạo office hour definition cho ngày này (mỗi ngày 1 definition)
                $daySlots = $slotsByDayOfWeek[$dayOfWeek];
                $definition = OfficeHourDefinition::create([
                    'faculty_user_id' => $facultyId,
                    'DayOfWeek' => $dayOfWeek,
                    'StartTime' => \Carbon\Carbon::parse($daySlots[0]['start_time'])->format('H:i:s'),
                    'EndTime' => \Carbon\Carbon::parse($daySlots[count($daySlots) - 1]['end_time'])->format('H:i:s'),
                    'SlotDurationMinutes' => $request->slot_duration,
                    'MaxStudentsPerSlot' => $request->max_students,
                    'Note' => $request->notes . " (" . $date->format('d/m/Y') . ")"
                ]);
                $createdDefinitions[] = $definition;
                // Tạo slots cho ngày này
                foreach ($daySlots as $slotData) {
                    $slotTime = \Carbon\Carbon::parse($slotData['start_time']);
                    $createdSlots[] = AvailableSlot::create([
                        'faculty_user_id' => $facultyId,
                        'StartTime' => $date->format('Y-m-d ') . $slotTime->format('H:i:s'),
                        'EndTime' => $date->format('Y-m-d ') . \Carbon\Carbon::parse($slotData['end_time'])->format('H:i:s'),
                        'MaxStudents' => $request->max_students,
                        'IsAvailable' => true,
                        'DefinitionId' => $definition->DefinitionId
                    ]);
                }
            }
        }

        $message = $request->apply_monthly 
            ? "Đã tạo lịch cho tháng $month/$year!"
            : "Đã tạo lịch cho tuần hiện tại!";

        return response()->json([
            'message' => $message,
            'definitions' => $createdDefinitions,
            'total_slots' => count($createdSlots),
            'month' => $month,
            'year' => $year,
            'pattern_days' => array_keys($slotsByDayOfWeek)
        ], 201);
    }

    private function getDayName($dayOfWeek)
    {
        $days = [
            1 => 'Thứ 2',
            2 => 'Thứ 3', 
            3 => 'Thứ 4',
            4 => 'Thứ 5',
            5 => 'Thứ 6',
            6 => 'Thứ 7',
            0 => 'Chủ nhật'
        ];
        
        return $days[$dayOfWeek] ?? 'Không xác định';
//code của main

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
            ->get();
            
        return response()->json($slots);
//code cua vanquy 
    }
} 