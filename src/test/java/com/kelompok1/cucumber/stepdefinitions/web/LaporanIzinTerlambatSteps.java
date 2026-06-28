package com.kelompok1.cucumber.stepdefinitions.web;

import com.kelompok1.cucumber.pages.web.LaporanIzinTerlambatPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.testng.Assert;

public class LaporanIzinTerlambatSteps {

    private final LaporanIzinTerlambatPage page = new LaporanIzinTerlambatPage();

    @And("web user should see {string} in the sidebar")
    public void webUserShouldSeeInTheSidebar(String menuName) {
        if ("Laporan".equalsIgnoreCase(menuName)) {
            Assert.assertTrue(page.isSidebarLaporanDisplayed(), "Sidebar menu 'Laporan' is not displayed.");
        }
    }

    @And("web user should click on {string} in the sidebar")
    public void webUserShouldClickOnInTheSidebar(String menuName) {
        if ("Laporan".equalsIgnoreCase(menuName)) {
            page.clickSidebarLaporan();
        } else if ("Izin Terlambat".equalsIgnoreCase(menuName)) {
            page.clickSidebarIzinTerlambat();
        }
    }

    @Then("web user should see the late arrival permission page")
    public void webUserShouldSeeTheLateArrivalPermissionPage() {
        Assert.assertTrue(page.isLateArrivalPermissionPageDisplayed(), 
            "Laporan Izin Terlambat page is not displayed.");
    }

    @Given("user is on the late arrival permission page")
    public void userIsOnTheLateArrivalPermissionPage() {
        page.navigateToLateArrivalPermissionPage();
        Assert.assertTrue(page.isLateArrivalPermissionPageDisplayed(), 
            "Laporan Izin Terlambat page is not displayed.");
    }

    @When("user should see the late arrival permission table")
    public void userShouldSeeTheLateArrivalPermissionTable() {
        Assert.assertTrue(page.isLateArrivalPermissionTableDisplayed(), 
            "Late arrival permission table is not displayed.");
    }

    @Then("web user should see the late arrival permission table")
    public void webUserShouldSeeTheLateArrivalPermissionTable() {
        Assert.assertTrue(page.isLateArrivalPermissionTableDisplayed(), 
            "Late arrival permission table is not displayed.");
    }

    @When("user input {string} in search box")
    public void userInputInSearchBox(String searchText) {
        page.enterSearchText(searchText);
    }

    @Then("web user should see the late arrival permission table with employee name {string}")
    public void webUserShouldSeeTheLateArrivalPermissionTableWithEmployeeName(String employeeName) {
        Assert.assertTrue(page.isEmployeeNameInTable(employeeName), 
            "Expected employee name '" + employeeName + "' in table, but it was not found.");
    }

    @When("user input {string} to {string} in date range filter")
    public void userInputToInDateRangeFilter(String startDate, String endDate) {
        page.enterDateRange(startDate, endDate);
    }

    @Then("web user should see the late arrival permission table with date range {string} to {string}")
    public void webUserShouldSeeTheLateArrivalPermissionTableWithDateRangeTo(String startDate, String endDate) {
        Assert.assertTrue(page.isLateArrivalPermissionTableDisplayed(), 
            "Late arrival permission table is not displayed.");
    }
}
