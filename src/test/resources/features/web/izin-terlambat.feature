@Laporan @IzinTerlambat
Feature: Menu Izin Terlambat
    As an authorized user
    I want to access the Late Arrival Permission menu
    So that I can view employee late arrival permission requests

    Background:
        Given user is on the web login page
        When user logs in with valid web credentials
        Then web user should be redirected to the dashboard
        And web user should see "Laporan" in the sidebar
        And web user should click on "Laporan" in the sidebar
        And web user should click on "Izin Terlambat" in the sidebar
        Then web user should see the late arrival permission page

    @positive
    Scenario: View late arrival permission list
        Given user is on the late arrival permission page
        When user should see the late arrival permission table
        Then web user should see the late arrival permission table

    @positive
    Scenario: Filter late arrival permission by employee name
        Given user is on the late arrival permission page
        When user input "kelompoksatu" in search box
        Then web user should see the late arrival permission table with employee name "kelompoksatu"

    @positive @test
    Scenario: Filter late arrival permission by date range
        Given user is on the late arrival permission page
        When user input "2022-01-01" to "2022-12-31" in date range filter
        Then web user should see the late arrival permission table with date range "2022-01-01" to "2022-12-31"

