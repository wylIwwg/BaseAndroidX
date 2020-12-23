package com.jdxy.cqhcht.inforelease;

import com.jdxy.wyl.baseandroidx.tools.IConfigs;

/**
 * Created by wyl on 2019/3/8.
 */
public interface Configs extends IConfigs {


    //展示主要内容
    String SP_SHOW_PROGRAM = "show_program";

    String URL_UPLOAD_IMAGE = "/baseConsultaioninfo/screenShot";
    String URL_UPLOAD_LOGS = "/upload/logs";

    String URL_ADD_TERMINAL = "/baseConsultaioninfo/addfacility";
    String URL_GET_REGION = "/baseConsultaioninfo/departAlls";//获取区域列表信息
    String URL_INIT = "/baseConsultaioninfo/facInit";
    String URL_INIT_YAOFANG = "/quePharmacy/PharmacyInit";


    String URL_UPDATE_VOICE = "/baseConsultaioninfo/callsucess";//语音播报完成接口  pid 病人id

    String SP_TIPS = "tips";


    String test1 = "{\n" +
            "  \"doctorInfo\": {\n" +
            "    \"doctorName\": \"医生1\",\n" +
            "    \"doctorLevel\": \"医生1\",\n" +
            "    \"doctorProfil\": \"医生1\",\n" +
            "    \"doctorNum\": \"医生1\",\n" +
            "    \"id\": 1,\n" +
            "    \"clinicName\": \"医生1\"\n" +
            "  },\n" +
            "  \"current\": {\n" +
            "    \"patientName\": \"病人\",\n" +
            "    \"patientNum\": \"1\",\n" +
            "    \"state\": \"1\",\n" +
            "    \"patientId\": 1\n" +
            "  },\n" +
            "  \"next\": {\n" +
            "    \"patientName\": \"病人1\",\n" +
            "    \"patientNum\": \"1\",\n" +
            "    \"state\": \"1\",\n" +
            "    \"patientId\": 1\n" +
            "  },\n" +
            "  \"passedList\": [\n" +
            "    {\n" +
            "      \"patientName\": \"病人1\",\n" +
            "      \"patientNum\": \"1\",\n" +
            "      \"state\": \"1\",\n" +
            "      \"patientId\": 1\n" +
            "    },\n" +
            "    {\n" +
            "      \"patientName\": \"病人2\",\n" +
            "      \"patientNum\": \"1\",\n" +
            "      \"state\": \"1\",\n" +
            "      \"patientId\": 1\n" +
            "    },\n" +
            "    {\n" +
            "      \"patientName\": \"病人3\",\n" +
            "      \"patientNum\": \"1\",\n" +
            "      \"state\": \"1\",\n" +
            "      \"patientId\": 1\n" +
            "    },\n" +
            "    {\n" +
            "      \"patientName\": \"病人4\",\n" +
            "      \"patientNum\": \"1\",\n" +
            "      \"state\": \"1\",\n" +
            "      \"patientId\": 1\n" +
            "    }\n" +
            "  ],\n" +
            "  \"currents\": [\n" +
            "    {\n" +
            "      \"patientName\": \"病人1\",\n" +
            "      \"patientNum\": \"1\",\n" +
            "      \"state\": \"1\",\n" +
            "      \"patientId\": 1\n" +
            "    },\n" +
            "    {\n" +
            "      \"patientName\": \"病人2\",\n" +
            "      \"patientNum\": \"1\",\n" +
            "      \"state\": \"1\",\n" +
            "      \"patientId\": 1\n" +
            "    },\n" +
            "    {\n" +
            "      \"patientName\": \"病人3\",\n" +
            "      \"patientNum\": \"1\",\n" +
            "      \"state\": \"1\",\n" +
            "      \"patientId\": 1\n" +
            "    },\n" +
            "    {\n" +
            "      \"patientName\": \"病人4\",\n" +
            "      \"patientNum\": \"1\",\n" +
            "      \"state\": \"1\",\n" +
            "      \"patientId\": 1\n" +
            "    }\n" +
            "  ],\n" +
            "  \"waitingList\": [\n" +
            "    {\n" +
            "      \"patientName\": \"病人5\",\n" +
            "      \"patientNum\": \"1\",\n" +
            "      \"state\": \"1\",\n" +
            "      \"patientId\": 1\n" +
            "    },\n" +
            "    {\n" +
            "      \"patientName\": \"病人6\",\n" +
            "      \"patientNum\": \"1\",\n" +
            "      \"state\": \"1\",\n" +
            "      \"patientId\": 1\n" +
            "    },\n" +
            "    {\n" +
            "      \"patientName\": \"病人7\",\n" +
            "      \"patientNum\": \"1\",\n" +
            "      \"state\": \"1\",\n" +
            "      \"patientId\": 1\n" +
            "    },\n" +
            "    {\n" +
            "      \"patientName\": \"病人8\",\n" +
            "      \"patientNum\": \"1\",\n" +
            "      \"state\": \"1\",\n" +
            "      \"patientId\": 1\n" +
            "    },\n" +
            "    {\n" +
            "      \"patientName\": \"病人9\",\n" +
            "      \"patientNum\": \"1\",\n" +
            "      \"state\": \"1\",\n" +
            "      \"patientId\": 1\n" +
            "    },\n" +
            "    {\n" +
            "      \"patientName\": \"病人10\",\n" +
            "      \"patientNum\": \"1\",\n" +
            "      \"state\": \"1\",\n" +
            "      \"patientId\": 1\n" +
            "    },\n" +
            "    {\n" +
            "      \"patientName\": \"病人11\",\n" +
            "      \"patientNum\": \"1\",\n" +
            "      \"state\": \"1\",\n" +
            "      \"patientId\": 1\n" +
            "    },\n" +
            "    {\n" +
            "      \"patientName\": \"病人12\",\n" +
            "      \"patientNum\": \"1\",\n" +
            "      \"state\": \"1\",\n" +
            "      \"patientId\": 1\n" +
            "    },\n" +
            "    {\n" +
            "      \"patientName\": \"病人13\",\n" +
            "      \"patientNum\": \"1\",\n" +
            "      \"state\": \"1\",\n" +
            "      \"patientId\": 1\n" +
            "    },\n" +
            "    {\n" +
            "      \"patientName\": \"病人14\",\n" +
            "      \"patientNum\": \"1\",\n" +
            "      \"state\": \"1\",\n" +
            "      \"patientId\": 1\n" +
            "    },\n" +
            "    {\n" +
            "      \"patientName\": \"病人15\",\n" +
            "      \"patientNum\": \"1\",\n" +
            "      \"state\": \"1\",\n" +
            "      \"patientId\": 1\n" +
            "    },\n" +
            "    {\n" +
            "      \"patientName\": \"病人16\",\n" +
            "      \"patientNum\": \"1\",\n" +
            "      \"state\": \"1\",\n" +
            "      \"patientId\": 1\n" +
            "    }\n" +
            "  ],\n" +
            "  \"depart\": {\n" +
            "    \"departName\": \"科室1\",\n" +
            "    \"departId\": \"医生2\",\n" +
            "    \"clinicName\": \"诊室1\",\n" +
            "    \"clinicId\": \"医生2\",\n" +
            "  }\n" +
            "}";

