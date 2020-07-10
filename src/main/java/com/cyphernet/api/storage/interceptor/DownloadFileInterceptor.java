package com.cyphernet.api.storage.interceptor;

import com.cyphernet.api.exception.UserFileNotFoundException;
import com.cyphernet.api.storage.AmazonClient;
import com.cyphernet.api.storage.model.UserFile;
import com.cyphernet.api.storage.service.UserFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
public class DownloadFileInterceptor implements HandlerInterceptor {

    private final UserFileService userFileService;
    private final AmazonClient amazonClient;

    @Autowired
    public DownloadFileInterceptor(UserFileService userFileService, AmazonClient amazonClient) {
        this.userFileService = userFileService;
        this.amazonClient = amazonClient;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (response.getStatus() != 200 || ex != null) {
            return;
        }
        Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        String fileUuid = (String) pathVariables.get("fileUuid");
        System.out.println("interceptor :" + fileUuid);

        UserFile userFile = userFileService.getUserFileByUuid(fileUuid)
                .orElseThrow(() -> new UserFileNotFoundException("uuid", fileUuid));
        if (userFile.getIsTemporary()) {
            amazonClient.deleteFileFromS3Bucket(userFile.getFileNamePrivate());
            userFileService.deleteUserFile(fileUuid);
        }
    }
}
