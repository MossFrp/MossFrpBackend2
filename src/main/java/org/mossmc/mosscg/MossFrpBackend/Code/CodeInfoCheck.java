package org.mossmc.mosscg.MossFrpBackend.Code;

import com.alibaba.fastjson.JSONObject;
import org.mossmc.mosscg.MossFrpBackend.BasicInfo;
import org.mossmc.mosscg.MossFrpBackend.Enums;
import org.mossmc.mosscg.MossFrpBackend.Node.NodeCache;
import org.mossmc.mosscg.MossFrpBackend.Node.NodeStatic;
import org.mossmc.mosscg.MossFrpBackend.User.UserInfo;

import java.util.Map;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendException;

public class CodeInfoCheck {
    public static String checkCreate(String node,String bandString,String timeString,String userID,int permission) {
        int band;
        int time;
        String errorMessage = null;
        Map<String,String> nodeInfo;
        if (!NodeCache.nodeCache.containsKey(node)) {
            return "未知的节点！";
        } else {
            nodeInfo = NodeCache.nodeCache.get(node);
        }
        try {
            errorMessage = "请输入正常的带宽参数！";
            band = Integer.parseInt(bandString);
            errorMessage = "请输入正常的时长参数！";
            time = Integer.parseInt(timeString);
        } catch (NumberFormatException e) {
            return "参数错误！"+errorMessage;
        }

        if (nodeInfo.get("enable").equals("false") && permission > 100) {
            return "该节点禁止创建穿透码！";
        }
        if (NodeCache.nodeStatusCache.get(node).equals(Enums.nodeStatusType.OFFLINE) && permission > 100) {
            return "节点不在线！请等待节点上线后再创建穿透码！";
        }
        if (Integer.parseInt(nodeInfo.get("band-max-per")) < band) {
            return "你选择的带宽过大！最大可用单穿透码带宽："+nodeInfo.get("band-max-per");
        }
        if (band <= 0) {
            return "带宽必须是大于0的正整数！";
        }
        if (time<3 || time >360) {
            return "只能创建3-360天的穿透码！";
        }
        if ((Integer.parseInt(NodeStatic.getStatic(node,"band")) +band > Integer.parseInt(nodeInfo.get("band-max-total"))) && permission > 100) {
            int rest = Integer.parseInt(nodeInfo.get("band-max-total"))-Integer.parseInt(NodeStatic.getStatic(node,"band"));
            return "该节点配额不足！剩余配额："+rest;
        }
        try {
            int maxCode = 10;
            if (permission <= 100) {
                maxCode = 1000;
            }
            if (CodeInfo.getUserCodeCount(userID) >= maxCode) {
                return "用户最多创建不超过"+maxCode+"条穿透码！";
            }
        } catch (Exception e) {
            sendException(e);
            return "数据检索错误！请联系管理员！";
        }
        return "pass";
    }

    public static String checkRemove(String node,String number,String infoType,String info) {
        String uid = UserInfo.getInfo(infoType,info,"userID");
        String userLevel = UserInfo.getInfo(infoType,info,"level");
        JSONObject jsonObject = CodeInfo.getCodeInfoByNodeNumber(node, number);
        if (userLevel == null || uid == null) {
            return "无法查询到用户信息！";
        }
        int level = BasicInfo.getUserLevelCode(userLevel);
        if (jsonObject != null) {
            if (level <= 0) {
                return "pass";
            }
            if (jsonObject.getString("user").equals(uid)) {
                if (CodeBan.getCodeBan(node, number).equals("true")) {
                    return "此穿透码已被封禁，无法删除！请联系管理员！";
                }
                if (jsonObject.getString("activity").equals("true")) {
                    return "此穿透码为活动期间特价创建，无法删除！";
                }
                return "pass";
            } else {
                return "这个穿透码不属于你！无法删除！";
            }
        } else {
            return "未知的节点或穿透码编号！";
        }
    }

    public static String checkDate(String node,String number,String infoType,String info,String date) {
        //基本参数检查
        if (!NodeCache.nodeCache.containsKey(node)) {
            return "未知的节点！";
        }
        String errorMessage = null;
        try {
            errorMessage = "请输入正常的日期参数！";
            Integer.parseInt(date);
            errorMessage = "请输入正常的七位数字编号参数！";
            Integer.parseInt(number);
        } catch (Exception e) {
            return "参数错误！"+errorMessage;
        }
        if (Integer.parseInt(date) < 3 || Integer.parseInt(date) > 360) {
            return "续期长度只能为3-360天！";
        }
        //用户数据检索
        String uid = UserInfo.getInfo(infoType,info,"userID");
        String userLevel = UserInfo.getInfo(infoType,info,"level");
        if (userLevel == null || uid == null) {
            return "无法查询到用户信息！";
        }
        //穿透码数据检索
        JSONObject jsonObject = CodeInfo.getCodeInfoByNodeNumber(node, number);
        int level = BasicInfo.getUserLevelCode(userLevel);
        if (jsonObject != null) {
            if (level <= 0) {
                return "pass";
            }
            if (jsonObject.getString("user").equals(uid)) {
                if (CodeBan.getCodeBan(node, number).equals("true")) {
                    return "此穿透码已被封禁，无法续期！请联系管理员！";
                }
                return "pass";
            } else {
                return "这个穿透码不属于你！无法续期！";
            }
        } else {
            return "未知的节点或穿透码编号！";
        }
    }

    public static String checkBand(String node,String number,String infoType,String info,String bandAdd) {
        //基本参数检查
        if (!NodeCache.nodeCache.containsKey(node)) {
            return "未知的节点！";
        }
        String errorMessage = null;
        try {
            errorMessage = "请输入正常的带宽参数！";
            Integer.parseInt(bandAdd);
            errorMessage = "请输入正常的七位数字编号参数！";
            Integer.parseInt(number);
        } catch (Exception e) {
            return "参数错误！"+errorMessage;
        }
        if (Integer.parseInt(bandAdd) <= 0) {
            return "带宽不能小于等于0！";
        }
        //用户数据检索
        String uid = UserInfo.getInfo(infoType,info,"userID");
        String userLevel = UserInfo.getInfo(infoType,info,"level");
        if (userLevel == null || uid == null) {
            return "无法查询到用户信息！";
        }
        //穿透码数据检索
        JSONObject jsonObject = CodeInfo.getCodeInfoByNodeNumber(node, number);
        int level = BasicInfo.getUserLevelCode(userLevel);
        if (jsonObject != null) {
            if (level <= 0) {
                return "pass";
            }
            if (jsonObject.getString("user").equals(uid)) {
                if (CodeBan.getCodeBan(node, number).equals("true")) {
                    return "此穿透码已被封禁，无法升配！请联系管理员！";
                }
                int bandNow = jsonObject.getInteger("band");
                int bandNew = bandNow+Integer.parseInt(bandAdd);
                if (bandNew > Integer.parseInt(NodeCache.nodeCache.get(node).get("band-max-per"))) {
                    return "你选择的带宽过大！最大可用单穿透码带宽："+NodeCache.nodeCache.get(node).get("band-max-per");
                }
                if (jsonObject.getString("status").equals("outdated")) {
                    return "穿透码已到期！请先续期再升配！";
                }
                return "pass";
            } else {
                return "这个穿透码不属于你！无法升配！";
            }
        } else {
            return "未知的节点或穿透码编号！";
        }
    }
}
