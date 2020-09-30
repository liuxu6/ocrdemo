package com.example.ocrdemo;


import com.aliyun.ocr20191230.Client;
import com.aliyun.ocr20191230.models.*;
import com.aliyun.tearpc.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class OcrService {

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
        config.endpoint = "ocr.cn-shanghai.aliyuncs.com";

        ocrClient = new Client(config);
        runtimeOptions = new RuntimeOptions();
    }

    //  车牌识别
    public RecognizeLicensePlateResponse recognizeLicensePlateAdvance(String filePath) throws Exception {
        RecognizeLicensePlateAdvanceRequest request = new RecognizeLicensePlateAdvanceRequest();
        request.imageURLObject = Files.newInputStream(Paths.get(filePath));
        return ocrClient.recognizeLicensePlateAdvance(request, runtimeOptions);
    }

    //  行驶证识别
    public RecognizeDrivingLicenseResponse recognizeDrivingLicenseAdvance(String filePath, String side) throws Exception {
        RecognizeDrivingLicenseAdvanceRequest request = new RecognizeDrivingLicenseAdvanceRequest();
        request.imageURLObject = Files.newInputStream(Paths.get(filePath));
        request.side = side;
        return ocrClient.recognizeDrivingLicenseAdvance(request, runtimeOptions);
    }

    //  身份证识别
    public RecognizeIdentityCardResponse recognizeIdentityCardAdvance(String filePath, String side) throws Exception {
        RecognizeIdentityCardAdvanceRequest request = new RecognizeIdentityCardAdvanceRequest();
        request.imageURLObject = Files.newInputStream(Paths.get(filePath));
        request.side = side;
        return ocrClient.recognizeIdentityCardAdvance(request, runtimeOptions);
    }

    //  驾驶证识别
    public RecognizeDriverLicenseResponse recognizeDriverLicenseAdvance(String filePath, String side) throws Exception {
        RecognizeDriverLicenseAdvanceRequest request = new RecognizeDriverLicenseAdvanceRequest();
        request.imageURLObject = Files.newInputStream(Paths.get(filePath));
        request.side = side;
        return ocrClient.recognizeDriverLicenseAdvance(request, runtimeOptions);
    }
}
