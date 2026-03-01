package org.airtribe.util;

import org.airtribe.entity.VerificationToken;
import org.airtribe.entity.User;

import java.util.UUID;

public class VerificationTokenUtil {
    public static VerificationToken generateToken(User user) {
        VerificationToken returnToken = new VerificationToken();
        String token = UUID.randomUUID().toString();
        returnToken.setToken(token);
        returnToken.setUser(user);
        return returnToken;
    }
    public static String generateUrl(String token) {
        String url = "http://127.0.0.1:9090/api/v1/chefs/verify-token?token="+token;
        return url;
    }
}
