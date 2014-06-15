package com.hyxt;

/**
 * Created by rocky on 14-6-10.
 */
public interface WeiXin {

    boolean validatorRegister(String signature , String timestamp , String nonce , String appId);



}
