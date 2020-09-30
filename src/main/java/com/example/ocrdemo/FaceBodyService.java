package com.example.ocrdemo;

import com.aliyun.facebody20191230.Client;
import com.aliyun.facebody20191230.models.RecognizeExpressionAdvanceRequest;
import com.aliyun.facebody20191230.models.RecognizeExpressionResponse;
import com.aliyun.tearpc.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class FaceBodyService {

    private Client ocrClient;

    private RuntimeOptions runtimeOptions;

    @Value("${aliyun.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun.accessKeySecret}")
    private String accessKeySecret;

    @PostConstruct
    private void init() throws Exception {
        Config config = new Config();
        config.regionId = "cn-shanghai";
        config.accessKeyId = accessKeyId;
        config.accessKeySecret = accessKeySecret;
        config.endpoint = "facebody.cn-shanghai.aliyuncs.com";

        ocrClient = new Client(config);
        runtimeOptions = new RuntimeOptions();
    }

    //  表情识别
    public RecognizeExpressionResponse recognizeExpressionAdvance(String filePath) throws Exception {
        RecognizeExpressionAdvanceRequest request = new RecognizeExpressionAdvanceRequest();
        request.imageURLObject = Files.newInputStream(Paths.get(filePath));
        return ocrClient.recognizeExpressionAdvance(request, runtimeOptions);
    }

}
