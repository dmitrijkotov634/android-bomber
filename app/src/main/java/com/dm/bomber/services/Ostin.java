package com.dm.bomber.services;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Request;

public class Ostin extends JsonService {

    public Ostin() {
        setUrl("https://ostin.com/api/v2/front/request-code");
        setMethod(POST);
    }

    @Override
    public Request buildRequest(Request.Builder builder) {
        builder.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.71 Safari/537.36");
        builder.addHeader("Cookie", "abbanner=ab_banner_main.1; abbanner_2=ab_banner_main_2.0; oabts3=qq.2; orange=s%3A9fjuzakHgW6gdZOolvRSmtA9d9GqyhTo.hyujNiDxoEQ8TJ%2Fg4IHz8lH9Ep1y%2FCV9o0rpdzkuMmY; TSPD_101=082b34025bab280003a67dca87cbc49a26f13945c03a77d974c7e79dc8b96a9e3e5c8d8817910dea656337af8f7003ae088331f83a0518001c86948aabbae0aa0daed425a8a247b96ba21e6d829c8392; _gcl_au=1.1.1036840040.1633262094; sessionStatus=true; _ga=GA1.2.838056504.1633262096; _gid=GA1.2.1814722679.1633262096; SessionCount=1; tmr_lvid=1b2c8de3c515c4a41d6fb68315502261; tmr_lvidTS=1633262099967; _ym_uid=1633262101669105553; _ym_d=1633262101; tgClc=www.google.com; rrpvid=165073832777804; _gaexp=GAX1.2.qXwte-b_TcqKtVlbNHM4-Q.18980.3; rcuid=61599a1653897c0001d741da; mindboxDeviceUUID=36d279a8-6ece-413c-944f-4cf0020799d6; directCrm-session=%7B%22deviceGuid%22%3A%2236d279a8-6ece-413c-944f-4cf0020799d6%22%7D; _ym_isad=2; popmechanic_sbjs_migrations=popmechanic_1418474375998%3D1%7C%7C%7C1471519752600%3D1%7C%7C%7C1471519752605%3D1; _ym_visorc=w; cto_bundle=8gDOQ19JemllaHlidGFibFpaU1c4YTlrUkdkZW9oJTJGV21IcWZJMHAyRnIlMkJmZTFZYzJkMSUyRkhWNVo3VU00STBOTHNLRU9OMGRLU3ZJTFlKMXVqOGVzR2JBSkY0MSUyRmFDZFhYVlNlMWRZbDJkcEt3bElaTFM1JTJGYk00MThpYjI3dktQV2VZWnVJYmc0cUI2RUw0VWNkZjFKaGhLM093JTNEJTNE; _fbp=fb.1.1633262113385.705738780; supportOnlineTalkID=1opzMaanC7hFAAO9EKVDhFEAoBPk45Z4; tmr_detect=0%7C1633262116049; tmr_reqNum=7; TSdc9dbfd0077=082b34025bab28004de3be2e679060e2c67996277fa0ca99545cae3fbb87941473ffb68f4deeb1fcac5dfc6cbbbd3a260884319a39172000913f45a7881567cfa2e8d68a6eddfe29a86ca39e3ac1d852c6318d3ee0ff40ec; TS00000000076=082b34025bab280064ef13f6c97f4b3261191db1b3f3075284fb449b4299ff3fa2b2499842b15aaf9befeca352eabac80884b1da7009d000ebbc7b0c1d08d37309ed824e717f1e1770add74419a102b7e20e86b024ec69317e2d0ab45a1888ae3ae42b0a34c6f367bca6d4d404a34cf8a97928b492aa11645ac804f82dbb729d900b5660ab508815a3baa179c14dd40ffdfc1633b7ec9fedaae4fdef783054a77f9db1ba5cc72a398baa509a8a096e37afc203d416308763a565d542b4cb25e35b0e80e999cf49a86768803984261bedc456e296f329dcbd3face7c6820a25a61a24bec18b5a27dd60bd7b0bb42ec747f0580dd87ce20610293f7eca5dfe2c27f3672a019f16d057; TSPD_101_DID=082b34025bab280064ef13f6c97f4b3261191db1b3f3075284fb449b4299ff3fa2b2499842b15aaf9befeca352eabac80884b1da700638004ba2845f0d4f8c29ed5c1950c0357253ddb9db897d9e168410301f7c99c97cefa729462cc8940b98759f3f8ec8413e2cf54f15548811f442; TS01b31f71=0107130fb679e625d9e67ce7c7624b9646ebb8d3622f1996b666184b2f4636d447b6e483b3eb6a15ee520f7ee749d096c9f186a952; _gat_UA-36387636-4=1; TSdc9dbfd0029=082b34025bab28005ff660defb120b0de5101dcf23e2bc40e67125d4a0a17e8bbf862b94c78137b76305f6c2e8dd9c1a; TS111ee6c0027=082b34025bab200050c98628120924eed735ed5b0319c5153b5936c688aaa3a3e321c1755b490cb30883b7fffa1130001c7e4d8086575ece99db5b9a94528e7b6a5f9bd6948e0e6d39a1afa33ad88f04f7de80eb458e9a1c43d6ed790b69b57c");

        return super.buildRequest(builder);
    }

    @Override
    public String buildJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("phone", "+" + getFormattedPhone());
            json.put("channel", "SMS");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }
}
