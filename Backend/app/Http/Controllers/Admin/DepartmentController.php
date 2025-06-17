<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use App\Models\Department;
use Illuminate\Http\Request;

class DepartmentController extends Controller
{
    public function index()
    {
        $departments = Department::paginate(10);
        return view('admin.departments.index', compact('departments'));
    }

    public function create()
    {
        return view('admin.departments.create');
    }

    public function store(Request $request)
    {
        $validated = $request->validate([
            'DepartmentId' => 'required|string|max:255|unique:departments,DepartmentId',
            'DepartmentName' => 'required|string|max:255'
        ]);

        Department::create([
            'DepartmentId' => $validated['DepartmentId'],
            'DepartmentName' => $validated['DepartmentName']
        ]);

        return redirect()->route('departments.index')
            ->with('success', 'Bộ môn đã được thêm thành công.');
    }

    public function update(Request $request, Department $department)
    {
        $validated = $request->validate([
            'DepartmentName' => 'required|string|max:255|unique:departments,DepartmentName,' . $department->DepartmentId . ',DepartmentId'
        ]);

        $department->update(['DepartmentName' => $validated['DepartmentName']]);

        return redirect()->route('departments.index')
            ->with('success', 'Bộ môn đã được cập nhật thành công.');
    }

    public function destroy(Department $department)
    {
        $department->delete();
        return redirect()->route('departments.index')
            ->with('success', 'Bộ môn đã được xóa thành công.');
    }
} 