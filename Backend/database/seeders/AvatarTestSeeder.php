<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\User;
use App\Models\StudentProfile;
use App\Models\FacultyProfile;
use App\Models\Role;
use Illuminate\Support\Facades\Hash;

class AvatarTestSeeder extends Seeder
{
    public function run()
    {
        // Tạo role nếu chưa có
        $studentRole = Role::firstOrCreate(['RoleName' => 'student']);
        $facultyRole = Role::firstOrCreate(['RoleName' => 'faculty']);

        // Tạo user sinh viên test
        $studentUser = User::firstOrCreate(
            ['email' => 'test.student@e.tlu.edu.vn'],
            [
                'email' => 'test.student@e.tlu.edu.vn',
                'password' => Hash::make('123456'),
            ]
        );

        // Gán role cho user
        if (!$studentUser->roles()->where('roles.RoleId', $studentRole->RoleId)->exists()) {
            $studentUser->roles()->attach($studentRole->RoleId);
        }

        // Tạo profile sinh viên với avatar
        StudentProfile::firstOrCreate(
            ['StudentUserId' => $studentUser->UserId],
            [
                'StudentUserId' => $studentUser->UserId,
                'StudentName' => 'Nguyễn Văn A',
                'StudentCode' => '2251172469',
                'ClassName' => 'CNTT-K65',
                'PhoneNumber' => '0123456789',
                'avatar' => 'avatars/student_avatar.jpg', // Placeholder path
            ]
        );

        // Tạo user giảng viên test
        $facultyUser = User::firstOrCreate(
            ['email' => 'test.faculty@tlu.edu.vn'],
            [
                'email' => 'test.faculty@tlu.edu.vn',
                'password' => Hash::make('123456'),
            ]
        );

        // Gán role cho user
        if (!$facultyUser->roles()->where('roles.RoleId', $facultyRole->RoleId)->exists()) {
            $facultyUser->roles()->attach($facultyRole->RoleId);
        }

        // Tạo profile giảng viên với avatar
        FacultyProfile::firstOrCreate(
            ['faculty_user_id' => $facultyUser->UserId],
            [
                'faculty_user_id' => $facultyUser->UserId,
                'faculty_name' => 'ThS. Trần Thị B',
                'department_id' => 'DEPT001',
                'degree' => 'Thạc sĩ',
                'phone_number' => '0987654321',
                'office_location' => 'Phòng 101',
                'avatar' => 'avatars/faculty_avatar.jpg', // Placeholder path
            ]
        );

        echo "Test data created successfully!\n";
        echo "Student: test.student@e.tlu.edu.vn / 123456\n";
        echo "Faculty: test.faculty@tlu.edu.vn / 123456\n";
    }
} 