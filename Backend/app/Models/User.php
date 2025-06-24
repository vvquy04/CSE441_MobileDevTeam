<?php

namespace App\Models;

// use Illuminate\Contracts\Auth\MustVerifyEmail;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Foundation\Auth\User as Authenticatable;
use Illuminate\Notifications\Notifiable;
use Laravel\Sanctum\HasApiTokens;
use App\Models\Role;
use App\Models\UserRole;
use App\Models\FacultyProfile;
use App\Models\StudentProfile;

class User extends Authenticatable
{
    /** @use HasFactory<\Database\Factories\UserFactory> */
    use HasFactory, Notifiable, HasApiTokens;

    protected $primaryKey = 'UserId';

    /**
     * The attributes that are mass assignable.
     *
     * @var array<int, string>
     */
    protected $fillable = [
        'email',
        'password',
    ];

    /**
     * The attributes that should be hidden for serialization.
     *
     * @var array<int, string>
     */
    protected $hidden = [
        'password',
        'remember_token',
    ];

    /**
     * Get the attributes that should be cast.
     *
     * @return array<string, string>
     */
    protected function casts(): array
    {
        return [
            'email_verified_at' => 'datetime',
            'password' => 'hashed',
        ];
    }

    public function roles()
    {
        return $this->belongsToMany(Role::class, 'user_roles', 'UserId', 'RoleId')
            ->withTimestamps();
    }

    public function userRoles()
    {
        return $this->hasMany(UserRole::class, 'UserId', 'UserId');
    }

    public function hasRole($roleName)
    {
        return $this->roles()
            ->where('roles.RoleName', $roleName)
            ->exists();
    }

    public function facultyProfile()
    {
        return $this->hasOne(FacultyProfile::class, 'faculty_user_id', 'UserId');
    }

    public function studentProfile()
    {
        return $this->hasOne(StudentProfile::class, 'StudentUserId', 'UserId');
    }
}
