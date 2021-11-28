package com.dm.bomber.bomber;

import com.dm.bomber.services.Service;
import com.dm.bomber.services.Services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Bomber {
    public static boolean isAlive(Attack attack) {
        return attack != null && attack.isAlive();
    }

    public static List<Service> getUsableServices(int countryCode) {
        List<Service> usableServices = new ArrayList<>();

        for (Service service : Services.services) {
            if (service.countryCodes == null || Arrays.asList(service.countryCodes).contains(countryCode))
                usableServices.add(service);
        }

        return usableServices;
    }

}
