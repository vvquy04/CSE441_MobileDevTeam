<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration {
    public function up()
    {
        Schema::table('office_hour_definitions', function (Blueprint $table) {
            $table->renameColumn('FacultyUserId', 'faculty_user_id');
        });
    }

    public function down()
    {
        Schema::table('office_hour_definitions', function (Blueprint $table) {
            $table->renameColumn('faculty_user_id', 'FacultyUserId');
        });
    }
}; 