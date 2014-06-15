package com.hyxt.impl;

import com.hyxt.WeiXin;
import com.hyxt.security.EncodeUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * Created by rocky on 14-6-10.
 */
public class WeiXinImpl implements WeiXin {
    @Override
    public boolean validatorRegister(String signature, String timestamp, String nonce, String appId) {
        String[] validators = new String[]{getToken(appId), timestamp, nonce};
        Arrays.sort(validators);
        String sha1 = StringUtils.join(validators);
        if (signature.equals(EncodeUtils.encodeSHA(sha1))) {
            return true;
        }
        return false;
    }


    private String getToken(String appId) {
        String token = appId;
        return token;
    }


}
