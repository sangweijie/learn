package com.billow.user.api;

import com.billow.common.base.BaseApi;
import com.billow.tools.resData.BaseResponse;
import com.billow.user.pojo.vo.UserVo;
import com.billow.user.service.UserService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户信息操作
 *
 * @author liuyongtao
 * @create 2018-11-05 15:11
 */
@Api(value = "用户信息操作")
@RestController
@RequestMapping("/userApi")
public class UserApi extends BaseApi {

    @Autowired
    private UserService userService;

    /**
     * 根据用户名查询出用户信息
     *
     * @param userCode 用户名
     * @return UserVo
     * @author LiuYongTao
     * @date 2018/11/5 15:18
     */
    @GetMapping("/findUserInfoByUsercode/{userCode}")
    public BaseResponse<UserVo> findUserInfoByUsercode(@PathVariable("userCode") String userCode) {
        BaseResponse<UserVo> baseResponse = super.getBaseResponse();
        try {
            UserVo userVo = userService.findUserInfoByUsercode(userCode);
            baseResponse.setResData(userVo);
        } catch (Exception e) {
            super.getErrorInfo(e, baseResponse);
        }
        return baseResponse;
    }

    /**
     * 根据用户code 查询用户信息，用于spring security 认证使用
     *
     * @param userCode 用户code
     * @return org.springframework.security.core.userdetails.UserDetails
     * @author LiuYongTao
     * @date 2019/4/28 9:27
     */
    @GetMapping("/loadUserByUsername/{userCode}")
    public BaseResponse<UserDetails> loadUserByUsername(@PathVariable("userCode") String userCode) {
        BaseResponse<UserDetails> baseResponse = super.getBaseResponse();
        try {
            UserDetails userDetails = userService.loadUserByUsername(userCode);
            baseResponse.setResData(userDetails);
        } catch (Exception e) {
            super.getErrorInfo(e, baseResponse);
        }
        return baseResponse;
    }
}