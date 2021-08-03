package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Request;

public class Ukrzoloto extends JsonService {

    public Ukrzoloto() {
        setUrl("https://ukrzoloto.ua/api/login");
        setMethod(POST);
        setPhoneCode("380");
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
        builder.addHeader("Accept-Encoding", "gzip, deflate, br");
        builder.addHeader("Accept-Language", "ru");
        builder.addHeader("Alt-Used", "ukrzoloto.ua");
        builder.addHeader("Connection", "keep-alive");
        builder.addHeader("Content-Length", "43");
        builder.addHeader("Content-Type", "application/json");
        builder.addHeader("Cookie", "XSRF-TOKEN=eyJpdiI6IlptazRKRGMyaUsrVFwvUnZadmpyRkdnPT0iLCJ2YWx1ZSI6ImNySXBvREgyZHpTOTY5VXBYWTVhWU9HVjh1WDVLY1VDZEFEb2crWUJ5N0Z2MW9RUlVCQUFPRzVMc2VFQUgyWDciLCJtYWMiOiI1OGNiY2M5MDlkZTBjZGEwYmRmZWE3NzZkODA4NmM1MGQ0MWExODRiOWMxNDU4NWY3NTE5ZmZiZTJjNWRhODdmIn0%3D; ukrzoloto_session=eyJpdiI6IjhiUlRaejhFdTRiUmVxbXlBM3VCQmc9PSIsInZhbHVlIjoiOUdhYWtEMkxNUW82OW4rcUJBRFhZZmszWkRhcWo1ZTREZEZWVmdDR2FqdkdNbVFWUnJ5M0xJRDc2V3dqVE5hNiIsIm1hYyI6ImZjY2Q0ODVkNTIwMDUwMTRhZTYyZGRkMzg1NTFlZWE2OTRkYWEzZTFkZmE1M2FlOTNiNDgzZjQxYzA5YjhiNDgifQ%3D%3D; _gcl_aw=GCL.1627885622.CjwKCAjwjJmIBhA4EiwAQdCbxiy00fsfkcnmsZHQbVEJzIjTAn4iPiGpxzX5y3YknG620hrIhYvbkxoCLpEQAvD_BwE; _gcl_au=1.1.400725908.1627885623; _ga_WMHQRHF8XN=GS1.1.1627885622.1.0.1627885622.60; _ga=GA1.2.1233045979.1627885625; _gid=GA1.2.1529702483.1627885628; _gac_UA-26080367-1=1.1627885628.CjwKCAjwjJmIBhA4EiwAQdCbxiy00fsfkcnmsZHQbVEJzIjTAn4iPiGpxzX5y3YknG620hrIhYvbkxoCLpEQAvD_BwE; _gat=1; biatv-cookie={%22firstVisitAt%22:1627885621%2C%22visitsCount%22:1%2C%22campaignCount%22:1%2C%22currentVisitStartedAt%22:1627885621%2C%22currentVisitLandingPage%22:%22https://ukrzoloto.ua/ru/?utm_campaign=&utm_term=%25D1%2583%25D0%25BA%25D1%2580%25D0%25B7%25D0%25BE%25D0%25BB%25D0%25BE%25D1%2582%25D0%25BE&gclid=CjwKCAjwjJmIBhA4EiwAQdCbxiy00fsfkcnmsZHQbVEJzIjTAn4iPiGpxzX5y3YknG620hrIhYvbkxoCLpEQAvD_BwE%22%2C%22currentVisitOpenPages%22:1%2C%22location%22:%22https://ukrzoloto.ua/ru/?utm_campaign=&utm_term=%25D1%2583%25D0%25BA%25D1%2580%25D0%25B7%25D0%25BE%25D0%25BB%25D0%25BE%25D1%2582%25D0%25BE&gclid=CjwKCAjwjJmIBhA4EiwAQdCbxiy00fsfkcnmsZHQbVEJzIjTAn4iPiGpxzX5y3YknG620hrIhYvbkxoCLpEQAvD_BwE%22%2C%22userAgent%22:%22Mozilla/5.0%20(X11%3B%20Linux%20x86_64%3B%20rv:90.0)%20Gecko/20100101%20Firefox/90.0%22%2C%22language%22:%22ru%22%2C%22encoding%22:%22utf-8%22%2C%22screenResolution%22:%221366x768%22%2C%22currentVisitUpdatedAt%22:1627885621%2C%22utmDataCurrent%22:{%22utm_source%22:%22google%22%2C%22utm_medium%22:%22cpc%22%2C%22utm_campaign%22:%22(not%20set)%22%2C%22utm_content%22:%22(not%20set)%22%2C%22utm_term%22:%22(not%20set)%22%2C%22beginning_at%22:1627885621}%2C%22campaignTime%22:1627885621%2C%22utmDataFirst%22:{%22utm_source%22:%22google%22%2C%22utm_medium%22:%22cpc%22%2C%22utm_campaign%22:%22(not%20set)%22%2C%22utm_content%22:%22(not%20set)%22%2C%22utm_term%22:%22(not%20set)%22%2C%22beginning_at%22:1627885621}%2C%22utmDataFirstPaid%22:{%22utm_source%22:%22google%22%2C%22utm_medium%22:%22cpc%22%2C%22utm_campaign%22:%22(not%20set)%22%2C%22utm_content%22:%22(not%20set)%22%2C%22utm_term%22:%22(not%20set)%22%2C%22beginning_at%22:1627885621}%2C%22utmDataLastPaid%22:{%22utm_source%22:%22google%22%2C%22utm_medium%22:%22cpc%22%2C%22utm_campaign%22:%22(not%20set)%22%2C%22utm_content%22:%22(not%20set)%22%2C%22utm_term%22:%22(not%20set)%22%2C%22beginning_at%22:1627885621}%2C%22geoipData%22:{%22country%22:%22Russian%20Federation%22%2C%22region%22:%22Moscow%20City%22%2C%22city%22:%22Moscow%22%2C%22org%22:%22T2%20Mobile%20LLC%22}}; _gat_UA-26080367-1=1; _fbp=fb.1.1627885628420.1004241448; cto_bundle=x2Ga1F9jaGx1cldKN2JpNVJMM01JaXZzWHElMkJPdUM1JTJCSHdCUGczZjdKYUJLRExnRXpnRWxMaVhVUlVoWlR2WHpZQms4VlZSOWFaZmdhdUwxcmVFNkxkTWtTMjY2T3J5WEVpQ0JJenpFNnE0blZTS1gwNDZQcDN4d3lTSXRVVDNDWnFJUFhNNk5FRkdyN1E2QTd3RFBVRWpuMW93JTNEJTNE; mindboxDeviceUUID=7cb09534-06ab-4fed-a2bc-30cbf9ece9fe; directCrm-session=%7B%22deviceGuid%22%3A%227cb09534-06ab-4fed-a2bc-30cbf9ece9fe%22%7D; bingc-activity-data={%22numberOfImpressions%22:0%2C%22activeFormSinceLastDisplayed%22:27%2C%22pageviews%22:1%2C%22callWasMade%22:0%2C%22updatedAt%22:1627885654}");
        builder.addHeader("Host", "ukrzoloto.ua");
        builder.addHeader("Origin", "https://ukrzoloto.ua");
        builder.addHeader("Referer", "https://ukrzoloto.ua/ru/?utm_campaign=&utm_term=%D1%83%D0%BA%D1%80%D0%B7%D0%BE%D0%BB%D0%BE%D1%82%D0%BE&gclid=CjwKCAjwjJmIBhA4EiwAQdCbxiy00fsfkcnmsZHQbVEJzIjTAn4iPiGpxzX5y3YknG620hrIhYvbkxoCLpEQAvD_BwE");
        builder.addHeader("Sec-Fetch-Dest", "empty");
        builder.addHeader("Sec-Fetch-Mode", "cors");
        builder.addHeader("Sec-Fetch-Site", "same-origin");
        builder.addHeader("TE", "trailers");
        builder.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:90.0) Gecko/20100101 Firefox/90.0");
        builder.addHeader("X-Requested-With", "XMLHttpRequest");

        return super.buildRequest(builder);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("data", new JSONObject().put("telephoneNumber", getFormattedPhone()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}
