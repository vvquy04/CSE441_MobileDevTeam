<?php

namespace App\Http\Controllers\Faculty;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\OfficeHourDefinition;
use Illuminate\Support\Facades\Auth;

class OfficeHourDefinitionController extends Controller
{
    public function index()
    {
        $facultyId = Auth::id();
        
        $definitions = OfficeHourDefinition::where('faculty_user_id', $facultyId)
            ->orderBy('DayOfWeek')
            ->orderBy('StartTime')
            ->get();
        
        return view('faculty.definitions.index', compact('definitions'));
    }

    public function create()
    {
        return view('faculty.definitions.create');
    }

    public function store(Request $request)
    {
        $request->validate([
            'day_of_week' => 'required|integer|between:0,6',
            'start_time' => 'required|date_format:H:i',
            'end_time' => 'required|date_format:H:i|after:start_time',
            'max_students' => 'required|integer|min:1|max:10',
            'is_active' => 'boolean'
        ]);

        $facultyId = Auth::id();
        
        // Kiểm tra xem đã có definition cho ngày này chưa
        $existingDefinition = OfficeHourDefinition::where('faculty_user_id', $facultyId)
            ->where('DayOfWeek', $request->day_of_week)
            ->first();
            
        if ($existingDefinition) {
            return redirect()->route('faculty.definitions.index')
                ->with('error', 'Đã có định nghĩa office hours cho ngày này!');
        }

        $definition = new OfficeHourDefinition();
        $definition->faculty_user_id = $facultyId;
        $definition->DayOfWeek = $request->day_of_week;
        $definition->StartTime = $request->start_time;
        $definition->EndTime = $request->end_time;
        $definition->MaxStudents = $request->max_students;
        $definition->IsActive = $request->has('is_active');
        $definition->save();

        return redirect()->route('faculty.definitions.index')
            ->with('success', 'Định nghĩa office hours đã được tạo thành công!');
    }

    public function edit($id)
    {
        $facultyId = Auth::id();
        
        $definition = OfficeHourDefinition::where('DefinitionId', $id)
            ->where('faculty_user_id', $facultyId)
            ->firstOrFail();
        
        return view('faculty.definitions.edit', compact('definition'));
    }

    public function update(Request $request, $id)
    {
        $facultyId = Auth::id();
        
        $definition = OfficeHourDefinition::where('DefinitionId', $id)
            ->where('faculty_user_id', $facultyId)
            ->firstOrFail();

        $request->validate([
            'day_of_week' => 'required|integer|between:0,6',
            'start_time' => 'required|date_format:H:i',
            'end_time' => 'required|date_format:H:i|after:start_time',
            'max_students' => 'required|integer|min:1|max:10',
            'is_active' => 'boolean'
        ]);

        // Kiểm tra xem có definition khác cho ngày này không (trừ definition hiện tại)
        $existingDefinition = OfficeHourDefinition::where('faculty_user_id', $facultyId)
            ->where('DayOfWeek', $request->day_of_week)
            ->where('DefinitionId', '!=', $id)
            ->first();
            
        if ($existingDefinition) {
            return redirect()->route('faculty.definitions.index')
                ->with('error', 'Đã có định nghĩa office hours cho ngày này!');
        }

        $definition->DayOfWeek = $request->day_of_week;
        $definition->StartTime = $request->start_time;
        $definition->EndTime = $request->end_time;
        $definition->MaxStudents = $request->max_students;
        $definition->IsActive = $request->has('is_active');
        $definition->save();

        return redirect()->route('faculty.definitions.index')
            ->with('success', 'Định nghĩa office hours đã được cập nhật thành công!');
    }

    public function destroy($id)
    {
        $facultyId = Auth::id();
        
        $definition = OfficeHourDefinition::where('DefinitionId', $id)
            ->where('faculty_user_id', $facultyId)
            ->firstOrFail();

        // Kiểm tra xem có slot nào đang sử dụng definition này không
        $activeSlots = $definition->slots()->where('IsAvailable', true)->count();
        if ($activeSlots > 0) {
            return redirect()->route('faculty.definitions.index')
                ->with('error', 'Không thể xóa vì có slot đang sử dụng định nghĩa này!');
        }

        $definition->delete();

        return redirect()->route('faculty.definitions.index')
            ->with('success', 'Định nghĩa office hours đã được xóa thành công!');
    }

    public function toggleActive($id)
    {
        $facultyId = Auth::id();
        
        $definition = OfficeHourDefinition::where('DefinitionId', $id)
            ->where('faculty_user_id', $facultyId)
            ->firstOrFail();

        $definition->IsActive = !$definition->IsActive;
        $definition->save();

        $status = $definition->IsActive ? 'kích hoạt' : 'vô hiệu hóa';
        
        return redirect()->route('faculty.definitions.index')
            ->with('success', "Định nghĩa đã được {$status} thành công!");
    }

    public function generateSlots()
    {
        $facultyId = Auth::id();
        
        $activeDefinitions = OfficeHourDefinition::where('faculty_user_id', $facultyId)
            ->where('IsActive', true)
            ->get();

        $generatedCount = 0;
        
        foreach ($activeDefinitions as $definition) {
            // Tạo slots cho tuần hiện tại
            $startOfWeek = now()->startOfWeek();
            
            for ($i = 0; $i < 7; $i++) {
                $date = $startOfWeek->copy()->addDays($i);
                
                if ($date->dayOfWeek == $definition->DayOfWeek) {
                    // Tạo slot cho ngày này
                    $slot = new \App\Models\AvailableSlot();
                    $slot->faculty_user_id = $facultyId;
                    $slot->StartTime = $date->copy()->setTimeFrom($definition->StartTime);
                    $slot->EndTime = $date->copy()->setTimeFrom($definition->EndTime);
                    $slot->MaxStudents = $definition->MaxStudents;
                    $slot->IsAvailable = true;
                    $slot->DefinitionId = $definition->DefinitionId;
                    $slot->save();
                    
                    $generatedCount++;
                }
            }
        }

        return redirect()->route('faculty.definitions.index')
            ->with('success', "Đã tạo {$generatedCount} slot office hours từ các định nghĩa!");
    }
} 