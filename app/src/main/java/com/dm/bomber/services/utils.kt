package com.dm.bomber.services

import com.dm.bomber.services.core.Service

typealias Services = List<Service>

fun collectAll(
    repositories: List<() -> List<Service>>,
    listener: (Int, Int, Services) -> Unit
): Services {
    val services = mutableListOf<Service>()

    repositories.run {
        listener(0, size, services)
        forEachIndexed { index, servicesRepository ->
            services.addAll(servicesRepository())
            listener(index + 1, size, services)
        }
    }

    return services
}

fun Services.filter(countryCode: String): Services {
    val countryCodeNum = if (countryCode.isEmpty()) 0 else countryCode.toInt()

    return buildList {
        this@filter.forEach {
            if (it.countryCodes.isEmpty()) {
                add(it)
                return@forEach
            }

            if (it.countryCodes.contains(countryCodeNum))
                add(it)
        }
    }
}