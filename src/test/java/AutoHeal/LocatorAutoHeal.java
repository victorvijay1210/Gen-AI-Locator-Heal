package AutoHeal;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;

import java.io.File;
import java.util.*;

public class LocatorAutoHeal {
    private static Map<String, List<By>> locatorFallbackMap = new HashMap<>();

    static {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, List<LocatorEntry>> rawMap = mapper.readValue(
                    new File("src/test/java/JsonFile/GPTLocator.json"),
                    new TypeReference<Map<String, List<LocatorEntry>>>() {});
            for (Map.Entry<String, List<LocatorEntry>> entry : rawMap.entrySet()) {
                List<By> byList = new ArrayList<>();
                for (LocatorEntry le : entry.getValue()) {
                    byList.add(convertToBy(le));
                }
                locatorFallbackMap.put(entry.getKey(), byList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Could not load locators.json: " + e.getMessage());
        }
    }

    private static By convertToBy(LocatorEntry le) {
        switch (le.LocatorType.toLowerCase()) {
            case "id": return By.id(le.Locator);
            case "name": return By.name(le.Locator);
            case "css": return By.cssSelector(le.Locator);
            case "xpath": return By.xpath(le.Locator);
            case "class": return By.className(le.Locator);
            default: throw new IllegalArgumentException("Unknown locator type: " + le.LocatorType);
        }
    }

    public static WebElement getElementWithAutoHeal(WebDriver driver, By originalLocator) {
        try {
            return driver.findElement(originalLocator);
        } catch (NoSuchElementException e) {
            String key = getLocatorKey(originalLocator);
            List<By> fallbacks = locatorFallbackMap.get(key);

            // Fallback with fuzzy key match if exact key not found
            if (fallbacks == null) {
                String fuzzyKey = findClosestMatchingKey(key);
                fallbacks = locatorFallbackMap.getOrDefault(fuzzyKey, Collections.emptyList());
                System.out.println("Fuzzy matched key: " + fuzzyKey);
            }

            System.out.println("Fallback locators: " + fallbacks);
            for (By locator : fallbacks) {
                try {
                    return driver.findElement(locator);
                } catch (NoSuchElementException ignore) {}
            }
            throw new NoSuchElementException("Element not found using original or fallback locators for: " + key);
        }
    }

    // Extract locator key
    private static String getLocatorKey(By by) {
        String desc = by.toString();
        if (desc.startsWith("By.id: ")) return desc.replace("By.id: ", "").trim();
        if (desc.startsWith("By.name: ")) return desc.replace("By.name: ", "").trim();
        if (desc.startsWith("By.cssSelector: #")) return desc.replace("By.cssSelector: #", "").trim();
        return desc;
    }

    // Fuzzy key matching
    private static String findClosestMatchingKey(String originalKey) {
        for (String key : locatorFallbackMap.keySet()) {
            if (key.contains(originalKey) || originalKey.contains(key)) {
                return key;
            }
        }
        return originalKey; // fallback to original key if no close match
    }
}
