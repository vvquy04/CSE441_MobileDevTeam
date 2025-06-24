<?php

namespace App\Http\Controllers\Faculty;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\FacultyProfile;
use App\Models\Department;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\Storage;

class ProfileController extends Controller
{
    public function show()
    {
        $facultyId = Auth::id();
        $profile = FacultyProfile::with(['department', 'user'])
            ->where('faculty_user_id', $facultyId)
            ->first();
        
        $departments = Department::all();
        
        return view('faculty.profile.show', compact('profile', 'departments'));
    }

    public function edit()
    {
        $facultyId = Auth::id();
        $profile = FacultyProfile::with(['department', 'user'])
            ->where('faculty_user_id', $facultyId)
            ->first();
        
        $departments = Department::all();
        
        return view('faculty.profile.edit', compact('profile', 'departments'));
    }

    public function update(Request $request)
    {
        $facultyId = Auth::id();
        
        $request->validate([
            'faculty_name' => 'required|string|max:255',
            'department_id' => 'required|exists:departments,DepartmentId',
            'degree' => 'required|string|max:255',
            'phone_number' => 'required|string|max:20',
            'office_location' => 'required|string|max:255',
            'avatar' => 'nullable|image|mimes:jpeg,png,jpg,gif|max:2048'
        ]);

        $profile = FacultyProfile::where('faculty_user_id', $facultyId)->first();
        
        if (!$profile) {
            $profile = new FacultyProfile();
            $profile->faculty_user_id = $facultyId;
        }

        $profile->faculty_name = $request->faculty_name;
        $profile->department_id = $request->department_id;
        $profile->degree = $request->degree;
        $profile->phone_number = $request->phone_number;
        $profile->office_location = $request->office_location;

        // Xử lý upload avatar
        if ($request->hasFile('avatar')) {
            // Xóa avatar cũ nếu có
            if ($profile->avatar && Storage::disk('public')->exists($profile->avatar)) {
                Storage::disk('public')->delete($profile->avatar);
            }
            
            $avatarPath = $request->file('avatar')->store('faculty-avatars', 'public');
            $profile->avatar = $avatarPath;
        }

        $profile->save();

        return redirect()->route('faculty.profile.show')
            ->with('success', 'Thông tin profile đã được cập nhật thành công!');
    }
} 