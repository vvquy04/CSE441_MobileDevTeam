<?php

require_once 'vendor/autoload.php';

$app = require_once 'bootstrap/app.php';
$app->make('Illuminate\Contracts\Console\Kernel')->bootstrap();

use App\Models\FacultyProfile;
use App\Models\Department;
use App\Models\StudentProfile;

echo "Faculty profiles: " . FacultyProfile::count() . "\n";
echo "Departments: " . Department::count() . "\n";
echo "Student profiles: " . StudentProfile::count() . "\n";

if (FacultyProfile::count() > 0) {
    echo "\nFaculty profiles:\n";
    FacultyProfile::all()->each(function($faculty) {
        echo "- " . $faculty->faculty_name . " (ID: " . $faculty->faculty_user_id . ")\n";
    });
}

if (Department::count() > 0) {
    echo "\nDepartments:\n";
    Department::all()->each(function($dept) {
        echo "- " . $dept->DepartmentName . " (ID: " . $dept->DepartmentId . ")\n";
    });
} 