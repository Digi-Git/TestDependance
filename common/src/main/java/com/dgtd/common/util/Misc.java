package com.dgtd.common.util;


import com.dgtd.common.exception.DataException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Misc {

    public static final String UTF8_BOM = "\uFEFF";

    /**
     * Equivalent .net du Try parse en moins bien
     * @param value
     * @return boolean
     */
    public static boolean tryParseInt (String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Encrypte une chaine en utilisant Spring Security Crypto
     * @param texte
     * @return encoded text
     */
     public static String encryp (String texte) {

        PasswordEncoder encoder = new BCryptPasswordEncoder();
        return (encoder.encode(texte));

    }

    /**
     * Retrouver le plus grand entier dans un tableau
     * @param tab tableau d'entiers
     * @param n nombre d'entiers dans le tableau
     * @return le plus grand entier
     */
    public static int findMax(int tab[], int n){
        if(n==1)return tab[0];
        return Math.max(tab[n-1], findMax(tab,n-1));
    }

    /**
     * Retourne une chaîne de caractères qui peut être enregistrée dans un dossier
     * en environnement windows
     * @param chaine de caractères à analyser
     * @return chaine sans les caractères spécifiques non souhaités
     */
    public static String getCleanedString(String chaine) {

        //String regex = "[^\\s\\w]*";
        String regex = "[;:<>|?*'\\\\/]+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(chaine);
        while(matcher.find()){
            chaine = matcher.replaceAll("_");
        }
        return chaine;

    }

    public static String getStringToCompare(String chaine){
        String cleanedChaine;
        chaine = StringUtils.stripAccents(chaine);
        cleanedChaine = chaine.toLowerCase().replace(" ","");
        return cleanedChaine;
    }

    public static File convertAndStore(String directoryPath, MultipartFile file) throws Exception {

        mkdir(directoryPath);
        File convFile = new File(directoryPath + "/"+ file.getOriginalFilename());
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }


    public static boolean checkIfZipFile(File file){
        int fileSignature ;
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            fileSignature = raf.readInt();
        } catch (IOException e) {
            throw new DataException("Votre fichier n'est pas valide " + e);
        }
        return fileSignature == 0x504B0304 || fileSignature == 0x504B0506 || fileSignature == 0x504B0708;
    }

    /**
     *
     * Création d'un dossier sur disque
     * @param path chemin complet
     *
     */
    public static void mkdir(String path) throws Exception {
        // creation ou replacement du dossier de destination
        try {
            FileUtils.forceMkdir(new File(path));
        } catch (Exception e) {
            throw new Exception("Problème lors de la création du dossier "
                    + e.getLocalizedMessage());

        }
    }

    public static boolean checkRiskFile(File file){
        String path = file.getPath();
        if(file.getPath().endsWith(".exe")){
            return true;
        }else if(file.getPath().endsWith(".js")){
            return true;
        }
        return false;
    }

    /**
     * Création d'un fichier zip (source unique)
     * @param sourceFile Chemin du fichier source
     * @param target Chemin de desitnation
     * @throws IOException
     */
    public static void saveToZipFile (String sourceFile, String target) throws IOException {



        FileOutputStream fos = new FileOutputStream(target);
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        File fileToZip = new File(sourceFile);
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        zipOut.close();
        fis.close();
        fos.close();
    }

    public static void copyFile(File sourceFile, File destinationFile)throws DataException{
        InputStream inputStream;
            try{
                inputStream = new BufferedInputStream(new FileInputStream(sourceFile));
            }catch (IOException e){
                String error = "Fichier source non disponible pour la copie " + e;
                throw new DataException(error);
            }
        OutputStream outputStream;
            try{
                outputStream = new BufferedOutputStream(new FileOutputStream(destinationFile));
            }catch (IOException e){
                String error = "Erreur lors de la création du fichier de destination en vue de la copie " + e;
                throw new DataException(error);
            }

            byte[] buffer = new byte[1024];
            int lengthRead;
            try{
                while ((lengthRead = inputStream.read(buffer))>0){
                    outputStream.write(buffer,0,lengthRead);
                    outputStream.flush();
                }
            }catch (IOException e){
                String error = "Erreur lors de la copie du fichier " + sourceFile.getName();
                throw new DataException(error);
            }
            try{
                outputStream.close();
                inputStream.close();
            }catch (IOException e){
                String error = "Erreur lors de la fermeture des fichiers " + sourceFile.getName()
                        + " "+ destinationFile.getName();
                throw new DataException(error);
            }

    }

    public static String removeUTF8BOM(String s) {
        if (s.startsWith(UTF8_BOM)) {
            s = s.substring(1);
        }
        return s;
    }

    public static File convert(MultipartFile multipartFile) throws IOException {

        File convFile = new File(multipartFile.getOriginalFilename());
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(multipartFile.getBytes());
        fos.close();
        return convFile;
    }
}
