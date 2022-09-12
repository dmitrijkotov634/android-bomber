package com.dm.bomber.services;

import java.util.List;

public interface Repository {
    List<Service> getServices(String countryCode);
}
