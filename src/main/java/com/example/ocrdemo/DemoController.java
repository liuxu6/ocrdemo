package com.example.ocrdemo;

import com.aliyun.facebody20191230.models.RecognizeExpressionResponse;
import com.aliyun.objectdet20191230.models.RecognizeVehicleDamageResponse;
import com.aliyun.ocr20191230.models.RecognizeDriverLicenseResponse;
import com.aliyun.ocr20191230.models.RecognizeDrivingLicenseResponse;
import com.aliyun.ocr20191230.models.RecognizeIdentityCardResponse;
import com.aliyun.ocr20191230.models.RecognizeLicensePlateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/")
public class DemoController {

    @Autowired
    private OcrService ocrService;

    @Autowired
    private FaceBodyService faceBodyService;

    @Autowired
    private ObjectDetService objectDetService;

    @Value("${upload.path}")
    private String uploadPath;

    public static final Map<String, String> CODE_MAP = new HashMap<String, String>(){
        {
            put("recognizelicenseplate", "车牌识别");
            put("recognizeexpression", "人脸表情识别");
            put("recognizedrivinglicense", "行驶证识别");
            put("recognizeidentitycard", "身份证识别");
            put("recognizedriverlicense", "驾驶证识别");
            put("recognizevehicledamage", "车辆损伤识别");
        }
    };

//    @PostConstruct
//    private void init() throws Exception {
//        ocrService = new OcrService();
//        CODE_MAP.put("recognizelicenseplate", "车牌识别");
//        CODE_MAP.put("recognizeexpression", "人脸表情识别");
//        CODE_MAP.put("recognizedrivinglicense", "行驶证识别");
//        CODE_MAP.put("recognizeidentitycard", "身份证识别");
//        CODE_MAP.put("recognizedriverlicense", "驾驶证识别");
//        CODE_MAP.put("recognizevehicledamage", "车辆损伤识别");
//    }

    private String saveFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        String fileSuffix = fileName.substring(fileName.lastIndexOf("."));
        String filePath = System.currentTimeMillis() + fileSuffix;
        Path dir = Paths.get(uploadPath);
        if (Files.notExists(dir)){
            Files.createDirectories(dir);
        }
        Path path = Paths.get(uploadPath + filePath);
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        return filePath;
    }

    @RequestMapping(value = "/index", method=RequestMethod.GET)
    public String index(){
        return "index";
    }

    @RequestMapping(value = "/upload/{code}",method= RequestMethod.GET)
    public String upload(@PathVariable String code, Map<String, Object> map){
        if (CODE_MAP.containsKey(code)){
            map.put("name", CODE_MAP.get(code));
            return "upload";
        }else{
            map.put("error","URL不存在");
            return "error";
        }
    }

    @RequestMapping(value = "/upload/{code}",method= RequestMethod.POST)
    public String result(@PathVariable String code, @RequestParam("file") MultipartFile file,String side, Map<String, Object> map) throws Exception {
        String filePath = saveFile(file);
        map.put("name", CODE_MAP.get(code));
        map.put("filePath", "/images/"+filePath);
        String localFilePath = uploadPath+filePath;
        if (!"back".equals(side)){
            side = "face";
        }
        switch (code){
            case "recognizelicenseplate":{
                RecognizeLicensePlateResponse result = ocrService.recognizeLicensePlateAdvance(localFilePath);
                map.put("result", result.data.plates);
                break;
            }
            case "recognizeexpression":{
                RecognizeExpressionResponse result = faceBodyService.recognizeExpressionAdvance(localFilePath);
                map.put("result", result.data.elements);
                break;
            }
            case "recognizedrivinglicense":{
                RecognizeDrivingLicenseResponse result = ocrService.recognizeDrivingLicenseAdvance(localFilePath,side);
                map.put("faceResult", result.data.faceResult);
                map.put("backResult", result.data.backResult);
                break;
            }
            case "recognizeidentitycard":{
                RecognizeIdentityCardResponse result = ocrService.recognizeIdentityCardAdvance(localFilePath,side);
                map.put("frontResult", result.data.frontResult);
                map.put("backResult", result.data.backResult);
                break;
            }
            case "recognizedriverlicense":{
                RecognizeDriverLicenseResponse result = ocrService.recognizeDriverLicenseAdvance(localFilePath,side);
                map.put("faceResult", result.data.faceResult);
                map.put("backResult", result.data.backResult);
                break;
            }
            case "recognizevehicledamage":{
                RecognizeVehicleDamageResponse response = objectDetService.recognizeVehicleDamageAdvance(localFilePath);
                Map<String, Integer> result = new HashMap<>();
                Map<String, String> damageType = new HashMap<String, String>(){{put("1","轻微刮擦");put("2","重度刮擦");
                    put("3","轻度变形");put("4","中度变形");put("5","重度变形");put("6","crack破损孔洞");
                    put("7","翼子板和大灯缝隙");put("8","翼子板保险杠缝隙");put("9","大灯轻微刮擦");put("10","大灯重度刮擦");
                    put("11","大灯破损");put("12","后视镜轻微刮擦");put("13","后视镜玻璃破损");put("14","后视镜脱落");
                    put("15","挡风玻璃破损");}};
                for(RecognizeVehicleDamageResponse.RecognizeVehicleDamageResponseDataElements element:response.data.elements){
                    String type = damageType.get(element.type);
                    if(result.containsKey(type)){
                        result.put(type, result.get(type)+1);
                    }else {
                        result.put(type, 1);
                    }
                }
                map.put("result", result);
                break;
            }

        }
        return "result";
    }

    @RequestMapping(value = "/upload/back/{code}",method= RequestMethod.POST)
    public String result(@PathVariable String code, @RequestParam("file") MultipartFile file, Map<String, Object> map) throws Exception {
        return result(code,file,"back",map);
    }

}
