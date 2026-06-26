@login @regression @web
Feature: Login to Hadir Web Admin Panel
  As an admin of Hadir
  I want to log in to the web admin panel
  So that I can manage the attendance system

  Background:
    Given user is on the web login page

  @smoke @positive
  Scenario: Successful login with valid credentials
    When user logs in with valid web credentials
    Then web user should be redirected to the dashboard

  @negative
  Scenario: Login with empty email and password
    When user logs in to web with email "" and password ""
    Then web error message should be displayed
    And web error message should contain "Akun tidak ditemukan"
    And web user should remain on the login page

  @negative
  Scenario: Login with valid email but wrong password
    When user logs in to web with email "admin@hadir.com" and password "wrongpassword"
    Then web error message should be displayed
    And web error message should contain "Email atau password salah"
    And web user should remain on the login page

  @negative
  Scenario: Login with email missing @ symbol
    When user enters web email "adminhaidircom" and password "password" without submitting
    Then web browser validation should contain "Please include an '@' in the email address"
    And web user should remain on the login page
