package com.example.ocrdemo;

import com.aliyun.objectdet20191230.Client;
import com.aliyun.objectdet20191230.models.RecognizeVehicleDamageAdvanceRequest;
import com.aliyun.objectdet20191230.models.RecognizeVehicleDamageResponse;
import com.aliyun.tearpc.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class ObjectDetService {

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
        config.endpoint = "objectdet.cn-shanghai.aliyuncs.com";

        ocrClient = new Client(config);
        runtimeOptions = new RuntimeOptions();
    }

    //  车牌识别
    public RecognizeVehicleDamageResponse recognizeVehicleDamageAdvance(String filePath) throws Exception {
        RecognizeVehicleDamageAdvanceRequest request = new RecognizeVehicleDamageAdvanceRequest();
        request.imageURLObject = Files.newInputStream(Paths.get(filePath));
        return ocrClient.recognizeVehicleDamageAdvance(request, runtimeOptions);
    }


}
