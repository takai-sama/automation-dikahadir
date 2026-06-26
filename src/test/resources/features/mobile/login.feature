@login @regression @mobile
Feature: Login to Hadir Mobile Mirroring Panel
  As a user of Hadir
  I want to log in to the mobile mirroring panel
  So that I can access the attendance system

  Background:
    Given user is on the mobile login page

  @smoke @positive
  Scenario: Successful login with valid credentials
    When user logs in with valid mobile credentials
    Then mobile user should be redirected to the dashboard

  @negative
  Scenario: Login with empty email and password
    When user logs in to mobile with email "" and password ""
    Then mobile error message should be displayed
    And mobile error message should contain "Akun tidak ditemukan"
    And mobile user should remain on the login page

  @negative
  Scenario: Login with valid email but wrong password
    When user logs in to mobile with email "hadirsqa1@gmail.com" and password "wrongpassword"
    Then mobile error message should be displayed
    And mobile error message should contain "Email atau password salah"
    And mobile user should remain on the login page

  @negative
  Scenario: Login with email missing @ symbol
    When user enters mobile email "hadirsqa1gmailcom" and password "password" without submitting
    Then mobile browser validation should contain "Please include an '@' in the email address"
    And mobile user should remain on the login page
