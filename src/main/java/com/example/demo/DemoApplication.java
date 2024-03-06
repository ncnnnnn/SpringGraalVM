package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import org.junit.Test;
 
import java.security.KeyPair;
@SpringBootApplication
public class DemoApplication {
	package com.prison.common.util;
 
 
		public static void sm2Test() {
	 
			String text = "wangjing";
	 
			//使用随机生成的密钥对加密或解密
			System.out.println("使用随机生成的密钥对加密或解密====开始");
			SM2 sm2 = SmUtil.sm2();
			// 公钥加密
			String encryptStr = sm2.encryptBcd(text, KeyType.PublicKey);
			System.out.println("公钥加密：" + encryptStr);
			//私钥解密
			String decryptStr = StrUtil.utf8Str(sm2.decryptFromBcd(encryptStr, KeyType.PrivateKey));
			System.out.println("私钥解密：" + decryptStr);
			System.out.println("使用随机生成的密钥对加密或解密====结束");
	 
	 
			//使用自定义密钥对加密或解密
			System.out.println("使用自定义密钥对加密或解密====开始");
	 
			KeyPair pair = SecureUtil.generateKeyPair("SM2");
			byte[] privateKey = pair.getPrivate().getEncoded();
			byte[] publicKey = pair.getPublic().getEncoded();
	 
			SM2 sm22 = SmUtil.sm2(privateKey, publicKey);
			// 公钥加密
			String encryptStr2 = sm22.encryptBcd(text, KeyType.PublicKey);
			System.out.println("公钥加密：" + encryptStr2);
			//私钥解密
			String decryptStr2 = StrUtil.utf8Str(sm22.decryptFromBcd(encryptStr2, KeyType.PrivateKey));
			System.out.println("私钥解密：" + decryptStr2);
			System.out.println("使用自定义密钥对加密或解密====结束");
	 
		}
	 
 
	public static void main(String[] args) {
		sm2Test()；
		SpringApplication.run(DemoApplication.class, args);
	}

}
