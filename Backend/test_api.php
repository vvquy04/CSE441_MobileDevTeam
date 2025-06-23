<?php

require_once 'vendor/autoload.php';

$app = require_once 'bootstrap/app.php';
$app->make('Illuminate\Contracts\Console\Kernel')->bootstrap();

use App\Models\User;
use App\Models\Booking;

echo "=== Testing API Response ===\n";

// Find a student user
$studentUser = User::whereHas('roles', function($query) {
    $query->where('RoleName', 'student');
})->first();

if (!$studentUser) {
    echo "No student user found\n";
    exit;
}

echo "Student User: {$studentUser->email} (ID: {$studentUser->UserId})\n";

$appointments = Booking::with(['slot.faculty.facultyProfile.department'])
    ->where('StudentUserId', $studentUser->UserId)
    ->orderBy('BookingTime', 'desc')
    ->get();
    
echo "Found bookings: " . $appointments->count() . "\n";

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
    
    $appointment = [
        'id' => $booking->BookingId,
        'teacherName' => $teacherName,
        'department' => $department ? $department->DepartmentName : '',
        'time' => $booking->slot ? date('H:i', strtotime($booking->slot->StartTime)) . ' - ' . date('H:i', strtotime($booking->slot->EndTime)) : '',
        'date' => $booking->slot ? date('d/m/Y', strtotime($booking->slot->StartTime)) : '',
        'room' => $facultyProfile ? ($facultyProfile->office_location ?? '') : '',
        'purpose' => $booking->Purpose ?? 'Không có mục đích',
        'status' => strtoupper($booking->Status ?? 'PENDING') === 'BOOKED' ? 'CONFIRMED' : strtoupper($booking->Status ?? 'PENDING'),
        'cancellationReason' => $booking->CancellationReason ?? '',
    ];
    
    return $appointment;
});

echo "\nAPI Response:\n";
foreach ($result as $appointment) {
    echo "ID: {$appointment['id']}\n";
    echo "  Teacher Name: {$appointment['teacherName']}\n";
    echo "  Department: {$appointment['department']}\n";
    echo "  Date: {$appointment['date']}\n";
    echo "  Time: {$appointment['time']}\n";
    echo "  Room: {$appointment['room']}\n";
    echo "  Purpose: {$appointment['purpose']}\n";
    echo "  Status: {$appointment['status']}\n";
    echo "---\n";
}

echo "\nJSON Response:\n";
echo json_encode($result, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);

echo "\n=== Test Complete ===\n"; 