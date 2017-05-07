package ske.aurora.utils;

import java.net.MalformedURLException;
import java.net.URL;

public class PrometheusUrlNormalizer {


    private PrometheusUrlNormalizer() {

    }

    public static String normalize(String stringUrl)  {

        URL url;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Not valid url", e);
        }

        String portSegment = "_" + url.getPort();
        if (url.getPort() == -1) {
            portSegment = "";
        }

        String pathSegment = url.getPath();
        if ("/".equalsIgnoreCase(pathSegment)) {
            pathSegment = "";
        } else if(pathSegment.startsWith("/")) {
            pathSegment = pathSegment.substring(1);
        }

        String finalUrl = String.format("%s%s%s", url.getHost(), portSegment, pathSegment);
        return finalUrl.replaceAll(":|-|/", "_");

    }
}
