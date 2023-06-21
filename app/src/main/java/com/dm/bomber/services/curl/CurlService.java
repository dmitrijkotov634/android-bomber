package com.dm.bomber.services.curl;

import com.dm.bomber.services.DefaultFormatting;
import com.dm.bomber.services.core.Callback;
import com.dm.bomber.services.core.Phone;
import com.dm.bomber.services.core.Service;

import java.util.List;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class CurlService extends Service {

    private final List<String> args;

    private String method = "POST";
    private MediaType mediaType = null;

    public CurlService(String command, int... countryCodes) {
        super(countryCodes);
        args = ArgumentTokenizer.tokenize(command);
    }

    @Override
    public void run(OkHttpClient client, Callback callback, Phone phone) {
        Request.Builder builder = new Request.Builder();

        int i = 0;
        while (i < args.size()) {
            String arg = args.get(i);
            switch (arg) {
                case "curl":
                case "\n":
                case "--compressed":
                    break;
                case "-H":
                    String[] header = args.get(++i).split(Pattern.quote(": "), 2);
                    if (header.length == 1)
                        header = new String[]{header[0].substring(0, header[0].length() - 2), ""};
                    if (header[0].equalsIgnoreCase("content-type"))
                        mediaType = MediaType.parse(header[1]);
                    builder.addHeader(header[0], DefaultFormatting.format(header[1], phone));
                    break;
                case "-X":
                    method = args.get(++i);
                    break;
                case "--data-raw":
                    builder.method(method, RequestBody.create(
                            DefaultFormatting.format(args.get(++i), phone), mediaType));
                    break;
                default:
                    builder.url(DefaultFormatting.format(arg, phone));
            }
            i++;
        }

        client
                .newCall(builder.build())
                .enqueue(callback);
    }
}
