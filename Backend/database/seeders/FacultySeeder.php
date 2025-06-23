<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\User;
use App\Models\FacultyProfile;
use App\Models\Department;
use App\Models\Role;
use App\Models\UserRole;
use Illuminate\Support\Facades\Hash;

class FacultySeeder extends Seeder
{
    public function run()
    {
        // Tạo role faculty nếu chưa có
        $facultyRole = Role::where('RoleName', 'faculty')->first();
        if (!$facultyRole) {
            $facultyRole = Role::create([
                'RoleName' => 'faculty'
            ]);
        }

        // Tạo departments với mã thực tế
        $departments = [
            ['DepartmentId' => 'CNPM', 'DepartmentName' => 'Công nghệ phần mềm'],
            ['DepartmentId' => 'HTTT', 'DepartmentName' => 'Hệ thống thông tin'],
            ['DepartmentId' => 'ANM', 'DepartmentName' => 'Mạng và an toàn bảo mật'],
            ['DepartmentId' => 'TTNT', 'DepartmentName' => 'Trí tuệ nhân tạo'],
        ];

        foreach ($departments as $dept) {
            Department::firstOrCreate(
                ['DepartmentId' => $dept['DepartmentId']],
                $dept
            );
        }

        // Tạo giảng viên mẫu với mã bộ môn thực tế
        $facultyData = [
            [
                'email' => 'thay.nguyen@tlu.edu.vn',
                'faculty_name' => 'ThS. Nguyễn Văn A',
                'department_id' => 'CNPM',
                'degree' => 'Thạc sĩ',
                'phone_number' => '0987654321',
                'office_location' => 'Phòng 101'
            ],
            [
                'email' => 'thay.tran@tlu.edu.vn',
                'faculty_name' => 'TS. Trần Thị B',
                'department_id' => 'HTTT',
                'degree' => 'Tiến sĩ',
                'phone_number' => '0987654322',
                'office_location' => 'Phòng 102'
            ],
            [
                'email' => 'thay.le@tlu.edu.vn',
                'faculty_name' => 'ThS. Lê Văn C',
                'department_id' => 'ANM',
                'degree' => 'Thạc sĩ',
                'phone_number' => '0987654323',
                'office_location' => 'Phòng 103'
            ],
            [
                'email' => 'thay.pham@tlu.edu.vn',
                'faculty_name' => 'TS. Phạm Thị D',
                'department_id' => 'TTNT',
                'degree' => 'Tiến sĩ',
                'phone_number' => '0987654324',
                'office_location' => 'Phòng 104'
            ]
        ];

        foreach ($facultyData as $data) {
            // Tạo user
            $user = User::firstOrCreate(
                ['email' => $data['email']],
                [
                    'email' => $data['email'],
                    'password' => Hash::make('123456'),
                    'email_verified_at' => now()
                ]
            );

            // Gán role faculty
            UserRole::firstOrCreate(
                [
                    'UserId' => $user->UserId,
                    'RoleId' => $facultyRole->RoleId
                ],
                [
                    'UserId' => $user->UserId,
                    'RoleId' => $facultyRole->RoleId
                ]
            );

            // Tạo faculty profile
            FacultyProfile::firstOrCreate(
                ['faculty_user_id' => $user->UserId],
                [
                    'faculty_user_id' => $user->UserId,
                    'faculty_name' => $data['faculty_name'],
                    'department_id' => $data['department_id'],
                    'degree' => $data['degree'],
                    'phone_number' => $data['phone_number'],
                    'office_location' => $data['office_location']
                ]
            );
        }

        echo "Faculty data seeded successfully!\n";
    }
} 