package com.dgtd.common.excel;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@Component
public class Element {

    //TODO à généraliser
    private static String baseClass = "DGTDError";
    private String classPrefix="";
    private String objectPrefix="";
    private String listPrefix="";
    private String property="";
    private LinkedList<String> objects;
    private Map<String,String> lastObjectPrefix;


    public Element(){
        objects=new LinkedList<>();
        lastObjectPrefix = new HashMap<>();
    }

    public static String getBaseClass() {
        return baseClass;
    }

    public static void setBaseClass(String baseClass) {
        Element.baseClass = baseClass;
    }

    public String getClassPrefix() {
        return classPrefix;
    }

    public void setClassPrefix(String classPrefix) {
        classPrefix+=".";
        this.classPrefix = classPrefix;
    }

    public String getObjectPrefix() {


        return objectPrefix ;
    }

    public void setObjectPrefix(String objectPrefix) {
        String[] values = objectPrefix.split("\\.");
        int size = values.length;
        this.objectPrefix = values[size-1]+".";
    }

    public void eraseObjectPrefix(){
        this.objectPrefix="";
    }

    public String getListPrefix() {
        return listPrefix+".";
    }

    public void setListPrefix(String listPrefix) {
        this.listPrefix = listPrefix;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public LinkedList<String> getObjects() {
        return objects;
    }

    public void addObject(String obj){
        objects.add(obj);
    }

    public boolean changeObject(String obj){

       int size = objects.size();
       if(size>1){
           if(objects.get(size-1).equals(obj)){
               return false;
           }else return true;
       }
       else return false;
    }

    public void setObjects(LinkedList<String> objects) {
        this.objects = objects;
    }

    String getElement(){
        return classPrefix+objectPrefix+listPrefix+property;
    }

    public Map<String, String> getLastObjectPrefix() {
        return lastObjectPrefix;
    }

    public void setLastObjectPrefix(Map<String, String> lastObjectPrefix) {
        this.lastObjectPrefix = lastObjectPrefix;
    }

    public void addObjectPrefix(String prefix, String propertyName){
        this.lastObjectPrefix.put(prefix,propertyName);
    }

    public String getLastObjectPrefixProperty(){
        String lastPropertyObject = null;
        for(Map.Entry map : lastObjectPrefix.entrySet()){
          lastPropertyObject =(String) map.getValue();
        }
        return lastPropertyObject;
    }
}
