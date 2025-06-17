<?php

namespace App\Http\Middleware;

use Closure;
use Illuminate\Http\Request;
use App\Models\Role;

class AdminMiddleware
{
    public function handle(Request $request, Closure $next)
    {
        if (!auth()->check()) {
            return redirect('/admin/login')->with('error', 'Unauthorized access');
        }

        $user = auth()->user();
        $adminRole = Role::where('RoleName', 'admin')->first();

        if (!$adminRole || !$user->roles()->where('roles.RoleId', $adminRole->RoleId)->exists()) {
            return redirect('/admin/login')->with('error', 'Unauthorized access');
        }

        return $next($request);
    }
} 