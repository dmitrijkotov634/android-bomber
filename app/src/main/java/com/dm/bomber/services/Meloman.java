package com.dm.bomber.services;

import okhttp3.FormBody;
import okhttp3.Request;

public class Meloman extends FormService {

    public Meloman() {
        setUrl("https://www.meloman.kz/loyalty/customer/createConfirm/");
        setMethod(POST);
        setPhoneCode("7");
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("Cookie", "PHPSESSID=aqjd0u7uonqfevn0gn3u4c61ad; region_id=541; _dyjsession=vsmhwwpdhvwqo9ulrxefl94oxmwa3j7r; dy_fs_page=www.meloman.kz%2Fcustomer%2Faccount%2Flogin%2Freferer%2Fahr0chm6ly93d3cubwvsb21hbi5rei9jdxn0b21lci9hy2nvdw50l2luzgv4lw%252c%252c; _dy_csc_ses=vsmhwwpdhvwqo9ulrxefl94oxmwa3j7r; _dy_c_exps=; mindboxDeviceUUID=36d279a8-6ece-413c-944f-4cf0020799d6; directCrm-session=%7B%22deviceGuid%22%3A%2236d279a8-6ece-413c-944f-4cf0020799d6%22%7D; _dycnst=dg; _ym_uid=1636207842539780160; _ym_d=1636207842; _gcl_au=1.1.1006729542.1636207843; _dyid=5444852144400665824; _dycst=dk.l.c.ms.; _dy_geo=RU.EU.RU_VLA.RU_VLA_Murom; _dy_df_geo=Russia..Murom; gtmSID=88464675-5672-4961-9f51-e865d3b19a11; _ym_visorc=w; mage-cache-storage=%7B%7D; mage-cache-storage-section-invalidation=%7B%7D; _ga=GA1.2.2097214475.1636207846; _gid=GA1.2.1159841544.1636207846; form_key=DeYA41E5ksznJxJN; _ym_isad=2; mage-cache-sessid=true; mage-messages=; recently_viewed_product=%7B%7D; recently_viewed_product_previous=%7B%7D; recently_compared_product=%7B%7D; recently_compared_product_previous=%7B%7D; product_data_storage=%7B%7D; form_key=DeYA41E5ksznJxJN; _dyid_server=5444852144400665824; _fbp=fb.1.1636207858404.99290380; _dy_c_att_exps=; private_content_version=aeb1168da72c028842b7d6b65a087fd8; _dyfs=1636207890986; _dy_toffset=-6; _dy_soct=398001.680451.1636207843.vsmhwwpdhvwqo9ulrxefl94oxmwa3j7r*477267.869366.1636207887*362831.602231.1636207887*393784.1064062.1636207893; section_data_ids=%7B%22customer%22%3A1636207852%2C%22cart%22%3A1636207897%2C%22gtm%22%3A1636207899%7D");

        return super.buildRequest(builder);
    }

    @Override
    public void buildBody(FormBody.Builder builder) {
        builder.add("form_key", "DeYA41E5ksznJxJN");
        builder.add("success_url", "");
        builder.add("error_url", "");
        builder.add("un_approved_mobile", format(phone, "+7(***)***-**-**"));
        builder.add("confirm_mobile_code", "");
        builder.add("terms", "on");
    }
}
