<?php

namespace App\Http\Controllers\Faculty;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\AvailableSlot;
use App\Models\OfficeHourDefinition;
use Illuminate\Support\Facades\Auth;
use Carbon\Carbon;

class SlotController extends Controller
{
    public function index()
    {
        $facultyId = Auth::id();
        
        $slots = AvailableSlot::where('faculty_user_id', $facultyId)
            ->with(['bookings.student'])
            ->orderBy('StartTime', 'desc')
            ->paginate(10);
        
        return view('faculty.slots.index', compact('slots'));
    }

    public function create()
    {
        $definitions = OfficeHourDefinition::all();
        return view('faculty.slots.create', compact('definitions'));
    }

    public function store(Request $request)
    {
        $request->validate([
            'start_time' => 'required|date|after:now',
            'end_time' => 'required|date|after:start_time',
            'max_students' => 'required|integer|min:1|max:10',
            'definition_id' => 'nullable|exists:office_hour_definitions,DefinitionId'
        ]);

        $facultyId = Auth::id();
        
        $slot = new AvailableSlot();
        $slot->faculty_user_id = $facultyId;
        $slot->StartTime = $request->start_time;
        $slot->EndTime = $request->end_time;
        $slot->MaxStudents = $request->max_students;
        $slot->IsAvailable = true;
        $slot->DefinitionId = $request->definition_id;
        $slot->save();

        return redirect()->route('faculty.slots.index')
            ->with('success', 'Slot office hours đã được tạo thành công!');
    }

    public function show($id)
    {
        $facultyId = Auth::id();
        
        $slot = AvailableSlot::where('SlotId', $id)
            ->where('faculty_user_id', $facultyId)
            ->with(['bookings.student'])
            ->firstOrFail();
        
        return view('faculty.slots.show', compact('slot'));
    }

    public function edit($id)
    {
        $facultyId = Auth::id();
        
        $slot = AvailableSlot::where('SlotId', $id)
            ->where('faculty_user_id', $facultyId)
            ->firstOrFail();
        
        $definitions = OfficeHourDefinition::all();
        
        return view('faculty.slots.edit', compact('slot', 'definitions'));
    }

    public function update(Request $request, $id)
    {
        $facultyId = Auth::id();
        
        $slot = AvailableSlot::where('SlotId', $id)
            ->where('faculty_user_id', $facultyId)
            ->firstOrFail();

        $request->validate([
            'start_time' => 'required|date',
            'end_time' => 'required|date|after:start_time',
            'max_students' => 'required|integer|min:1|max:10',
            'is_available' => 'boolean',
            'definition_id' => 'nullable|exists:office_hour_definitions,DefinitionId'
        ]);

        $slot->StartTime = $request->start_time;
        $slot->EndTime = $request->end_time;
        $slot->MaxStudents = $request->max_students;
        $slot->IsAvailable = $request->has('is_available');
        $slot->DefinitionId = $request->definition_id;
        $slot->save();

        return redirect()->route('faculty.slots.index')
            ->with('success', 'Slot office hours đã được cập nhật thành công!');
    }

    public function destroy($id)
    {
        $facultyId = Auth::id();
        
        $slot = AvailableSlot::where('SlotId', $id)
            ->where('faculty_user_id', $facultyId)
            ->firstOrFail();

        // Kiểm tra xem có booking nào đang pending không
        $pendingBookings = $slot->bookings()->where('Status', 'pending')->count();
        if ($pendingBookings > 0) {
            return redirect()->route('faculty.slots.index')
                ->with('error', 'Không thể xóa slot vì có booking đang chờ xử lý!');
        }

        $slot->delete();

        return redirect()->route('faculty.slots.index')
            ->with('success', 'Slot office hours đã được xóa thành công!');
    }

    public function toggleAvailability($id)
    {
        $facultyId = Auth::id();
        
        $slot = AvailableSlot::where('SlotId', $id)
            ->where('faculty_user_id', $facultyId)
            ->firstOrFail();

        $slot->IsAvailable = !$slot->IsAvailable;
        $slot->save();

        $status = $slot->IsAvailable ? 'mở' : 'đóng';
        
        return redirect()->route('faculty.slots.index')
            ->with('success', "Slot đã được {$status} thành công!");
    }
} 