    String test2 = "{\n" +
            "  \"doctorInfo\": {\n" +
            "    \"doctorName\": \"医生2\",\n" +
            "    \"doctorLevel\": \"医生2\",\n" +
            "    \"doctorProfil\": \"医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2医生2\",\n" +
            "    \"doctorNum\": \"医生2\",\n" +
            "    \"id\": 2,\n" +
            "    \"clinicName\": \"医生2\"\n" +
            "  },\n" +
            "  \"current\": {\n" +
            "    \"patientName\": \"1病人\",\n" +
            "    \"patientNum\": \"1\",\n" +
            "    \"state\": \"1\",\n" +
            "    \"patientId\": 1\n" +
            "  },\n" +
            "  \"next\": {\n" +
            "    \"patientName\": \"1病人1\",\n" +
            "    \"patientNum\": \"1\",\n" +
            "    \"state\": \"1\",\n" +
            "    \"patientId\": 1\n" +
            "  },\n" +
            "  \"passedList\": [\n" +
            "    {\n" +
            "      \"patientName\": \"病人1\",\n" +
            "      \"patientNum\": \"A001\",\n" +
            "      \"state\": \"1\",\n" +
            "      \"patientId\": 1\n" +
            "    },\n" +
            "    {\n" +
            "      \"patientName\": \"病人2\",\n" +
            "      \"patientNum\": \"A002\",\n" +
            "      \"state\": \"1\",\n" +
            "      \"patientId\": 1\n" +
            "    },\n" +
            "    {\n" +
            "      \"patientName\": \"病人3\",\n" +
            "      \"patientNum\": \"A003\",\n" +
            "      \"state\": \"1\",\n" +
            "      \"patientId\": 1\n" +
            "    },\n" +
            "    {\n" +
            "      \"patientName\": \"病人4\",\n" +
            "      \"patientNum\": \"A004\",\n" +
            "      \"state\": \"1\",\n" +
            "      \"patientId\": 1\n" +
            "    }\n" +
            "  ],\n" +
            "  \"waitingList\": [\n" +
            "    {\n" +
            "      \"patientName\": \"1病人5\",\n" +
            "      \"patientNum\": \"A002\",\n" +
            "      \"state\": \"1\",\n" +
            "      \"patientId\": 1\n" +
            "    },\n" +
            "    {\n" +
            "      \"patientName\": \"1病人6\",\n" +
            "      \"patientNum\": \"A002\",\n" +
            "      \"state\": \"1\",\n" +
            "      \"patientId\": 1\n" +
            "    },\n" +
            "    {\n" +
            "      \"patientName\": \"1病人7\",\n" +
            "      \"patientNum\": \"A002\",\n" +
            "      \"state\": \"1\",\n" +
            "      \"patientId\": 1\n" +
            "    },\n" +
            "    {\n" +
            "      \"patientName\": \"1病人8\",\n" +
            "      \"patientNum\": \"A002\",\n" +
            "      \"state\": \"1\",\n" +
            "      \"patientId\": 1\n" +
            "    },\n" +
            "    {\n" +
            "      \"patientName\": \"1病人9\",\n" +
            "      \"patientNum\": \"A002\",\n" +
            "      \"state\": \"1\",\n" +
            "      \"patientId\": 1\n" +
            "    },\n" +
            "    {\n" +
            "      \"patientName\": \"1病人10\",\n" +
            "      \"patientNum\": \"A002\",\n" +
            "      \"state\": \"1\",\n" +
            "      \"patientId\": 1\n" +
            "    },\n" +
            "    {\n" +
            "      \"patientName\": \"1病人11\",\n" +
            "      \"patientNum\": \"A002\",\n" +
            "      \"state\": \"1\",\n" +
            "      \"patientId\": 1\n" +
            "    },\n" +
            "    {\n" +
            "      \"patientName\": \"1病人12\",\n" +
            "      \"patientNum\": \"A002\",\n" +
            "      \"state\": \"1\",\n" +
            "      \"patientId\": 1\n" +
            "    },\n" +
            "    {\n" +
            "      \"patientName\": \"1病人13\",\n" +
            "      \"patientNum\": \"A002\",\n" +
            "      \"state\": \"1\",\n" +
            "      \"patientId\": 1\n" +
            "    },\n" +
            "    {\n" +
            "      \"patientName\": \"1病人14\",\n" +
            "      \"patientNum\": \"A002\",\n" +
            "      \"state\": \"1\",\n" +
            "      \"patientId\": 1\n" +
            "    },\n" +
            "    {\n" +
            "      \"patientName\": \"1病人15\",\n" +
            "      \"patientNum\": \"A002\",\n" +
            "      \"state\": \"1\",\n" +
            "      \"patientId\": 1\n" +
            "    },\n" +
            "    {\n" +
            "      \"patientName\": \"1病人16\",\n" +
            "      \"patientNum\": \"A002\",\n" +
            "      \"state\": \"1\",\n" +
            "      \"patientId\": 1\n" +
            "    }\n" +
            "  ],\n" +
            "  \"depart\": {\n" +
            "    \"departName\": \"科室2\",\n" +
            "    \"departId\": \"医生2\",\n" +
            "    \"clinicName\": \"诊室2\",\n" +
            "    \"clinicId\": \"医生2\",\n" +
            "  }\n" +
            "}";

}
