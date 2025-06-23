<?php
require_once 'vendor/autoload.php';
$app = require_once 'bootstrap/app.php';
$app->make('Illuminate\Contracts\Console\Kernel')->bootstrap();

$student = \App\Models\StudentProfile::whereNotNull('avatar')->first();
if($student) {
    echo "Student: " . $student->StudentName . "\n";
    echo "Avatar: " . $student->avatar . "\n";
    echo "File exists: " . (file_exists(storage_path('app/public/' . $student->avatar)) ? 'YES' : 'NO') . "\n";
} else {
    echo "No student with avatar found\n";
} 