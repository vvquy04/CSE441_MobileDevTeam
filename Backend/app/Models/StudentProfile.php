<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class StudentProfile extends Model
{
    use HasFactory;

    protected $primaryKey = 'StudentUserId';
    protected $fillable = [
        'StudentUserId',
        'StudentName',
        'StudentCode',
        'ClassName',
        'PhoneNumber',
        'avatar'
    ];

    public function user()
    {
        return $this->belongsTo(User::class, 'StudentUserId', 'UserId');
    }

    public function department()
    {
        return $this->belongsTo(Department::class, 'DepartmentId', 'DepartmentId');
    }
} 