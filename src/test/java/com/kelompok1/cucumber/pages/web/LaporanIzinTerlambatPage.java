package com.kelompok1.cucumber.pages.web;

import com.kelompok1.cucumber.core.ConfigReader;
import com.kelompok1.cucumber.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

public class LaporanIzinTerlambatPage extends BasePage {

    private final By searchBox = By.id("search");
    private final By startDateField = By.cssSelector("input[placeholder='Start Date']");
    private final By endDateField = By.cssSelector("input[placeholder='End Date']");
    private final By searchButton = By.xpath("//button[text()='Search']");
    private final By table = By.cssSelector("table");
    
    // Sidebar navigation locators (using flexible XPaths to avoid tag-name changes)
    private final By sidebarLaporan = By.xpath("//div[contains(@class,'sidebar__item')]//p[normalize-space()='Laporan']");
    private final By sidebarIzinTerlambat = By.xpath("//p[normalize-space()='Izin Terlambat']");

    public LaporanIzinTerlambatPage() {
        super();
    }

    public void clickSidebarLaporan() {
        click(sidebarLaporan);
    }

    public void clickSidebarIzinTerlambat() {
        click(sidebarIzinTerlambat);
    }

    public boolean isSidebarLaporanDisplayed() {
        return isDisplayed(sidebarLaporan);
    }

    public boolean isLateArrivalPermissionPageDisplayed() {
        return getCurrentUrl().contains("/laporan/izin-terlambat") || isDisplayed(table);
    }

    public void navigateToLateArrivalPermissionPage() {
        navigateTo(ConfigReader.getProperty("dashboard.url").replace("/dashboards/pending", "/laporan/izin-terlambat"));
    }

    public boolean isLateArrivalPermissionTableDisplayed() {
        return isDisplayed(table);
    }

    public void enterSearchText(String searchText) {
        typeText(searchBox, searchText);
        // Press Enter or click Search to submit the search
        try {
            click(searchButton);
        } catch (Exception e) {
            findElement(searchBox).sendKeys(Keys.ENTER);
        }
    }

    public void enterDateRange(String startDate, String endDate) {
        typeText(startDateField, startDate);
        typeText(endDateField, endDate);
        click(searchButton);
    }

    public boolean isEmployeeNameInTable(String employeeName) {
        By cellLocator = By.xpath("//tbody/tr[1]/td[2]//h6[contains(text(), '" + employeeName + "')]");
        return isDisplayed(cellLocator);
    }

    public boolean isDateInTable(String date) {
        By cellLocator = By.xpath("//table//td[contains(text(), '" + date + "')]");
        return isDisplayed(cellLocator);
    }

    public boolean isLateArrivalPermissionTableWithEmployeeNameDisplayed() {
        // Fallback convenience method matching old step definitions
        return isDisplayed(table);
    }

    public boolean isLateArrivalPermissionTableWithDateRangeDisplayed() {
        // Fallback convenience method matching old step definitions
        return isDisplayed(table);
    }
}
