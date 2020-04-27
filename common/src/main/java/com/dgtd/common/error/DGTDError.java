package com.dgtd.common.error;

public interface DGTDError {
    public static final String temporaryKey = "TEMP";

    String getId();

    void setId(String id);

    String getDate() ;

    TypeError getTypeError();

    void setTypeError(TypeError typeError);

    String getDetails();

    void setDetails(String details);


}
