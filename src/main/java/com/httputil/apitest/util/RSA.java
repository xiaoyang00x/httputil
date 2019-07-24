package com.httputil.apitest.util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

/**
 * Created by yangyu on 2019/3/21.
 */
public class RSA {

    public static final String RSA_MODULUS = "AC14E4A51F1B8E11A95971CA4EBD7E2314631F137596A66A43FA2D792B2FD8447CBD6553D591F00A8B9D58E8BA33C229317A0D122C965D84A286114A963C3AE2694C81665D5AF04C80A71CBF350CD4C66280DC8FADBE6B8EDA7B2EC3D0C50E150850445EF84D3A4192662AC135D912C2CA2C68176D79EC64CACFF34089482B69";
    public static final String RSA_EXPONENT = "010001";

    public RSA() {
    }

    public static String encryptByPublic(String relativeUrl, long osTime) {
        BigInteger modulus = new BigInteger("AC14E4A51F1B8E11A95971CA4EBD7E2314631F137596A66A43FA2D792B2FD8447CBD6553D591F00A8B9D58E8BA33C229317A0D122C965D84A286114A963C3AE2694C81665D5AF04C80A71CBF350CD4C66280DC8FADBE6B8EDA7B2EC3D0C50E150850445EF84D3A4192662AC135D912C2CA2C68176D79EC64CACFF34089482B69", 16);
        BigInteger pubExp = new BigInteger("010001", 16);
        if(relativeUrl.charAt(relativeUrl.length() - 1) != 47) {
            relativeUrl = relativeUrl + "/";
        }

        if(relativeUrl.charAt(0) != 47) {
            relativeUrl = "/" + relativeUrl;
        }

        try {
            KeyFactory e = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(modulus, pubExp);
            RSAPublicKey key = (RSAPublicKey)e.generatePublic(pubKeySpec);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(1, key);
            byte[] plainText = (relativeUrl + osTime).getBytes();
            byte[] cipherData = cipher.doFinal(plainText);
            return Base64.encodeToString(cipherData, 0);
        } catch (NoSuchAlgorithmException var11) {
            var11.printStackTrace();
        } catch (IllegalBlockSizeException var12) {
            var12.printStackTrace();
        } catch (InvalidKeyException var13) {
            var13.printStackTrace();
        } catch (BadPaddingException var14) {
            var14.printStackTrace();
        } catch (InvalidKeySpecException var15) {
            var15.printStackTrace();
        } catch (NoSuchPaddingException var16) {
            var16.printStackTrace();
        } catch (Exception var17) {
            var17.printStackTrace();
        }

        return "";
    }
}
