package com.dm.bomber.services;

import com.dm.bomber.services.core.Phone;
import com.dm.bomber.services.core.Service;
import com.dm.bomber.services.core.ServicesRepository;

import java.util.ArrayList;
import java.util.List;

public class MainServices {

    public interface OnCollectedListener {
        void onCollected(int progress, int maxProgress);
    }

    private List<ServicesRepository> repositories;
    private final List<Service> services = new ArrayList<>();

    private final List<OnCollectedListener> onCollectedListeners = new ArrayList<>();

    public void registerOnCollectAllListener(OnCollectedListener listener) {
        onCollectedListeners.add(listener);
    }

    public void unregisterOnCollectAllListener(OnCollectedListener listener) {
        onCollectedListeners.remove(listener);
    }

    public void setRepositories(List<ServicesRepository> repositories) {
        this.repositories = repositories;
    }

    private void callOnCollectedListeners(int progress, int maxProgress) {
        for (OnCollectedListener listener : onCollectedListeners)
            listener.onCollected(progress, maxProgress);
    }

    public void collectAll() {
        services.clear();
        callOnCollectedListeners(0, repositories.size());
        for (int i = 0; i < repositories.size(); i++) {
            services.addAll(repositories.get(i).collect());
            callOnCollectedListeners(i + 1, repositories.size());
        }
    }

    public List<Service> getServices(Phone phone) {
        return getServices(phone.getCountryCode());
    }

    public List<Service> getServices(String countryCode) {
        List<Service> usableServices = new ArrayList<>();

        int countryCodeNum = countryCode.isEmpty() ? 0 : Integer.parseInt(countryCode);
        for (Service service : services) {
            if (service.getCountryCodes() == null || service.getCountryCodes().length == 0) {
                usableServices.add(service);
                continue;
            }
            for (final int i : service.getCountryCodes()) {
                if (i == countryCodeNum) {
                    usableServices.add(service);
                    break;
                }
            }
        }

        return usableServices;
    }
}
