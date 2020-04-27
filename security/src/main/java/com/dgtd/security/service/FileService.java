package com.dgtd.security.service;


import com.dgtd.common.error.ErrorObject;
import com.dgtd.common.error.ErrorService;
import com.dgtd.common.error.TypeError;
import com.dgtd.common.exception.DataException;
import com.dgtd.common.exception.WsBackOfficeException;
import com.dgtd.security.config.Constant;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.dgtd.security.config.Constant.JSON_CRYPTED_AK;
import static com.dgtd.security.config.Constant.JSON_CRYPTED_IV;

@Service
public class FileService {

    private final Logger log = LoggerFactory.getLogger(FileService.class);
    private final ErrorService errorService;

    public FileService(ErrorService errorService) {
        this.errorService = errorService;
    }

    /**
     * Fonction de décryptage avec Cipher Blowfish
     * @param file
     * @return
     * @throws IOException
     * @throws WsBackOfficeException
     */
    public String decrypt(File file) throws IOException, WsBackOfficeException {
        InputStream stream = new FileInputStream(file);
        String unCryptStr;

        try {
            // Décryptage du fichier
            byte[] bytes = IOUtils.toByteArray (stream);
            SecretKeySpec akpec = new SecretKeySpec(Constant.getJsonCryptedAk().getBytes(), "Blowfish");
            Cipher cipher;
            cipher = Cipher.getInstance("Blowfish/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, akpec, new IvParameterSpec(Constant.getJsonCryptedIv().getBytes()));

            String str = new String(bytes);
            byte[] encrypted = new byte[str.length() / 2];
            for (int i = 0; i < str.length(); i += 2)
                encrypted[i/2] = (byte)((Character.digit(str.charAt(i), 16)  << 4) + Character.digit(str.charAt(i + 1), 16));
            byte[] decrypted = cipher.doFinal( encrypted );
            unCryptStr = new String(decrypted, StandardCharsets.UTF_8);
        }
        catch (Exception ex) {
            String error = "Erreur lors du decryptage du fichier, veuillez vérifier le cryptage de votre fichier " +
                    file.getName();
            log.error("***** "+ error) ;
            errorService.insertError(new ErrorObject(TypeError.SECURITE, error + ex.getLocalizedMessage() ),true);
            throw new DataException(error);
        }
        finally {
            if(stream!=null)stream.close();
        }
        //Remplacement du String crypté par le string décrypté
        List<String> str = new ArrayList<>();
        str.add(unCryptStr);
        Files.write(Paths.get(file.getPath()), str);

        return unCryptStr;
    }


    public String encrypt(String str) throws Exception
    {
        SecretKeySpec akpec = new SecretKeySpec(JSON_CRYPTED_AK.getBytes(), "Blowfish");
        Cipher cipher = null;
        cipher = Cipher.getInstance("Blowfish/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, akpec, new IvParameterSpec(JSON_CRYPTED_IV.getBytes()));
        byte[] encrypted = cipher.doFinal(str.getBytes("UTF8"));
        StringBuilder sb = new StringBuilder();
        for (byte b : encrypted) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}
