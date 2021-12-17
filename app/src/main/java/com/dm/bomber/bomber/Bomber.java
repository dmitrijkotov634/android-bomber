package com.dm.bomber.bomber;

import com.dm.bomber.services.Service;
import com.dm.bomber.services.Services;

import java.util.ArrayList;
import java.util.List;

public class Bomber {
    public static boolean isAlive(Attack attack) {
        return attack != null && attack.isAlive();
    }

    public static boolean contains(final int[] array, final int key) {
        for (final int i : array) {
            if (i == key) {
                return true;
            }
        }
        return false;
    }

    public static List<Service> getUsableServices(int countryCode) {
        List<Service> usableServices = new ArrayList<>();

        for (Service service : Services.services) {
            if (service.countryCodes == null || service.countryCodes.length == 0 || contains(service.countryCodes, countryCode))
                usableServices.add(service);
        }

        return usableServices;
    }
}
