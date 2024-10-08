package com.example.controller.web;

import com.example.constant.JwtClaimsConstant;
import com.example.constant.MessageConstant;
import com.example.context.BaseContext;
import com.example.dto.UserDTO;
import com.example.dto.UserLoginDTO;
import com.example.entity.User;
import com.example.exception.ParamErrorException;
import com.example.mapper.UserMapper;
import com.example.properties.JwtProperties;
import com.example.result.Result;
import com.example.service.UserService;
import com.example.utils.JwtUtil;
import com.example.vo.UserLoginVO;
import com.example.vo.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户
 */
@RestController
@RequestMapping("/api/user")
@Slf4j
@Api(tags = "用户相关接口")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/register")
    @ApiOperation(value = "用户注册")
    public Result UserRegister(@RequestBody UserDTO userDTO){

        log.info("用户注册{}", userDTO);
        userService.UserRegister(userDTO);
        return Result.success();

    }

    @PostMapping("/login")
    @ApiOperation(value = "用户登录接口")
    public Result<UserLoginVO> UserLogin(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("用户登录：{}", userLoginDTO);

        User user = userService.UserLogin(userLoginDTO);
//        log.info("登录成功：{}", user);
        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());

        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);
//        log.info("生成的token：{}", token);
        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .userName(user.getUsername())
                .isBanned(userMapper.getIsBanned(user.getId()))
                .isActivated(userMapper.getIsActivated(user.getId()))
                .token(token)
                .build();

        return Result.success(userLoginVO);
    }

    @GetMapping("/sendActivate")
    @ApiOperation(value = "发送邮件激活地址")
    public Result sendActivateEmail() {
        Long userId = BaseContext.getCurrentId();
        userService.sendActivateEmail(userId);
        return Result.success(MessageConstant.MAIL_SEND_SUCCESS);
    }

    @GetMapping("/activate")
    @ApiOperation(value = "用户激活")
    public Result activateUser(@RequestParam String code, @RequestParam(required = false) Long Id) {
        log.info("用户激活，code:{}", code);
        if (Id == null){
            Id = BaseContext.getCurrentId();
        }
//        Long Id = BaseContext.getCurrentId();//不用jwt获取了，直接传入
        userService.activateUser(Id, code);
        return Result.success(MessageConstant.ACTIVATE_SUCCESS);
    }

    @GetMapping("/getSelfInfo")
    @ApiOperation("获取本用户信息")
    public Result<UserVO> getSelfInfo(){
        Long userId = BaseContext.getCurrentId();
        log.info("获取本用户信息，userId:{}",userId);
        User user = userService.getUserById(userId);
        UserVO userVO = UserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .email(user.getEmail())
                .isBanned(user.isBanned())
                .isActivated(user.isActivated())
                .unbanTime(user.getUnbanTime())
                .build();
        return Result.success(userVO);
    }

    @GetMapping("/getUserInfo")
    @ApiOperation("获取其它用户信息")
    public Result<UserVO> getUserInfo(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String username){
        log.info("获取用户信息，userId:{},username:{}",userId,username);
        User user;
        if (userId != null){
            user = userService.getUserById(userId);}
        else if (username != null){
            user = userService.getUserByUsername(username);
        }
        else {
            throw new ParamErrorException("参数错误");
        }
        UserVO userVO = UserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .isBanned(user.isBanned())
                .build();
        return Result.success(userVO);
    }

    @PostMapping("/updateUser")
    @ApiOperation("更新用户信息")
    public Result<UserVO> updateUser(@RequestBody UserDTO userDTO){
        Long userId = BaseContext.getCurrentId();
        log.info("更新用户信息，userId:{},userDTO:{}",userId,userDTO);
        UserVO userVO = userService.updateUser(userId,userDTO);
        return Result.success(userVO);
    }

//    @PostMapping("/uploadProfile")
//    @ApiOperation("上传头像")
//    public Result uploadProfile(@RequestParam MultipartFile profile){
//        Long userId = BaseContext.getCurrentId();
//        log.info("上传头像，userId:{}",userId);
//        userService.uploadProfile(userId,profile);
//        return Result.success();
//    }
}
