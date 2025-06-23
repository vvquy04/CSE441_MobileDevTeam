<?php

require_once 'vendor/autoload.php';

$app = require_once 'bootstrap/app.php';
$app->make('Illuminate\Contracts\Console\Kernel')->bootstrap();

use App\Models\Booking;
use App\Models\FacultyProfile;

echo "=== Testing Bookings and Faculty Data ===\n";

// Check faculty profiles
echo "\nFaculty Profiles:\n";
$facultyProfiles = FacultyProfile::all();
foreach ($facultyProfiles as $fp) {
    echo "ID: {$fp->faculty_user_id}, Name: {$fp->faculty_name}, Department: " . 
         ($fp->department ? $fp->department->DepartmentName : 'N/A') . "\n";
}

// Check bookings
echo "\nBookings:\n";
$bookings = Booking::with(['slot.faculty.facultyProfile.department'])->get();
foreach ($bookings as $booking) {
    echo "Booking ID: {$booking->BookingId}\n";
    echo "  Slot: " . ($booking->slot ? $booking->slot->SlotId : 'N/A') . "\n";
    echo "  Faculty User ID: " . ($booking->slot && $booking->slot->faculty ? $booking->slot->faculty->UserId : 'N/A') . "\n";
    echo "  Faculty Profile: " . ($booking->slot && $booking->slot->faculty && $booking->slot->faculty->facultyProfile ? $booking->slot->faculty->facultyProfile->faculty_name : 'N/A') . "\n";
    echo "  Department: " . ($booking->slot && $booking->slot->faculty && $booking->slot->faculty->facultyProfile && $booking->slot->faculty->facultyProfile->department ? $booking->slot->faculty->facultyProfile->department->DepartmentName : 'N/A') . "\n";
    echo "  Purpose: {$booking->Purpose}\n";
    echo "  Status: {$booking->Status}\n";
    echo "---\n";
}

echo "\n=== Test Complete ===\n"; 