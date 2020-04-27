package com.dgtd.evelin_common;

import java.io.IOException;
import java.util.List;

public interface HandleAttachment {

    String insertPhotoFromActeToECWFolder() throws IOException;

    List<String> addPhotoErrorToList(String path);

    List<String> getPhotoFoldersErrors();

    List<String> generateAttachement();

    String getAttachementNameFromPath(String name);

}
