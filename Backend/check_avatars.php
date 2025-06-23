<?php

require_once 'vendor/autoload.php';

$app = require_once 'bootstrap/app.php';
$app->make('Illuminate\Contracts\Console\Kernel')->bootstrap();

use App\Models\StudentProfile;
use App\Models\FacultyProfile;

echo "Student profiles with avatars:\n";
StudentProfile::whereNotNull('avatar')->get()->each(function($student) {
    echo "- Student ID: " . $student->StudentUserId . "\n";
    echo "  Name: " . $student->StudentName . "\n";
    echo "  Avatar path: " . $student->avatar . "\n";
    echo "  Full URL: " . asset('storage/' . $student->avatar) . "\n";
    echo "  File exists: " . (file_exists(storage_path('app/public/' . $student->avatar)) ? 'YES' : 'NO') . "\n\n";
});

echo "Faculty profiles with avatars:\n";
FacultyProfile::whereNotNull('avatar')->get()->each(function($faculty) {
    echo "- Faculty ID: " . $faculty->faculty_user_id . "\n";
    echo "  Name: " . $faculty->faculty_name . "\n";
    echo "  Avatar path: " . $faculty->avatar . "\n";
    echo "  Full URL: " . asset('storage/' . $faculty->avatar) . "\n";
    echo "  File exists: " . (file_exists(storage_path('app/public/' . $faculty->avatar)) ? 'YES' : 'NO') . "\n\n";
}); 