<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::table('office_hour_definitions', function (Blueprint $table) {
            $table->unsignedBigInteger('faculty_user_id')->after('DefinitionId');
        });
    }

    public function down(): void
    {
        Schema::table('office_hour_definitions', function (Blueprint $table) {
            $table->dropColumn('faculty_user_id');
        });
    }
}; 