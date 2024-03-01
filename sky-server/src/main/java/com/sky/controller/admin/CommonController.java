/**
 * @Author：乐
 * @Package：com.sky.controller.admin
 * @Project：sky-take-out
 * @name：CommonController
 * @Date：2024/3/1 0001  16:41
 * @Filename：CommonController
 */
package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * 通用接口
 */
@RestController
@RequestMapping("/admin/common")
@Slf4j
@Api(tags = "通用接口")
public class CommonController {


    //获取yaml中配置的上传路径属性
    @Value(("${web.upload-path}"))
    private String uploadPath;


    /**
     * 文件上传
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("/upload")
    @ApiOperation(value = "文件上传")
    public  Result<String> upload(@RequestPart("file") MultipartFile file) throws IOException {

        log.info("文件上传：{}",file);
        String fileName = file.getOriginalFilename();  //获取文件原名
        String visibleUri="/"+fileName;     //拼接访问图片的地址
        String saveUri=uploadPath+"/"+fileName;        //拼接保存图片的真实地址


        log.info("图片原文件名={} 图片访问地址={} 图片保存真实地址={}",fileName,visibleUri,saveUri);

        File saveFile = new File(saveUri);
        //判断是否存在文件夹，不存在就创建，但其实可以直接手动确定创建好，这样不用每次保存都检测
        if (!saveFile.exists()){
            saveFile.mkdirs();
        }
        try {
            file.transferTo(saveFile);  //保存文件到真实存储路径下
        } catch (IOException e) {
            e.printStackTrace();
        }

        //文件上传成功
        return Result.success(saveUri);
    }




}
