package com.example.taller2

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest


object SupabaseClient {

    val client = createSupabaseClient(
        supabaseUrl = "https://fkjfjuszqoejwvyijwao.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImZramZqdXN6cW9land2eWlqd2FvIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzU2MDE5MDcsImV4cCI6MjA5MTE3NzkwN30.R-sETOwqthONS5pmc6iuhOxefP18QODraaajDYCoX6s"

    ){
        install(Postgrest)
        install(Auth)
    }
}