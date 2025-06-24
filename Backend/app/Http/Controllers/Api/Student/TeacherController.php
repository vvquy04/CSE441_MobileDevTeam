<?php

namespace App\Http\Controllers\Api\Student;

use App\Http\Controllers\Controller;
use App\Models\FacultyProfile;
use App\Models\Department;
use Illuminate\Http\Request;

class TeacherController extends Controller
{
    public function getFeaturedTeachers()
    {
        $teachers = FacultyProfile::with(['department', 'user'])
            ->select('faculty_profiles.*')
            ->join('departments', 'faculty_profiles.department_id', '=', 'departments.DepartmentId')
            ->join('users', 'faculty_profiles.faculty_user_id', '=', 'users.UserId')
            ->select([
                'faculty_profiles.faculty_user_id',
                'faculty_profiles.faculty_name',
                'faculty_profiles.degree',
                'faculty_profiles.phone_number',
                'faculty_profiles.office_location',
                'faculty_profiles.avatar',
                'departments.DepartmentName as department_name',
                'users.email'
            ])
            ->limit(5)
            ->get()
            ->map(function ($teacher) {
                $teacher->avatar_url = $teacher->avatar ? 'http://10.0.2.2:8000/storage/' . $teacher->avatar : null;
                return $teacher;
            });

        return response()->json($teachers);
    }

    public function getTeachersByDepartment($departmentId)
    {
        $teachers = FacultyProfile::with(['department', 'user'])
            ->where('department_id', $departmentId)
            ->select([
                'faculty_profiles.faculty_user_id',
                'faculty_profiles.faculty_name',
                'faculty_profiles.degree',
                'faculty_profiles.phone_number',
                'faculty_profiles.office_location',
                'faculty_profiles.avatar',
                'departments.DepartmentName as department_name',
                'users.email'
            ])
            ->join('departments', 'faculty_profiles.department_id', '=', 'departments.DepartmentId')
            ->join('users', 'faculty_profiles.faculty_user_id', '=', 'users.UserId')
            ->get()
            ->map(function ($teacher) {
                $teacher->avatar_url = $teacher->avatar ? 'http://10.0.2.2:8000/storage/' . $teacher->avatar : null;
                return $teacher;
            });

        return response()->json($teachers);
    }

    public function searchTeachers(Request $request)
    {
        $query = $request->input('query');
        $teachers = FacultyProfile::with(['department', 'user'])
            ->join('departments', 'faculty_profiles.department_id', '=', 'departments.DepartmentId')
            ->join('users', 'faculty_profiles.faculty_user_id', '=', 'users.UserId')
            ->select([
                'faculty_profiles.faculty_user_id',
                'faculty_profiles.faculty_name',
                'faculty_profiles.degree',
                'faculty_profiles.phone_number',
                'faculty_profiles.office_location',
                'faculty_profiles.avatar',
                'departments.DepartmentName as department_name',
                'users.email'
            ])
            ->when($query, function ($q) use ($query) {
                $q->where('faculty_profiles.faculty_name', 'like', '%' . $query . '%');
            })
            ->get()
            ->map(function ($teacher) {
                $teacher->avatar_url = $teacher->avatar ? 'http://10.0.2.2:8000/storage/' . $teacher->avatar : null;
                return $teacher;
            });

        return response()->json($teachers);
    }

    public function getTeacherDetail($facultyUserId)
    {
        $teacher = FacultyProfile::with(['department', 'user'])
            ->join('departments', 'faculty_profiles.department_id', '=', 'departments.DepartmentId')
            ->join('users', 'faculty_profiles.faculty_user_id', '=', 'users.UserId')
            ->select([
                'faculty_profiles.faculty_user_id',
                'faculty_profiles.faculty_name',
                'faculty_profiles.degree',
                'faculty_profiles.phone_number',
                'faculty_profiles.office_location',
                'faculty_profiles.avatar',
                'departments.DepartmentName as department_name',
                'users.email'
            ])
            ->where('faculty_profiles.faculty_user_id', $facultyUserId)
            ->first();

        if (!$teacher) {
            return response()->json(['message' => 'Teacher not found'], 404);
        }

        $teacher->avatar_url = $teacher->avatar ? 'http://10.0.2.2:8000/storage/' . $teacher->avatar : null;

        return response()->json($teacher);
    }
} 