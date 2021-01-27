package com.jdxy.wyl.baseandroidx.tools;

import android.os.Environment;

import com.jdxy.wyl.baseandroidx.base.IDescription;

/**
 * Created by wyl on 2019/5/29.
 */
public interface IConfigs {

    String LOG_ERROR = "【Error】";
    String LOG_SOCKET = "【Socket】";
    String LOG_HTTP = "【Http】";

    String DATABASE_SETTING_NAME = "setting";


    String PATH_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath() + "/sjjd/";
    String PATH_APK = PATH_ROOT + "apk/";
    String PATH_TTS = PATH_ROOT + "tts/";
    String PATH_LOG = PATH_ROOT + "log/";//数据日志
    String PATH_MAC = PATH_ROOT + "mac/";
    String PATH_ERROR = PATH_ROOT + "icon_error/";//错误日志
    String PATH_CAPTURE = PATH_ROOT + "capture/";//截图保存
    String PATH_VIDEO = PATH_ROOT + "video/";//截图保存

    String PATH_PROGRAM = PATH_ROOT + "program/";//项目节目
    String PATH_ZIP = PATH_ROOT + "zip/";//项目节目

    String URL_ADD_TERMINAL = "/baseConsultaioninfo/addfacility";

    /**
     * 网络请求相关
     */
    int NET_LOAD_DATA_SUCCESS = 200;//数据加载成功
    int NET_LOAD_DATA_FAILED = -1;//数据加载失败
    int NET_CONNECT_ERROR = 300;//网络错误
    int NET_SERVER_ERROR = 400;//服务器错误
    int NET_URL_SUCCESS = 500;//链接请求正常
    int NET_URL_ERROR = 501;//链接请求错误
    int NET_UNKNOWN_ERROR = 250;//未知错误
    int NET_TIMEOUT = 201;//请求超时
    int NET_TIME_CHANGED = 10001;//时间变化

    String HOST = "http://%1$s:%2$s";//192.168.2.188:8080
    String HOSTS = "https://%1$s:%2$s";//192.168.2.188:8080

    ///*socket*//
    String TYPE = "type";
    String HEARTBREAK = "ping";
    String PING = "{\"type\":\"ping\"}";

    // 单个CPU线程池大小
    int POOL_SIZE = 5;
    int MSG_SOCKET_RECEIVED = 2000;//接收socket通知
    int MSG_SOCKET_DISCONNECT = -2000;//socke断开连接
    int MSG_CREATE_TCP_ERROR = 2001;//tcp创建失败
    int MSG_PING_TCP_TIMEOUT = 2002;//tcp连接超时

    int MSG_REBOOT_LISTENER = 2003;//设备关机重启

    int MSG_MEDIA_INIT = 17853;//媒体数据准备完成

    ///设备注册
    int REGISTER_FORBIDDEN = 0;//禁止注册
    int REGISTER_FOREVER = -1;//永久注册
    int REGISTER_LIMIT = 1;//注册时间剩余

    String APK_VERSION_CODE = "versionCode";

    /*节目配置相关*/
    String SP_PROGRAM_ID = "proId";//节目id
    String SP_APK_ID = "apkid";//
    String SP_TARGET_APP = "targetApp";//需要跳转的app报名
    String SP_PATH_DATA = "proSource";//节目资源目录
    String SP_PATH_DATA_BACKUP = "proSourceBackup";//节目资源备份
    String SP_SETTING_TRY_TIME = "tryTime";//失败尝试次数
    String SP_SETTING_START_TIME = "proStartTime";//节目开始播放时间
    String SP_SETTING_END_TIME = "proEndTime";//节目结束时间

    /*配置相关*/
    String SP_MODIFIED_PROJECT_NAME = "modifyProjectName";//修改后的项目
    String SP_DEFAULT_PROJECT_NAME = "defaultProjectName";//默认项目

    String SP_HIDE_PATIENTNAME = "hidename";//隐藏病人姓名
    String SP_IP = "ip";
    String SP_PORT_HTTP = "portHttp";
    String SP_PORT_SOCKET = "portSocket";//
    String SP_VOICE_TEMP = "voiceFormat";//

    String SP_API = "api";
    String SP_HOST = "host";
    String SP_TIPS = "tips";//终端提示
    String SP_PHONE = "phone";///联系号码
    String SP_POWER = "power";//开关机
    String SP_SHOWLOG = "showlog";//是否显示日志
    /*区域*/


    String SP_SOFT_TYPE = "softType";//软件类型 门牌 综合 呼叫器等

    String SP_APP_TYPE = "appType";//门牌综合屏应用类型

    String INTENT_APP_TYPE = "intent_apptype";//应用类型
    String INTENT_CLEAR_APP_TYPE = "clear_intent_apptype";//清除应用类型


    int APP_TYPE_YaoFang = 1;//药房
    int APP_TYPE_YiJi = 2;//医技
    int APP_TYPE_MenZhen = 3;//门诊


    String SP_PATH_REGISER = "pathRegister";
    String SP_PATH_MAX = "pathMac";
    String SP_PATH_LOG = "pathLog";
    String SP_DEV_UPTIME = "devUpTime";
    String SP_DEV_DOWNTIME = "devDownTime";
    String SP_VOICE_FORMAT = "voiceFormat";
    String SP_VOICE_SWITCH = "voiceSwitch";//
    String SP_CONTENT_SWITCH = "contentSwitch";//节目内容 与 数据切换


    String SP_UNIT_ID = "unitId";

    String SP_UNIT_NAME = "unitName";
    String SP_FLOOR_NAME = "floorName";
    String SP_FLOOR_ID = "floorId";
    String SP_AREA_ID = "areaId";
    String SP_AREA_NAME = "areaName";
    String SP_WINDOW_NAME = "windowName";

    String SP_DEPART_NAME = "deptName";//科室名
    String SP_DEPART_ID = "deptId";//科室id

    String SP_CLINIC_NAME = "clinicName";//诊室名
    String SP_CLINIC_ID = "clinicId";//诊室id

    String SP_WINDOW_NUM = "windowNum";//窗口号
    String SP_WINDOW_ID = "windowId";//窗口id
    String SP_ROOM_NUM = "roomNum";//房间号
    String SP_ROOM_NAME = "roomName";//房间号
    String SP_ROOM_ID = "roomId";//房间id
    String SP_SETTING_SCROLL_TIME = "scroll";//轮播滚动时间
    String SP_SETTING_DELAY_TIME = "delay";//延迟滚动时间
    String SP_SETTING_BACK_TIME = "icon_back";//界面操作返回时间

    String SP_FORCED_URL = "forcedurl";
    String SP_FORCED_STATE = "forcedstate";
    String SP_APK_VERSION_CODE = "code";
    String SP_CLIENT_ID = "clientId";

    int DEVICE_FORBIDDEN = 0;//未注册
    int DEVICE_REGISTERED = 1;//已注册
    int DEVICE_OUTTIME = 2;//已过期

    String URL_ADD_PUSH = "/Push/addPush";

    String METHOD_BIND = "Bind.machineRegister";
    String METHOD_ADD_DEVICE = "Bind.machineRegister";
    String METHOD_GET_AREA = "Bind.collocation";
    String METHOD_UPLOAD_LOG = "Other.machineLogFile";//上传日志
    String METHOD_UPLOAD_CAPTURE = "Other.uploadCapture";//上传截图
    String METHOD_VOICE_FINISH = "Call.finishVoice";

}
