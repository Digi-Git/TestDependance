package com.dgtd.common.type;

public enum TypeTemplate {
    MAIN_TEMPLATE("Main Template"),
    BEAN_TEMPLATE("Bean Template"),
    PERSON_TEMPLATE("Person Template"),
    ATTACHMENT_TEMPLATE("Attachment Template"),
    META_TEMPLATE("Meta template"),
    GLOBAL_TEMPLATE("Global template");

    private String template;

    private TypeTemplate(String template){
        this.template = template;
    }

    public String getTypeTemplate(){
        return this.template;
    }
}
