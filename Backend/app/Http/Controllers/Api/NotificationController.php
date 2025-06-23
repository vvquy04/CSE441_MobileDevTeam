<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\Notification;

class NotificationController extends Controller
{
    // Lấy danh sách notification cho user
    public function index(Request $request)
    {
        $notifications = Notification::where('user_id', $request->user()->UserId)
            ->orderBy('created_at', 'desc')
            ->get();

        return response()->json($notifications);
    }

    // Đánh dấu đã đọc
    public function markAsRead($id, Request $request)
    {
        $notification = Notification::where('id', $id)
            ->where('user_id', $request->user()->UserId)
            ->firstOrFail();

        $notification->is_read = true;
        $notification->save();

        return response()->json(['success' => true]);
    }

    // Tạo notification (dùng cho test/demo)
    public function store(Request $request)
    {
        $data = $request->validate([
            'user_id' => 'required|integer|exists:users,UserId',
            'title' => 'required|string',
            'message' => 'required|string',
            'type' => 'nullable|string',
        ]);
        $notification = Notification::create($data);
        return response()->json($notification, 201);
    }
} 