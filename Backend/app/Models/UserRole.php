<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class UserRole extends Model
{
    use HasFactory;

    protected $primaryKey = 'UserRoleId';
    protected $fillable = [
        'UserId',
        'RoleId'
    ];

    public function user()
    {
        return $this->belongsTo(User::class, 'UserId', 'UserId');
    }

    public function role()
    {
        return $this->belongsTo(Role::class, 'RoleId', 'RoleId');
    }
} 