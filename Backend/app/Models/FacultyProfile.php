<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class FacultyProfile extends Model
{
    use HasFactory;

    protected $primaryKey = 'FacultyUserId';
    protected $fillable = [
        'FacultyUserId',
        'DepartmentId',
        'OfficeLocation',
        'Bio'
    ];

    public function user()
    {
        return $this->belongsTo(User::class, 'FacultyUserId', 'UserId');
    }

    public function department()
    {
        return $this->belongsTo(Department::class, 'DepartmentId', 'DepartmentId');
    }
} 